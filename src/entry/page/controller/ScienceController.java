package entry.page.controller;

import source.kernel.controller.ActionSupport;
import source.kernel.controller.Controller;
import source.module.economy.Economyindex;

import javax.servlet.annotation.WebServlet;
import java.sql.SQLException;

/**
 *
 * 你喜欢的都在这里
 *
 * @author Hai Thomson
 */
@WebServlet(name = "ScienceController", urlPatterns = "/science/*")
public class ScienceController extends ActionSupport {
	public String index() throws SQLException {

		// 测试异常
		/*if (1 == 1) {
			throw new SQLException("Test Exception");
		}*/

		try {
			Economyindex.run();
		} catch (Exception e) {
			return Controller.ERROR;
		}
		return "/economy/index.jsp";
	}
}
