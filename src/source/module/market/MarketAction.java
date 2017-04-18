package source.module.market;

import entry.page.web.MarketServlet;
import source.kernel.Container;
import source.kernel.Core;
import source.kernel.ognl.Ognl;
import source.module.market.vo.Goods;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class MarketAction {
	public static void index() throws ServletException, IOException {
		System.out.println(Ognl.getLang("core.nextpage"));
		System.out.println(Ognl.getLang("core.action.signup"));
		System.out.println(Ognl.getLang("core.date.before"));
		System.out.println(Ognl.getLang("core.weeks.a"));
		System.out.println(Ognl.getLang("core.weeks.2"));
		System.out.println(Ognl.getGlobal("lang.core.nextpage"));
	}

	public static void input() throws ServletException, IOException {
	}

	public static void show() throws ServletException, IOException {
		Goods goods = Core.fillFormBean(new Goods());
		System.out.println(goods.toString());
		if (!goods.validate()) {
			Core.forward("/market/show_error.jsp");
			// 不加return无法结束module方法.代码将继续向下运行!
			return;
		}
		Core.forward("/market/show.jsp");
	}

	public static String auto() throws ServletException, IOException {
		Container.app().Global.put("number", "number");
		return MarketServlet.SUCCESS;
	}
}
