package source.module.demo;

import source.kernel.Core;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by Hai on 2017/2/16.
 */
public class Demoshow {
	public static void run() throws ServletException, IOException {
		Core.forward("/demo/show.jsp");
	}
}
