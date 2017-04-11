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
package source.kernel.db;

import source.kernel.base.Base;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Hai Thomson
 */
public abstract class DataBaseDriver extends Base {

    protected abstract void setTablePrefix(String tablepre);

    protected abstract void connect() throws SQLException;

    protected abstract Connection getConnection();

    protected abstract void closeConnection();

    protected abstract String getDatabaseVersion();

    protected abstract String getRealTableName(String table);

    protected abstract String makeInsert(String table, Map<String, Object> data);

    protected abstract String makeDelete(String table, Map<String, Object> condition);

    protected abstract String makeDelete(String table, String condition);

    protected abstract String makeUpdate(String table, Map<String, Object> data, Map<String, Object> condition);

    protected abstract String makeUpdate(String table, Map<String, Object> data, String condition);

    protected abstract String makePagination(String table, String condition, int start, int limit);

    protected abstract String makeTruncate(String table);

    protected abstract String makeCondition(String field, Object value, String glue);

    protected abstract String makeOrder(String field, String direction);

    protected abstract String makeLimit(int m, int n);

    protected abstract String implode(Map<String, Object> data, String glue);
}
