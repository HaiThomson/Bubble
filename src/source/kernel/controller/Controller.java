package source.kernel.controller;

import source.kernel.Container;
import source.kernel.Core;
import source.kernel.base.Base;
import source.kernel.base.ExceptionHandler;
import source.kernel.config.GlobalConfig;
import source.kernel.log.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 过滤器是拦截器，不是资源.访问路径还是要找原来访问路径.找不到报错
 * org.apache.jasper.servlet.JspServlet.handleMissingResource File [**********] not found
 * 用拦截器做Controller的框架都不会继续向下传递职责链，而是return 向上传递职责链
 * 所以,请将其它拦截器至于该拦截器之前。
 * @since 1.7
 * @author Hai Thomson
 */
public abstract class Controller extends Base implements Filter {

	public static final String NONE = "NONE";
	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR = "ERROR";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = null;
		HttpServletResponse response = null;
		try {
			request = (HttpServletRequest) servletRequest;
			response = (HttpServletResponse) servletResponse;
		} catch (ClassCastException e) {
			throw new ServletException("non-HTTP request or response");
		}

		Object result = null;

		try {
			// 从ServletPath开始
			StringBuffer stringBuffer = new StringBuffer(request.getServletPath());
			// 刨去第一个'/'
			stringBuffer.deleteCharAt(0);
			// 刨去过滤路径 + 第二个'/'
			if (stringBuffer.length() == 0) {
				throw new ServletException(this.getClass().getName() + ". The subclass of Controller does not support filtering the root directory!");
			} else if (stringBuffer.indexOf("/") > 0) {
				stringBuffer.delete(0, stringBuffer.indexOf("/") + 1);
			} else {
				throw new ServletException(this.getClass().getName() + ". The subclass of Controller does not support filtering the root directory!");
			} // 超过一层的路径无法被鉴定 ['Controller' 的子类过滤项目根] 这种情况.

			String resName = stringBuffer.toString();
			String methodName = (resName.equals("") ? "index" : resName).replaceAll(GlobalConfig.RES_SUFFIX, "");

			Method[] methods = this.getClass().getDeclaredMethods();
			if (methods == null || methods.length == 0) {
				Logger.warn(this.getClass().getName() + ": not have method");
				filterChain.doFilter(servletRequest, servletResponse);
				return;
			} else {
				boolean found = false;
				for (int i = 0; i < methods.length; i++) {
					// System.out.println(methodName + " " + methods[i].getName());
					if (methodName.equals(methods[i].getName())) {
						// System.out.println(methodName + " " + methods[i].getName());
						found = true;
					}
				}

				if (!found) {
					filterChain.doFilter(servletRequest, servletResponse);
					return;
				} else {
					Container.creatApp(request, response);
					result = this.call(methodName);
				}
			}
		} catch (Exception e) {
			ExceptionHandler.handling(e);
		}

		// 加载视图
		if (result != null) {
			switch (result.toString()) {
				// 额外功能
				case Controller.SUCCESS :
					// 自动生成跳转路径
					// Core.loadView(""); // 默认跳转至监听目录
					return;
				case Controller.ERROR :
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return ;
				case Controller.NONE :
					// 不加载视图层
					return ;
				// 半个额外功能
				default:
					Core.forward(result.toString());
					return ;
			}
		} else {
			// 不加载视图
			return ;
		}

	}

	@Override
	public void destroy() {

	}
}