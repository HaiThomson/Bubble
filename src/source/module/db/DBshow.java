package source.module.db;

import source.kernel.Container;
import source.kernel.Core;
import source.table.common_session_struct;

/**
 * @author Hai Thomson
 */
public class DBshow {
	public static void run() throws Exception {

		Container.app().Global.put("allsession", ((common_session_struct) Container.table("common_session_struct")).fetchAll());
		Container.app().Global.put("allsession", Container.table("common_session_struct").call("fetchAll"));

		/*if (1 == 1) {
		throw new SQLException("测试");
		}*/

		Core.forward("/db/show.jsp");
	}
}
