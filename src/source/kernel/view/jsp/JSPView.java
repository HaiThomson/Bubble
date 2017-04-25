package source.kernel.view.jsp;

import source.kernel.Container;
import source.kernel.view.ViewProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class JSPView extends ViewProvider {
	public static void forward(String realViemPath, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Container.app().request.getRequestDispatcher(realViemPath).forward(Container.app().request, Container.app().response);
	}

	@Override
	public void outputData(String realViemPath, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSPView.forward(realViemPath, request, response);
	}
}
