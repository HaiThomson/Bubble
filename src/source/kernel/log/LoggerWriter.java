package source.kernel.log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Hai Thomson
 */
class LoggerWriter {

	static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Logger.LOGCONFIG.DATE_PATTERN);

	public static void writeLog(LogNode log) {
		// Tomcat 会记录 java1.4+ 标准log 到自己的日志文件
		if (log != null) {
			System.out.println(LoggerWriter.simpleDateFormat.format(new Date(log.dateline)) + "\t" + log.level + "\t" + "[" + log.threadName + "]" + "\t" + log.msg);
		}
	}
}
