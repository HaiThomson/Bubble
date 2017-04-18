package source.kernel.mvc;

import source.kernel.Container;
import source.kernel.Core;
import source.kernel.base.Base;
import source.kernel.base.ExceptionHandler;
import source.kernel.config.GlobalConfig;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * @since 1.7
 * @author Hai Thomson
 */
public class Controller extends Base implements Filter {

	public static final String NONE = "NONE";
	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR = "ERROR";
	// 开发者已自行跳转或输出数据,但仍需传递职责链
	public static final String CHAIN = "CHAIN";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		Object result = null;

		try {
			Container.creatApp((HttpServletRequest)servletRequest, (HttpServletResponse)servletResponse);
			String resName = (String) Container.app().Global.get("res");
			String methodName = (resName != null && !resName.equals("") ? resName : "index").replaceAll(GlobalConfig.RES_SUFFIX, "");

			Container.app().init();
			result = this.call(methodName);
		} catch (Exception e) {
			ExceptionHandler.handling(e);
		}

		if (this.forward(result)) {
			filterChain.doFilter(servletRequest, servletResponse);
		} else {
			return;
		}

	}

	/**
	 * 根据控制器返回结果，判断是否继续传递职责链
	 * 加载视图属于额外功能
	 * @param result
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 */
	public boolean forward(Object result) throws IOException, ServletException {
		if (result != null) {
			switch (result.toString()) {
				// 额外功能
				case Controller.SUCCESS :
					// 自动生成跳转路径
					// Core.forward(""); // 默认跳转至监听目录
					return Boolean.TRUE;
				case Controller.ERROR :
					Container.app().response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return Boolean.FALSE;
				case Controller.NONE :
					// 不加载视图层,不传递职责链
					return Boolean.FALSE;
				case Controller.CHAIN :
					// 不加载视图层，传递职责链
					return Boolean.TRUE;
				// 半个额外功能
				default:
					Core.forward(result.toString());
					return Boolean.TRUE;
			}
		} else {
			// 不加载视图层,不传递职责链
			return Boolean.FALSE;
		}
	}

	@Override
	public void destroy() {

	}
}