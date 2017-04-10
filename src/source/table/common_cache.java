package source.table;

import source.kernel.Container;
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
		super(); // 不写编译时也会加上! 强调下，Table类默认构造函数有关键流程！
		this.primaryKey = "cachekey";
		this.tableName = "common_cache";
	}

	public static int insert(String cachename, byte[] data) {
		String sql = "REPLACE INTO " + DB.getRealTableName(common_cache.tableName) + " VALUES(?, ?, ?)";

		SerialBlob blob= null;
		try {
			blob = new SerialBlob(data);
		} catch (SQLException e) {
			ExceptionHandler.handling(e);
		}

		return DB.update(sql, cachename, blob, Container.app().TIMESTAMP);
	}
}
