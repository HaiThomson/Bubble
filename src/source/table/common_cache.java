package source.table;

import source.kernel.DB;
import source.kernel.base.*;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.SQLException;

/**
 * @author Hai Thomson
 */
public class common_cache extends Table {
	private static final common_cache common_cache = new common_cache();

	public common_cache() {
		super("common_cache", "cachekey");
	}

	public static int insert(String cachename, byte[] data, int dateline) {
		String sql = "REPLACE INTO " + DB.getRealTableName(common_cache.tableName) + " VALUES(?, ?, ?)";

		SerialBlob blob= null;
		try {
			blob = new SerialBlob(data);
		} catch (SQLException e) {
			ExceptionHandler.handling(e);
		}

		try {
			return DB.update(sql, cachename, blob, dateline);
		} catch (SQLException e) {
			ExceptionHandler.handling(e);
		}
		return 0;
	}
}
