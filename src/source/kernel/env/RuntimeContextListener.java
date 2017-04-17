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
package source.kernel.env;

import source.kernel.config.GlobalConfig;
import source.kernel.db.pool.ConnectionPooling;
import source.kernel.log.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.HashMap;


/**
 * @since 1.8
 * @author Hai Thomson
 */
@WebListener
public class RuntimeContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        this.getENVInfomation(servletContextEvent);
        this.checkRuntimeEnvironment(servletContextEvent);
        this.loadConfig(servletContextEvent);
        this.initResouce(servletContextEvent);
        Logger.init(GlobalConfig.LOG_CONFIG);
        this.checkResource(servletContextEvent);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        this.CloseResouce();
        Logger.destory();
    }

    private void getENVInfomation(ServletContextEvent servletContextEvent) {
        final HashMap<String, Object> ENV = new HashMap<String, Object>();
        ServletContext servletContext = servletContextEvent.getServletContext();
        String serverInfo = servletContext.getServerInfo();
        if (serverInfo == null && serverInfo.equals("")) {
            serverInfo = "WEB容器未提供自身信息";
        }
        ENV.put("server.info", serverInfo);
        ENV.put("context.path", servletContext.getContextPath());
        ENV.put("os.name", System.getProperty("os.name"));
        ENV.put("os.version", System.getProperty("os.version"));
        ENV.put("os.arch", System.getProperty("os.arch"));
        ENV.put("os.username", System.getProperty("user.name"));
        ENV.put("java.version", System.getProperty("java.version"));
        ENV.put("java.vendor", System.getProperty("java.vendor"));
        ENV.put("java.vm.name", System.getProperty("java.vm.name"));
        // JVM内时区不会随操作系统变化而变化.如更改系统时区，必须重启JVM
        ENV.put("zoneoffset",  new GregorianCalendar().get(java.util.Calendar.ZONE_OFFSET));

        servletContext.setAttribute("ENV", ENV);
    }

    private void checkRuntimeEnvironment(ServletContextEvent servletContextEvent) {
    }

    private void loadConfig(ServletContextEvent servletContextEvent) {
        try {
            GlobalConfig.init(servletContextEvent.getServletContext().getResource("/config/GlobalConfig.json").getPath());
        } catch (MalformedURLException e) {
           throw new RuntimeException("未能加载到全局配置文件! " + e.getMessage());
        }
    }

    private void initResouce(ServletContextEvent servletContextEvent) {
        this.initComboPooledDataSource(servletContextEvent);
    }

    private void initComboPooledDataSource(ServletContextEvent servletContextEvent) {
        try {
            ConnectionPooling.init(GlobalConfig.CONNECTIONPOOL_TYPE, servletContextEvent.getServletContext().getResource(GlobalConfig.CONNECTIONPOOL_CONFIG_PATH).getPath());
        } catch (MalformedURLException e) {
            throw new RuntimeException("未能加载到连接池配置文件! " + e.getMessage());
        }
    }

    private void checkResource(ServletContextEvent servletContextEvent) {
        this.checkComboPooledDataSource();
    }

    private void checkComboPooledDataSource() {
        try {
            Connection connection = ConnectionPooling.getConnection();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库连接池加载异常！");
        }
    }

    private void CloseResouce() {
        ConnectionPooling.destory();
    }
}
