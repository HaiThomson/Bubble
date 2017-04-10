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

import source.kernel.db.dbutils.DbUtils;
import source.kernel.db.dbutils.ResultSetHandler;
import source.kernel.db.dbutils.handlers.*;
import source.kernel.security.sql.SQLWatcher;

import java.sql.*;
import java.util.*;

/**
 * @author Hai Thomson
 */
public class DataBase {

    // 多数据库支持，增加driver列表.
    // 增加getDriver(String dbName)
    // C3P0增加命名空间方式
    // 每个执行函数增加一个多参函数
    // 思考再三，决定不支持多数据库开发模式！原因请转向博客
    private static ThreadLocal threadDriver = new ThreadLocal();

    public static DataBaseDriver getDriver() {
        return (DataBaseDriver) threadDriver.get();
    }

    public static void destoryDriver() {
        DataBase.threadDriver.set(null);
    }

    /**
     *
     * @param dbDriverPath
     * @param tablepre
     * @throws SQLException
     */
    public static void init(String dbDriverPath, String tablepre) throws SQLException {
        if ( DataBase.getDriver() == null || DataBase.getDriver().getConnection() == null || DataBase.getDriver().getConnection().isClosed()) {
            try {
                Class<?> dbDriverClass = Class.forName(dbDriverPath);
                DataBase.threadDriver.set((DataBaseDriver) dbDriverClass.newInstance());
                DataBase.getDriver().setTablePrefix(tablepre);
                DataBase.getDriver().connect();
            } catch (Exception e) {
                ExceptionHandler.handling(e);
            }
        }
    }

    /**
     * connection.close()；后connection并不会指向null。
     * 多次调用不报错。不建议多次调用。
     */
    public static void closeConnection() {
        DataBase.getDriver().closeConnection();
    }

    public static String getDatabaseVersion() {
        return DataBase.getDriver().getDatabaseVersion();
    }

    public static String getRealTableName(String table) {
        return DataBase.getDriver().getRealTableName(table);
    }

