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

import java.util.Map;

/**
 * 该全名 MemoryDriver 实现
 * @author Hai Thomson
 */
public class MemoryCache {

	public static Object get(String key) {
		return Container.memory().get(key);
	}

	public static Map getMultiple(String[] keys) {
		return Container.memory().getMultiple(keys);
	}

	public static boolean set(String key, Object value, long ttl) {
		return Container.memory().set(key, value, ttl);
	}

	public static boolean remove(String key) {
		return Container.memory().remove(key);
	}

	public static boolean clear() {
		return Container.memory().clear();
	}

	public static boolean increment(String key) {
		return Container.memory().increment(key, 1);
	}

	public static boolean decrement(String key) {
		return Container.memory().decrement(key, 1);
	}

	public static boolean increment(String key, int step) {
		return Container.memory().increment(key, step);
	}

	public static boolean decrement(String key, int step) {
		return Container.memory().decrement(key, step);
	}

}
