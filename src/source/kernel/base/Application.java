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
package source.kernel.base;

import source.kernel.Core;
import source.kernel.DB;
import source.kernel.config.GlobalConfig;
import source.kernel.helper.ArraysHelper;
import source.kernel.helper.MD5Helper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hai Thomson
 */
public class Application {

	public HttpServletRequest request = null;
	public HttpServletResponse response = null;

	// 约定：Global 的自定义key全部小写.
	public HashMap<String, Object> Global = new HashMap<String, Object>();
	// Global 是 value 的引用
	protected HashMap<String, Object> value = null;
	public HashMap<String, Object> ENV = null;
	public HashMap<String, Object> config = null;
	public Session session = null;
	// 为全局提供标准时间.Unix时间戳,以秒计数
	public final Long TIMESTAMP = System.currentTimeMillis() / 1000;

	public boolean initated = false;

	public boolean initDB = true;
	public boolean initSetting = true;
	public boolean initUser = true;
	public boolean initSession = true;
	public boolean initMisc = true;

	protected Application() {}

	public static Application instance(HttpServletRequest request, HttpServletResponse response) {
		return new Application(request, response);
	}

	protected Application(HttpServletRequest request, HttpServletResponse response) {
		// 不能打乱已有初始化的先后顺序
		this.request = request;
		this.response = response;
		this.initENV();
		this.initInput();
		this.initOutput();
	}

	public void init() {
		// 不初始化DB，其它都无法运行
		if (!initated && initDB) {
			this.initDB();
			this.initSetting();
			this.initUser();
			this.initSession();
			this.initMisc();
		}
		this.initated = true;
	}

	protected void initENV() {
		this.ENV = (HashMap) this.request.getServletContext().getAttribute("ENV");
		this.request.setAttribute("Global", Global);
		this.request.setAttribute("ENV", this.ENV);
		// 反向代理记得配置代理服务器和WEB容器HTTP信息头
		this.ENV.put("server.port", this.request.getServerPort());
		this.ENV.put("context.root", this.request.getServletContext().getRealPath(""));
		this.ENV.put("site.port", this.getSitPort());
		this.ENV.put("site.url", this.getSitURL());


		// 检查请求是否由搜索引擎或菜鸟级黑客发起
		this.Global.put("is.robot", this.checkrobot());

		// userid 不能设为空，初始化Session时用到. 0 保留给Guest用户
		this.Global.put("userid", "0");
		this.Global.put("username", "");
		this.Global.put("groupid", "");
		this.Global.put("adminid", "");
		this.Global.put("sessionid", "");
		this.Global.put("formhash", "");
		this.Global.put("connectguest", false);

		this.Global.put("timestamp", this.TIMESTAMP);
		this.Global.put("starttime", System.currentTimeMillis());

		this.Global.put("clientip", this.getUserIP(this.request));
		this.Global.put("remoteport", this.request.getRemotePort());
		this.Global.put("referer", this.request.getHeader("Referer"));
		this.Global.put("is.https", this.request.isSecure());

		this.Global.put("charset", GlobalConfig.OUTPUT_CHARSET);
		this.Global.put("gzip", "");
		this.Global.put("authkey", "");

		// 占时没有plugin计划
		// this.Global.put("pluginlist", new HashMap<String, Object>());

		this.Global.put("config", GlobalConfig.instance());
		this.Global.put("member", new HashMap<String, Object>());
		this.Global.put("cookie", new HashMap<String, Object>());
		this.Global.put("session", new HashMap<String, Object>());
		this.Global.put("lang", new HashMap<String, Object>());

		this.Global.put("actionname", request.getRequestURI());

		this.value = this.Global;
	}

	protected String getSitURL() {
		String url = this.request.getRequestURL().toString();
		String uri = this.request.getRequestURI();
		return url.replace(uri, "");
	}

