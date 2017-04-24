package entry.page.controller;

import source.kernel.Container;
import source.kernel.controller.Controller;
import source.module.economy.Economyindex;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Hai Thomson
 */
@WebFilter(filterName = "LiveController", urlPatterns = "/live/*")
public class LiveController extends Controller {
	public String index() {
		try {
			Economyindex.run();
			return "/economy/index.jsp";
		} catch (Exception e) {
			return Controller.ERROR;
		}
	}

	/**
	 *
	 * @param request
	 * @param response
	 * @param methodName
	 * @return 返回值确定是否继续向下运行
	 * @throws Exception
	 */
	protected boolean _runBeforeAspect(HttpServletRequest request, HttpServletResponse response, String methodName) throws Exception {
		Container.creatApp(request, response);
		Container.app().init();

		return Boolean.TRUE;
	}

	/**
	 *
	 * @param request
	 * @param response
	 * @param methodName
	 * @return 返回值确定是否继续向下运行
	 * @throws Exception
	 */
	protected boolean _runAfterAspect(HttpServletRequest request, HttpServletResponse response, String methodName) throws Exception {
		return Boolean.TRUE;
	}
}
