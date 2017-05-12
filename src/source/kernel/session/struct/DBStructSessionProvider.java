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
package source.kernel.session.struct;

import source.kernel.Container;
import source.kernel.Core;
import source.kernel.session.SessionProvider;
import source.table.common_session_struct;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
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
public class DBStructSessionProvider extends SessionProvider {

	// 初始化数据可以不用修改
	// 新用户
	// newguest 必须和 session表结构保持一致
	private Map<String, Object> newguest = new HashMap<String, Object>() {{
		put("sessionid", "");put("dateline", 0);;put("ip1", "");put("ip2", "");put("ip3", "");put("ip4", "");
		put("userid", "");put("username", "");put("groupid", 0);put("invisible", 0);put("lastactivity", 0L);put("actionname", "");
		put("cartid", 0);put("xid", 0);
	}};

	public Map<String, Object> value = null;
	public common_session_struct table = null;

	public DBStructSessionProvider() {
		this.table = (common_session_struct) Container.table("common_session_struct");
	}

	@Override
	public void set(String key, Object value) {
		// 拆分IP，用数字存储.便于查找，控制
		if (key.equals("ip")) {
			String[] ip = ((String)value).split("\\.");
			this.set("ip1", ip[0]);
			this.set("ip2", ip[1]);
			this.set("ip3", ip[2]);
			this.set("ip4", ip[3]);
		} else {
			if (!this.value.containsKey(key)) {
				return;
			}
			this.value.put(key, value);
		}
	}

	@Override
	public Object get(String key) {
		if (key.equals("ip")) {
			return this.get("ip1") + "." + this.get("ip2") + "." + this.get("ip3") + this.get("ip4");
		} else {
			return this.value.get(key);
		}
	}

	@Override
	public boolean isExistent(String sessionid, String ip, String userid) throws SQLException {
		Map sessionValue = null;
		if(sessionid != null && !sessionid.equals("")) {
			sessionValue = this.table.fetch(sessionid, ip, userid);
		} else {
			return false;
		}

		if(sessionValue == null || sessionValue.get("sessionid").equals("") || sessionValue.get("userid").equals("") || !((sessionValue.get("userid")).toString()).equals(userid)) {
			return false;
		}

		// 检查是否过期
		if ((long)sessionValue.get("dateline") < System.currentTimeMillis() / 1000) {
			return false;
		}

		this.value = sessionValue;
		return true;
	}

	@Override
	public void create(String sessionid, String ip, String userid) throws SQLException {
		this.value = this.newguest;
		this.set("sessionid", sessionid);
		this.set("ip", ip);
		this.set("userid", userid);

		this.set("lastactivity", Container.app().TIMESTAMP);
	}

	@Override
	public long delete() {
		this.value = null;
		return this.table.deleteBySession(this.value, 3600, 60);
	}

	@Override
	public void update(boolean isnew) throws SQLException {
		String sessionid = (String) this.get("sessionid");
		if(sessionid != null && !sessionid.equals("")) {
			if(isnew) {
				this.table.insert(this.value);
			} else {
				this.table.update((String) this.value.get("sessionid"), this.value);
			}
			Core.setCookie("sessionid", sessionid, 86400);
		}
	}

	@Override
	public long clear() throws SQLException {
		return this.table.clear();
	}

	public long count() throws SQLException {
		return this.table.countAll();
	}
}
