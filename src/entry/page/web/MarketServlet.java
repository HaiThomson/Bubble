package entry.page.web;

import source.kernel.Core;
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
 * 逻辑控制层技术探索原型
 * @author Hai Thomson
 */
@WebServlet(name = "MarketServlet", urlPatterns = "/market/*")
public class MarketServlet extends HttpServlet {
	// 使用资源列表进行精确控制
	// e 错误测试点
	public static final String[] RES_ARRAY = {
			"", "index.htm", "show.htm", "input.htm", "auto.htm",
			"e",
	};

	// public static final String RES_SUFFIX = ".htm";
	public static final String RES_SUFFIX = GlobalConfig.RES_SUFFIX;

	public static final String NONE = "NONE";
	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR = "ERROR";

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
			Class moduleClass = Class.forName(GlobalConfig.SOURCE_PATH + ".module.market.MarketAction");
			String returned = (String) moduleClass.getMethod(moduleName).invoke(null);

			// 根据module返回值进行页面调度
			if (returned != null) {
				if (returned.equals(MarketServlet.SUCCESS)) {
					Core.forward("/market/" + moduleName + ".jsp");
				}

				if (returned.equals(MarketServlet.ERROR)) {
					Container.app().response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}

				if (returned.equals(MarketServlet.NONE) ||  returned.equals("")) {
					// Nothing to do
				}
			} else {
				// Nothing to do
			}
		} catch (Exception e) {
			ExceptionHandler.handling(e);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request,response);
	}
}