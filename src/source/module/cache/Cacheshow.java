package source.module.cache;

import source.kernel.Container;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class Cacheshow {
	public static void run() throws ServletException, IOException {
		Container.app().request.getRequestDispatcher("/template/default/cache/show.jsp").forward(Container.app().request, Container.app().response);
	}
}