	protected String getSitPort() {
		String url = this.request.getRequestURL().toString();

		Pattern pattern = Pattern.compile("^(?<proto>\\w+)://[^/]+?(?<port>:\\d+)?/");
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			String port = matcher.group("port");
			if (port == null) {
				return "80";
			} else {
				return port.replace(":", "");
			}
		}
		return "";
	}

	protected String getUserIP(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if(ip != null && ip.length() > 0 && !"unKnown".equalsIgnoreCase(ip)){
			// 多次反向代理后会有多个IP值，第一个IP才是用户IP
			int index = ip.indexOf(",");
			if(index != -1){
				return ip.substring(0, index);
			}else{
				return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if(ip != null && ip.length() > 0 && !"unKnown".equalsIgnoreCase(ip)){
			return ip;
		}
		return request.getRemoteAddr();
	}

	/**
	 * 检查请求是否由机器人发起
	 * 只能检查新手和搜索引擎
	 */
	protected boolean checkrobot() {
		String[] spiders = {"spider", "bot", "sohu-search", "crawl", "slurp", "lycos", "robozilla"};
		String[] browsers = {"msie", "webkit", "mozilla", "netscape", "opera", "konqueror"};

		String useragent = this.request.getHeader("user-agent");

		if (useragent == null || useragent.equals("")) {
			return true;
		}

		useragent = useragent.toLowerCase();

		if(ArraysHelper.strpos(useragent, browsers) == -1 || useragent.contains("http://") || useragent.contains("https://")) {
			return true;
		}

		if(ArraysHelper.strpos(useragent, spiders) != -1) {
			return true;
		}

		return false;
	}

	protected void initInput() {
		// this.request.getPathInfo()不适用于Filter
		// 取得请求的资源名
		String resourceName = this.request.getPathInfo();
		if (resourceName != null) {
			this.value.put("res", this.request.getPathInfo().replaceAll("/", ""));
		} else {
			this.value.put("res", null);
		}

		// 取得请求的模块名
		String moduleName = this.request.getParameter("mod");
		if (moduleName != null) {
			this.value.put("mod", moduleName);
		} else {
			this.value.put("mod", null);
		}

		// 去除cookie前缀,并把cookie放到 Global 备用
		// Map是引用传递而不是值传递
		int prelength = GlobalConfig.COOKIE_CONFIG.COOKIE_PRE.length();
		Cookie[] cookieArray = this.request.getCookies();
		HashMap<String, Object> cookieMap = (HashMap<String, Object>) this.value.get("cookie");
		HashMap<String, Object> _cookie = new HashMap<String, Object>();
		if (cookieArray != null) {
			for (int i = 0; i < cookieArray.length; i++) {
				String oldName = cookieArray[i].getName();
				if (oldName.length() > prelength && oldName.substring(0, prelength).equals(GlobalConfig.COOKIE_CONFIG.COOKIE_PRE)) {
					cookieArray[i] = new Cookie(oldName.substring(prelength, oldName.length()), cookieArray[i].getValue());
					cookieMap.put(cookieArray[i].getName(), cookieArray[i]);
					_cookie.put(cookieArray[i].getName(), cookieArray[i]);
				}
			}
		}

		// HashMap的key区分大小写，为了区分请求Cookie和[请求Cookie + 即将放置的Cookie + 即将设置过期的Cookie]
		// HashMap是浅层拷贝
		this.value.put("_cookie", _cookie);

		// 检查sessionid
		Cookie sessionidCookie = (Cookie) cookieMap.get("sessionid");
		if (sessionidCookie != null) {
			this.value.put("sessionid", sessionidCookie.getValue());
		} else {
			this.value.put("sessionid", "");
			sessionidCookie = new Cookie("sessionid", "");
			cookieMap.put("sessionid", sessionidCookie);
		}

		if(cookieMap.get("saltkey") == null) {
			this.setCookie("saltkey", Core.random(8), 86400 * 30, "", true);
		}

		this.value.put("authkey", MD5Helper.md5(GlobalConfig.SECURITY_AUTHKEY + ((Cookie) cookieMap.get("saltkey")).getValue()));

	}

	protected void initOutput() {
	}

	protected void initDB() {
		try {
			DB.init(GlobalConfig.DBDRIVER_PATH, GlobalConfig.TABLE_PREFIX);
		} catch (SQLException e) {
			ExceptionHandler.handling(e);
		}
	}

	/**
	 * 如果Core.loadSyscache.加载系统功能配置到ServletContext放在项目初始化时运行.
	 * 那么系统初始化时就要为项目初始化进程单独启用DB支持。可以，但有风险！
	 * 为项目进行初始化的线程从哪来到哪去，不知道!要研究每种WEB容器的实现方式！
	 */
	protected void initSetting() {
		if (this.initSetting) {
			if (this.request.getServletContext().getAttribute("setting") != null) {
				this.value.put("setting", this.request.getServletContext().getAttribute("setting"));
			} else {
				Core.loadSyscache(this.request.getServletContext());
				if (this.request.getServletContext().getAttribute("setting") == null) {
					// 如果系统功能配置功能丢失，而且无法使用管理界面重置.
					// 在测试用例"common_syscacheTest"有一份手动写入系统功能配置的例子
					throw new RuntimeException("系统功能配置丢失！");
				} else {
					this.value.put("setting", this.request.getServletContext().getAttribute("setting"));
				}
			}
		}
	}

	protected void initUser() {
		if(this.initUser) {
			String password = null;
			String userid = null;
			Map user = null;

			// 确定类型的map可以更换Object类型到指定类型
			HashMap<String, Object> cookie = (HashMap<String, Object>) this.value.get("cookie");
			Cookie authCookie = (Cookie) cookie.get("auth");
			if(authCookie != null) {
				// authcode加密势在必行
				// cookie.value 特殊字符要处理
				String[] authString = Core.authcode(authCookie.getValue(), "DECODE").split("\t");
				if (authString.length >= 2 ){
					password = authString[0];
					userid = authString[1];
				}
			}

			if(userid != null) {
				user = Core.getUserByUid(userid);
			}

			if(user != null && ((String)user.get("password")).equals(password)) {
				this.value.put("member", user);
			} else {
				// 未满足条件置空, 为后续可能的增加的代码做准备
				user = null;
				this.initGuest();
			}

		} else {
			this.initGuest();
		}

		HashMap<String, Object> member = (HashMap<String, Object>)this.value.get("member");
		this.value.put("groupid", member.get("groupid"));

		HashMap<String, Object> cookie = (HashMap<String, Object>) this.value.get("cookie");
		if (cookie.get("lastvisit") == null) {
			member.put("lastvisit", this.TIMESTAMP - 3600);
			Core.setCookie("lastvisit", Long.toString(this.TIMESTAMP - 3600), 86400 * 30);
		} else {
			member.put("lastvisit", ((Cookie)cookie.get("lastvisit")).getValue());
		}

		this.value.put("userid", member.get("userid"));
		this.value.put("username", member.get("username"));
		this.value.put("groupid", member.get("groupid"));
	}

	protected void initGuest() {
		this.Global.put("connectguest", true);

		HashMap<String, Object> member = new HashMap<String, Object>();
		member.put("userid", "0");
		// "guest" or ""
		member.put("username", "guest");
		member.put("groupid", "0");
		member.put("timeoffset", 36000);
		this.value.put("member", member);
	}

	protected void initSession() {
		if (this.initSession) {
			this.session = new Session();

			this.session.init(((Cookie) ((HashMap) this.value.get("cookie")).get("sessionid")).getValue(), (String) this.value.get("clientip"), (String) this.value.get("userid"));
			this.value.put("sessionid", this.session.sessionid);
			this.value.put("session", this.session.value);

			if (this.value.get("sessionid") != null && !((String) this.value.get("sessionid")).equals(((Cookie) ((HashMap) this.value.get("cookie")).get("sessionid")).getValue())) {
				// 有效时间一小时，改读配置和动态配置
				Core.setCookie("sessionid", (String) this.value.get("sessionid"), 60 * 60);
			}

			// session不自动续时，因为和登录cookie无关
			// session表清理需要额外的线程或计划任务处理

			// 5分钟内算一次浏览
			if (this.value.get("userid") != null && (this.session.isnew || ((Long) this.session.get("lastactivity") + 60 * 5) < this.TIMESTAMP)) {
				this.session.set("lastactivity", this.TIMESTAMP);
			}
		}
	}

	/**
	 * 	基础参数初始化 Initialize miscellaneous(混杂的,各种各样的) variables
	 */
	protected void initMisc() {
		if(GlobalConfig.SECURITY_URLXSSDEFEND) {
			if (this.checkXSS()) {
				ExceptionHandler.handling(new RuntimeException("检测到XSS攻击, 准备返还对方500"));
			}
		}

		if(!this.initMisc) {
			return;
		}

		// 加载Application语言包，属于公共需求
		Core.loadLang("core");

		String lastact = TIMESTAMP + this.request.getRequestURI();
		Core.setCookie("lastact", lastact, 86400);
	}

	protected boolean checkXSS() {
		return false;
	}

	/**
	 * 在执行Application构造方法时Container.app()无法取得有效的对象。
	 * 这将导致Core内的setCookie方法无法运行.必须调用Application自己的setCookie方法才能解决这个问题.
	 */
	protected void setCookie(String key, String value, int life, String prefix, boolean httponly) {
		String enKey = (prefix.equals("") ? GlobalConfig.COOKIE_CONFIG.COOKIE_PRE : prefix) + key;

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
		//如果使用HTTPS则设置Secure
		boolean secure = this.request.isSecure();
		enCookie.setSecure(secure);
		cookie.setSecure(secure);
		enCookie.setHttpOnly(httponly);
		cookie.setHttpOnly(httponly);
		enCookie.setMaxAge(life);
		cookie.setMaxAge(life);

		this.response.addCookie(enCookie);
		((HashMap<String, Object>)this.Global.get("cookie")).put(key, cookie);
	}
}
