package source.kernel.view;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class View {

	protected static ViewProvider viewProvider = null;

	static {
		View.init();
	}

	public static void init() {

	}

	public static void deliveryView(String path, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		viewProvider.outputData(path, request, response);
	}
}
