package source.kernel.cron;

import source.kernel.DB;
import source.kernel.config.GlobalConfig;
import source.kernel.db.pool.ConnectionPooling;

import java.sql.SQLException;

/**
 * @author Hai Thomson
 */
public class CronTest {
	public static void main(String[] args) throws SQLException {
		// 为 Cron 模块启用 DB 支持
		ConnectionPooling.init("c3p0", "classloader:" + ("./config/c3p0-config.xml"));
		DB.init(GlobalConfig.DATABASE_CONFIG);

		Cron.init();
	}
}