    /**
     * 执行数据库命令.
     * 返回是否执行成功
     * @param command
     */
    public static boolean executeCommand(String command) {
        Connection connection = DataBase.getDriver().getConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
            return statement.execute(command);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 第一个条件，第二个条件。。。影响的行数数组
     * 增删改
     * @param sql
     * @param params
     * @return
     */
    public static int[] batch(String sql, Object[][] params) {
        if (sql == null) {
            //throw new SQLException("Null SQL statement");
        }

        if (params == null) {
            //throw new SQLException("Null parameters. If parameters aren't need, pass an empty array.");
        }

        PreparedStatement stmt = null;
        int[] rows = null;
        Connection connection = DataBase.getDriver().getConnection();
        try {
            stmt = connection.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                DataBase.fillStatement(stmt, params[i]);
                stmt.addBatch();
            }
            rows = stmt.executeBatch();

        } catch (SQLException e) {
            DataBase.recodeSQLException(e, sql, (Object[]) params);
        } finally {
            try {
                DbUtils.close(stmt);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return rows;
    }

    /**
     * 批量插入数据并返回每个参数生成的id数组
     * @param sql
     * @param params
     * @return
     */
    public static List<Object> insertBatch(String sql, Object[][] params) {
        return insertBatch(sql, new ColumnListHandler<Object>(1), params);
    }

    private static <T> T insertBatch(String sql, ResultSetHandler<T> rsh, Object[][] params) {
        if (sql == null) {
            //throw new SQLException("Null SQL statement");
        }

        if (params == null) {
            //throw new SQLException("Null parameters. If parameters aren't need, pass an empty array.");
        }

        Connection connection = DataBase.getDriver().getConnection();
        PreparedStatement stmt = null;
        T generatedKeys = null;
        try {
            stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            for (int i = 0; i < params.length; i++) {
                DataBase.fillStatement(stmt, params[i]);
                stmt.addBatch();
            }
            stmt.executeBatch();
            ResultSet rs = stmt.getGeneratedKeys();
            generatedKeys = rsh.handle(rs);

        } catch (SQLException e) {
            DataBase.recodeSQLException(e, sql, (Object[]) params);
        } finally {
            try {
                DbUtils.close(stmt);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return generatedKeys;
    }

    /**
     * 插入一行数据并返回自动增长的id
     * @param sql
     * @return
     */
    public static Object insert(String sql) {
        return DataBase.insert(sql, new ScalarHandler(), (Object[]) null);
    }

    /**
     * 插入一行数据并返回自动增长的id
     * @param sql
     * @param params
     * @return
     */
    public static Object insert(String sql, Object... params) {
        return DataBase.insert(sql, new ScalarHandler(), params);
    }

    private static <T> T insert(String sql, ResultSetHandler<T> rsh, Object... params) {
        if (sql == null) {
            //throw new SQLException("Null SQL statement");
        }

        if (rsh == null) {
            //throw new SQLException("Null ResultSetHandler");
        }

        Connection connection = DataBase.getDriver().getConnection();
        PreparedStatement stmt = null;
        T generatedKeys = null;

        try {
            stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            DataBase.fillStatement(stmt, params);
            stmt.executeUpdate();
            ResultSet resultSet = stmt.getGeneratedKeys();
            generatedKeys = rsh.handle(resultSet);
        } catch (SQLException e) {
            DataBase.recodeSQLException(e, sql, params);
        } finally {
            try {
                DbUtils.close(stmt);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return generatedKeys;
    }

    public static Object insert(String table, Map<String, Object> data) {
        String sql = DataBase.getDriver().makeInsert(table, data);
        return DataBase.insert(sql);
    }

    /**
     * 增删改，只返回受影响的行数
     * @param sql
     * @return
     */
    public static int update(String sql) {
        return DataBase.update(sql, (Object[]) null);
    }

    public static int update(String sql, Object param) {
        return DataBase.update(sql, new Object[]{param});
    }

    public static int update(String sql, Object... params) {
        if (sql == null) {
            //throw new SQLException("Null SQL statement");
        }

        Connection connection = DataBase.getDriver().getConnection();
        PreparedStatement stmt = null;
        int rows = 0;

        try {
            stmt = connection.prepareStatement(sql);
            DataBase.fillStatement(stmt, params);
            rows = stmt.executeUpdate();

        } catch (SQLException e) {
            DataBase.recodeSQLException(e, sql, params);

        } finally {
            try {
                DbUtils.close(stmt);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return rows;
    }

    public static int update(String table, Map<String, Object> data, Map<String, Object> condition) {
        String sql = DataBase.getDriver().makeUpdate(table, data, condition);
        return DataBase.update(sql);
    }

    public static int update(String table, Map<String, Object> data, String condition) {
        String sql = DataBase.getDriver().makeUpdate(table, data, condition);
        return DataBase.update(sql);
    }

    public static int delete(String table, Map<String, Object> condition) {
        String sql = DataBase.getDriver().makeDelete(table, condition);
        return DataBase.update(sql);
    }

    public static int delete(String table, String condition) {
        String sql = DataBase.getDriver().makeDelete(table, condition);
        return DataBase.update(sql);
    }

    //查询不到返回null
    /**
     * 查询并取得结果。结果形式为Map<Object, Map<String, Object>>
     * @param sql
     * @return
     */
    public static Map<Object, Map<String, Object>> queryAll(String sql) {
        return DataBase.query(sql, new KeyedHandler<Object>(), (Object[]) null);
    }

    public static Map<Object, Map<String, Object>> queryAll(String sql, Object param) {
        return DataBase.query(sql, new KeyedHandler<Object>(), new Object[]{param});
    }

    public static Map<Object, Map<String, Object>> queryAll(String sql, Object... params) {
        return DataBase.query(sql, new KeyedHandler<Object>(), params);
    }

    /**
     * 查询并取得结果。结果形式为List<Map<String, Object>>
     * @param sql
     * @return
     */
    public static List<Map<String, Object>> queryMapList(String sql) {
        return DataBase.query(sql, new MapListHandler(), (Object[]) null);
    }

    public static List<Map<String, Object>> queryMapList(String sql, Object param) {
        return DataBase.query(sql, new MapListHandler(), new Object[]{param});
    }

    public static List<Map<String, Object>> queryMapList(String sql, Object... params) {
        return DataBase.query(sql, new MapListHandler(), params);
    }

    /**
     * 查询并取得某列数据。列序号
     * @param sql
     * @param columnIndex
     * @return
     */
    public static List<Object> queryColumn(String sql, int columnIndex) {
        return DataBase.query(sql, new ColumnListHandler<>(columnIndex), (Object[]) null);
    }

    public static List<Object> queryColumn(String sql, int columnIndex, Object param) {
        return DataBase.query(sql, new ColumnListHandler<>(columnIndex), new Object[]{param});
    }

    public static List<Object> queryColumn(String sql, int columnIndex, Object... params) {
        return DataBase.query(sql, new ColumnListHandler<>(columnIndex), params);
    }

    /**
     * 查询并取得某列数据
     * @param sql
     * @return
     */
    public static List<Object> queryColumn(String sql, String columnName) {
        return DataBase.query(sql, new ColumnListHandler<>(columnName), (Object[]) null);
    }

    public static List<Object> queryColumn(String sql, String columnName, Object param) {
        return DataBase.query(sql, new ColumnListHandler<>(columnName), new Object[]{param});
    }

    public static List<Object> queryColumn(String sql, String columnName, Object... params) {
        return DataBase.query(sql, new ColumnListHandler<>(columnName), params);
    }

    /**
     * 查询并取得首行数据
     * @param sql
     * @return
     */
    public static List<Object> queryFirstColumn(String sql) {
        return DataBase.query(sql, new ColumnListHandler<>(1), (Object[]) null);
    }

    public static List<Object> queryFirstColumn(String sql, Object param) {
        return DataBase.query(sql, new ColumnListHandler<>(1), new Object[]{param});
    }

    public static List<Object> queryFirstColumn(String sql, Object... params) {
        return DataBase.query(sql, new ColumnListHandler<>(1), params);
    }

    /**
     * 查询并取得首行数据
     * @param sql
     * @return
     */
    public static Map<String, Object> queryFirstRow(String sql) {
        return DataBase.query(sql, new MapHandler(), (Object[]) null);
    }

    public static Map<String, Object> queryFirstRow(String sql, Object param) {
        return DataBase.query(sql, new MapHandler(), new Object[]{param});
    }

    public static Map<String, Object> queryFirstRow(String sql, Object... params) {
        return DataBase.query(sql, new MapHandler(), params);
    }

    /**
     * 查询并返回首行首列值
     * @param sql
     * @return
     */
    public static Object queryScalar(String sql) {
        return DataBase.query(sql, new ScalarHandler<Object>(), (Object[]) null);
    }

    public static Object queryScalar(String sql, Object param) {
        return DataBase.query(sql, new ScalarHandler<Object>(), new Object[]{param});
    }

    public static Object queryScalar(String sql, Object... params) {
        return DataBase.query(sql, new ScalarHandler<Object>(), params);
    }

    /**
     * 查询并返回首行指定列的值
     * @param sql
     * @return
     */
    public static Object queryScalar(String sql, int columnIndex) {
        return DataBase.query(sql, new ScalarHandler<Object>(columnIndex), (Object[]) null);
    }

    public static Object queryScalar(String sql, int columnIndex, Object param) {
        return DataBase.query(sql, new ScalarHandler<Object>(columnIndex), new Object[]{param});
    }

    public static Object queryScalar(String sql, int columnIndex, Object... params) {
        return DataBase.query(sql, new ScalarHandler<Object>(columnIndex), params);
    }

    /**
     * 查询并返回首行指定列的值
     * @param sql
     * @return
     */
    public static Object queryScalar(String sql, String columnName) {
        return DataBase.query(sql, new ScalarHandler<Object>(columnName), (Object[]) null);
    }

    public static Object queryScalar(String sql, String columnName, Object param) {
        return DataBase.query(sql, new ScalarHandler<Object>(columnName), new Object[]{param});
    }

    public static Object queryScalar(String sql, String columnName, Object... params) {
        return DataBase.query(sql, new ScalarHandler<Object>(columnName), params);
    }

    private static <T> T query(String sql, ResultSetHandler<T> rsh, Object... params) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        T result = null;

        try {
            stmt = DataBase.getDriver().getConnection().prepareStatement(sql);
            DataBase.fillStatement(stmt, params);
            rs = stmt.executeQuery();
            result = rsh.handle(rs);

        } catch (SQLException e) {
            DataBase.recodeSQLException(e, sql, params);
        } finally {
            try {
                DbUtils.close(rs);
            } catch (SQLException e) {
                DataBase.recordCloseResultSetException(e);
            } finally {
                try {
                    DbUtils.close(stmt);
                } catch (SQLException e) {
                    DataBase.recordCloseStatementException(e);
                }
            }
        }

        return result;
    }

    /**
     * Fill the <code>PreparedStatement</code> replacement parameters with the
     * given objects.
     *
     * @param stmt
     *            PreparedStatement to fill
     * @param params
     *            Query replacement parameters; <code>null</code> is a valid
     *            value to pass in.
     * @throws SQLException
     *             if a database access error occurs
     */
    protected static void fillStatement(PreparedStatement stmt, Object... params) throws SQLException {
        boolean pmdKnownBroken = false;
        // check the parameter count, if we can
        ParameterMetaData pmd = null;
        if (!pmdKnownBroken) {
            pmd = stmt.getParameterMetaData();
            int stmtCount = pmd.getParameterCount();
            int paramsCount = params == null ? 0 : params.length;

            if (stmtCount != paramsCount) {
                throw new SQLException("Wrong number of parameters: expected " + stmtCount + ", was given " + paramsCount);
            }
        }

        // nothing to do here
        if (params == null) {
            return;
        }

        for (int i = 0; i < params.length; i++) {
            if (params[i] != null) {
                stmt.setObject(i + 1, params[i]);
            } else {
                // VARCHAR works with many drivers regardless
                // of the actual column type. Oddly, NULL and
                // OTHER don't work with Oracle's drivers.
                int sqlType = Types.VARCHAR;
                if (!pmdKnownBroken) {
                    try {
                        /*
                         * It's not possible for pmdKnownBroken to change from
                         * true to false, (once true, always true) so pmd cannot
                         * be null here.
                         */
                        sqlType = pmd.getParameterType(i + 1);
                    } catch (SQLException e) {
                        pmdKnownBroken = true;
                    }
                }
                stmt.setNull(i + 1, sqlType);
            }
        }
    }

    /**
     * 构造完整错误信息并记录
     *
     * @param cause
     *            The original exception that will be chained to the new
     *            exception when it's rethrown.
     *
     * @param sql
     *            The query that was executing when the exception happened.
     *
     * @param params
     *            The query replacement parameters; <code>null</code> is a valid
     *            value to pass in.
     *
     */
    public static void recodeSQLException(SQLException cause, String sql, Object... params) {
        String causeMessage = cause.getMessage();
        if (causeMessage == null) {
            causeMessage = "";
        }
        StringBuffer msg = new StringBuffer(causeMessage);

        msg.append(" Query: ");
        msg.append(sql);
        msg.append(" Parameters: ");

        if (params == null) {
            msg.append("[]");
        } else {
            msg.append(Arrays.deepToString(params));
        }

        //错误应该被完整的记录，而不是单纯的打印或者输出到日志。
        System.out.println(msg.toString() + cause.getSQLState() + cause.getErrorCode());
    }

    /**
     * 构造完整错误信息并记录
     *
     * @param cause
     *            The original exception that will be chained to the new
     *            exception when it's rethrown.
     *
     */
    public static void recordCloseResultSetException(SQLException cause) {
        String causeMessage = cause.getMessage();
        if (causeMessage == null) {
            causeMessage = "";
        }
        StringBuffer msg = new StringBuffer(causeMessage);

        msg.append("ResultSet没有被正确关闭");

        //错误应该被完整的记录，而不是单纯的打印或者输出到日志。
        System.out.println(msg.toString() + cause.getSQLState() + cause.getErrorCode());
    }

    /**
     * 构造完整错误信息并记录
     *
     * @param cause
     *            The original exception that will be chained to the new
     *            exception when it's rethrown.
     *
     */
    public static void recordCloseStatementException(SQLException cause) {
        String causeMessage = cause.getMessage();
        if (causeMessage == null) {
            causeMessage = "";
        }
        StringBuffer msg = new StringBuffer(causeMessage);

        msg.append("Statement没有被正确关闭");

        //错误应该被完整的记录，而不是单纯的打印或者输出到日志。
        System.out.println(msg.toString() + cause.getSQLState() + cause.getErrorCode());
    }

    /**
     * 构造完整错误信息并记录
     *
     * @param cause
     *            The original exception that will be chained to the new
     *            exception when it's rethrown.
     *
     */
    public static void recordCloseConnectionException(SQLException cause) {
        String causeMessage = cause.getMessage();
        if (causeMessage == null) {
            causeMessage = "";
        }
        StringBuffer msg = new StringBuffer(causeMessage);

        msg.append("Statement没有被正确关闭");

        //错误应该被完整的记录，而不是单纯的打印或者输出到日志。
        System.out.println(msg.toString() + cause.getSQLState() + cause.getErrorCode());
    }

    /**
     * SQL安全性检查
     * @param sql
     */
    public static void checkSQL(String sql) {
        SQLWatcher.checkSQL(sql);
    }

    public static String makePagination(String table, String condition, int m, int n) {
        return DataBase.getDriver().makePagination(table, condition, m, n);
    }

    public static String makeTruncate(String table) {
        return DataBase.getDriver().makeTruncate(table);
    }

    public static String makeCondition(String field, Object value, String glue) {
        return DataBase.getDriver().makeCondition(field, value, glue);
    }

    public static String makeCondition(String field, Object value) {
        return DataBase.getDriver().makeCondition(field, value, "=");
    }

    public static String makeOrder(String field, String direction) {
        return DataBase.getDriver().makeOrder(field, direction);
    }

    public static String makeOrder(String field) {
        return DataBase.getDriver().makeOrder(field, "ASC");
    }

    public static String makeLimit(int m, int n) {
        return DataBase.getDriver().makeLimit(m, n);
    }

}