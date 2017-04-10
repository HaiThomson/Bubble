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

import source.kernel.Core;
import source.kernel.Container;
import source.kernel.DB;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.annotation.WebListener;

/**
 * @author Hai Thomson
 */
@WebListener
public class ThreadContextListener implements ServletRequestListener {

    /**
     * 用户访问静态资源也会创建ServletRequest
     * 运行期间发生异常被ExceptionHandler处理，抛出java.lang.RuntimeException后仍会销毁request并执行requestDestroyed方法
     * @param servletRequestEvent
     */
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        // System.out.println("Listener" + "\t" + Thread.currentThread().getName() + "\t" + ((HttpServletRequest) servletRequestEvent.getServletRequest()).getRequestURL());
        Core.persistenceSession();
        ThreadContextListener.destroyResource();
    }

    public void requestInitialized(ServletRequestEvent servletRequestEvent) {

    }

    public static void destroyResource() {
        ThreadContextListener.closeDataBaseConnnection();
        ThreadContextListener.destoryDriver();
        ThreadContextListener.destoryApplication();
    }

    private static void closeDataBaseConnnection() {
        if (DB.getDriver() != null) {
            // 如果开启事务并未提交，则提交,并关闭事务手动提交
            // 收回资源
            DB.closeConnection();
        }
    }

    private static void destoryDriver() {
        DB.destoryDriver();
    }

    private static void destoryApplication() {
        Container.destoryApp();
    }
}
