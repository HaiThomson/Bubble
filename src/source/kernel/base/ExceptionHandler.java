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
import source.kernel.log.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
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

	protected ExceptionHandler() {}

	public static void handling(Exception e) {
		ExceptionHandler.handling(e, true, true, true);
	}

	public static void handling(Exception e, boolean show, boolean log, boolean halt) {
		if (show && GlobalConfig.DEBUG) {
			System.err.println(new Date().toString() + " ERROR [" + Thread.currentThread().getName() + "] " + e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}

		if (log) {
			Logger.error(e.getClass().getName() + ": " + e.getMessage());
		}

		if (halt) {
			if (DB.getDriver() != null) {
				try {
					if (!DB.isAutoCommit()) {
						DB.rollBackTransaction();
						DB.closeTransaction();
					}
					DB.closeConnection();
				} catch (SQLException sqlException) {
					throw new RuntimeException("ExceptionHandler在执行数据库操作时发生 [" + sqlException.getClass().getName() + ": " + sqlException.getMessage() + "] 问题");
				}
			}

			throw new RuntimeException("发生 [" + e.getClass().getName() + ": " + e.getMessage() + "] 问题");
		}

	}
}
