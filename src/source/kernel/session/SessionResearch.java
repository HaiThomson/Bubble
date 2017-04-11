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
package source.kernel.session;

import source.kernel.Container;
import source.kernel.Core;
import source.kernel.base.Base;
import source.kernel.security.token.GeneralToken;
import source.kernel.session.struct.DBStructSessionProvider;
import source.table.common_session_struct;

import java.util.HashMap;
import java.util.Map;

/**
 * 如果要序列化Session,传输Session的value属性即可。
 *
 * @author Hai Thomson
 */
public class SessionResearch extends Base {

	public boolean isnew = false;
	public boolean saved = false;

	public SessionProvider sessionProvider = null;

	public SessionResearch(SessionConfig config) {
		sessionProvider = new DBStructSessionProvider(); // 修改为根据config，动态生成对象
	}

	public void set(String key, Object value) {
		sessionProvider.set(key, value);
	}

	public Object get(String key) {
		return this.sessionProvider.get(key);
	}

	// 返回值，既可以用方法return返回值，也可以让方法修改引用; put container
	public void init(String sessionid, String ip, String userid) {
		// SessionProvider必须保证init后,sessionProvider.value和sessionProvider.sessionid有值！
		// sessionProvider应该判断sessionid和userid对应关系
		// 这里是再次判断保证无误
		if (!this.sessionProvider.isExistent(sessionid, ip, userid) || ((String)this.sessionProvider.get("uid")).equals(userid)) {
			this.sessionProvider.create(sessionid, ip, userid);
		}
	}

	// Session和登录状态无关
	// 只需删除数据即可无需发送cookie
	public void delete() {
		this.sessionProvider.delete();
	}

	// 不应该包含设置Cookie>仅包含持久化部分
	public void update() {
		this.sessionProvider.update();
	}

	public void clear() {
		this.sessionProvider.clear();
	}
}
