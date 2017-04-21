package source.kernel.helper;

import source.kernel.log.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Hai Thomson
 */
public class StackTraceHelper {
	private StackTraceHelper() {}

	/**
	 * 获取异常的堆栈信息
	 *
	 * @param t
	 * @return
	 */
	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		t.printStackTrace(pw);
		pw.close();
		return sw.toString();
	}

	public static String getStackTrace(Exception e) {
		ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(buf, true);
		e.printStackTrace(pw);
		String expMessage = buf.toString();

		pw.close();
		try {
			buf.close();
		} catch (IOException ioException) {
			// 处理异常的方法，再次抛出异常不合适.
			// 对于此类问题只能记录日志，排查
			Logger.error("获取堆栈错误信息时, 未能正常关闭输出流！ " + ioException.getMessage());
		}
		return expMessage;
	}
}
