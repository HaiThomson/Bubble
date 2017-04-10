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
package source.kernel.serialization.oracle;

import java.io.*;

/**
 * @author Hai Thomson
 */
public class JavaSerialization {

	private JavaSerialization() {}

	public static byte[] serializableToBytes(Object object) throws IOException {
		if (object == null) {
			return null;
		}

		byte[] bytes = null;

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);

		outputStream.writeObject(object);
		outputStream.close();

		bytes = byteArrayOutputStream.toByteArray();

		return bytes;
	}


	public static Object serializationFromBytes(byte[] data) throws IOException, ClassNotFoundException {
		if (data == null) {
			return null;
		}

		Object object = null;

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
		ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);

		object = inputStream.readObject();
		inputStream.close();

		return object;
	}
}
