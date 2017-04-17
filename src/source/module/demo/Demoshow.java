package source.module.demo;

import source.kernel.Core;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class Demoshow {
	public static void run() throws ServletException, IOException {
		Core.forward("/demo/show.jsp");
	}
}
