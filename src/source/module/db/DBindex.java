package source.module.db;

import source.kernel.Container;
import source.kernel.DB;
import source.kernel.base.ExceptionHandler;

import javax.servlet.ServletException;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Hai Thomson
 */
public class DBindex {
	public static void run() throws ServletException, IOException {
		try {
			Container.app().Global.put("version", DB.getDatabaseVersion());
			Container.app().Global.put("table", DB.getRealTableName("common_session_struct"));
			Container.app().Global.put("command", DB.executeCommand("show databases;"));
			Container.app().Global.put("AssociativeArray", DB.queryAll("SELECT * FROM " + DB.getRealTableName("common_session_struct")));
		} catch (SQLException e) {
			ExceptionHandler.handling(e);
		}

		// 跳转时不能写点斜杠开头的相对路径如：./template/default/data/index.jsp
		// servlet位于根目录，相对目录起始点为web.这样是可以跳转到相应页面。
		// 当servlet匹配规则为“/data/*”则上面的写法回陷入无限循环。此时的./意味着/data/下的文件。
		Container.app().request.getRequestDispatcher("/template/default/db/index.jsp").forward(Container.app().request, Container.app().response);
		// 内部跳转后的代码依然可以继续执行。
		// System.out.println("A");
	}
}
