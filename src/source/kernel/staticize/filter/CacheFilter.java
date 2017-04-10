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
package source.kernel.staticize.filter;

import source.kernel.base.ExceptionHandler;
import source.kernel.cache.MemoryCache;
import source.kernel.helper.StringHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.List;

/**
 * @author Hai Thomson
 */
public class CacheFilter implements Filter {
	// Header
	public static final String HEADER_LAST_MODIFIED = "Last-Modified";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
	public static final String HEADER_EXPIRES = "Expires";
	public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
	public static final String HEADER_CACHE_CONTROL = "Cache-Control";
	public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";

	// Fragment parameter
	public static final int FRAGMENT_AUTODETECT = -1;
	public static final int FRAGMENT_NO = 0;
	public static final int FRAGMENT_YES = 1;

	// No cache parameter
	public static final int NOCACHE_OFF = 0;
	public static final int NOCACHE_SESSION_ID_IN_URL = 1;

	// Last Modified parameter
	public static final long LAST_MODIFIED_OFF = 0L;
	public static final long LAST_MODIFIED_ON = 1L;
	public static final long LAST_MODIFIED_INITIAL = 1L;

	// Expires parameter
	public static final long EXPIRES_OFF = 0L;
	public static final long EXPIRES_ON = 1L;
	public static final long EXPIRES_TIME = 1L;

	// Cache Control
	public static final long MAX_AGE_NO_INIT = Long.MIN_VALUE;
	public static final long MAX_AGE_TIME = Long.MAX_VALUE;

	// 一个Filter可在web.xml多次配置，生成多个实例。这里避免的不是这种情况
	// 防止request重复进入同一个静态化过滤器标示
	// 标示头
	private final static String REQUEST_FILTERED = "__cache_filtered__";
	private String requestFiltered = null;

	// filter variables
	private FilterConfig config = null;

	// 下面的控制参数可由FilterConfig提供也可由子类提供

	// 关键控制参数！单位：秒
	// 页面缓存在MemoryCache的有效时长也使用了该值
	// 缓存页面刷新时间间隔 - 默认 3600秒
	private long time = 60L * 60L;
	// private long time = 0L;

	// cache作用域 - 默认值 APPLICATION.占时只支持非PageContext.SESSION_SCOPE的情况. 针对Session的情况，需要架构师确认Session选型（Servlet Session | bubble Session）
	private int cacheScope = PageContext.APPLICATION_SCOPE;

	// 如果CacheFilter过滤的URL，Servlet，JSP等资源被内部引用或者request为内部跳转，资源仍可被缓存但不应再设置HTTP头信息。头信息应由包含者提供。
	// 默认进行判断，如果是上述情况，则不再设置头信息。
	// 可以用 FRAGMENT_NO 设置不再进行判断，总是设置头信息，可以用 FRAGMENT_YES 设置不再进行判断，总是不设置头信息。
	private int fragment = CacheFilter.FRAGMENT_AUTODETECT;

	// 判断是否可以Cache - 默认开启
	// 如果路过滤器的 request 不是 HttpServletRequest 的实例则不予cache.isCacheable方法内部判断
	// 对出现在列表中的HTTP 请求方式 不予cache
	private List disableCacheOnMethods = null;
	// nocache 针对SessionID in URL！ - 默认开启. NOCACHE_OFF 支持SessionID in URL！ NOCACHE_SESSION_ID_IN_URL 不支持SessionID in URL!
	// 有赖于getCacheKey() Key生成算法
	private int nocache = CacheFilter.NOCACHE_OFF;

	// HTTP头信息控制,默认开启
	// 不是 LAST_MODIFIED_INITIAL 不设置头信息
	private long lastModified = CacheFilter.LAST_MODIFIED_INITIAL;
	// HTTP EXPIRES 头信息控制,默认开启
	// 不是 EXPIRES_TIME 不设置头信息
	private long expires = CacheFilter.EXPIRES_TIME;
	// 表示当访问此网页后的 abs(cacheControlMaxAge) 秒内不会去再次访问服务器.配文文件请使用非负数，代码请使用非正数
	// 默认值60秒
	private long cacheControlMaxAge = -60L;

