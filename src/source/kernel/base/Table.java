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
package source.kernel.base;

import source.kernel.DB;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author Hai Thomson
 */
public abstract class Table extends Base {
    protected String tableName;
    protected String primaryKey;

    protected Table(String tableName, String primaryKey) {
        this.tableName = tableName;
        this.primaryKey = primaryKey;
        this.checkTableName();
        this.checkPrimaryKey();
    }

    /**
     * 用fetch更准确表达含义
     * 很多人用select为了和SQL CURD用词保持一致.
     * ORM搞了那么多年，词都憋不过来.搞个什么劲~~
     * @param value
     * @return
     * @throws SQLException
     */
    public Map fetchByPrimaryKey(Object value) throws SQLException {
        return DB.queryFirstRow("SELECT * FROM " + DB.getRealTableName(this.tableName) + " WHERE " + DB.makeCondition(this.primaryKey, value));
    }

    public Map fetchAll(Object value) throws SQLException {
        return DB.queryAll("SELECT * FROM " + DB.getRealTableName(this.tableName));
    }

    public Map fetchTablefield() throws SQLException {
        return DB.queryAll(DB.makeSelectTableField(this.tableName));
    }

    protected Map fetchRange(String condition, String sort, int m, int n) throws SQLException {
        return DB.queryAll(DB.makePagination(this.tableName, condition, sort, m, n));
    }

    public Map fetchRangeByPrimaryKey(String sort, int m, int n) throws SQLException {
        return this.fetchRange("", DB.makeOrder(this.primaryKey, sort), m, n);
    }

    public long countAll() throws SQLException {
        return (long) DB.queryScalar("SELECT count(*) FROM " + DB.getRealTableName(this.tableName));
    }

    public int updateByPrimaryKey(Object value, Map<String, Object> data) throws SQLException {
        if(data != null) {
            if (data.getClass().isArray()) {
                return DB.update(this.tableName, data, DB.makeCondition(this.primaryKey, value, "="));
            } else {
                return DB.update(this.tableName, data, DB.makeCondition(this.primaryKey, value, "in"));
            }
        } else {
            throw new SQLException("传入的数据为null");
        }
    }

    public int deleteByPrimaryKey(Object value) throws SQLException {
        if(value != null) {
            return DB.delete(this.tableName, DB.makeCondition(this.primaryKey, value));
        } else {
            throw new SQLException("传入的数据为null");
        }
    }

    public Object insert(Map<String, Object> data) throws SQLException {
        if (data != null) {
            return DB.insert(this.tableName, data);
        } else {
            throw new SQLException("传入的数据为null");
        }
    }

    public Boolean truncate() throws SQLException {
        return DB.executeCommand(DB.makeTruncate(this.tableName));
    }

    public void checkPrimaryKey() {
        if (this.primaryKey == null || this.primaryKey.equals("")) {
            throw new NullPointerException("Table的子类["+ this.getClass().getName() +"]未按规范初始化有效的主键");
        }
    }

    public void checkTableName() {
        if (this.tableName == null || this.tableName.equals("")) {
            throw new NullPointerException("Table的子类["+ this.getClass().getName() +"]未按规范初始化有效的表名");
        }
    }

    public String toString() {
        if (this.tableName == null || this.tableName.equals("") || this.primaryKey == null || this.primaryKey.equals("")) {
            return "TableName=" + this.tableName + " PrimaryKey=" + this.primaryKey;
        } else {
           return super.toString();
        }
    }
}
