package source.table;

import source.kernel.DB;
import source.kernel.base.Table;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author Hai Thomson
 */
public class common_session_struct extends Table {

	public common_session_struct() {
		super("common_session_struct", "sessionid");
	}

	public Map fetchAll() throws SQLException {
		String sql = "SELECT * FROM " + DB.getRealTableName(this.tableName);
		return DB.queryAll(sql);
	}

	public Map fetch(String sessionid, String ip, String userid) throws SQLException {
		String sql = "SELECT * FROM " + DB.getRealTableName(this.tableName) + " WHERE " + DB.makeCondition(this.primaryKey, "?");
		return DB.queryFirstRow(sql, sessionid);
	}

	public long deleteBySession(Map<String, Object> value, int onlinehold, int i) {
		return 0;
	}

	public void update(String sessionid, Map<String, Object> data) throws SQLException {
		DB.update(this.tableName, data, DB.makeCondition(this.primaryKey, sessionid));
	}

	public long clear() throws SQLException {
		int now = (int) (System.currentTimeMillis() / 1000);
		String sql = "DELETE FROM " + DB.getRealTableName(this.tableName) + " WHERE `dateline` < " + now;
		return DB.update(sql);
	}
}
