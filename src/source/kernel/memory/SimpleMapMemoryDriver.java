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
package source.kernel.memory;

import source.kernel.Container;
import source.kernel.base.*;
import source.kernel.base.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Hai Thomson
 */
public class SimpleMapMemoryDriver extends MemoryDriver {

	public static boolean initated = false;

	protected static final ConcurrentHashMap<String, CacheNode> CACHE = new ConcurrentHashMap<String, CacheNode>();
	protected static long NODE_MAX_TIME = 60L * 60L * 24L * 1000L;

	@Override
	public void init(HashMap config) {
		if (!SimpleMapMemoryDriver.initated) {
			NODE_MAX_TIME = NODE_MAX_TIME;
			CacheGC cacheGC = new CacheGC("CacheGC");
			cacheGC.start();
			SimpleMapMemoryDriver.initated = true;
		}
	}

	@Override
	public Object get(String key) {
		CacheNode cacheNode = SimpleMapMemoryDriver.CACHE.get(key);
		if (cacheNode == null || cacheNode.getExpiration() < System.currentTimeMillis()) {
			return null;
		} else {
			return cacheNode.getValue();
		}
	}

	@Override
	public Map getMultiple(String[] keys) {
		if (keys == null && keys.length == 0 ) {
			return null;
		}

		Map tmp = new HashMap<String, Object>();
		for (String key : keys) {
			CacheNode cacheNode = SimpleMapMemoryDriver.CACHE.get(key);
			if (cacheNode == null || cacheNode.getExpiration() < System.currentTimeMillis() + SimpleMapMemoryDriver.NODE_MAX_TIME) {
				tmp.put(key, null);
			} else {
				tmp.put(key, cacheNode.getValue());
			}
		}
		return tmp;
	}

	@Override
	public boolean set(String key, Object value, long ttl) {
		if (ttl < SimpleMapMemoryDriver.NODE_MAX_TIME) {
			SimpleMapMemoryDriver.CACHE.put(key, new CacheNode(key, value, System.currentTimeMillis() + ttl));
			return true;
		} else {
			SimpleMapMemoryDriver.CACHE.put(key, new CacheNode(key, value, System.currentTimeMillis() + SimpleMapMemoryDriver.NODE_MAX_TIME));
			return true;
		}
	}

	@Override
	public boolean remove(String key) {
		SimpleMapMemoryDriver.CACHE.remove(key);
		return true;
	}

	@Override
	public boolean clear() {
		SimpleMapMemoryDriver.CACHE.clear();
		return true;
	}

	@Override
	public boolean increment(String key, int step) {
		CacheNode cacheNode = SimpleMapMemoryDriver.CACHE.get(key);
		if (cacheNode == null ) {
			return false;
		}

		Object value = cacheNode.getValue();
		if (value == null || !(value instanceof Integer) || !(value instanceof Long)) {
			return false;
		} else if (value instanceof Integer) {
			int newValue = (int)value + step;
			cacheNode.setValue(newValue);
			SimpleMapMemoryDriver.CACHE.put(key, cacheNode);
			return true;
		} else if (value instanceof Long) {
			long newValue = (long)value + step;
			cacheNode.setValue(newValue);
			SimpleMapMemoryDriver.CACHE.put(key, cacheNode);
			return true;
		}
		return false;
	}

	@Override
	public boolean decrement(String key, int step) {
		CacheNode cacheNode = SimpleMapMemoryDriver.CACHE.get(key);
		if (cacheNode == null ) {
			return false;
		}

		Object value = cacheNode.getValue();
		if (value == null || !(value instanceof Integer) || !(value instanceof Long)) {
			return false;
		} else if (value instanceof Integer) {
			int newValue = (int)value - step;
			cacheNode.setValue(newValue);
			SimpleMapMemoryDriver.CACHE.put(key, cacheNode);
			return true;
		} else if (value instanceof Long) {
			long newValue = (long)value - step;
			cacheNode.setValue(newValue);
			SimpleMapMemoryDriver.CACHE.put(key, cacheNode);
			return true;
		}
		return false;
	}

}

class CacheNode {
	private String key; // 缓存ID
	private Object value; // 缓存数据
	private long expiration; // 过期时刻

	public CacheNode(String key, Object value, long life) {
		this.key = key;
		this.value = value;
		this.expiration = life;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

	public long getExpiration() {
		return expiration;
	}
}

class CacheGC extends Thread {

	protected CacheGC() {}

	protected CacheGC(String name) {
		super(name);
	}

	private static long STEP = 60L * 60L * 1000L;

	public void run() {
		while (true) {
			// Runtime.getRuntime().freeMemory() < 5L * 1024L * 1024L)
			try {
				long sleepTime = CacheGC.STEP * (Runtime.getRuntime().freeMemory() / Runtime.getRuntime().maxMemory());
				if (sleepTime <  5l * 60L * 1000L) {
					sleepTime = 5l * 60L * 1000L;
				}
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				Container.memory().is_enable = false;
				ExceptionHandler.handling(e);
			}

			ConcurrentHashMap.KeySetView<String, CacheNode> keySet = SimpleMapMemoryDriver.CACHE.keySet();
			Object[] keys = keySet.toArray();
			for (Object key : keys) {
				if (SimpleMapMemoryDriver.CACHE.get(key).getExpiration() < System.currentTimeMillis()) {
					SimpleMapMemoryDriver.CACHE.remove(key);
				}
			}
		}
	}
}