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

import source.kernel.base.Base;
import source.kernel.security.token.GeneralToken;
import source.kernel.session.struct.DBStructSessionProvider;

import java.sql.SQLException;

/**
 *
 * @author Hai Thomson
 */
public class Session {

	public boolean isnew = false;
	public boolean saved = false;

	public SessionProvider sessionProvider = null;

	private SessionConfig sessionConfig = null;

	public Session(SessionConfig config) {
		this.sessionConfig = config;
		sessionProvider = new DBStructSessionProvider(); // 修改为根据config，动态生成对象
	}

	// 返回值，既可以用方法return返回值，也可以让方法修改引用; put container
	public void init(String sessionid, String ip, String userid) throws SQLException {
		// SessionProvider必须保证init后,sessionProvider.value和sessionProvider.sessionid有值！
		// sessionProvider应该判断sessionid和userid对应关系
		// 这里是再次判断保证无误
		if (!this.sessionProvider.isExistent(sessionid, ip, userid)) {
			this.sessionProvider.create(GeneralToken.generateToken(), ip, userid);
			this.isnew = true;
		}
	}

	public void set(String key, Object value) {
		sessionProvider.set(key, value);
	}

	public Object get(String key) {
		return this.sessionProvider.get(key);
	}

	// Session和登录状态无关
	// 只需删除数据即可无需发送cookie
	public void delete() {
		this.sessionProvider.delete();
	}

	public void update() throws SQLException {
		if (!this.saved) {
			this.sessionProvider.update(this.isnew);
			this.saved = true;
		}
	}

	public void clear() {
		this.sessionProvider.clear();
	}
}
