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

import source.kernel.config.GlobalConfig;
import source.kernel.DB;

import java.util.Date;

/**
 * @author Hai Thomson
 */
public class ExceptionHandler {

	public static final boolean EXCEPTION_SHOW_OFF = true;
	public static final boolean EXCEPTION_SHOW_ON = false;

	public static final boolean EXCEPTION_LOG_OFF = true;
	public static final boolean EXCEPTION_LOG_ON = false;

	public static final boolean EXCEPTION_HALT_OFF = true;
	public static final boolean EXCEPTION_HALT_ON = false;

	private ExceptionHandler() {}

	public static void handling(Exception e) {
		ExceptionHandler.handling(e, true, true, true);
	}

	public static void handling(Exception e, boolean show, boolean log, boolean halt) {
		if (show && GlobalConfig.DEBUG) {
			System.out.println(new Date().toString() + " ERROR [" + Thread.currentThread().getName() + "] " + e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}

		if (log) {
			System.out.println(new Date().toString() + " ERROR [" + Thread.currentThread().getName() + "] " + e.getClass().getName() + ": " + e.getMessage());
		}

		if (halt) {
			if (DB.getDriver() != null) {
				// 如果开启了事务，则回滚.并关闭事务手动提交
				// 收回资源
				DB.closeConnection();
			}

			throw new RuntimeException("发生 [" + e.getClass().getName() + ": " + e.getMessage() + "] 问题");
		}

	}
}
