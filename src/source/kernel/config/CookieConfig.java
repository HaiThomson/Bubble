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

/**
 * COOKIE设置
 * 类访问权限决定性影响该类属性和方法访问权限
 *
 * @author Hai Thomson
 */
public class CookieConfig extends Base {
    // 配置可以动态修改.动态更改配置后，应同步至配置文件
    // COOKIE前缀
    public String COOKIE_PRE = "bubble_";
    // COOKIE作用域
    public String COOKIE_DOMAIN = "";
    // COOKIE作用路径
    public String COOKIE_PATH = "/";
}