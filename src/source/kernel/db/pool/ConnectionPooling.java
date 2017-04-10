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
package source.kernel.db.pool;

import source.kernel.base.ExceptionHandler;
import source.kernel.config.GlobalConfig;
import source.kernel.log.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * 修改：命名一团糟
 * @author Hai Thomson
 */
public class ConnectionPooling {
    private static ConnectionPoolingDriver CONNECTIONPOOLING = null;
    // private static ConnectionPoolingDriver mainConnectionPooling;
    // private static ConnectionPoolingDriver db1ConnectionPooling;
    // private static ConnectionPoolingDriver db2ConnectionPooling;
    // private static ConnectionPoolingDriver[] db2ConnectionPooling;
    // private static Map<ConnectionPoolingDriver> db2ConnectionPooling;

    //屏蔽新建对象操作
    private ConnectionPooling() {}

    /**
     * 初始化数据库链接池。
     * @param cpName
     */
    public static void init(String cpName, String path) {
        if (ConnectionPooling.CONNECTIONPOOLING == null) {
            try {
                if (cpName.equals("c3p0")) {
                    Class<?> cpClass = Class.forName(GlobalConfig.SOURCE_PATH + ".kernel.db.pool.C3P0ConnectionPooling");
                    ConnectionPooling.CONNECTIONPOOLING = (ConnectionPoolingDriver) cpClass.newInstance();
                    ConnectionPooling.CONNECTIONPOOLING.init(path);
                }
            } catch (Exception e) {
                ExceptionHandler.handling(e);
            }
        }
    }

    /**
     * 初始化数据库链接池。
     * @param cpName
     */
    public static void init(String cpName, String path, String dbName) {
        if (ConnectionPooling.CONNECTIONPOOLING == null) {
            try {
                if (cpName.equals("c3p0")) {
                    Class<?> cpClass = Class.forName(GlobalConfig.SOURCE_PATH + ".kernel.db.pool.C3P0ConnectionPooling");
                    ConnectionPooling.CONNECTIONPOOLING = (ConnectionPoolingDriver) cpClass.newInstance();
                    ConnectionPooling.CONNECTIONPOOLING.init(path, dbName);
                }
            } catch (Exception e) {
                ExceptionHandler.handling(e);
            }
        }
    }

    /**
     * 关闭数据库连接池。
     * 由于连接池有独立的线程，发出关闭动作后代码将继续向下执行。
     * WEB应用卸载的速度可能非常快，这将导致连接池不能正确关闭所有数据连接。这是一个风险。
     * 所以在这里暂停了1秒，等待连接池关闭所有连接后再向下执行。
     */
    public static void destory() {
        Logger.info("正在关闭线程池......");

        ConnectionPooling.CONNECTIONPOOLING.close();

        // 主线程等待线程池线程关闭所有连接
        // 运行时出了InterruptedException无法处理，输出大多没人看
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Logger.info("主线程在等待c3p0关闭时出现了些问题: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return ConnectionPooling.CONNECTIONPOOLING.getConnection();
    }
}
