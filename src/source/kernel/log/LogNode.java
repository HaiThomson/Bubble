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

import java.util.Date;

/**
 * 使用数据结构和Map<String, Map>在性能上没有太多区别
 * 对于基本固定的数据接口抽象成数据结构，只要不反射性能就还好。
 * @author Hai Thomson
 */
public class LogNode {
	protected LogNode() {}

	protected String level = null;
	protected long dateline = System.currentTimeMillis();
	protected String msg = null;
	protected String threadName = Thread.currentThread().getName();

	public String toString() {
		return new Date(this.dateline).toString() + "\t" + this.level + "\t" + this.msg;
	}
}