package entry.web;

import source.kernel.Container;
import source.kernel.config.GlobalConfig;
import source.kernel.base.ExceptionHandler;
import source.kernel.helper.ArraysHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 控制器演示, 只支持单级目录访问
 *
 * 修改RES_ARRAY加载规则即可实现伪静态
 * 伪静态化可以利用浏览器缓存做真静态化.处理好用户状态及动态部分
 * @author Hai Thomson
 */
@WebServlet(name = "PageServlet", urlPatterns = "/page/*")
public class PageServlet extends HttpServlet {

	// e 错误测试点
	public static final String[] RES_ARRAY = {
			"", "index.htm", "show.htm",
			"e",
	};

	// 资源后缀名,用于将请求的资源名转换为模块名.
	public static final String RES_SUFFIX = ".htm";

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//System.out.println("Servlet" + "\t" + Thread.currentThread().getName() + "\t" + request.getRequestURL());
		Container.creatApp(request, response);

		String resName = (String) Container.app().Global.get("res");
		String moduleName = "";
		// 这里可以改照成 IS_IN + RegEx 等各种模式
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

		Container.app().init();

		try {
			Class moduleClass = Class.forName(GlobalConfig.SOURCE_PATH + ".module.page.Page" + moduleName);
			moduleClass.getMethod("run").invoke(null);
		} catch (Exception e) {
			ExceptionHandler.handling(e);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request,response);
	}
}