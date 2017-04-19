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
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * @author Hai Thomson
 */
public abstract class ActionSupport extends Base implements Servlet, ServletConfig {

	public static final String NONE = "NONE";
	public static final String SUCCESS = "SUCCESS";
	public static final String ERROR = "ERROR";

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
			Container.creatApp(request, response);
			String resName = (String) Container.app().Global.get("res");
			String methodName = (resName != null && !resName.equals("") ? resName : "index").replaceAll(GlobalConfig.RES_SUFFIX, "");

			Method[] methods = this.getClass().getDeclaredMethods();
			if (methods == null || methods.length == 0) {
				Logger.warn(this.getClass().getName() + ": not have method");
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			} else {
				boolean found = false;
				for (int i = 0; i < methods.length; i++) {
					if (methodName.equals(methods[i].getName())) {
						found = true;
					}
				}
				if (!found) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
			}

			result = this.call(methodName);
		} catch (Exception e) {
			ExceptionHandler.handling(e);
		}

		this.loadView(result);
	}

	public void loadView(Object result) throws IOException, ServletException {
		if (result != null) {
			switch (result.toString()) {
				// 额外功能
				case Controller.SUCCESS :
					// 自动生成跳转路径
					// Core.loadView(""); // 默认跳转至监听目录
					return;
				case Controller.ERROR :
					Container.app().response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
			// 不加载视图层
			return ;
		}
	}
}
