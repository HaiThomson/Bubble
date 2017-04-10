package source.module.demo;

import source.kernel.Core;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class Demoinput {
	public static void run() throws ServletException, IOException {
		Core.forward("/demo/input.jsp");
	}
}
