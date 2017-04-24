package source.kernel.controller;

import source.kernel.Container;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Hai Thomson
 */
public class ControllerSupport extends Controller {
	protected void _createApp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Container.creatApp(request, response);
	}

	protected void _initApp() throws SQLException, IOException, ClassNotFoundException {
		Container.app().init();
	}
}
