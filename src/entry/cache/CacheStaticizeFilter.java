package entry.cache;

import source.kernel.staticize.filter.CacheFilterByFileCache;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@WebFilter(filterName = "CacheStaticizeFilter", urlPatterns = "/cache/*")
public class CacheStaticizeFilter extends CacheFilterByFileCache {

	/**
	 * 测试路径： /cache/index.htm_df=32&dfd=49
	 * @param request
	 * @return
	 */
	public String getCacheKey(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder(request.getRequestURI());
		if (request.getQueryString() != null && !"".equals(request.getQueryString())) {
			builder.append("_").append(request.getQueryString());
		}
		return builder.toString();
	}
}
