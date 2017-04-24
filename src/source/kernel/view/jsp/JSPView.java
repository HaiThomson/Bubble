package source.kernel.view.jsp;

import source.kernel.Container;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class JSPView {
	public static void output(String realViemPath, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Container.app().request.getRequestDispatcher(realViemPath).forward(Container.app().request, Container.app().response);
	}
}
