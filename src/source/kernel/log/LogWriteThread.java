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

import java.text.SimpleDateFormat;

/**
 * @author Hai Thomson
 */
public class LogWriteThread extends Thread {

	protected LogWriteThread() {}

	protected LogWriteThread(String name) {
		super(name);
	}

	protected static long STEP_TIME = 1L * 1000L;
	protected static int STEP_LINE = 100;

	protected static SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.S z]");

	public void run() {
		while (true) {
			try {
				long sleepTime = STEP_TIME;
				if (!Logger.LOG_QUEUE.isEmpty()) {
					sleepTime = Math.round(LogWriteThread.STEP_TIME * (1f / (Logger.LOG_QUEUE.size() / 100f)));
					if (sleepTime >  STEP_TIME ) {
						sleepTime = STEP_TIME;
					}
				} else {
					sleepTime = STEP_TIME;
				}

				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// Nothing to do
			}

			for (int i = 0; i < LogWriteThread.STEP_LINE; i++) {
				if (!Logger.LOG_QUEUE.isEmpty()) {
					LoggerWriter.writeLog(Logger.LOG_QUEUE.remove());
				} else {
					break;
				}
			}
		}
	}
}