package source.kernel.log;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Hai Thomson
 */
class LoggerWriter {

	static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Logger.LOGCONFIG.DATE_PATTERN);
	public static final String LINE_SEPARATOR = System.getProperty("line.separator");
	//public static final String LINE_SEPARATOR = (String) java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

	public static void writeLog(LogNode log) {
		// Tomcat 会记录 java1.4+ 标准log 到自己的日志文件
		if (log != null) {
			// System.out.println(LoggerWriter.simpleDateFormat.format(new Date(log.dateline)) + "\t" + log.level + "\t" + "[" + log.threadName + "]" + "\t" + log.msg);
			try {
				FileWriter fileWriter = new FileWriter (Logger.LOGCONFIG.FILE_NAME, true);
				fileWriter.write(LoggerWriter.simpleDateFormat.format(new Date(log.dateline)) + "\t" + log.level + "\t" + "[" + log.threadName + "]" + "\t" + log.msg);
				fileWriter.write(LINE_SEPARATOR);
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
