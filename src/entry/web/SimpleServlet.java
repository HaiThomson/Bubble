package entry.web;

import source.kernel.Container;
import source.kernel.base.ExceptionHandler;
import source.kernel.config.GlobalConfig;
import source.kernel.helper.ArraysHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
@WebServlet(name = "SimpleServlet", urlPatterns = "/simple/*")
public class SimpleServlet extends HttpServlet {

	public static final String[] RES_ARRAY = {
			"", "index.htm", "show.htm",
	};

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Container.creatApp(request, response);
		String resName = (String) Container.app().Global.get("res");
		String moduleName = (ArraysHelper.inArrays(RES_ARRAY, resName) && !resName.equals("") ? resName : "index").replaceAll(GlobalConfig.RES_SUFFIX, "");

		try {
			Container.app().init();
			Class moduleClass = Class.forName(GlobalConfig.SOURCE_PATH  + ".module.simple.Simple" + moduleName);
			moduleClass.getMethod("run").invoke(null);
		} catch (Exception e) {
			ExceptionHandler.handling(e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
}
