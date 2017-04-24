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

import source.kernel.config.GlobalConfig;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
  
public class Authcode3 {

    private Authcode3() {}
  
    public enum DiscuzAuthcodeMode {
        Encode, Decode
    }

    /**
     * 从字符串的指定位置截取指定长度的子字符串
     * @param str 原字符串
     * @param startIndex 子字符串的起始位置
     * @param length 子字符串的长度
     * @return 子字符串
     */
    private static String cutString(String str, int startIndex, int length) {
        if (startIndex >= 0) {
            if (length < 0) {
                length = length * -1;
                if (startIndex - length < 0) {
                    length = startIndex;
                    startIndex = 0;
                } else {
                    startIndex = startIndex - length;
                }
            }
  
            if (startIndex > str.length()) {
                return "";
            }
  
        } else {
            if (length < 0) {
                return "";
            } else {
                if (length + startIndex > 0) {
                    length = length + startIndex;
                    startIndex = 0;
                } else {
                    return "";
                }
            }
        }
  
        if (str.length() - startIndex < length) {
            length = str.length() - startIndex;
        }
  
        return str.substring(startIndex, startIndex + length);
    }

    /**
     * 从字符串的指定位置开始截取到字符串结尾的了符串
     *
     * @param str 原字符串
     * @param startIndex 子字符串的起始位置
     * @return 子字符串
     */
    private static String cutString(String str, int startIndex) {
        return cutString(str, startIndex, str.length());
    }   

    /**
     * 用于 RC4 处理密码
     * @param pass 密码字串
     * @param kLen 密钥长度, 一般取 256
     * @return
     */
    private static byte[] getKey(byte[] pass, int kLen) {
        byte[] mBox = new byte[kLen];
  
        for (int i = 0; i < kLen; i++) {
            mBox[i] = (byte) i;
        }   
  
        int j = 0;   
        for (int i = 0; i < kLen; i++) {
            j = (j + (int) ((mBox[i] + 256) % 256) + pass[i % pass.length]) % kLen;
  
            byte temp = mBox[i];
            mBox[i] = mBox[j];
            mBox[j] = temp;
        }
  
        return mBox;
    }   

    /**
     * 生成随机字符
     * @param lens 随机字符长度
     * @return
     */
    private static String randomString(int lens) {
        char[] CharArray = {
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k',
                'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',   
                'x', 'y', 'z',
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
        };

        int clens = CharArray.length;
        String sCode = "";   
        Random random = new Random();
        for (int i = 0; i < lens; i++) {
            sCode += CharArray[Math.abs(random.nextInt(clens))];
        }
        return sCode;
    }

    /**
     * 使用 Discuz authcode 方法对字符串加密
     * @param source 原始字符串
     * @param key 密钥
     * @param expiry 加密字串有效时间，单位是秒
     * @return 加密结果
     */
    public static String authcodeEncode(String source, String key, int expiry) {
        return authcode(source, key, DiscuzAuthcodeMode.Encode, expiry);
    }

    /**
     * 使用 Discuz authcode 方法对字符串加密
     * @param source 原始字符串
     * @param key 密钥
     * @return 加密结果
     */
    public static String authcodeEncode(String source, String key) {
        return authcode(source, key, DiscuzAuthcodeMode.Encode, 0);
    }

    /**
     * 使用 Discuz authcode 方法对字符串解密
     * @param source 原始字符串
     * @param key 密钥
     * @return 解密结果
     */
    public static String authcodeDecode(String source, String key) {
        return authcode(source, key, DiscuzAuthcodeMode.Decode, 0);
    }

