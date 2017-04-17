package entry.cache;

import source.kernel.staticize.filter.CacheFilter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Hai Thomson
 */
@WebFilter(filterName = "PageStaticizeFilter", urlPatterns = "/page/*")
public class PageStaticizeFilter extends CacheFilter {

	/**
	 * 测试路径：/page/index.htm_df=32&dfd=49
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
