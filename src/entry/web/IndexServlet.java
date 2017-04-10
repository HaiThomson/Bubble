package entry.web;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 欢迎页面
 * @author Hai Thomson
 */
@WebServlet(name = "IndexServlet", urlPatterns = "/index.htm")
public class IndexServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().write("<html>");
        response.getWriter().write("<body>");
        response.getWriter().write("<div style=\"float:left;\"><img style=\"width:128px;height:128px\" src=\"/favicon.ico\"></div>");
        response.getWriter().write("<div style=\"float:left;\"><p style=\"margin-top:80px\">Welcome to bubble!</p></div>");
        response.getWriter().write("</body>");
        response.getWriter().write("</html>");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
