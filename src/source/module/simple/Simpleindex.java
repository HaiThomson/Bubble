package source.module.simple;

import source.kernel.Container;
import source.kernel.Core;
import source.table.common_session_struct;

/**
 * @author Hai Thomson
 */
public class Simpleindex {
	public static void run() throws Exception {
		// 两种写法
		Container.app().Global.put("allsession", ((common_session_struct) Container.table("common_session_struct")).fetchAll());
		Container.app().Global.put("allsession", Container.table("common_session_struct").call("fetchAll"));

		Core.forward("/simple/index.jsp");
	}
}
