package source.table;

import source.kernel.DB;
import source.kernel.base.DataBase;
import source.kernel.base.Table;

import java.util.Map;

/**
 * @author Hai Thomson
 */
public class common_session_general extends Table {

	public common_session_general() {
		super();
		this.tableName = "common_session_general";
		this.primaryKey = "sessionid";
	}

	public Map fetch(String sessionid, String ip, String userid) {
		String sql = "SELECT * FROM " + DB.getRealTableName(this.tableName) + " WHERE " + DB.makeCondition(this.primaryKey, "?");
		return DB.queryFirstRow(sql, sessionid);
	}

	public Map fetchAll() {
		String sql = "SELECT * FROM " + DB.getRealTableName(this.tableName);
		return DB.queryAll(sql);
	}

	public long delete_by_session(Map<String, Object> value, int onlinehold, int i) {
		return 0;
	}

	public Object insert(Map<String, Object> data) {
		return DataBase.insert(this.tableName, data);
	}

	public void update(String sessionid, Map<String, Object> data) {
		DB.update(this.tableName, data, DB.makeCondition(this.primaryKey, sessionid));
	}

	public long clear() {
		return 0;
	}
}
