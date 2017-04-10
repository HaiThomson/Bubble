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
package source.kernel.helper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Hai Thomson
 */
public class MapHelper {

	private MapHelper() {}

	public static String mapToString(Map map) {
		return mapToString(map, 1);
	}

	// 需更改循环部分,冗余的代码
	// 静态方法调用本类方法不再加类名
	private static String mapToString(Map map, int n) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Map\n" + nt(n-1) + "(" + "\n");

		if (map == null || map.size() == 0) {
			buffer.append(nt(n-1) + ")");
			return buffer.toString();
		}

		int i = 1;
		Set keySet = map.keySet();
		int keySize = keySet.size();

		for(Object key : keySet) {
			if ( i < keySize ) {
				String value = "";
				if (map.get(key) instanceof Map) {
					value = mapToString((Map) map.get(key), n + 2);
				} else {
					if (map.get(key) == null) {
						value = "null";
					} else {
						value = map.get(key).toString();
					}
				}
				buffer.append( nt(n) + "[" + key + "]" + " => " + value + "\n");
			}  else if (i == keySize) {
				String value = "";
				if (map.get(key) instanceof Map) {
					value = mapToString((Map) map.get(key), n + 2);
				} else {
					if (map.get(key) == null) {
						value = "null";
					} else {
						value = map.get(key).toString();
					}
				}
				buffer.append( nt(n) + "[" + key + "]" + " => " + value + "\n");
			}

			i++;
		}

		buffer.append(nt(n-1) + ")");
		return buffer.toString();
	}

	private static String nt(int n) {
		StringBuffer table = new StringBuffer();
		for (int i = 0; i < n; i++) {
			table.append("\t");
		}
		return table.toString();
	}
	
	public static byte[] serializableToBytes(Map map) {
		if (map == null) {
			return null;
		}

		byte[] bytes = null;
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);

			outputStream.writeObject(map);
			outputStream.close();

			bytes = byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bytes;
	}
	
	
	public static Map serializationFromBytes(byte[] data) {
		if (data == null) {
			return null;
		}

		Map result = null;
		try {
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
			ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);

			result = (Map) inputStream.readObject();
			inputStream.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static void main(String[] args) {
		HashMap<String, Object> map = new HashMap<String, Object>() {{
			put("name", "pingbao");
			put("age", 27);
			put("shape", new HashMap<String, Object>() {{
				put("height", "174CM");
				put("weight", "68KG");
			}});
		}};

		System.out.println(MapHelper.mapToString(map));
	}
	
}
