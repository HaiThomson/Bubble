package source.module.db;

import source.kernel.Core;
import source.kernel.log.Logger;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class DBinput {
	public static void run() throws ServletException, IOException {
		Logger.info(DBinput.class.getName() + " 随机测试" + (int) (Math.random() * 100 + 1));
		Core.forward("/db/input.jsp");
	}
}
