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
package source.kernel.security.token;

import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;

/**
 * 生成32位字符串
 * MySQL的char类型不补空格
 * @author Hai Thomson
 */
public class GeneralToken {

	public static final String DEFAULT_MODE = "UUID";
	public static final String MODE_UUID = "UUID";
	public static final String MODE_RANDOW = "RANDOW";

	private static final Random RANDOM = new Random();

	public static String generateToken() {
		return GeneralToken.generateToken(GeneralToken.DEFAULT_MODE);
	}

	public static String generateToken(String mode) {
		if (mode == null || mode.equals("")) {
			return GeneralToken.generateToken();
		}

		switch (mode) {
			case "UUID" : return GeneralToken.generateUUID();
			case "RANDOW" : return GeneralToken.randow();
		}

		return GeneralToken.generateToken();
	}

	private static String generateUUID() {
		return UUID.randomUUID().toString().replace("-", "").toUpperCase();
		// return UUID.randomUUID().toString().toUpperCase();
	}

	private static String randow() {
		// 36 指进制，不指长度。此地可能不产生32位字符串
		return new BigInteger(165, RANDOM).toString(36).toUpperCase();
	}

	public static void main(String[] args) {
		System.out.println(GeneralToken.generateToken());
		System.out.println(GeneralToken.generateToken(GeneralToken.MODE_RANDOW));

		new Thread(new Runnable() {
			int i = 0;
			@Override
			public void run() {
				while (true) {
					i++;
					System.out.println("count " + i);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (GeneralToken.generateToken(GeneralToken.MODE_UUID).length() != 32) {
						System.out.println(GeneralToken.MODE_UUID + " 没有产生32位的字符串");
					}
					if (GeneralToken.generateToken(GeneralToken.MODE_RANDOW).length() != 32) {
						System.out.println(GeneralToken.MODE_RANDOW + " 没有产生32位的字符串");
					}
				}
			}
		}).start();
	}
}
