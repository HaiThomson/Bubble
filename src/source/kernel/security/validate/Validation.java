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
package source.kernel.security.validate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @since 1.8
 * @author Hai Thomson
 */
public interface Validation {

	public boolean validate();

	default public boolean isEmail(String address) {
		if (address.length() > 6 && address.length() <= 32) {
			Pattern pattern = Pattern.compile("/^([A-Za-z0-9\\-_.+]+)@([A-Za-z0-9\\-]+[.][A-Za-z0-9\\-.]+)$/");
			Matcher matcher = pattern.matcher(address);
			if (matcher.find()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 整数
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("^(\\-?)\\d+$");
		Matcher isNum = pattern.matcher(str);
		if ( !isNum.matches() ) {
			return false;
		}
		return true;
	}

	/**
	 * 非负数
	 * @param str
	 * @return
	 */
	public static boolean isNonnegativeNumber(String str) {
		Pattern pattern = Pattern.compile("^\\d+$");
		Matcher isNum = pattern.matcher(str);
		if ( !isNum.matches() ) {
			return false;
		}
		return true;
	}

	/**
	 * 负数
	 * @param str
	 * @return
	 */
	public static boolean isNegativeNumber(String str) {
		Pattern pattern = Pattern.compile("^-[1-9]\\d*$");
		Matcher isNum = pattern.matcher(str);
		if ( !isNum.matches() ) {
			return false;
		}
		return true;
	}

	/**
	 * 正数
	 * @param str
	 * @return
	 */
	public static boolean isPositiveNumber(String str) {
		Pattern pattern = Pattern.compile("^[1-9]\\d*$");
		Matcher isNum = pattern.matcher(str);
		if ( !isNum.matches() ) {
			return false;
		}
		return true;
	}

	public static boolean isBoolean(String str) {
		Pattern pattern = Pattern.compile("^(true)|(false)$");
		Matcher isNum = pattern.matcher(str.toLowerCase());
		if ( !isNum.matches() ) {
			return false;
		}
		return true;
	}

	public static boolean isByte(String str) {
		if (Validation.isNumeric(str)) {
			try {
				Byte.valueOf(str);
				return true;
			} catch (NumberFormatException e) {
				// ExceptionHandler.handling(e, ExceptionHandler.EXCEPTION_SHOW_ON, ExceptionHandler.EXCEPTION_LOG_ON, ExceptionHandler.EXCEPTION_HALT_ON);
				return false;
			}
		}
		return false;
	}

	public static boolean isShort(String str) {
		if (Validation.isNumeric(str)) {
			try {
				Short.valueOf(str);
				return true;
			} catch (NumberFormatException e) {
				// ExceptionHandler.handling(e, ExceptionHandler.EXCEPTION_SHOW_ON, ExceptionHandler.EXCEPTION_LOG_ON, ExceptionHandler.EXCEPTION_HALT_ON);
				return false;
			}
		}
		return false;
	}

	public static boolean isInteger(String str) {
		if (Validation.isNumeric(str)) {
			try {
				Integer.valueOf(str);
				return true;
			} catch (NumberFormatException e) {
				// ExceptionHandler.handling(e, ExceptionHandler.EXCEPTION_SHOW_ON, ExceptionHandler.EXCEPTION_LOG_ON, ExceptionHandler.EXCEPTION_HALT_ON);
				return false;
			}
		}
		return false;
	}

	public static boolean isLong(String str) {
		if (Validation.isNumeric(str)) {
			try {
				Long.valueOf(str);
				return true;
			} catch (NumberFormatException e) {
				// ExceptionHandler.handling(e, ExceptionHandler.EXCEPTION_SHOW_ON, ExceptionHandler.EXCEPTION_LOG_ON, ExceptionHandler.EXCEPTION_HALT_ON);
				return false;
			}
		}
		return false;
	}

	public static boolean isFloat(String str) {
		Pattern pattern = Pattern.compile("^-?([1-9]/d*/./d*|0/./d*[1-9]/d*|0?/.0+|0)$");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			try {
				Float.valueOf(str);
				return true;
			} catch (NumberFormatException e) {
				// ExceptionHandler.handling(e, ExceptionHandler.EXCEPTION_SHOW_ON, ExceptionHandler.EXCEPTION_LOG_ON, ExceptionHandler.EXCEPTION_HALT_ON);
				return false;
			}
		}
		return false;
	}

	public static boolean isDouble(String str) {
		Pattern pattern = Pattern.compile("^-?([1-9]/d*/./d*|0/./d*[1-9]/d*|0?/.0+|0)$");
		Matcher isNum = pattern.matcher(str);
		if (isNum.matches()) {
			try {
				Double.valueOf(str);
				return true;
			} catch (NumberFormatException e) {
				// ExceptionHandler.handling(e, ExceptionHandler.EXCEPTION_SHOW_ON, ExceptionHandler.EXCEPTION_LOG_ON, ExceptionHandler.EXCEPTION_HALT_ON);
				return false;
			}
		}
		return false;
	}

	public static boolean isChar(String str) {
		if (str.length() == 1) {
			return true;
		} else {
			return false;
		}
	}
}
