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

import java.sql.SQLException;
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
        try {
            return DB.queryFirstRow("SELECT * FROM " + DB.getRealTableName(this.tableName) + " WHERE " + DB.makeCondition(this.primaryKey, primaryKey));
        } catch (SQLException e) {
            ExceptionHandler.handling(e);
        }
        return null;
    }

    public long count() {
        long count = 0;
        try {
            count = (long) DB.queryScalar("SELECT count(*) FROM " + DB.getRealTableName(this.tableName));
        } catch (SQLException e) {
            ExceptionHandler.handling(e);
        }
        return count;
    }

    public int update(Map<String, Object> data, Object condition) {
        DB.makeCondition(this.primaryKey, data, "=");
        if(data != null) {
            this.checkPrimaryKey();
            if (data.getClass().isArray()) {
                int count = 0;
                try {
                    count = (int) DB.update(this.tableName, data, DB.makeCondition(this.primaryKey, condition, "="));
                } catch (SQLException e) {
                    ExceptionHandler.handling(e);
                }
                return count;
            } else {
                int count = 0;
                try {
                    count = (int) DB.update(this.tableName, data, DB.makeCondition(this.primaryKey, condition, "in"));
                } catch (SQLException e) {
                    ExceptionHandler.handling(e);
                }
                return count;
            }
        } else {
            ExceptionHandler.handling(new NullPointerException("传入的数据为null"));
        }
        return 0;
    }

    public int delete(Object object) {
        if(object != null) {
            this.checkPrimaryKey();
            try {
                return DB.delete(this.tableName, DB.makeCondition(this.primaryKey, object));
            } catch (SQLException e) {
                ExceptionHandler.handling(e);
            }
        } else {
            ExceptionHandler.handling(new NullPointerException("传入的数据为null"));
        }
        return 0;
    }

    public Object insert(Map<String, Object> data) {
        try {
            return DB.insert(this.tableName, data);
        } catch (SQLException e) {
            ExceptionHandler.handling(e);
        }
        return null;
    }

    public Boolean truncate() {
        try {
            return DB.executeCommand(DB.makeTruncate(this.tableName));
        } catch (SQLException e) {
            ExceptionHandler.handling(e);
        }
        return true;
    }

    public void checkPrimaryKey() {
        if (this.primaryKey == null && this.primaryKey.equals("")) {
            ExceptionHandler.handling(new RuntimeException("Table的子类["+ this.getClass().getName() +"]未按规范定义有效的主键"));
        }
    }
}
