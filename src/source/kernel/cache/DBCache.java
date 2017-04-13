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
package source.kernel.cache;

import source.kernel.Container;
import source.table.common_cache;

import java.util.Map;

/**
 * 代码本身不会发生线程安全问题
 * 高并发可能发生数据一致性问题，未来将增加事务控制解决。
 * @author Hai Thomson
 */
public class DBCache {
	public static boolean saveCache(String cachename, byte[] data, int dateline) {
		int number = ((common_cache) Container.table("common_cache")).insert(cachename, data, dateline);
		if (number > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static byte[] loadCache(String cachename) {
		Map result = ((common_cache) Container.table("common_cache")).query(cachename);
		if (result != null && (long) result.get("dateline") > System.currentTimeMillis()) {
			return (byte[]) result.get("data");
		}
		return null;
	}

	public static boolean deleteCache(String cachename) {
		int number = ((common_cache) Container.table("common_cache")).delete(cachename);
		if (number > 0) {
			return true;
		} else {
			return false;
		}
	}
}
