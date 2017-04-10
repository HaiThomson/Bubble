package source.module.misc;

import source.kernel.Container;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Hai Thomson
 */
public class Miscerror {
	public static void run() throws ServletException, IOException {
		Container.app().response.setContentType("text/html");
		PrintWriter out = Container.app().response.getWriter();
		out.println("Access Denied, Error mod request!");
		out.flush();
		out.close();
	}
}
