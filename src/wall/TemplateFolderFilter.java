package wall;

import source.kernel.security.wall.WallFilter;

import javax.servlet.annotation.WebFilter;

/**
 *
 */
@WebFilter(filterName = "TemplateFolderFilter", urlPatterns = "/template/*")
public class TemplateFolderFilter extends WallFilter {

}
