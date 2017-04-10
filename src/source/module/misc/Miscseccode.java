package source.module.misc;

import source.kernel.Core;
import source.kernel.Container;
import source.kernel.security.seccode.GeneralSeccode;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class Miscseccode {
	public static void run() throws ServletException, IOException {
		HttpServletResponse response = Container.app().response;
		// 设置响应的类型格式为图片格式
		response.setContentType("image/jpeg");
		// 禁止浏览器缓存
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		GeneralSeccode instance = new GeneralSeccode();
		Core.setCookie("scaptcha", instance.getCode(), 1800);
		instance.write(response.getOutputStream());
		return;
	}
}
