package source.module.demo;

import source.kernel.Core;
import source.kernel.Container;
import source.table.common_session_struct;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public class Demoindex {

    /**
     * 不建议改造成Service封装模式
     * @throws ServletException
     * @throws IOException
     */
    public static void run() throws Exception {
        Container.app().Global.put("count", ((common_session_struct)(Container.table("common_session_struct"))).countAll());

        Core.forward("/demo/index.jsp");
    }
}