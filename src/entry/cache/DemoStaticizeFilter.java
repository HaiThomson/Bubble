package entry.cache;

import source.kernel.staticize.filter.CacheFilter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

/**
 *
 */
@WebFilter(filterName = "DemoStaticizeFilter", servletNames = "Demo")
public class DemoStaticizeFilter extends CacheFilter {
	public String getCacheKey(HttpServletRequest request) {
		StringBuilder builder = new StringBuilder(request.getRequestURI());
		if (request.getParameter("mod") != null && !"".equals(request.getParameter("mod"))) {
			builder.append("_").append(request.getQueryString());
		}
		return builder.toString();
	}
}
