package source.module.cache;

import source.kernel.Core;
import source.kernel.log.Logger;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class Cacheindex {
	public static void run() throws ServletException, IOException {
		Logger.info("测试log");
		Core.setGlobal("number", (int) (Math.random() * 2 + 1));
		Core.forward("/cache/index.jsp");
	}
}