	/**
	 * Filter clean-up
	 */
	public void destroy() {
		// Nothing to do
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		// System.out.println("Filter" + "\t" + Thread.currentThread().getName() + "\t" + ((HttpServletRequest)req).getRequestURL());

		if (isFilteredBefore(req) || !isCacheableInternal(req)) {
			chain.doFilter(req, res);
			return;
		}

		HttpServletRequest request = (HttpServletRequest) req;

		// 避免重复调用
		if (requestFiltered != null) {
			request.setAttribute(REQUEST_FILTERED, Boolean.TRUE);
		} else {
			// 继承CacheFilter可以什么都不写.如覆盖init方法，则init内要调用CacheFilter的makeRequestFiltered方法！
			ExceptionHandler.handling(new RuntimeException("No Zuo No die!"));
		}

		// 检查响应是否被包含于另一个页面
		boolean fragmentRequest = isFragment(request);

		// 生成Key
		String key = this.getCacheKey(request).replace("/", "_");
		// 尝试从内存缓存取得数据
		ResponseContent responseContent = (ResponseContent) MemoryCache.get(key);
		// 如果从内存取得静态过的页面则输出页面
		if (responseContent != null) {
			boolean acceptsGZip = false;
			if ((!fragmentRequest) && (lastModified != LAST_MODIFIED_OFF)) {
				// 如果没有相关头信息，则返回 -1
				long clientLastModified = request.getDateHeader(HEADER_IF_MODIFIED_SINCE);

				// 回复HTTP状态304
				if ((clientLastModified != -1) && (clientLastModified >= responseContent.getLastModified())) {
					((HttpServletResponse) res).setStatus(HttpServletResponse.SC_NOT_MODIFIED);
					return;
				}

				acceptsGZip = responseContent.isContentGZiped() && acceptsGZipEncoding(request);
			}

			responseContent.writeTo(res, fragmentRequest, acceptsGZip);
			return;
		}

		// 用CacheHttpServletResponseWrapper来代替HttpServletResponse，用于记录HttpServletResponse输出的内容。
		CacheHttpServletResponseWrapper cacheResponse = new CacheHttpServletResponseWrapper((HttpServletResponse) res, fragmentRequest, time * 1000L, lastModified, expires, cacheControlMaxAge);

		chain.doFilter(request, cacheResponse);

		cacheResponse.flushBuffer();
		if (isCacheableInternal(cacheResponse)) {
			// 缓存响应结果
			// 时间单位 ms
			// 缓存提前失效 1000 毫秒
			MemoryCache.set(key, cacheResponse.getContent(), this.time * 1000L - 1000L);
		}
	}

	public String getCacheKey(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder(request.getRequestURI());
		if (request.getQueryString() != null && !"".equals(request.getQueryString())) {
			builder.append("_").append(request.getQueryString());
		}
		return builder.toString();
	}

	/**
	 * Checks if the request is a fragment in a page.
	 *
	 * According to Java Servlet API 2.2 (8.2.1 Dispatching Requests, Included
	 * Request Parameters), when a servlet is being used from within an include,
	 * the attribute <code>javax.servlet.include.request_uri</code> is set.
	 * According to Java Servlet API 2.3 this is excepted for servlets obtained
	 * by using the getNamedDispatcher method.
	 *
	 * @param request the to be handled request
	 * @return true if the request is a fragment in a page
	 */
	public boolean isFragment(HttpServletRequest request) {
		if (fragment == FRAGMENT_AUTODETECT) {
			return request.getAttribute("javax.servlet.include.request_uri") != null;
		} else {
			return (fragment == FRAGMENT_NO) ? false : true;
		}
	}

	/**
	 * Checks if the request was filtered before, so
	 * guarantees to be executed once per request. You
	 * can override this methods to define a more specific
	 * behaviour.
	 *
	 * @param request checks if the request was filtered before.
	 * @return true if it is the first execution
	 */
	public boolean isFilteredBefore(ServletRequest request) {
		return request.getAttribute(requestFiltered) != null;
	}

	/**
	 * isCacheableInternal gurarantees that the log information is correct.
	 *
	 * @param request The servlet request
	 * @return Returns a boolean indicating if the request can be cached or not.
	 */
	private final boolean isCacheableInternal(ServletRequest request) {
		final boolean cacheable = isCacheable(request);
		return cacheable;
	}

	/**
	 * isCacheable is a method allowing a subclass to decide if a request is
	 * cachable or not.
	 *
	 * @param request The servlet request
	 * @return Returns a boolean indicating if the request can be cached or not.
	 */
	public boolean isCacheable(ServletRequest request) {
		boolean cacheable = request instanceof HttpServletRequest;

		if (cacheable) {
			HttpServletRequest requestHttp = (HttpServletRequest) request;
			// 某些HTTP请求方式不被支持
			if ((disableCacheOnMethods != null) && (disableCacheOnMethods.contains(requestHttp.getMethod()))) {
				return false;
			}
			if (nocache == CacheFilter.NOCACHE_SESSION_ID_IN_URL) {
				// 不支持,session in url
				cacheable = !requestHttp.isRequestedSessionIdFromURL();
			}
		}

		return cacheable;
	}

