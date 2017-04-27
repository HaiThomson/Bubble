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
package source.kernel.db;

import source.kernel.db.pool.ConnectionPooling;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Hai Thomson
 */
public abstract class DataBaseDriver {

    protected String tablepre = "";
    protected Connection connection = null;

    protected void setTablePrefix(String tablepre) {
        this.tablepre = tablepre;
    }

    protected void connect() throws SQLException {
        this.connection =  ConnectionPooling.getConnection();
    }

    protected Connection getConnection() {
        return this.connection;
    }

    /**
     * connection.close() 后connection并不会指向null。
     * 多次调用不报错。
     */
    protected void closeConnection() throws SQLException {
        this.connection.close();
        // connection.close()后connection并不会指向null.
        // 为DataBase下次初始化时提供稳定的状态信息。
        this.connection = null;
    }

    protected String getDatabaseProductName() throws SQLException {
        return this.connection.getMetaData().getDatabaseProductName();
    }

    /**
     * 获取数据版本号
     * @return
     */
    protected String getDatabaseVersion() throws SQLException {
        return this.connection.getMetaData().getDatabaseProductVersion();
    }

    /**
     * 获取正确带前缀的表名，转换数据库句柄
     * @param tablename
     */
    protected String getRealTableName(String tablename) {
        return this.tablepre + tablename;
    }


    protected void beginTransaction() throws SQLException {
        if(connection.getAutoCommit()) {
            this.connection.setAutoCommit(false);
        }
    }

    public void closeTransaction() throws SQLException {
        if(!this.connection.getAutoCommit()) {
            this.connection.setAutoCommit(true);
        }
    }

    protected boolean isAutoCommit() throws SQLException {
        if(this.connection.getAutoCommit()) {
            return true;
        } else {
            return false;
        }
    }

    protected void commitTransaction() throws SQLException {
        if(!this.connection.getAutoCommit()){
            this.connection.commit();
        }
    }

    protected void rollBackTransaction() throws SQLException {
        if(!this.connection.getAutoCommit()){
            this.connection.rollback();
        }
    }

    public abstract String makeLockTable(String table, String type) throws SQLException;

    public abstract String makeUnlockTable(String table, String type);

    protected abstract String makeTruncate(String table);

    protected abstract String makeUpdate(String table, Object data, String condition) throws SQLException;

    protected abstract String makePagination(String table, String condition, String sort, int start, int limit);

    protected abstract String makeSelectTableField(String table);

    protected abstract String makeInsert(String table, Map<String, Object> data);

    protected abstract String makeInsert(String table, Object data) throws SQLException;

    protected abstract String makeDelete(String table, Map<String, Object> condition);

    protected abstract String makeDelete(String table, Object condition) throws SQLException;

    protected abstract String makeDelete(String table, String condition);

    protected abstract String makeUpdate(String table, Map<String, Object> data, Map<String, Object> condition);

    protected abstract String makeUpdate(String table, Object data, Object condition) throws SQLException;

    protected abstract String makeUpdate(String table, Map<String, Object> data, String condition);

    protected abstract String makeCondition(String field, Object value, String glue);

    protected abstract String makeGroup(String... fields) throws SQLException;

    protected abstract String makeHaving(String method, String field, String direction, Object value) throws SQLException;

    protected abstract String makeOrder(String field, String direction);

    protected abstract String makeLimit(int m, int n);

    protected abstract String implode(Map<String, Object> data, String glue);

}
