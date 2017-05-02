package wall;

import source.kernel.security.wall.WallFilter;

import javax.servlet.annotation.WebFilter;

/**
 *
 */
@WebFilter(filterName = "ConfigFolderFilter", urlPatterns = "/config/*")
public class ConfigFolderFilter extends WallFilter {

}
