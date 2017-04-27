package source.include.cron;

import source.kernel.session.SessionProvider;
import source.kernel.session.struct.DBStructSessionProvider;

import java.sql.SQLException;

/**
 * @author Hai Thomson
 */
public class SessionClearTask {
	public static void clearOutOfDateSession() {
		SessionProvider sessionProvider = new DBStructSessionProvider();
		try {
			sessionProvider.clear();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("清理过期Session的计划任务已执行");
	}
}
