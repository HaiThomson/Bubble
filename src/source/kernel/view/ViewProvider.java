package source.kernel.view;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Hai Thomson
 */
public abstract class ViewProvider {
	public abstract void outputData(String realViemPath, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
