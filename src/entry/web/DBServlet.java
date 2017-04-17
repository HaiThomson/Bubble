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
 * Database类功能测试，访问入口
 * @author Hai Thomson
 */
@WebServlet(name = "DBServlet", urlPatterns = "/db/*")
public class DBServlet extends HttpServlet {

	// e 错误测试点
	public static final String[] RES_ARRAY = {
			"", "index.htm", "input.htm", "show.htm",
			"e",
	};

	public static final String RES_SUFFIX = ".htm";

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Container.creatApp(request, response);

		String resName = (String) Container.app().Global.get("res");
		String moduleName = "";
		if(ArraysHelper.inArrays(this.RES_ARRAY, resName)) {
			moduleName = resName ;
			if (moduleName.equals("")) {
				moduleName = "index";
			} else {
				moduleName = moduleName.replaceAll(this.RES_SUFFIX, "");
			}
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, request.getRequestURI());
			return;
		}

		try {
			Container.app().init();
			Class moduleClass = Class.forName(GlobalConfig.SOURCE_PATH + ".module.db.DB" + moduleName);
			moduleClass.getMethod("run").invoke(null);
		} catch (Exception e) {
			ExceptionHandler.handling(e);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request,response);
	}
}