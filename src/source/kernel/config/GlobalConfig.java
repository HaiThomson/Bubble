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
package source.kernel.config;

import source.kernel.base.Base;
import source.kernel.db.DatabaseConfig;
import source.kernel.log.LogConfig;
import source.kernel.serialization.json.Json;
import source.kernel.session.SessionConfig;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * 全局配置
 * 属性类型约定：[String | int | long | boolean]
 * @author Hai Thomson
 */
public class GlobalConfig extends Base {
    protected GlobalConfig() {}

    public static GlobalConfig instance() {
        return globalConfig;
    }

    protected static final GlobalConfig globalConfig = new GlobalConfig();

    // 代码主目录, 可以用支持重构的IDE更改source路径.如.com.hai.bubble
    public static String SOURCE_PATH = "source";
    // 项目编码
    public static String JAVA_ENCODING = "UTF-8";
    // 输出编码
    public static String OUTPUT_CHARSET = "UTF-8";
    // 全局访问资源后缀
    public static String RES_SUFFIX = ".htm";
    // 是否开启调试模式.生产环境请关闭
    public static Boolean DEBUG = true;
    // Cookie配置
    public static CookieConfig COOKIE_CONFIG = new CookieConfig();
    // AUTHKEY. 随意输一个大于8位长的字符串就可以
    public static String SECURITY_AUTHKEY = "asdfasfas";
    // 是否启用内存XSS检查功能,开启后拒绝有XSS嫌疑的访问
    public static boolean SECURITY_URLXSSDEFEND = true;
    // 是否启用爬虫检查功能,开启后拒绝爬虫访问
    public static boolean SECURITY_ROBOOT = true;

    // DataBase配置
    public static DatabaseConfig DATABASE_CONFIG = new DatabaseConfig();

    // Session模块配置
    public static SessionConfig SESSION_CONFIG = new SessionConfig();

    // Cache相关
    // 文件缓存位置
    public static String FILE_CACHE_DIRECTORY = "./cache/";

    // 日志配置
    public static LogConfig LOG_CONFIG = new LogConfig();

    public static void init(String path) {
        try {
            Map config = Json.parseMap(GlobalConfig.loadConfigFile(path));
            if (config != null && config.size() > 0) {
                GlobalConfig.setItemValue(config);
            }
        } catch (IOException e) {
            throw new RuntimeException("载入配置文件时发生IO错误.文件不存在或无法读取! " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("没有找到子配置对应的类文件！ " + e.getMessage());
        } catch (InstantiationException e) {
            throw new RuntimeException("子配置类不是一个实体类！ " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("无法访问子配置类的构造函数！ " + e.getMessage());
        }
    }

    protected static void setItemValue(Map config) throws ClassCastException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        Field[] declaredFields = GlobalConfig.class.getFields();
        for(Field item : declaredFields) {
            Object value = config.get(item.getName());
            if (value != null && !value.toString().equals("")) {
                String typeName = item.getGenericType().getTypeName();
                switch (typeName) {
                    case "java.lang.String" :
                        item.set(null, (String) value);
                        break;
                    case "java.lang.Integer" :
                        item.set(null, new Integer((String) value));
                        break;
                    case "java.lang.Long" :
                        item.set(null, new Long((String) value));
                        break;
                    case "java.lang.Boolean" :
                        item.set(null, new Boolean((String)value));
                        break;
                    default :
                        if (value instanceof Map) {
                            // System.out.println(value);
                            item.set(null, GlobalConfig.createSub_itemValue(typeName, (Map) value));
                        }
                }
            }
        }
    }

    // sub_itemObject 纯粹的引用
    protected static Object createSub_itemValue(String typeName, Map config) throws ClassCastException, IllegalAccessException, ClassNotFoundException, InstantiationException {
        Class sub_itemClass = Class.forName(typeName);
        Object sub_itemObject = sub_itemClass.newInstance();
        Field[] declaredFields = sub_itemClass.getFields();
        for(Field item : declaredFields) {
            Object value = config.get(item.getName());
            if (value != null && !value.toString().equals("")) {
                String subTypeName = item.getGenericType().getTypeName();
                switch (subTypeName) {
                    case "java.lang.String" :
                        item.set(sub_itemObject, (String) value);
                        break;
                    case "java.lang.Integer" :
                        item.set(sub_itemObject, new Integer((String) value));
                        break;
                    case "java.lang.Long" :
                        item.set(sub_itemObject, Long.valueOf((String) value));
                        break;
                    case "java.lang.Boolean" :
                        item.set(sub_itemObject, new Boolean((String)value));
                        break;
                }
            }
        }
        // System.out.println(sub_itemObject);
        return sub_itemObject;
    }

    protected static String loadConfigFile(String path) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(path), "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line=bufferedReader.readLine()) != null){
            stringBuilder.append(line);
        }
        bufferedReader.close();
        inputStreamReader.close();
        return stringBuilder.toString();
    }

}
