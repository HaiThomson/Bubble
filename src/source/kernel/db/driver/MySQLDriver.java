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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * 操作MySQL的通用驱动。
 * 反射必须是有访问权限的类
 *
 * @since 1.7
 * @author Hai Thomson
 */
public class MySQLDriver extends DataBaseDriver {

	@Override
	protected String makeInsert(String table, Map<String, Object> data) {
		return "INSERT INTO " + this.getRealTableName(table) + " SET " + this.implode(data, ",");
	}

	@Override
	protected String makeInsert(String table, Object data) throws SQLException {
		return "INSERT INTO " + this.getRealTableName(table) + " SET " + this.implode(data, ",");
	}

	@Override
	protected String makeDelete(String table, Map<String, Object> condition) {
		return "DELETE FROM " + this.getRealTableName(table) + " WHERE " + this.implode(condition, "AND");
	}

	@Override
	protected String makeDelete(String table, Object condition) throws SQLException {
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
	protected String makeUpdate(String table, Object data, Object condition) throws SQLException {
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
	protected String makeUpdate(String table, Object data, String condition) throws SQLException {
		if (condition == null || condition.equals("")) {
			return "UPDATE " + this.getRealTableName(table) + " SET " + this.implode(data, ",");
		} else {
			return "UPDATE " + this.getRealTableName(table) + " SET " + this.implode(data, ",") + " WHERE " + condition;
		}
	}

	@Override
	protected String makePagination(String table, String condition, String sort, int start, int limit) {
		if (condition == null || condition.equals("")) {
			if (sort == null || sort.equals("")) {
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
					return "SELECT * FROM " + this.getRealTableName(table) + " " + sort + " LIMIT " + start + ", " + limit;
				} else if (limit > 0) {
					return "SELECT * FROM " + this.getRealTableName(table) + " " + sort + " LIMIT " + limit;
				} else if (start > 0) {
					return "SELECT * FROM " + this.getRealTableName(table) + " " + sort + " LIMIT " + start;
				} else {
					return "";
				}
			}
		} else {
			if (sort == null || sort.equals("")) {
				if (start > 0 && limit > 0) {
					return "SELECT * FROM " + this.getRealTableName(table) + " WHERE " + condition + " LIMIT " + start + ", " + limit;
				} else if (limit > 0) {
					return "SELECT * FROM " + this.getRealTableName(table) + " WHERE " + condition + " LIMIT " + limit;
				} else if (start > 0) {
					return "SELECT * FROM " + this.getRealTableName(table) + " WHERE " + condition + " LIMIT " + start;
				} else {
					return "";
				}
			} else {
				if (start > 0 && limit > 0) {
					return "SELECT * FROM " + this.getRealTableName(table) + " WHERE " + condition + " " + sort + " LIMIT " + start + ", " + limit;
				} else if (limit > 0) {
					return "SELECT * FROM " + this.getRealTableName(table) + " WHERE " + condition + " " + sort + " LIMIT " + limit;
				} else if (start > 0) {
					return "SELECT * FROM " + this.getRealTableName(table) + " WHERE " + condition + " " + sort + " LIMIT " + start;
				} else {
					return "";
				}
			}
		}
	}

	@Override
	public String makeLockTable(String table, String type) throws SQLException {
		if (type == null || type.equals("")) {
			throw new SQLException("Null type");
		}

		if (table == null || table.equals("")) {
			throw new SQLException("Null table");
		}

		type = type.toUpperCase();
		switch (type) {
			case "X" :
				return "LOCK TABLE " + this.getRealTableName(table) + " WRITE";
		}

		throw new SQLException("not supported " + type);
	}

	@Override
	public String makeUnlockTable(String table, String type) {
		return "UNLOCK TABLES";
	}

	@Override
	protected String makeTruncate(String table) {
		return "TRUNCATE " + this.getRealTableName(table);
	}

	@Override
	protected String makeSelectTableField(String table) {
		return "SHOW FIELDS FROM " + this.getRealTableName(table);
	}

	@Override
	protected String makeOrder(String field, String direction) {
		return " ORDER BY " + this.quoteField(field) + " " + direction + " ";
	}

	@Override
	protected String makeLimit(int start, int limit) {
		if (start > 0 && limit > 0) {
			return " LIMIT " + start + ", " + limit + " ";
		} else if (limit > 0) {
			return " LIMIT " + limit + " ";
		} else if (start > 0) {
			return " LIMIT " + start + " ";
		} else {
			return " ";
		}
	}

	@Override
	protected String makeCondition(String field, Object value, String glue) {
		glue = glue.toLowerCase();

		if (value instanceof List) {
			glue = glue.equals("notin") || glue.equals("not in") ? "notin" : "in";
			String sql = "";
			sql = this.quoteValueList((List<Object>) value);
			return " " + field + (glue.equals("notin") ? " NOT" : "") + " IN("  + sql + ") ";
		}

		if (value.getClass().isArray()) {
			glue = glue.equals("notin") || glue.equals("not in") ? "notin" : "in";
			String sql = "";
			sql = this.quoteValueArray((Object[]) value);
			return " " + field + (glue.equals("notin") ? " NOT" : "") + " IN("  + sql + ") ";
		}

		switch (glue) {
			case "=":
				return " " + field + glue + this.quoteValue(value) + " ";

			case "-":
			case "+":
				return " " + field + "=" + field + glue + this.quoteValue(value) + " ";

			case "|":
			case "&":
			case "^":
				return " " + field + "=" + field + glue + this.quoteValue(value) + " ";

			case ">":
			case "<":
			case "<>":
			case "<=":
			case ">=":
				return " " + field + glue + this.quoteValue(value) + " ";

			//like多条件时，传入的值要自己处理
			//如：传入 "%sf%","g%","joke" 生成 LIKE ("%sf%","g%","joke")
			case "like":
				return " " + field +  " LIKE (" + this.quoteValue(value) +  ") ";

			default:
				return " ";
		}
	}

	@Override
	protected String makeGroup(String... fields) throws SQLException {
		if (fields == null || fields.length == 0) {
			throw new SQLException("Null Fields");
		}

		StringBuffer stringBuffer = new StringBuffer(" GROUP BY ");
		for (int i = 0; i < fields.length; i++) {
			if (fields[i] != null && fields[i].equals("")) {
				stringBuffer.append(fields[i]);
				stringBuffer.append(", ");
			} else {
				throw new SQLException("Null Field in subscript " + i);
			}
		}
		stringBuffer.delete(stringBuffer.length() - 3, stringBuffer.length() - 1);
		stringBuffer.append(" ");

		return stringBuffer.toString();
	}

	@Override
	protected String makeHaving(String method, String field, String direction, Object value) throws SQLException {
		return null;
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

	//@Override
	protected String implode(Object data, String glue) throws SQLException {
		String sql = "";

		try {
			StringBuffer stringBuffer = new StringBuffer();
			String comma = "";
			glue = " " + glue + " ";
			BeanInfo beanInfo = Introspector.getBeanInfo(this.getClass());
			PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
			if (proDescrtptors != null && proDescrtptors.length - 1  > 0) {
				for (PropertyDescriptor propDesc : proDescrtptors) {
					Method getMethod = propDesc.getReadMethod();
					Object propertyObject = getMethod.invoke(this);
					if (propertyObject != null) {
						stringBuffer.append(comma);
						stringBuffer.append(this.quoteField(propDesc.getName()));
						stringBuffer.append(" = ");
						stringBuffer.append(this.quoteValue(propertyObject));
						comma = glue;
					}
				}

				if (stringBuffer.length() > 0) {
					return stringBuffer.toString();
				}  else {
					throw new SQLException(data.getClass().getName() + " all get method return null!");
				}
			} else {
				throw new SQLException(data.getClass().getName() + " is not a JavaBean. Not found 'get' method!");
			}
		} catch (InvocationTargetException e) {
			throw new SQLException(data.getClass().getName() + "   " + e.getMessage());
		} catch (IntrospectionException e) {
			throw new SQLException(data.getClass().getName() + "   " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new SQLException(data.getClass().getName() + "   " + e.getMessage());
		}

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
		sql = "(" + fields + ")" + " VALUES " + "(" + values + ") ";
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
