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
import source.kernel.memory.SimpleMapMemoryDriver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hai Thomson
 */
public class Memory {
	public boolean is_init = false;
	public boolean is_enable = false;
	public String type = "";
	public HashMap<String, Boolean> extension = new HashMap<String, Boolean>();
	public HashMap config = null;

	private MemoryDriver memoryDriver = null;

	public Memory() {
		try {
			if (!(Class.forName("redis").getName().equals(""))) {
				extension.put("redis", true);
			}
		} catch (ClassNotFoundException e) {
			//尝试加载，如未加载到无需处理
		}

		try {
			if (!(Class.forName("memcache").getName().equals(""))) {
				extension.put("memcache", true);
			}
		} catch (ClassNotFoundException e) {
			//尝试加载，如未加载到无需处理
		}

		try {
			if (!(Class.forName(GlobalConfig.SOURCE_PATH + ".kernel.memory.SimpleMapMemoryDriver").getName().equals(""))) {
				extension.put("SimpleMap", true);
			}
		} catch (ClassNotFoundException e) {
			//尝试加载，如未加载到无需处理
		}
	}

	public void init(HashMap config) {
		this.config = config;

		if (extension.get("SimpleMap")) {
			memoryDriver = new SimpleMapMemoryDriver();
		}

		/*if (extension.get("Guava Cache")) {
			memoryDriver = new SimpleMapMemoryDriver();
		}

		if (extension.get("Guava")) {
			memoryDriver = new SimpleMapMemoryDriver();
		}*/

		if (memoryDriver != null) {
			this.is_enable = true;
			this.type = memoryDriver.getClass().getTypeName();
		}
	}

	public Object get(String key) {
		return memoryDriver.get(key);
	}

	public Map getMultiple(String[] keys) {
		return memoryDriver.getMultiple(keys);
	}

	public boolean set(String key, Object value, long ttl) {
		return memoryDriver.set(key, value, ttl);
	}

	public boolean remove(String key) {
		return memoryDriver.remove(key);
	}

	public boolean clear() {
		return memoryDriver.clear();
	}

	public boolean increment(String key) {
		return memoryDriver.increment(key, 1);
	}

	public boolean decrement(String key) {
		return memoryDriver.decrement(key, 1);
	}

	public boolean increment(String key, int step) {
		return memoryDriver.increment(key, step);
	}

	public boolean decrement(String key, int step) {
		return memoryDriver.decrement(key, step);
	}
}
