package source.kernel.view.data.json;

import source.kernel.config.GlobalConfig;
import source.kernel.serialization.json.Json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class JsonView {
	public static void output(Object object, HttpServletRequest request, HttpServletResponse response) throws IOException {
		/*if (request.getHeader("user-agent").toUpperCase().contains("ie") || request.getHeader("user-agent").toLowerCase().contains("trident")) {
			response.setHeader("Content-type", "text/json");
		} else {
			response.setHeader("Content-type", "application/json");
		}*/

		response.setHeader("Content-type", "text/html");
		response.setCharacterEncoding(GlobalConfig.OUTPUT_CHARSET);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
		response.setHeader("Expires", "0");

		response.getWriter().write(Json.toJson(object));
		response.getWriter().flush();
	}

	public static void outputHtml(Object object, HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setHeader("Content-type", "text/html");
		response.setCharacterEncoding(GlobalConfig.OUTPUT_CHARSET);
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
		response.setHeader("Expires", "0");

		response.getWriter().write(Json.toJson(object));
		response.getWriter().flush();
	}
}
