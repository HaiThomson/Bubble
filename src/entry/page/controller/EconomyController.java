package entry.page.controller;

import source.kernel.controller.Controller;
import source.module.economy.Economyindex;

import javax.servlet.annotation.WebFilter;
import java.sql.SQLException;

/**
 * 1、控制器职责拿到控制参数(URL/module参数)判断调用什么业务逻辑（一个或一组（N + 1| M)）。
 *    Service就里就做具体的业务逻辑判断，调用DAO处理用户数据，DAO层只充当数据仓库的代理。
 * 2、Service可以重复调用，但不可以重用。针对某一个控制逻辑开发的Service往往绑定用户数据结构，极难重用。
 * 	  某些针对共通业务设计的Service方法比如上传，验证码，图片显示可以做成切面以备重用。
 * 3、Servlet本身就是Controller.Struts, SpringMVC解决的不是没有控制的问题。
 *    在Servlet3.0标准之前，需要在web.xml中配置Servlet.项目大了，几乎无法维护。
 *    Servlet3.0有了注解，一定程度上缓解了该问题，但远远不够。
 *    这也就是说Struts, SpringMVC解决了控制器拓展的问题。
 * 4、Struts1, Struts2, SpringMVC等MVC框架采用了方法扩展方式。虽然在形式上解决了控制器扩展的问题，但有逻辑问题。
 *    试想，建立了一个方法充当控制器，方法名就是控制器名。如果在里面写了业务逻辑，该方法立刻升级变成业务层。
 *    这就变成了模糊地带，方法是什么只在一念之间。真正实践上也无法阻止程序员在控制器方法内不写业务代码。
 * 5、为什么Bobble提供了Controller父类？人生不只诗和远方，还有苟且。
 * 6、给一个好的设计？请查阅web包控制器实现。控制器组是一个字符串数据，控制器就是一字符串。
 * 7、为什么Struts1, Struts2, SpringMVC不做成web包内的形式？因为无法做成父类，做成框架。所以也不要吐槽Struts1, Struts2, SpringMVC。
 * 8、需不需需要Service层接口？这个要看项目管理，架构如何分工。按层次分工就得用接口隔离上下层依赖，如果按模块分工就不需要。
 *
 * @author Hai Thomson
 */
@WebFilter(filterName = "EconomyController", urlPatterns = "/economy/*")
public class EconomyController extends Controller {
	public String index() {
		try {
			Economyindex.run();
		} catch (Exception e) {
			return Controller.ERROR;
		}
		return "/economy/index.jsp";
	}
}
