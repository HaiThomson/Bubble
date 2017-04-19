package entry.page.controller;

import source.kernel.Container;
import source.kernel.controller.ActionSupport;
import source.kernel.controller.Controller;
import source.kernel.log.Logger;
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
			Logger.error(this.getClass().getName() + " " + e.getClass() + " " + e.getMessage());
			return ActionSupport.ERROR;
		}
		return "/economy/index.jsp";
	}

	public String log() {
		try {
			Container.app().initSession = false;
			Container.app().init();


			Long count = (Long) Container.app().request.getServletContext().getAttribute("count");
			if (count == null) {
				count = 1L;
				Container.app().request.getServletContext().setAttribute("count", count);
			}

			Logger.info("测试1数量 " + count.toString());
			Logger.info("测试2数量 " + count.toString());
			Logger.info("测试3数量 " + count.toString());
			Logger.info("测试4数量 " + count.toString());
			Logger.info("测试5数量 " + count.toString());

			count = count + 1L;
			Container.app().request.getServletContext().setAttribute("count", count);

			} catch (Exception e) {
				return ActionSupport.ERROR;
			}

		return ActionSupport.NONE;
	}
}
