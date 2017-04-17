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
package source.kernel;

import source.kernel.base.*;
import source.kernel.config.GlobalConfig;
import source.kernel.env.ThreadContextDestroyer;
import source.kernel.log.Logger;
import source.kernel.memory.Memory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 * @author Hai Thomson
 */
public class Container {
	private static final ThreadLocal threadApp = new ThreadLocal();
	private static final SuperContainer tablesCache = new SuperContainer();
	private static final Memory memory = new Memory();

	private static void setApplication(Application application) {
		Container.threadApp.set(application);
	}

	private static Application getApplication() {
		return (Application) Container.threadApp.get();
	}

	public static void creatApp(HttpServletRequest request, HttpServletResponse response) {
		// 如果WEB容器内部跳转，清理资源
		if (request.getAttribute("javax.servlet.include.request_uri") != null) {
			// 如果使用框架内跳转，即跳转至其它主控制器的子模块.不会执行这段代码.但需要和其它主控制器作者沟通是否兼容

			// 应该在这里进行错误处理
			try {
				ThreadContextDestroyer.destroyResource();
			} catch (SQLException e) {
				ExceptionHandler.handling(e);
			}
			Container.setApplication(Application.instance(request, response));
			return;
		}

		if (Container.getApplication() != null) {
			Logger.warn("线程 " + Thread.currentThread().getName() + " 在上次运行时未能将app置空！");
		}
		Container.setApplication(Application.instance(request, response));
	}

	public static Application app() {
		return Container.getApplication();
	}

	public static void destoryApp() {
		Container.setApplication(null);
	}

	/**
	 * Container.table() 实现了IOC，并可应用缓存大幅提升性能。
	 * 但频繁的类型转换实在太恶心。
	 *
	 * 新的机制，把对象作为私有属性保存在类中。全静态的方法无需再实例化即可调用。
	 * 这样做还可以让IDE提示表名，还支持编译检查。
	 *
	 * @param name 封装数据库表操作的类名
	 * @return
	 */
	public static Base table(String name) {
		return Container.makeTableObject(name, false);
	}

	private static Base makeTableObject(String name, boolean extendable) {
		String classPath = GlobalConfig.SOURCE_PATH + ".table." + name;
		if (extendable) {
			// 如果Table对象启用AOP支持后，要考虑对象还原
			// container
		} else {
			try {
				Class<?> tableClass = Class.forName(classPath);
				return (Base) tableClass.newInstance();
			} catch (Exception e) {
				ExceptionHandler.handling(e);
			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public static Memory memory() {
		if (!memory.is_init) {
			memory.init(null);
			return Container.memory;
		}
		return Container.memory;
	}

}