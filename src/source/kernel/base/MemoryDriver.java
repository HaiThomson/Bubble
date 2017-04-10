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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hai Thomson
 */
public abstract class MemoryDriver {
	public abstract void init(HashMap config);

	public abstract Object get(String key);

	public abstract Map getMultiple(String[] keys);

	public abstract boolean set(String key, Object value, long ttl);

	public abstract boolean remove(String key);

	public abstract boolean clear();

	public abstract boolean increment(String key, int step);

	public abstract boolean decrement(String key, int step);
}
