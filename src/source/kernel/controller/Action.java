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
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * @author Hai Thomson
 */
public abstract class Action extends Base implements Servlet, ServletConfig {

	public static final String NONE = "NONE";
	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR = "ERROR";

	protected String RES_SUFFIX = GlobalConfig.RES_SUFFIX;

	private static final ResourceBundle lStrings = ResourceBundle.getBundle("javax.servlet.http.LocalStrings");

	private transient ServletConfig config;

	public String getInitParameter(String name) {
		return this.getServletConfig().getInitParameter(name);
	}

	public Enumeration<String> getInitParameterNames() {
		return this.getServletConfig().getInitParameterNames();
	}

	public ServletConfig getServletConfig() {
		return this.config;
	}

	public ServletContext getServletContext() {
		return this.getServletConfig().getServletContext();
	}

	public String getServletInfo() {
		return "";
	}

	public void destroy() {
	}

	public void init(ServletConfig config) throws ServletException {
		this.config = config;

		String config_suffix = config.getInitParameter("suffix");
		if (config_suffix != null) {
			this.RES_SUFFIX = config_suffix;
		}

		this.init();
	}

	public void init() throws ServletException {
	}

	public void log(String msg) {
		this.getServletContext().log(this.getServletName() + ": " + msg);
	}

	public void log(String message, Throwable t) {
		this.getServletContext().log(this.getServletName() + ": " + message, t);
	}

	public String getServletName() {
		return this.config.getServletName();
	}

	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		HttpServletRequest request = null;
		HttpServletResponse response = null;
		try {
			request = (HttpServletRequest)req;
			response = (HttpServletResponse)res;
		} catch (ClassCastException e) {
			throw new ServletException("non-HTTP request or response");
		}

		String method = request.getMethod();
		if (method.equals("GET") || method.equals("POST")) {
			this.service(request, response);
		} else {
			String errMsg = lStrings.getString("http.method_not_implemented");
			Object[] errArgs = new Object[]{method};
			errMsg = MessageFormat.format(errMsg, errArgs);
			// 501
			response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, errMsg);
		}
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Object result = null;

		try {
			String resName = Core.getRequestResource(request);
			String methodName = (resName != null && !resName.equals("") ? resName : "index").replaceAll(this.RES_SUFFIX, "");

			Method[] methods = this.getClass().getDeclaredMethods();
			if (methods == null || methods.length == 0) {
				// Logger.warn(this.getClass().getName() + ": not have method");
				// Servlet再次传给自己？只能响应404
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			} else {
				boolean found = false;
				int subscript = 0;
				for (int i = 0; i < methods.length; i++) {
					if (methodName.equals(methods[i].getName()) && methods[subscript].getName().charAt(0) != '_') {
						found = true;
						subscript = i;
					}
				}

				if (!found) {
					// Servlet再次传给自己？只能响应404
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
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
}
