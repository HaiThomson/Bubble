package wall;

import source.kernel.security.wall.WallFilter;

import javax.servlet.annotation.WebFilter;

/**
 *
 */
@WebFilter(filterName = "DataFolderFilter", urlPatterns = "/data/*")
public class DataFolderFilter extends WallFilter {

}
