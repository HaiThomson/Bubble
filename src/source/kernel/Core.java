/**
 * 版权所有 (c) 2017， 吕绪海. 保留所有权利
 * Copyright (c) 2017, Hai Thomson. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package source.kernel;

import source.kernel.cache.DBCache;
import source.kernel.config.GlobalConfig;
import source.kernel.base.ExceptionHandler;
import source.kernel.helper.MD5Helper;
import source.kernel.helper.MapHelper;
import source.kernel.security.validate.Validation;
import source.table.common_member;
import source.table.common_syscache;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * 为Application保持单一职责,为WEB开发提供内聚支持。
 *
 * 精挑细选，个大量足，颗粒饱满，童叟无欺
 *
 * 必要的重复代码避免读者到处找方法.
 *
 * @author Hai Thomson
 */
public class Core {

	// RFC3986
	// 应用查表法实现可以提高效率
	public static String rawEncodeURL(String url) {
		if (url == null) {
			return "";
		}
		try {
			return URLEncoder.encode(url, GlobalConfig.JAVA_ENCODING).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
		} catch (UnsupportedEncodingException e) {
			ExceptionHandler.handling(e);
		}
		return "";//这句不会被执行
	}

	// RFC1738
	public static String EncodeURL(String url) {
		if (url == null) {
			return "";
		}
		try {
			return URLEncoder.encode(url, GlobalConfig.JAVA_ENCODING);
		} catch (UnsupportedEncodingException e) {
			ExceptionHandler.handling(e);
		}
		return "";//这句不会被执行
	}

	// RFC3986
	public static String rawDecodeURL(String url) {
		if (url == null) {
			return "";
		}
		try {
			return URLDecoder.decode(url, GlobalConfig.JAVA_ENCODING);
		} catch (UnsupportedEncodingException e) {
			ExceptionHandler.handling(e);
		}
		return "";//这句不会被执行
	}

	// RFC1738
	public static String decodeURL(String url) {
		if (url == null) {
			return "";
		}
		try {
			return URLDecoder.decode(url, GlobalConfig.JAVA_ENCODING);
		} catch (UnsupportedEncodingException e) {
			ExceptionHandler.handling(e);
		}
		return "";//这句不会被执行
	}

	/**
	 * 原则上应该在渲染页面前持久化session.
	 * 可以放到模板的页眉页脚中自动执行。
	 * 也可以放到加载模板前一行
	 * 也可以用过滤器，监听器（默认）
	 * @return
	 */
	public static void persistenceSession() {
		if (Container.app() != null && Container.app().initSession) {
			if(!Container.app().session.saved) {
				HashMap<String, Object> G = Container.app().Global;

				Map member = (Map) (G.get("member"));
				Container.app().session.set("groupid", member.get("groupid"));
				Container.app().session.set("username", member.get("username"));

				Container.app().session.set("actionname", G.get("actionname") );

				Container.app().session.update();

				Container.app().session.saved = true;
			}
		}
	}

