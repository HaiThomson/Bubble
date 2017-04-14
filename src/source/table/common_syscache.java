package source.table;

import source.kernel.DB;
import source.kernel.base.*;
import source.kernel.base.ExceptionHandler;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.SQLException;
import java.util.Map;

/**
 * @author Hai Thomson
 */
public class common_syscache extends Table {

	private static final common_syscache common_syscache = new common_syscache();

	public common_syscache() {
		super();
		this.tableName = "common_syscache";
		this.primaryKey = "cachename";
	}

	public static Object insert(String cachename, byte[] data, int  dateline) {
		String sql = "REPLACE INTO " + DB.getRealTableName(common_syscache.tableName) + " VALUES(?, ?, ?)";

		SerialBlob blob= null;
		try {
			blob = new SerialBlob(data);
		} catch (SQLException e) {
			ExceptionHandler.handling(e);
		}

		try {
			return DB.insert(sql, cachename, blob, dateline);
		} catch (SQLException e) {
			ExceptionHandler.handling(e);
		}
		return null;
	}

	public Map fetchAll() {
		try {
			return DB.queryAll("SELECT * FROM " + DB.getRealTableName(common_syscache.tableName));
		} catch (SQLException e) {
			ExceptionHandler.handling(e);
		}
		return null;
	}
}
