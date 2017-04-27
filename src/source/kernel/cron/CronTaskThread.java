package source.kernel.cron;

import source.kernel.DB;
import source.kernel.config.GlobalConfig;

/**
 * @author Hai Thomson
 */
public class CronTaskThread extends Thread {
	protected String taskclass = null;
	protected String method    = null;

	public CronTaskThread() {
	}

	public CronTaskThread(String taskclass, String method) {
		this.taskclass = taskclass;
		this.method = method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setTaskclass(String taskclass) {
		this.taskclass = taskclass;
	}

	public void run() {
		try{
			DB.init(GlobalConfig.DATABASE_CONFIG);

			Class<?> task = Class.forName(taskclass);
			task.getMethod(method).invoke(task.newInstance());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