	/**
	 *
	 * @param length
	 * @return
	 */
	public static String random(int length) {
		String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		Random random = new Random();
		int max = base.length();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			// 0 >= number > max
			int number = random.nextInt(max);
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static Object getGlobal(String key) {
		return Container.app().Global.get(key);
	}

	public static void setGlobal(String key, Object value) {
		Container.app().Global.put(key, value);
	}

	public static void setCookie(String key, String value) {
		Core.setCookie(key, value, -1, false);
	}

	public static void setCookie(String key, String value, int life) {
		Core.setCookie(key, value, life, false);
	}

	// life 单位秒
	public static void setCookie(String key, String value, int life, boolean httponly) {
		String enKey = GlobalConfig.COOKIE_CONFIG.COOKIE_PRE + key;

		// 使cookie失效时，value没有意义
		if(life == 0 || value.equals("")) {
			value = "";
			life = 0;
		}

		Cookie enCookie = new Cookie(enKey, value);
		Cookie cookie = new Cookie(enKey, value);
		if (!GlobalConfig.COOKIE_CONFIG.COOKIE_DOMAIN.equals("")) {
			enCookie.setDomain(GlobalConfig.COOKIE_CONFIG.COOKIE_DOMAIN);
			cookie.setDomain(GlobalConfig.COOKIE_CONFIG.COOKIE_DOMAIN);
		}
		if (!GlobalConfig.COOKIE_CONFIG.COOKIE_PATH.equals("")) {
			enCookie.setPath(GlobalConfig.COOKIE_CONFIG.COOKIE_PATH);
			cookie.setPath(GlobalConfig.COOKIE_CONFIG.COOKIE_PATH);
		}
		// 如果使用HTTPS则设置Secure
		boolean secure = Container.app().request.isSecure();
		enCookie.setSecure(secure);
		cookie.setSecure(secure);
		enCookie.setHttpOnly(httponly);
		cookie.setHttpOnly(httponly);
		enCookie.setMaxAge(life);
		cookie.setMaxAge(life);

		Container.app().response.addCookie(enCookie);
		((HashMap<String, Object>) Container.app().Global.get("cookies")).put(key, cookie);
	}

	// 占时不加密
	public static String authcode(String s, String decode) {
		return s;
	}

	public static void loadLang(String core) {
		Map lang = (Map) Container.app().Global.get("lang");
		lang.put("core", source.language.zh.Core.instance());
	}

	public static String generateFormhash() {
		return MD5Helper.md5(Container.app().Global.get("authkey").toString() + Container.app().Global.get("userid") + Container.app().Global.get("timestamp").toString().substring(0, 7)).substring(8, 16);
	}

	/**
	 * 检查FormHash, 如果FormHash值异常返回true
	 * “灌水攻击”，“多次提交”的正确含义：未经授权的表单提交
	 * 若检查机制，意义不大
	 * @return
	 */
	private boolean checkFormhash() {
		String formhash = Container.app().request.getParameter("formhash");
		if (Container.app().request.getParameter("formhash") != null && !formhash.equals(Core.generateFormhash())) {
			return true;
		} else {
			return false;
		}
	}

	public Cookie getCookie(String key) {
		return (Cookie) ((HashMap<String, Object>) Container.app().Global.get("cookies")).get(key);
	}

	public Cookie getRequestCookie(String key) {
		return (Cookie) ((HashMap<String, Object>) Container.app().Global.get("_cookies")).get(key);
	}

	/**
	 *
	 * @param relativeViemPath
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void forward(String relativeViemPath) throws ServletException, IOException {
		if (Container.app().response.isCommitted()) {
			throw new ServletException("页面已经输出，无法跳转！");
		}

		// 可修改为：不经判断直接跳转，找不到时返回404.
		// 主题模板找不到去默认模板加载属于额外功能（默认）
		// default 模板主题
		String realViemPath = "/template/" + "default" + relativeViemPath;
		if (!Container.app().request.getServletContext().getRealPath(realViemPath).equals("")) {
			Container.app().request.getRequestDispatcher(realViemPath).forward(Container.app().request, Container.app().response);
		} else if (!Container.app().request.getServletContext().getRealPath("/template/" + "default" + relativeViemPath).equals("")) {
			Container.app().request.getRequestDispatcher("/template/" + "default" + relativeViemPath).forward(Container.app().request, Container.app().response);
		} else {
			throw new FileNotFoundException("Don't find View");
		}
	}

	public static void redirect(String url) {

	}

	public static Map getUserByUid(String userid) {
		Map user = ((common_member) Container.table("common_member")).query(userid);
		// 安全检查: 传入的userid和取出的userid可能不相等
		if (user != null && userid.equals((String) user.get("userid"))) {
			// 安全检查通过，打个合格标签
			user.put("self", 1);
		}
		return user;
	}

	public static <T> T fillFormBean(T formBean) {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(formBean.getClass());
			PropertyDescriptor[] proDescrtptors = beanInfo.getPropertyDescriptors();
			if (proDescrtptors != null && proDescrtptors.length > 0) {
				for (PropertyDescriptor propDesc : proDescrtptors) {
					String parameter = Container.app().request.getParameter(propDesc.getName());
					if (parameter != null) {
						Method setMethod = propDesc.getWriteMethod();
						if (setMethod != null && setMethod.getParameterTypes().length == 1 ) {
							switch (setMethod.getParameterTypes()[0].getTypeName()) {
								case "java.lang.String" :
									setMethod.invoke(formBean, parameter);
									break;
								case "java.lang.Integer" :
									if (Validation.isInteger(parameter)) {
										setMethod.invoke(formBean, Integer.valueOf(parameter));
									} else {
										throw new NumberFormatException(parameter + " is not a valid Float! Can't chanage to " + propDesc.getName());
									}
									break;
								case "java.lang.Float" :
									if (Validation.isFloat(parameter)) {
										setMethod.invoke(formBean, Float.valueOf(parameter));
									} else {
										throw new NumberFormatException(parameter + " is not a valid Float! Can't chanage to " + propDesc.getName());
									}
									break;
								case "java.lang.Double" :
									if (Validation.isDouble(parameter)) {
										setMethod.invoke(formBean, Double.valueOf(parameter));
									} else {
										throw new NumberFormatException(parameter + " is not a valid Double! Can't chanage to " + propDesc.getName());
									}
									break;
								case "java.lang.Long" :
									if (Validation.isLong(parameter)) {
										setMethod.invoke(formBean, Long.valueOf(parameter));
									} else {
										throw new NumberFormatException(parameter + " is not a valid Long! Can't chanage to " + propDesc.getName());
									}
									break;
								case "java.lang.Boolean" :
									if (Validation.isBoolean(parameter)) {
										setMethod.invoke(formBean, Boolean.valueOf(parameter));
									} else {
										throw new IllegalArgumentException(parameter + " is not a valid Boolean! Can't chanage to " + propDesc.getName());
									}
									break;
								case "java.lang.Character" :
									if (Validation.isChar(parameter)) {
										setMethod.invoke(formBean, Character.valueOf(parameter.charAt(0)));
									} else {
										throw new IllegalArgumentException(parameter + " is not a valid Character! Can't chanage to " + propDesc.getName());
									}
									break;
								case "java.lang.Short" :
									if (Validation.isShort(parameter)) {
										setMethod.invoke(formBean, Short.valueOf(parameter));
									} else {
										throw new NumberFormatException(parameter + " is not a valid Short! Can't chanage to " + propDesc.getName());
									}
									break;
								case "java.lang.Byte" :
									if (Validation.isByte(parameter)) {
										setMethod.invoke(formBean, Byte.valueOf(parameter));
									} else {
										throw new NumberFormatException(parameter + " is not a valid Byte! Can't chanage to " + propDesc.getName());
									}
									break;
							}
						}
					} else {
						// 填充FormBean,找不到对应值就算了
					}
				}
			}
		}  catch (Exception e) {
			ExceptionHandler.handling(e);
		}
		return formBean;
	}

	/**
	 * 系统缓存被设计用来存储可动态更改的系统配置，系统核心数据。
	 * common_syscache当且应当保留一份缓存.不应有deleteSyscache方法。
	 * 用户不应将非系统缓存放入common_syscache表!这样做会产生严重性能问题！
	 * 如需使用Database缓存请参见 {@link DBCache}
	 *
	 * 必须开启事务,以免导致缓存数据出错
	 *
	 * 配合后台配置页面使用，敬请期待
	 *
	 * @param cachename
	 * @param data
	 */
	public static void saveSyscache(String cachename, Map data, int dateline) {
		((common_syscache) Container.table("common_syscache")).insert(cachename, MapHelper.serializableToBytes(data), dateline);
		Container.app().request.getServletContext().setAttribute(cachename, data);
	}

	// 载入必要的系统缓存到 ServletContext
	// 避免每次加载系统缓存时的资源消耗
	// 使用Java序列化和反序列化大Map，过于消耗资源.
	public static void loadSyscache(ServletContext servletContext) {
		Map result = ((common_syscache) Container.table("common_syscache")).fetchAll();
		if (result != null) {
			Set<String> cacheKeys = result.keySet();
			for (String key : cacheKeys) {
				if (key.equals("setting")) {
					servletContext.setAttribute(key, MapHelper.serializationFromBytes((byte[]) (((Map) result.get(key))).get("cachedata")));
				}
			}
		}
	}

	// 载入非必要系统缓存
	public static Map loadSyscache(String cachename) {
		Map result = ((common_syscache) Container.table("common_syscache")).query(cachename);
		if (result != null && (long)result.get("dateline") > System.currentTimeMillis() / 1000) {
			return MapHelper.serializationFromBytes((byte[]) result.get("data"));
		}
		return null;
	}
}
