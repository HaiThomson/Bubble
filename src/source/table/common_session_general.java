package source.table;

import source.kernel.DB;
import source.kernel.base.ExceptionHandler;
import source.kernel.db.DataBase;
import source.kernel.base.Table;

import java.sql.SQLException;
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
		try {
			return DB.queryFirstRow(sql, sessionid);
		} catch (SQLException e) {
			ExceptionHandler.handling(e);
		}
		return null;
	}

	public Map fetchAll() {
		String sql = "SELECT * FROM " + DB.getRealTableName(this.tableName);
		try {
			return DB.queryAll(sql);
		} catch (SQLException e) {
			ExceptionHandler.handling(e);
		}
		return null;
	}

	public long delete_by_session(Map<String, Object> value, int onlinehold, int i) {
		return 0;
	}

	public Object insert(Map<String, Object> data) {
		try {
			return DataBase.insert(this.tableName, data);
		} catch (SQLException e) {
			ExceptionHandler.handling(e);
		}
		return null;
	}

	public void update(String sessionid, Map<String, Object> data) {
		try {
			DB.update(this.tableName, data, DB.makeCondition(this.primaryKey, sessionid));
		} catch (SQLException e) {
			ExceptionHandler.handling(e);
		}
	}

	public long clear() {
		return 0;
	}
}