	/**
	 * isCacheableInternal gurarantees that the log information is correct.
	 *
	 * @param cacheResponse the HTTP servlet response
	 * @return Returns a boolean indicating if the response can be cached or not.
	 */
	private final boolean isCacheableInternal(CacheHttpServletResponseWrapper cacheResponse) {
		final boolean cacheable = isCacheable(cacheResponse);
		return cacheable;
	}

	/**
	 * isCacheable is a method allowing subclass to decide if a response is
	 * cachable or not.
	 *
	 * @param cacheResponse the HTTP servlet response
	 * @return Returns a boolean indicating if the response can be cached or not.
	 */
	public boolean isCacheable(CacheHttpServletResponseWrapper cacheResponse) {
		// 页面响应200，才会被缓存
		return cacheResponse.getStatus() == HttpServletResponse.SC_OK;
	}

	/**
	 * Check if the client browser support gzip compression.
	 *
	 * @param request the http request
	 * @return true if client browser supports GZIP
	 */
	public boolean acceptsGZipEncoding(HttpServletRequest request) {
		String acceptEncoding = request.getHeader(HEADER_ACCEPT_ENCODING);
		return  (acceptEncoding != null) && (acceptEncoding.indexOf("gzip") != -1);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		config = filterConfig;
		this.makeRequestFiltered(filterConfig);

		// filter parameter time
		String timeParam = config.getInitParameter("time");
		if (timeParam != null) {
			try {
				setTime(Integer.parseInt(timeParam));
			} catch (NumberFormatException nfe) {
				ExceptionHandler.handling(new NumberFormatException("Unexpected value for the init parameter 'time', defaulting to one hour. Message=" + nfe.getMessage()));
			}
		}

		// filter parameter scope
		String scopeParam = config.getInitParameter("scope");
		if (scopeParam != null) {
			if ("session".equalsIgnoreCase(scopeParam)) {
				setCacheScope(PageContext.SESSION_SCOPE);
			} else if ("application".equalsIgnoreCase(scopeParam)) {
				setCacheScope(PageContext.APPLICATION_SCOPE);
			} else {
				ExceptionHandler.handling(new NumberFormatException("Wrong value '" + scopeParam + "' for init parameter 'scope', defaulting to 'application'."));
			}

		}

		// filter parameter fragment
		String fragmentParam = config.getInitParameter("fragment");
		if (fragmentParam != null) {
			if ("no".equalsIgnoreCase(fragmentParam)) {
				setFragment(FRAGMENT_NO);
			} else if ("yes".equalsIgnoreCase(fragmentParam)) {
				setFragment(FRAGMENT_YES);
			} else if ("auto".equalsIgnoreCase(fragmentParam)) {
				setFragment(FRAGMENT_AUTODETECT);
			} else {
				ExceptionHandler.handling(new NumberFormatException("Wrong value '" + fragmentParam + "' for init parameter 'fragment', defaulting to 'auto detect'."));
			}
		}

		// filter parameter nocache
		String nocacheParam = config.getInitParameter("nocache");
		if (nocacheParam != null) {
			if ("off".equalsIgnoreCase(nocacheParam)) {
				nocache = NOCACHE_OFF;
			} else if ("sessionIdInURL".equalsIgnoreCase(nocacheParam)) {
				nocache = NOCACHE_SESSION_ID_IN_URL;
			} else {
				ExceptionHandler.handling(new NumberFormatException("Wrong value '" + nocacheParam + "' for init parameter 'nocache', defaulting to 'off'."));
			}
		}

		// filter parameter last modified
		String lastModifiedParam = config.getInitParameter("lastModified");
		if (lastModifiedParam != null) {
			if ("off".equalsIgnoreCase(lastModifiedParam)) {
				lastModified = LAST_MODIFIED_OFF;
			} else if ("on".equalsIgnoreCase(lastModifiedParam)) {
				lastModified = LAST_MODIFIED_ON;
			} else if ("initial".equalsIgnoreCase(lastModifiedParam)) {
				lastModified = LAST_MODIFIED_INITIAL;
			} else {
				ExceptionHandler.handling(new NumberFormatException("OSCache: Wrong value '" + lastModifiedParam + "' for init parameter 'lastModified', defaulting to 'initial'."));
			}
		}

		// filter parameter expires
		String expiresParam = config.getInitParameter("expires");
		if (expiresParam != null) {
			if ("off".equalsIgnoreCase(expiresParam)) {
				setExpires(EXPIRES_OFF);
			} else if ("on".equalsIgnoreCase(expiresParam)) {
				setExpires(EXPIRES_ON);
			} else if ("time".equalsIgnoreCase(expiresParam)) {
				setExpires(EXPIRES_TIME);
			} else {
				ExceptionHandler.handling(new NumberFormatException("OSCache: Wrong value '" + expiresParam + "' for init parameter 'expires', defaulting to 'on'."));
			}
		}

		// filter parameter Cache-Control
		String cacheControlMaxAgeParam = config.getInitParameter("max-age");
		if (cacheControlMaxAgeParam != null) {
			if (cacheControlMaxAgeParam.equalsIgnoreCase("no init")) {
				setCacheControlMaxAge(MAX_AGE_NO_INIT);
			} else if (cacheControlMaxAgeParam.equalsIgnoreCase("time")) {
				setCacheControlMaxAge(MAX_AGE_TIME);
			} else {
				try {
					setCacheControlMaxAge(Long.parseLong(cacheControlMaxAgeParam));
				} catch (NumberFormatException nfe) {
					ExceptionHandler.handling(new NumberFormatException("OSCache: Unexpected value for the init parameter 'max-age', defaulting to '60'. Message=" + nfe.getMessage()));
				}
			}
		}

		// filter parameter scope
		String disableCacheOnMethodsParam = config.getInitParameter("disableCacheOnMethods");
		if (StringHelper.hasLength(disableCacheOnMethodsParam)) {
			disableCacheOnMethods = StringHelper.split(disableCacheOnMethodsParam, ',');
			ExceptionHandler.handling(new NumberFormatException("Wrong value '" + disableCacheOnMethodsParam + "' for init parameter 'disableCacheOnMethods', defaulting to 'null'."));
		}
	}

