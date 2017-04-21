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


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @author Hai Thomson
 */
public class LogWriteThread extends Thread {

	protected LogWriteThread() {}

	protected LogWriteThread(String name) {
		super(name);
	}

	protected static long STEP_TIME = 5L * 1000L;
	protected static int STEP_LINE = 5000;

	public void run() {
		while (true) {
			try {
				long sleepTime = LogWriteThread.STEP_TIME;
				if (!Logger.LOG_QUEUE.isEmpty()) {
					sleepTime = Math.round(LogWriteThread.STEP_TIME * (1f / (Logger.LOG_QUEUE.size() / LogWriteThread.STEP_LINE)));

					if (sleepTime >  LogWriteThread.STEP_TIME ) {
						sleepTime = LogWriteThread.STEP_TIME;
					}
				}

				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// Nothing to do
			}

			try {
				FileOutputStream fileOutputStream = new FileOutputStream(LoggerWriter.getLogFilePath(), true);
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Logger.LOGCONFIG.DATE_PATTERN);
				for (int i = 0; i < LogWriteThread.STEP_LINE; i++) {
					if (!Logger.LOG_QUEUE.isEmpty()) {
						LoggerWriter.writeLog(Logger.LOG_QUEUE.remove(), simpleDateFormat, fileOutputStream);
					} else {
						break;
					}
				}
				fileOutputStream.close();
			} catch (FileNotFoundException e) {
				// 写到Tomcat里面
				e.printStackTrace();
			} catch (IOException e) {
				// 写到Tomcat里面
				e.printStackTrace();
			}

		}
	}
}