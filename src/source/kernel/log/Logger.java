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
package source.kernel.log;

import source.kernel.helper.ArraysHelper;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Hai Thomson
 */
public class Logger {

	public static boolean initated = false;

	public static String level = "ALL";

	protected static final String[] OPTIONAL_LEVEL = {"ALL", "TRACE", "DEBUG", "INFO", "WARN", "ERROR", "FATAL", "OFF"};

	protected static final ConcurrentLinkedQueue<LogNode> LOG_QUEUE = new ConcurrentLinkedQueue<LogNode>();

	protected static LogWriteThread logWriteThread = new LogWriteThread("LogWriteThread");

	public static void init(LogConfig config) {
		if (!Logger.initated) {
			Logger.level = config.LEVEL;
			LogWriteThread.STEP_TIME = config.STEP_TIME;
			LogWriteThread.STEP_TIME = config.STEP_LINE;

			logWriteThread.start();

			LogNode end = new LogNode();
			end.level = "INFO";
			end.msg = "日志组件开始工作";
			LoggerWriter.writeLog(end);

			Logger.initated = true;
		}
	}

	public static void destory() {
		logWriteThread.interrupt();

		if (!Logger.LOG_QUEUE.isEmpty()) {
			for (int i = 0; i < LOG_QUEUE.size(); i++) {
				if (!Logger.LOG_QUEUE.isEmpty()) {
					LoggerWriter.writeLog(Logger.LOG_QUEUE.remove());
				} else {
					break;
				}
			}
		}

		LogNode end = new LogNode();
		end.level = "INFO";
		end.msg = "已写入所有日志";
		LoggerWriter.writeLog(end);
	}

	/**
	 * 记录跟踪日志
	 */
	public static void trace(String msg) {
		LogNode log = new LogNode();
		log.level = "TRACE";
		log.msg = msg;
		Logger.add(log);
	}

	/**
	 * 记录调试日志
	 */
	public static void debug(String msg) {
		LogNode log = new LogNode();
		log.level = "DEBUG";
		log.msg = msg;
		Logger.add(log);
	}

	/**
	 * 记录状态日志
	 */
	public static void info(String msg) {
		LogNode log = new LogNode();
		log.level = "INFO";
		log.msg = msg;
		Logger.add(log);
	}

	/**
	 * 记录警告日志
	 */
	public static void warn(String msg) {
		LogNode log = new LogNode();
		log.level = "WARN";
		log.msg = msg;
		Logger.add(log);
	}

	/**
	 * 记录错误日志
	 */
	public static void error(String msg) {
		LogNode log = new LogNode();
		log.level = "ERROR";
		log.msg = msg;
		Logger.add(log);
	}

	/**
	 * 记录灾难日志
	 */
	public static void fatal(String msg) {
		LogNode log = new LogNode();
		log.level = "FATAL";
		log.msg = msg;
		Logger.add(log);
	}

	protected static void add(LogNode log) {
		if (ArraysHelper.getSubscript(Logger.OPTIONAL_LEVEL, log.level) >= ArraysHelper.getSubscript(Logger.OPTIONAL_LEVEL, Logger.level)) {
			LOG_QUEUE.add(log);
		}
	}
}



