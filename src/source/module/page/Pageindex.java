package source.module.page;

import source.kernel.Core;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class Pageindex {
	public static void run() throws ServletException, IOException {
		Core.setGlobal("number", (int) (Math.random() * 2 + 1));
		Core.forward("/page/index.jsp");
	}
}