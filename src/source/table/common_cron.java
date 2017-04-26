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

	public Map fetchNextTask(int now) throws SQLException {
		return DB.queryAll("SELECT * FROM " + DB.getRealTableName(this.tableName) + " WHERE available>'0' and nextrun <= "+ now + " ORDER BY cronid");
	}
}