	protected void makeRequestFiltered(FilterConfig filterConfig) {
		if (config != null && config.getFilterName() != null && !config.getFilterName().equals("")) {
			requestFiltered = CacheFilter.REQUEST_FILTERED + config.getFilterName();
		} else {
			// 不被覆盖的情况下，this.getClass().getSimpleName() 值为 “子类名”
			requestFiltered = CacheFilter.REQUEST_FILTERED + this.getClass().getSimpleName();
		}
	}

	/**
	 * @return the max-age of the cache control
	 */
	public long getCacheControlMaxAge() {
		if ((cacheControlMaxAge == MAX_AGE_NO_INIT) || (cacheControlMaxAge == MAX_AGE_TIME)) {
			return cacheControlMaxAge;
		}
		return - cacheControlMaxAge;
	}

	/**
	 * <b>max-age</b> - defines the cache control response header max-age. Acceptable values are
	 * <code>MAX_AGE_NO_INIT</code> for don't initializing the max-age cache control,
	 * <code>MAX_AGE_TIME</code> the max-age information will be based on the time parameter and creation time of the content (expiration timestamp minus current timestamp), and
	 * <code>[positive integer]</code> value constant in seconds to be set in every response, the default value is 60.
	 *
	 * @param cacheControlMaxAge the cacheControlMaxAge to set
	 */
	public void setCacheControlMaxAge(long cacheControlMaxAge) {
		if ((cacheControlMaxAge == MAX_AGE_NO_INIT) || (cacheControlMaxAge == MAX_AGE_TIME)) {
			this.cacheControlMaxAge = cacheControlMaxAge;
		} else if (cacheControlMaxAge >= 0) {
			// declare the cache control as a constant
			// TODO check if this value can be stored as a positive long without changing it
			this.cacheControlMaxAge = - cacheControlMaxAge;
		} else {
			ExceptionHandler.handling(new NumberFormatException("OSCache: 'max-age' must be at least a positive integer, defaulting to '60'. "));
			this.cacheControlMaxAge = -60;
		}
	}

	/**
	 * Returns PageContext.APPLICATION_SCOPE or PageContext.SESSION_SCOPE.
	 * @return the cache scope
	 */
	public int getCacheScope() {
		return cacheScope;
	}

