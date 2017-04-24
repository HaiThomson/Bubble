package source.kernel.db;

import source.kernel.base.Base;
import source.kernel.config.GlobalConfig;

/**
 * Database模块设置
 *
 * @author Hai Thomson
 */
public class DatabaseConfig extends Base {
	// 内部数据库驱动抽象和JDBC无关.
	public String DBDRIVER_PATH = GlobalConfig.SOURCE_PATH + ".kernel.db.driver.MySQLDriver";
	// 表名前缀，可为空字符串
	public String TABLE_PREFIX = "pre_";
	// 连接池配置类型
	public static String CONNECTIONPOOL_TYPE = "c3p0";
	// 连接池配置文件位置
	public static String CONNECTIONPOOL_CONFIG_PATH = "/config/c3p0-config.xml";
}
