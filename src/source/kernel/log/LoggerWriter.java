package source.kernel.log;

import java.util.Date;

/**
 * @author Hai Thomson
 */
class LoggerWriter {
	public static void writeLog(LogNode log) {
		// Tomcat 会记录 java1.4+ 标准log 到自己的日志文件
		if (log != null) {
			System.out.println(LogWriteThread.dateFormat.format(new Date(log.dateline)) + "\t" + log.level + "\t" + "[" + log.threadName + "]" + "\t" + log.msg);
		}
	}
}
