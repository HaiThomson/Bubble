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

/**
 * @author Hai Thomson
 */
public class ArraysHelper {
	private ArraysHelper() {}

	public static boolean inArrays(String[] arr, String targetValue) {
		if (targetValue == null || arr == null) {
			return false;
		}

		for(String s: arr){
			if(s.equals(targetValue))
				return true;
		}
		return false;
	}

	public static boolean inArrays(String targetValue, String[] arr) {
		if (targetValue == null || arr == null) {
			return false;
		}

		for(String s: arr){
			if(s.equals(targetValue))
				return true;
		}
		return false;
	}

	public static int strpos(String targetValue, String[] arr) {
		if (targetValue == null || arr == null) {
			return -1;
		}

		for (int i = 0; i < arr.length; i++) {
			if (targetValue.contains(arr[i])) {
				return i;
			}
		}

		return -1;
	}

	public static int getSubscript(String[] arr, String targetValue) {
		if (arr == null || targetValue == null) {
			return -1;
		}

		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(targetValue)) {
				return i;
			}
		}

		return -1;
	}

	public static String[] split(String[] arr, int start, int stop) {

		if (arr == null || start < 0 || stop < 0 || stop < start) {
			return null;
		}

		if (start == stop) {
			return new String[]{arr[start]};
		}

		String[] n = new String[stop - start + 1];

		for (int i = 0; i < n.length; i++,start++) {
			n[i] = arr[start];
		}

		return n;
	}
}
