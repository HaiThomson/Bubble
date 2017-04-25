package source.kernel.controller;

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
import java.lang.reflect.Parameter;
import java.sql.SQLException;
import java.util.Enumeration;

/**
 * 过滤器是拦截器，不是资源.访问路径还是要找原来访问路径.找不到报错
 * org.apache.jasper.servlet.JspServlet.handleMissingResource File [**********] not found
 * 用拦截器做Controller的框架都不会继续向下传递职责链，而是return 向上传递职责链
 * 所以,请将其它拦截器至于该拦截器之前。
 *
 * Controller基于Filter, 遇到自己不能处理的继续向下传递.Action没这功能.
 * 推荐使用Controller，可以在一个ServletPath下构建一组 .htm, 一组 .json.
 * Action只可以构建一组 .htm + N个.json
 *
 * @since 1.7
 * @author Hai Thomson
 */
public  class Controller extends Base implements Filter, FilterConfig {

	public static final String NONE = "NONE";
	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR = "ERROR";

	protected String RES_SUFFIX = GlobalConfig.RES_SUFFIX;

	private transient FilterConfig config = null;

	@Override
	public String getFilterName() {
		return this.config.getFilterName();
	}

	@Override
	public ServletContext getServletContext() {
		return this.config.getServletContext();
	}

	@Override
	public String getInitParameter(String name) {
		return this.config.getInitParameter(name);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return this.config.getInitParameterNames();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		this.config = filterConfig;

		String config_suffix = filterConfig.getInitParameter("suffix");
		if (config_suffix != null) {
			this.RES_SUFFIX = config_suffix;
		}

		this._init();
	}

	protected void _init() {

	}

	public FilterConfig getFilterConfig() {
		return this.config;
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
			String methodName = (resName.equals("") ? "index" : resName).replaceAll(this.RES_SUFFIX, "");

			Method[] methods = this.getClass().getDeclaredMethods();
			if (methods == null || methods.length == 0) {
				// Logger.warn(this.getClass().getName() + ": not have method");
				filterChain.doFilter(servletRequest, servletResponse);
				return;
			} else {
				boolean found = false;
				int subscript = 0;
				for (int i = 0; i < methods.length; i++) {
					// System.out.println(methodName + " " + methods[i].getName());
					if (methodName.equals(methods[i].getName()) && methods[subscript].getName().charAt(0) != '_') {
						// System.out.println(methodName + " " + methods[i].getName());
						found = true;
						subscript = i;
					}
				}

				if (!found) {
					filterChain.doFilter(servletRequest, servletResponse);
					return;
				} else {
					this._createApp(request, response);
					this._initApp();

					if (!this._runBeforeAspect(request, response, methodName)) {
						return;
					}

					int parameterCount = methods[subscript].getParameterCount();
					if (parameterCount == 0) {
						result = this.call(methodName);
					} else {
						Parameter[] parameters = methods[subscript].getParameters();
						Object[] parameterValue = null;
						if (parameters != null && parameters.length > 0) {
							parameterValue = new Object[parameters.length];

							for (int i = 0; i < parameters.length; i++) {
								Class<?> type = parameters[i].getType();
								parameterValue[i] = Core.fillFormBean(type.newInstance());
							}

							result = this.call(methodName, parameterValue);
						} else {
							result = this.call(methodName);
						}
					}
				}
			}

			// 加载视图
			if (result != null) {
				switch (result.toString()) {
					case Controller.SUCCESS :
						// 额外功能,根据请求路径自动生成跳转路径.慎用
						Core.forward(request.getServletPath());
						break;
					case Controller.ERROR :
						response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						break;
					case Controller.NONE :
						// 不加载视图层
						break;
					default:
						Core.forward(result.toString());
						break;
				}
			} else {
				// Nothin to do
			}

			if (!this._runAfterAspect(request, response, methodName)) {
				return;
			}

		} catch (Exception e) {
			ExceptionHandler.handling(e);
		}
	}

	protected void _createApp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return;
	}

	protected void _initApp() throws SQLException, IOException, ClassNotFoundException {
		return;
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

	@Override
	public void destroy() {
		this._destroy();
	}

	protected void _destroy() {

	}
}