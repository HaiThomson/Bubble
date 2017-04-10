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
package source.kernel.base;

import source.kernel.DB;

import java.util.Map;

/**
 * @author Hai Thomson
 */
public class Table extends Base {
    protected String tableName;
    protected String primaryKey;

    public Table() {
        // 即将增加缓存功能
    }

    public Table(String tableName, String primaryKey) {
        this();
        this.tableName = tableName;
        this.primaryKey = primaryKey;
    }

    public String getTableName() {
        return this.tableName;
    }

    public String setTableName(String tableName) {
        return this.tableName = tableName;
    }

    public Map<String, Object> query(String primaryKey) {
        return DB.queryFirstRow("SELECT * FROM " + DB.getRealTableName(this.tableName) + " WHERE " + DB.makeCondition(this.primaryKey, primaryKey));
    }

    public long count() {
        long count = (long) DB.queryScalar("SELECT count(*) FROM " + DB.getRealTableName(this.tableName));
        return count;
    }

    public int update(Map<String, Object> data, Object condition) {
        DB.makeCondition(this.primaryKey, data, "=");
        if(data != null) {
            this.checkpk();
            if (data.getClass().isArray()) {
                int count = (int) DB.update(this.tableName, data, DB.makeCondition(this.primaryKey, condition, "="));
                return count;
            } else {
                int count = (int) DB.update(this.tableName, data, DB.makeCondition(this.primaryKey, condition, "in"));
                return count;
            }
        } else {
            throw new RuntimeException("");
        }
    }

    public int delete(Object object) {
        if(object != null) {
            this.checkpk();
            return DB.delete(this.tableName, DB.makeCondition(this.primaryKey, object));
        } else {
            throw new RuntimeException("");
        }
    }

    public Object insert(Map<String, Object> data) {
        return DB.insert(this.tableName, data);
    }

    public Boolean truncate() {
       return DB.executeCommand(DB.makeTruncate(this.tableName));
    }

    public void checkpk() {
        if (this.primaryKey.equals("") && this.primaryKey == null) {

        }
    }
}
