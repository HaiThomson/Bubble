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
package source.kernel.db.pool;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Hai Thomson
 */
public class C3P0ConnectionPooling extends ConnectionPoolingDriver {
	private ComboPooledDataSource datasource = null;

	@Override
	public void init(String path) {
		// System.setProperty("com.mchange.v2.c3p0.cfg.xml", "classloader:" + path); // 配置文件在src目录
		System.setProperty("com.mchange.v2.c3p0.cfg.xml", path); // 配置文件在WEB目录
		this.datasource = new ComboPooledDataSource();
	}

	@Override
	public void init(String path, String dbName) {
		// System.setProperty("com.mchange.v2.c3p0.cfg.xml", "classloader:" + path); // 配置文件在src目录
		System.setProperty("com.mchange.v2.c3p0.cfg.xml", path); // 配置文件在WEB目录
		this.datasource = new ComboPooledDataSource(dbName);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return this.datasource.getConnection();
	}

	@Override
	public void close() {
		this.datasource.close();
	}
}