    /**
     * 使用 变形的 rc4 编码方法对字符串进行加密或者解密
     * @param source 原始字符串
     * @param key 密钥
     * @param operation 操作 加密还是解密
     * @param expiry 加密字串过期时间
     * @return 加密或者解密后的字符串
     */
    private static String authcode(String source, String key, DiscuzAuthcodeMode operation, int expiry) {
        try {   
            if (source == null || key == null) {
                return "";
            }

            int ckey_length = 0;
            String keya, keyb, keyc, cryptkey, result;

            key = MD52(key);
            keya = MD52(cutString(key, 0, 16));
            keyb = MD52(cutString(key, 16, 16));
            keyc = ckey_length > 0 ? (operation == DiscuzAuthcodeMode.Decode ? cutString(source, 0, ckey_length) : randomString(ckey_length)) : "";

            cryptkey = keya + MD52(keya + keyc);
  
            if (operation == DiscuzAuthcodeMode.Decode) {
                byte[] temp;
                   
                temp = Base64.decode(cutString(source, ckey_length));
                result = new String(RC4(temp, cryptkey));   
                if (cutString(result, 10, 16).equals(cutString(MD52(cutString(result, 26) + keyb), 0, 16))) {
                    return cutString(result, 26);
                } else {   
                    temp = Base64.decode(cutString(source + "=", ckey_length));
                    result = new String(RC4(temp, cryptkey));   
                    if (cutString(result, 10, 16).equals(cutString(MD52(cutString(result, 26) + keyb), 0, 16))) {
                        return cutString(result, 26);
                    } else {   
                        temp = Base64.decode(cutString(source + "==", ckey_length));
                        result = new String(RC4(temp, cryptkey));   
                        if (cutString(result, 10, 16).equals(cutString(MD52(cutString(result, 26) + keyb), 0, 16))) {
                            return cutString(result, 26);
                        }else{
                            return "2";
                        }
                    }
                }
            } else {
                source = "0000000000" + cutString(MD52(source + keyb), 0, 16) + source;
                // 编码格式,这个大坑
                byte[] temp = RC4(source.getBytes(GlobalConfig.JAVA_ENCODING), cryptkey);
                return keyc + Base64.encodeBytes(temp);
            }
        } catch (Exception e) {
            return "";
        }   
  
    }

    /**
     * RC4 原始算法
     * @param input 原始字串数组
     * @param pass 密钥
     * @return 处理后的字串数组
     */
    private static byte[] RC4(byte[] input, String pass) {
        if (input == null || pass == null) {
            return null;
        }

        byte[] output = new byte[input.length];
        byte[] mBox = getKey(pass.getBytes(), 256);
  
        // 加密
        int i = 0;
        int j = 0;
  
        for (int offset = 0; offset < input.length; offset++) {
            i = (i + 1) % mBox.length;   
            j = (j + (int) ((mBox[i] + 256) % 256)) % mBox.length;

            byte temp = mBox[i];
            mBox[i] = mBox[j];
            mBox[j] = temp;
            byte a = input[offset];
  
            // byte b = mBox[(mBox[i] + mBox[j] % mBox.Length) % mBox.Length];
            // mBox[j] 一定比 mBox.Length 小，不需要再取模
            byte b = mBox[(toInt(mBox[i]) + toInt(mBox[j])) % mBox.length];
  
            output[offset] = (byte) ((int) a ^ (int) toInt(b));
        }
  
        return output;
    }   
    
    private static String MD52(String MD5) {
        StringBuffer sb = new StringBuffer();
        String part = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5 = md.digest(MD5.getBytes());

            for (int i = 0; i < md5.length; i++) {
                part = Integer.toHexString(md5[i] & 0xFF);
                if (part.length() == 1) {
                    part = "0" + part;
                }
                sb.append(part);
            }

        } catch (NoSuchAlgorithmException ex) {

        }
        return sb.toString();
    }
  
    private static int toInt(byte b) {
        return (int) ((b + 256) % 256);
    }   
  
    private long getUnixTimestamp() {
        Calendar cal = Calendar.getInstance();
        return cal.getTimeInMillis() / 1000;
    }
  
    public static void main(String[] args) {
        String test = "UTF-8中文";
        String key = "bubble";
        String afStr = Authcode3.authcodeEncode(test, key);
        System.out.println("encode: " + afStr);

        String deStr = Authcode3.authcodeDecode(afStr, key);
        System.out.println("decode: " + deStr);
    }
}