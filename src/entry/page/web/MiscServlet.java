package entry.page.web;

import source.kernel.Container;
import source.kernel.base.ExceptionHandler;
import source.kernel.config.GlobalConfig;
import source.kernel.helper.ArraysHelper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * miscellaneous 杂项测试
 *
 */
@WebServlet(name = "MiscServlet", urlPatterns = "/misc.htm")
public class MiscServlet extends HttpServlet {

    public static final String[] MOD_ARRAY = {
            "seccode", "secqaa",
            "e",
    };

    public static final String[] MOD_GUEST_ARRAY = {
            "seccode", "secqaa",
    };

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Container.creatApp(request, response);

        String modName = (String) Container.app().Global.get("mod");
        String moduleName = ArraysHelper.inArrays(MOD_ARRAY, modName) ? modName : "error";

        switch (moduleName) {
            case "error" :
            case "secqaa" :
            case "seccode" :
                Container.app().initSession = false;
            default :
                break;
        }

        try {
            Container.app().init();
            Class moduleClass = Class.forName(GlobalConfig.SOURCE_PATH + ".module.misc.Misc" + moduleName);
            moduleClass.getMethod("run").invoke(null);
        } catch (Exception e) {
            ExceptionHandler.handling(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request,response);
    }
}
