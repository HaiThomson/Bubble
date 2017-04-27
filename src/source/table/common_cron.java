package source.table;

import source.kernel.DB;
import source.kernel.base.Table;

import java.sql.SQLException;
import java.util.Map;

/**
 * SQL时间以数据库服务器时间为准, 这意味着时间字段值将从时间聚集函数获取
 * @author Hai Thomson
 */
public class common_cron extends Table {
	public common_cron() {
		super("common_cron", "cronid");
	}

	public void updateLastrun(int cronid, int now) throws SQLException {
		String sql = "UPDATE " + DB.getRealTableName(this.tableName) + " SET lastrun = " + now + " WHERE cronid = " + cronid;
		DB.update(sql);
	}

	public Map fetchTask(int now) throws SQLException {
		// 玩集群,时间一致是最基本的要求.
		// 允许10s的差值.
		now = now - 10;
		String sql = "SELECT * FROM " + DB.getRealTableName(this.tableName) + " WHERE lastrun < " + now;
		return DB.queryAll(sql);
	}
}
