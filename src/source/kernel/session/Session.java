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
import source.table.common_session_struct;

import java.util.HashMap;
import java.util.Map;

/**
 * 如果要序列化Session,传输Session的value属性即可。
 *
 * # Maximum size for internal (in-memory) temporary tables. If a table
 * # grows larger than this value, it is automatically converted to disk
 * # based table This limitation is for a single table. There can be many
 * # of them.
 * tmp_table_size=1024M
 * max_heap_table_size=1024M
 *
 * @author Hai Thomson
 */
public class Session extends Base {

	public String sessionid = null;
	public Map<String, Object> value = null;
	public boolean isnew = false;
	public boolean saved = false;

	protected static final String[] kes = {"",};

	// 初始化数据可以不用修改
	// 新用户
	protected Map<String, Object> newguest = new HashMap<String, Object>() {{
		put("sessionid", "");put("ip1", "");put("ip2", "");put("ip3", "");put("ip4", "");
		put("userid", "");put("username", "");put("groupid", 0);put("invisible", 0);put("lastactivity", 0L);put("actionname", "");
	}};

	protected common_session_struct table = null;

	public Session() {
		this.value = this.newguest;
		this.table = (common_session_struct) Container.table("common_session_struct");
	}

	public void set(String key, Object value) {
		// 拆分IP，用数字存储.便于查找，控制
		if (key.equals("ip")) {
			String[] ip = ((String)value).split("\\.");
			this.set("ip1", ip[0]);
			this.set("ip2", ip[1]);
			this.set("ip3", ip[2]);
			this.set("ip4", ip[3]);
		} else {
			this.value.put(key, value);
		}
	}

	public Object get(String key) {
		if (key.equals("ip")) {
			return this.get("ip1") + "." + this.get("ip2") + "." + this.get("ip3") + this.get("ip4");
		} else {
			return this.value.get(key);
		}
	}

	public void init(String sessionid, String ip, String userid) {
		Map session = null;
		if(sessionid !=null && !sessionid.equals("")) {
			session = this.table.fetch(sessionid, ip, userid);
		}

		if(session == null || !((session.get("userid")).toString()).equals(userid)) {
			session = this.create(ip, userid);
		}

		this.value = session;
		this.sessionid = (String) session.get("sessionid");
	}

	protected Map<String, Object> create(String ip, String userid) {
		this.isnew = true;
		this.value = this.newguest;
		this.set("sessionid", GeneralToken.generateToken(GeneralToken.MODE_UUID));
		this.set("userid", userid);
		this.set("ip", ip);
		if (userid != null && !userid.equals("")) {
			Map user = Core.getUserByUid(userid);
			if (user != null && user.get("invisible") != null) {
				this.set("invisible", user.get("invisible"));
			}
		}
		this.set("lastactivity", Container.app().TIMESTAMP);
		this.sessionid = (String) this.value.get("sessionid");

		return this.value;
	}

	public long delete() {
		return this.table.delete_by_session(this.value, 3600, 60);
	}

	public void update() {
		if(this.sessionid != null && !this.sessionid.equals("")) {
			if(this.isnew) {
				this.delete();
				this.table.insert(this.value);
			} else {
				this.table.update((String) this.value.get("sessionid"), this.value);
			}
			Core.setGlobal("session", this.value);
			Core.setCookie("sessionid", this.sessionid, 86400);
		}
	}

	/**
	 * 表行数
	 * @return
	 */
	public long count() {
		return this.table.count();
	}

	public long clear() {
		return this.table.clear();
	}
}
