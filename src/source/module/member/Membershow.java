package source.module.member;

import source.kernel.Core;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class Membershow {
	public static void run() throws ServletException, IOException {
		Core.forward("/member/show.jsp");
	}
}
