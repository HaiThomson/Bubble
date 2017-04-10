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

import source.kernel.base.ExceptionHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Hai Thomson
 */
public class MD5Helper {

	private MD5Helper() {}

	/**
	 * 默认的字符串组合，用来将字节转换成 16 进制表示的字符,apache校验下载的文件的正确性用的就是默认的这个组合
	 */
	protected static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * 生成字符串的校验值
	 * @param s 字符串
	 * @return
	 */
	public static String md5(String s) {
		return md5(s.getBytes());
	}

	/**
	 * 生成文件的校验值
	 * 
	 * @param file 要校验的文件
	 * @return
	 * @throws IOException
	 */
	public static String md5(File file) throws IOException {
		MessageDigest messageDigest = MD5Helper.messageDigestMD5();
		InputStream fis;
	    fis = new FileInputStream(file);
	    byte[] buffer = new byte[1024];
	    int numRead = 0;
	    while ((numRead = fis.read(buffer)) > 0) {
			messageDigest.update(buffer, 0, numRead);
	    }
	    fis.close();
		return bufferToHex(messageDigest.digest());
	}

	private static MessageDigest messageDigestMD5() {
		try {
			return MessageDigest.getInstance("md5");
		} catch (NoSuchAlgorithmException e) {
			// 99.9999999%不会发生异常
			ExceptionHandler.handling(e);
		}
		return null;
	}

	private static synchronized String md5(byte[] bytes) {
		MessageDigest messageDigest = MD5Helper.messageDigestMD5();
		messageDigest.update(bytes);
		return bufferToHex(messageDigest.digest());
	}

	private static String bufferToHex(byte bytes[]) {
		return bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte bytes[], int m, int n) {
		StringBuffer stringbuffer = new StringBuffer(2 * n);
		int k = m + n;
		for (int l = m; l < k; l++) {
			appendHexPair(bytes[l], stringbuffer);
		}
		return stringbuffer.toString();
	}

	private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
		char c0 = hexDigits[(bt & 0xf0) >> 4];// 取字节中高 4 位的数字转换, >>> 为逻辑右移，将符号位一起右移,此处未发现两种符号有何不同 
		char c1 = hexDigits[bt & 0xf];// 取字节中低 4 位的数字转换 
		stringbuffer.append(c0);
		stringbuffer.append(c1);
	}
}
