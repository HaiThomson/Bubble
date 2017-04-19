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

import source.kernel.base.Base;

/**
 * @author Hai Thomson
 */
public class LogConfig extends Base {
	public String   LEVEL = "ALL";
	public String   FILE_PATH = "./";
	public String   FILE_NAME = "log";
	public String   FILE_NAME_WITH_DATE = "yyyy-MM-dd";
	public Boolean	APPEND = true;
	public String   DATE_PATTERN = "[yyyy-MM-dd HH:mm:ss.S z]";
	// 每隔STEP_TIME毫秒记录一次
	public Long     STEP_TIME = 5L * 1000L;
	// 每次记录STEP_LINE行
	public Integer  STEP_LINE = 5000;
}
