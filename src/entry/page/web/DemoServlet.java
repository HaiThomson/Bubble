package entry.page.web;

import source.kernel.Container;
import source.kernel.config.GlobalConfig;
import source.kernel.base.ExceptionHandler;
import source.kernel.helper.ArraysHelper;
import source.aspect.method.DemoMethod;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 简单的入口测试
 *
 * DataBaseDriver并不能做到无缝切换数据库.自动生成SQL语句只是辅助功能。
 * DataBaseDriver + DAO + 不在Table子类外使用DB类方法才能做到平缓切换数据库。
 * Container.table("") + 外部内存缓存，实现数据缓存, 也要求不在Table子类外使用DB类方法才能做到平缓切换数据库。
 *
 * @author Hai Thomson
 */
@WebServlet(name = "DemoServlet", urlPatterns = "/demo.htm")
public class DemoServlet extends HttpServlet {

    // 错误测试点
    public static final String[] MOD_ARRAY = {
            "index", "show", "input",
            "e",
    };

    // IOException仅指网络IO错误，不要把自己的IO异常抛给WEB容器
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Container.creatApp(request, response);

        String modName = (String) Container.app().Global.get("mod");
        // 因为demo.htm是固定的.用户请求的资源不能时有时没有,如mod名称错误最好选择回到资源主页面
        String moduleName = ArraysHelper.inArrays(MOD_ARRAY, modName) ? modName : "index";

        // 强调，module包不是service层,只是某个大功能的子模块.
        // Service接口模式只适用于展示层比较简单的页面(企业数据展示类页面)
        // module 和 service 在设计上有本质区别。service取决于业务封装抽象，体现在Bean。module只针对页面，没有Bean
        // 现在这样为复杂互联网功能页面设计，一个module类代码可能非常长
        // “将业务数据结构转至数据库表定义, 代码内数据用Map传递”这种方式对企业级开发非常不友好
        try {
            Container.app().init();

            DemoMethod.pub();
            DemoMethod.commonHandle();
            DemoMethod.communal();

            Class moduleClass = Class.forName(GlobalConfig.SOURCE_PATH + ".module.demo.Demo" + moduleName);
            // Java反射moduleClass.getMethods() | getName 时只能输出不带参数的名字.run方法尽量不要带参数
            moduleClass.getMethod("run").invoke(null);
        } catch (Exception e) {
            ExceptionHandler.handling(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);
    }
}
