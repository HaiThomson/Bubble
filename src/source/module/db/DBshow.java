package source.module.db;

import source.kernel.Container;
import source.kernel.Core;
import source.table.common_session_struct;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class DBshow {
	public static void run() throws ServletException, IOException {
		Container.app().Global.put("all", ((common_session_struct) Container.table("common_session_struct")).fetchAll());
		Container.app().Global.put("all", Container.table("common_session_struct").call("fetchAll"));

		Core.forward("/db/show.jsp");
	}
}
