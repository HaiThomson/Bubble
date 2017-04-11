/**
 * 版权所有 (c) 2017， 吕绪海. 保留所有权利
 * Copyright (c) 2017, Hai Thomson. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package source.kernel.db.driver;

import source.kernel.db.DataBaseDriver;
import source.kernel.db.pool.ConnectionPooling;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * 操作MySQL的通用驱动。
 * 反射必须是有访问权限的类
 *
 * @since 7
 * @author Hai Thomson
 */
public class MySQLDriver extends DataBaseDriver {

	private String tablepre = "";
	private Connection connection = null;

	@Override
	protected void setTablePrefix(String tablepre) {
		this.tablepre = tablepre;
	}

	@Override
	protected void connect() throws SQLException {
		connection =  ConnectionPooling.getConnection();
	}

	@Override
	protected Connection getConnection() {
		return this.connection;
	}

	/**
	 * connection.close() 后connection并不会指向null。
	 * 多次调用不报错。
	 */
	@Override
	protected void closeConnection() {
		try {
			connection.close();
			// connection.close()后connection并不会指向null.
			// 为DataBase初始化时提供稳定的状态信息。
			connection = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取数据版本号，如出现问题则返回 ""
	 * @return
	 */
	@Override
	protected String getDatabaseVersion() {
		try {
			return connection.getMetaData().getDatabaseProductVersion();
		} catch (SQLException e) {
			return "";
		}
	}

	/**
	 * 获取正确带前缀的表名，转换数据库句柄
	 * @param tablename
	 */
	@Override
	protected String getRealTableName(String tablename) {
		return tablepre + tablename;
	}

	@Override
	protected String makeInsert(String table, Map<String, Object> data) {
		//return "INSERT INTO " + table + this.implodeInsert(data);
		return "INSERT INTO " + this.getRealTableName(table) + " SET " + this.implode(data, ",");
	}

	@Override
	protected String makeDelete(String table, Map<String, Object> condition) {
		return "DELETE FROM " + this.getRealTableName(table) + " WHERE " + this.implode(condition, "AND");
	}

	@Override
	protected String makeDelete(String table, String condition) {
		return "DELETE FROM " + this.getRealTableName(table) + " WHERE " + condition;
	}

	@Override
	protected String makeUpdate(String table, Map<String, Object> data, Map<String, Object> condition) {
		if (condition == null) {
			return "UPDATE " + this.getRealTableName(table) + " SET " + this.implode(data, ",");
		} else {
			return "UPDATE " + this.getRealTableName(table) + " SET " + this.implode(data, ",") + " WHERE " + this.implode(condition, "AND");
		}
	}

	@Override
	protected String makeUpdate(String table, Map<String, Object> data, String condition) {
		if (condition == null || condition.equals("")) {
			return "UPDATE " + this.getRealTableName(table) + " SET " + this.implode(data, ",");
		} else {
			return "UPDATE " + this.getRealTableName(table) + " SET " + this.implode(data, ",") + " WHERE " + condition;
		}
	}

	@Override
	protected String makePagination(String table, String condition, int start, int limit) {
		if (condition == "") {
			if (start > 0 && limit > 0) {
				return "SELECT * FROM " + this.getRealTableName(table) + " LIMIT " + start + ", " + limit;
			} else if (limit > 0) {
				return "SELECT * FROM " + this.getRealTableName(table) + " LIMIT " + limit;
			} else if (start > 0) {
				return "SELECT * FROM " + this.getRealTableName(table) + " LIMIT " + start;
			} else {
				return "";
			}
		} else {
			if (start > 0 && limit > 0) {
				return "SELECT * FROM " + this.getRealTableName(table) + " WHERE " + condition + " LIMIT " + start + ", " + limit;
			} else if (limit > 0) {
				return "SELECT * FROM " + this.getRealTableName(table) + " WHERE " + condition + " LIMIT " + limit;
			} else if (start > 0) {
				return "SELECT * FROM " + this.getRealTableName(table) + " WHERE " + condition + " LIMIT " + start;
			} else {
				return "";
			}
		}
	}

	@Override
	protected String makeTruncate(String table) {
		return "TRUNCATE " + this.getRealTableName(table);
	}

	@Override
	protected String makeOrder(String field, String direction) {
		return " ORDER BY " + this.quoteField(field) + " " + direction;
	}

	@Override
	protected String makeLimit(int start, int limit) {
		if (start > 0 && limit > 0) {
			return " LIMIT " + start + ", " + limit;
		} else if (limit > 0) {
			return " LIMIT " + limit;
		} else if (start > 0) {
			return " LIMIT " + start;
		} else {
			return "";
		}
	}

	@Override
	protected String makeCondition(String field, Object value, String glue) {
		glue = glue.toLowerCase();

		if (value instanceof List) {
			glue = glue.equals("notin") || glue.equals("not in") ? "notin" : "in";
			String sql = "";
			sql = this.quoteValueList((List<Object>) value);
			return field + (glue.equals("notin") ? " NOT" : "") + " IN("  + sql + ")";
		}

		if (value.getClass().isArray()) {
			glue = glue.equals("notin") || glue.equals("not in") ? "notin" : "in";
			String sql = "";
			sql = this.quoteValueArray((Object[]) value);
			return field + (glue.equals("notin") ? " NOT" : "") + " IN("  + sql + ")";
		}

		switch (glue) {
			case "=":
				return field + glue + this.quoteValue(value);

			case "-":
			case "+":
				return field + "=" + field + glue + this.quoteValue(value);

			case "|":
			case "&":
			case "^":
				return field + "=" + field + glue + this.quoteValue(value);

			case ">":
			case "<":
			case "<>":
			case "<=":
			case ">=":
				return field + glue + this.quoteValue(value);

			//makeCondition like多条件时，传入的值要自己处理（很难也不很少自动生成）。
			//LIKE ("%sf%","g%","joke")
			case "like":
				return field +  " LIKE (" + this.quoteValue(value) +  ")";

			default:
				return "";
		}
	}

	@Override
	protected String implode(Map<String, Object> data, String glue) {
		String sql = "";
		String comma = "";
		glue = " " + glue + " ";
		Set<String> keys = data.keySet();
		for (String key : keys) {
			sql = sql + comma + this.quoteField(key) + " = " + this.quoteValue(data.get(key));
			comma = glue;
		}
		return sql;
	}

	/**
	 *
	 * @param data
	 * @return
	 */
	protected String implodeInsert(Map<String, Object> data) {
		String sql = "";
		String comma = "";
		String glue = ", ";
		String fields = "";
		String values = "";
		Set<String> keys = data.keySet();
		for (String key : keys) {
			fields = fields + comma + this.quoteField(key);
			values = values + comma + this.quoteValue(data.get(key));
			comma = glue;
		}
		sql = "(" + fields + ")" + " VALUES " + "(" + values + ")";
		return sql;
	}

	protected String quoteValue(Object value) {
		if (new StringBuffer().append(value).toString().equals("?")) {
			return "?";
		}
		return "\'" + value + "\'" ;
	}

	protected String quoteValueArray(Object[] value) {
		String sql = "";
		String comma = "";
		String glue = ", ";
		for(Object object: value) {
			sql = sql + comma + value;
			comma = glue;
		}
		return sql;
	}

	protected String quoteValueList(List<Object> value) {
		String sql = "";
		String comma = "";
		String glue = ", ";
		for(Object object: value) {
			sql = sql + comma + value;
			comma = glue;
		}
		return sql;
	}

	/**
	 * 工具方法，转换字符串
	 */
	protected String quoteField(String field) {
		return "`" + field + "`";
	}
}
