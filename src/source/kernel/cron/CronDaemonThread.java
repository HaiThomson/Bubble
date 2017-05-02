package source.kernel.cron;

import source.kernel.Container;
import source.kernel.DB;
import source.kernel.config.GlobalConfig;
import source.table.common_cron;

import java.sql.SQLException;
import java.util.*;

/**
 * @author Hai Thomson
 */
class CronDaemonThread extends Thread {

	protected common_cron table = (common_cron) Container.table("common_cron");

	@Override
	public void run() {
		this.setName("Cron-Daemon-Thread");
		try {
			DB.init(GlobalConfig.DATABASE_CONFIG);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				// 以操作系统时间为基准,每分钟执行一次
				// 误差 -10~0 毫秒
				long now = System.currentTimeMillis();
				long sleep = 60000 - now % 60000;
				// long sleep = 600 - now % 600;
				// System.out.println(sleep);
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				// Nothing to do
			}
			// System.out.println(System.currentTimeMillis());

			try {
				DB.beginTransaction();
				DB.executeCommand(DB.getDriver().makeLockTable(table.getTableName(), "X"));

				Date now = new Date();
				int now_int = (int) (now.getTime() / 1000);

				Map crons = this.getTasks(now_int);
				Set<Integer> keys = crons.keySet();
				for (Integer key : keys) {
					Map cron = (Map) crons.get(key);
					if (this.isRun(cron, now)) {
						// 设置这次运行时间.
						table.updateLastrun((int) cron.get("cronid"), now_int);
						CronTaskThread taskThread = new CronTaskThread((String)cron.get("taskclass"), (String)cron.get("taskmethod"));
						taskThread.start();
					}
				}
				// System.out.println("本次计划任务已全部加载");
				DB.executeCommand(DB.getDriver().makeUnlockTable(table.getTableName(), "X"));
				DB.commitTransaction();
			} catch (Exception e) {
				try {
					DB.executeCommand(DB.getDriver().makeUnlockTable(table.getTableName(), "X"));
					DB.rollBackTransaction();
				} catch (SQLException cause) {
					cause.printStackTrace();
				}
				e.printStackTrace();
			} finally {
				try {
					DB.closeTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

	}

	private Map getTasks(int now) throws SQLException {
		Map cron = table.fetchTask(now);
		return cron;
	}

	@SuppressWarnings("deprecation")
	private boolean isRun(Map cron, Date now) {

		if (cron == null || now == null) {
			return false;
		}

		int now_minute = now.getMinutes();
		int now_hour = now.getHours();
		int now_day = now.getDay();
		int now_month = now.getMonth() + 1;
		int now_weekday = this.getWeekDay(now);

		String minute =  (String) cron.get("minute");
		boolean is_minute = this.check(minute, now_minute, 0, 59);

		String hour =  (String) cron.get("hour");
		boolean is_hour = this.check(hour, now_hour, 0, 23);

		String day =  (String) cron.get("day");
		boolean is_day = this.check(day, now_day, 1, 31);

		String month =  (String) cron.get("month");
		boolean is_month = this.check(month, now_month, 1, 12);


		String weekday =  (String) cron.get("weekday");
		boolean is_weekday = this.check(weekday, now_weekday, 0, 6);

		return is_minute && is_hour && is_day && is_month && is_weekday;
	}

	private boolean check(String config, int now, int sdoor, int sfloor) {
		if (config != null) {
			if (config.equals("*") || config.equals(Integer.valueOf(now))) {
				return true;
			} else {
				if (config.contains(",")) {
					String[] split = config.split(",");
					if (split != null && split.length > 0) {
						for (int i = 0; i < split.length; i++) {
							// 占时不支持多条件
						}
					}
				} else {
					if (config.contains("-") && !config.contains("/")) {
						String[] intervals = config.split("-");
						if (intervals != null && intervals.length == 2) {
							int door = Integer.valueOf(intervals[0]);
							int floor  = Integer.valueOf(intervals[1]);
							if (door <= now && now <= floor) {
								return true;
							}
						}
					}

					if (config.contains("/") && !config.contains("-")) {
						String[] spaces = config.split("/");
						if (spaces != null && spaces.length == 2) {
							int space = Integer.valueOf(spaces[1]);
							if (now % space == 0) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	private int getWeekDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1 ;
		return w;
	}
}
