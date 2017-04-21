package source.kernel.log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author Hai Thomson
 */
class LoggerWriter {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final Pattern FilePattern = Pattern.compile("[\\\\/:*?\"<>|]");
	// private static final String LINE_SEPARATOR = (String) java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("line.separator"));

	public static void writeLog(LogNode log) {
		// Tomcat 会记录 java1.4+ 标准log 到自己的日志文件
		if (log != null) {
			// System.out.println(LoggerWriter.simpleDateFormat.format(new Date(log.dateline)) + "\t" + log.level + "\t" + "[" + log.threadName + "]" + "\t" + log.msg);
			try {
				FileWriter fileWriter = new FileWriter (Logger.LOGCONFIG.FILE_NAME, true);
				fileWriter.write(new SimpleDateFormat(Logger.LOGCONFIG.DATE_PATTERN).format(new Date(log.dateline)) + "\t" + log.level + "\t" + "[" + log.threadName + "]" + "\t" + log.msg);
				fileWriter.write(LoggerWriter.LINE_SEPARATOR);
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeLog(LogNode log, SimpleDateFormat simpleDateFormat, OutputStream outputStream) throws IOException {
		outputStream.write((simpleDateFormat.format(new Date(log.dateline)) + "\t" + log.level + "\t" + "[" + log.threadName + "]" + "\t" + log.msg).getBytes());
		outputStream.write(LoggerWriter.LINE_SEPARATOR.getBytes());
	}


	public static String getLogFilePath() {
		StringBuffer pathStringBuffer = new StringBuffer();
		pathStringBuffer.append(Logger.LOGCONFIG.FILE_PATH);

		StringBuffer fileNameStringBuffer = new StringBuffer();
		fileNameStringBuffer.append(Logger.LOGCONFIG.FILE_NAME);
		if (Logger.LOGCONFIG.FILE_NAME_WITH_DATE != null && !Logger.LOGCONFIG.FILE_NAME_WITH_DATE.equals("")) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(Logger.LOGCONFIG.FILE_NAME_WITH_DATE);
			fileNameStringBuffer.append(dateFormat.format(new Date(System.currentTimeMillis())));
		}

		pathStringBuffer.append(LoggerWriter.filenameFilter(fileNameStringBuffer.toString()));
		return pathStringBuffer.toString();
	}

	private static String filenameFilter(String str) {
		return str == null ? null : FilePattern.matcher(str).replaceAll("");
	}
}
