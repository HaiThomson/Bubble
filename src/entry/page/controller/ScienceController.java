package entry.page.controller;

import source.kernel.Container;
import source.kernel.controller.ActionSupport;
import source.kernel.helper.StackTraceHelper;
import source.kernel.log.Logger;
import source.module.economy.Economyindex;
import source.module.economy.vo.NewsJavaBean;

import javax.servlet.annotation.WebServlet;

/**
 *
 * 你喜欢的都在这里
 *
 * @author Hai Thomson
 */
@WebServlet(name = "ScienceController", urlPatterns = "/science/*")
public class ScienceController extends ActionSupport {
	/*public String index() {
		try {
			// Container.app().initSession = false;
			// Container.app().init();
			Economyindex.run();
			return "/economy/index.jsp";
		} catch (Exception e) {
			Logger.error(this.getClass().getName() + " " + e.getClass() + " " + e.getMessage() + " " + e.getCause().getMessage());
			return ActionSupport.ERROR;
		}
	}*/

	/*public String index() throws Exception {
		// Container.app().initSession = false;
		// Container.app().init();
		Economyindex.run();
		return "/economy/index.jsp";
	}*/

	public String index() {
		try {
			Container.app().initSession = false;
			Container.app().init();

			Economyindex.run();
			return "/economy/index.jsp";
		} catch (Exception e) {
			if (e.getMessage() == null || e.getMessage().equals("")) {
				Logger.error(this.getClass().getName() + " " + e.getClass() + " " + StackTraceHelper.getStackTrace(e));
			} else {
				Logger.error(this.getClass().getName() + " " + e.getClass() + " " + e.getMessage() + " " + e.getCause().getMessage());
			}
			return ActionSupport.ERROR;
		}
	}

	public void para(NewsJavaBean news, NewsJavaBean newss) {
		if (news != null) {
			System.out.println(news.toString());
		}
		if (newss != null) {
			System.out.println(newss.toString());
		}
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

			return ActionSupport.NONE;
		} catch (Exception e) {
			return ActionSupport.ERROR;
		}
	}
}