	/**
	 * <b>scope</b> - the default scope to cache content. Acceptable values
	 * are <code>PageContext.APPLICATION_SCOPE</code> (default) and <code>PageContext.SESSION_SCOPE</code>.
	 *
	 * @param cacheScope the cacheScope to set
	 */
	public void setCacheScope(int cacheScope) {
		if ((cacheScope != PageContext.APPLICATION_SCOPE) && (cacheScope != PageContext.SESSION_SCOPE))
			throw new IllegalArgumentException("Acceptable values for cache scope are PageContext.APPLICATION_SCOPE or PageContext.SESSION_SCOPE");
		this.cacheScope = cacheScope;
	}

	/**
	 * @return the expires header
	 */
	public long getExpires() {
		return expires;
	}

	/**
	 * <b>expires</b> - defines if the expires header will be sent in the response. Acceptable values are
	 * <code>EXPIRES_OFF</code> for don't sending the header, even it is set in the filter chain,
	 * <code>EXPIRES_ON</code> (default) for sending it if it is set in the filter chain and
	 * <code>EXPIRES_TIME</code> the expires information will be intialized based on the time parameter and creation time of the content.
	 *
	 * @param expires the expires to set
	 */
	public void setExpires(long expires) {
		if ((expires < EXPIRES_TIME) || (expires > EXPIRES_ON)) throw new IllegalArgumentException("Expires value out of range.");
		this.expires = expires;
	}

	/**
	 * @return the fragment
	 */
	public int getFragment() {
		return fragment;
	}

	/**
	 * <b>fragment</b> - defines if this filter handles fragments of a page. Acceptable values
	 * are <code>FRAGMENT_AUTODETECT</code> (default) for auto detect, <code>FRAGMENT_NO</code> and <code>FRAGMENT_YES</code>.
	 *
	 * @param fragment the fragment to set
	 */
	public void setFragment(int fragment) {
		if ((fragment < FRAGMENT_AUTODETECT) || (fragment > FRAGMENT_YES)) throw new IllegalArgumentException("Fragment value out of range.");
		this.fragment = fragment;
	}

	/**
	 * @return the lastModified
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * <b>lastModified</b> - defines if the last modified header will be sent in the response. Acceptable values are
	 * <code>LAST_MODIFIED_OFF</code> for don't sending the header, even it is set in the filter chain,
	 * <code>LAST_MODIFIED_ON</code> for sending it if it is set in the filter chain and
	 * <code>LAST_MODIFIED_INITIAL</code> (default) the last modified information will be set based on the current time and changes are allowed.
	 *
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(long lastModified) {
		if ((lastModified < LAST_MODIFIED_INITIAL) || (lastModified > LAST_MODIFIED_ON)) throw new IllegalArgumentException("LastModified value out of range.");
		this.lastModified = lastModified;
	}

	/**
	 * @return the nocache
	 */
	public int getNocache() {
		return nocache;
	}

	/**
	 * <b>nocache</b> - defines which objects shouldn't be cached. Acceptable values
	 * are <code>NOCACHE_OFF</code> (default) and <code>NOCACHE_SESSION_ID_IN_URL</code> if the session id is
	 * contained in the URL.
	 *
	 * @param nocache the nocache to set
	 */
	public void setNocache(int nocache) {
		if ((nocache < NOCACHE_OFF) || (nocache > NOCACHE_SESSION_ID_IN_URL)) throw new IllegalArgumentException("Nocache value out of range.");
		this.nocache = nocache;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * <b>time</b> - the default time (in seconds) to cache content for. The default
	 * value is 3600 seconds (one hour). Specifying -1 (indefinite expiry) as the cache
	 * time will ensure a content does not become stale until it is either explicitly
	 * flushed or the expires refresh policy causes the entry to expire.
	 *
	 * @param time the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * @link http://java.sun.com/j2ee/sdk_1.3/techdocs/api/javax/servlet/http/HttpServletRequest.html#getMethod()
	 * @return the list of http method names for which cacheing should be disabled
	 */
	public List getDisableCacheOnMethods() {
		return disableCacheOnMethods;
	}

	/**
	 * <b>disableCacheOnMethods</b> - Defines the http method name for which cacheing should be disabled.
	 * The default value is <code>null</code> for cacheing all requests without regarding the method name.
	 * @link http://java.sun.com/j2ee/sdk_1.3/techdocs/api/javax/servlet/http/HttpServletRequest.html#getMethod()
	 * @param disableCacheOnMethods the list of http method names for which cacheing should be disabled
	 */
	public void setDisableCacheOnMethods(List disableCacheOnMethods) {
		this.disableCacheOnMethods = disableCacheOnMethods;
	}
}
