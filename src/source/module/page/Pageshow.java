package source.module.page;

import source.kernel.Core;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class Pageshow {
	public static void run() throws ServletException, IOException {
		Core.forward("/page/show.jsp");
	}
}
