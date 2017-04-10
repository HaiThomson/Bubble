package entry.web;

import source.kernel.Container;
import source.kernel.config.GlobalConfig;
import source.kernel.DB;
import source.kernel.base.ExceptionHandler;
import source.kernel.helper.ArraysHelper;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet Filter实现控制器实验
 * 内部跳转不自动过滤器.需手动跳转！
 * @author Hai Thomson
 */
@WebFilter(filterName = "MemberFilter", urlPatterns = "/member/*")
public class MemberFilter implements Filter {

	// e 错误测试点
	public static final String[] RES_ARRAY = {
			"", "index.htm", "show.htm",
			"e",
	};

	// 可被持久化调用
	public static final String RES_SUFFIX = ".htm";

	public void init(FilterConfig config) throws ServletException {

	}

	public void destroy() {
	}

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
		// System.out.println("Servlet" + "\t" + Thread.currentThread().getName() + "\t" + request.getRequestURL());
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		Container.creatApp(request, response);

		String resourceName = (String) Container.app().Global.get("res");
		System.out.println(resourceName);
		String moduleName = "";
		if(ArraysHelper.inArrays(this.RES_ARRAY, resourceName)) {
			moduleName = resourceName ;
			if (moduleName.equals("")) {
				moduleName = "index";
			} else {
				moduleName = moduleName.replaceAll(this.RES_SUFFIX, "");
			}
		} else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, request.getRequestURI());
			return;
		}

		Container.app().init();

		try {
			Class moduleClass = Class.forName(GlobalConfig.SOURCE_PATH + ".module.member.Member" + moduleName);
			moduleClass.getMethod("run").invoke(null);
		} catch (Exception e) {
			ExceptionHandler.handling(e);
		}

		chain.doFilter(req, resp);

		this.closeDataBaseConnnection();
		this.destoryDriver();
		this.destoryApplication();

	}

	private void closeDataBaseConnnection() {
		if (DB.getDriver() != null) {
			// 如果开启事务并未提交，则提交,并关闭事务手动提交
			// 收回资源
			DB.closeConnection();
		}
	}

	private void destoryDriver() {
		DB.destoryDriver();
	}

	private void destoryApplication() {
		Container.destoryApp();
	}
}
