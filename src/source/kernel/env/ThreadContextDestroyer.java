package source.kernel.env;

import source.kernel.Container;
import source.kernel.Core;
import source.kernel.DB;

import java.sql.SQLException;

/**
 * @author Hai Thomson
 */
public class ThreadContextDestroyer {

	public static void destroyResource() throws SQLException {
		// 执行持久化Session操作, 如已保存不会发生任何事情
		ThreadContextDestroyer.persistenceSession();

		// 逐步清理资源
		ThreadContextDestroyer.closeDataBaseConnnection();
		ThreadContextDestroyer.destoryDataBaseDriver();
		ThreadContextDestroyer.destoryApplication();
		// 内存缓存, Log等模块不需要清理
	}

	protected static void persistenceSession() throws SQLException {
		Core.persistenceSession();
	}

	protected static void closeDataBaseConnnection() throws SQLException {
		DB.closeConnection();
	}

	protected static void destoryDataBaseDriver() {
		DB.destoryDriver();
	}

	protected static void destoryApplication() {
		Container.destoryApp();
	}
}
