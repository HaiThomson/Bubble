package source.kernel.cron;

import source.kernel.Container;
import source.kernel.DB;
import source.kernel.config.GlobalConfig;
import source.kernel.log.Logger;
import source.table.common_cron;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author Hai Thomson
 */
public class CronDaemonThread extends Thread {

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

				Date now = new Date();
				int now_int = (int) (now.getTime() / 1000);

				Map crons = this.getNextTasks(now_int);

				Set<Integer> keys = crons.keySet();
				for (Integer key : keys) {
					Map cron = (Map) crons.get(key);
					// 启动线程,run起来


					// 设置下次启动时间, 设置这次运行时间.
					//cron.put("", now_int);
					//cron.put("", this.nextRun(cron, now));
					System.out.println(cron);
					System.out.println(this.nextRun(cron, now));
					//table.updateByPrimaryKey(cron.get("cronid") , cron);
				}


				DB.commitTransaction();
			} catch (SQLException e) {
				try {
					DB.rollBackTransaction();
				} catch (SQLException cause) {

				}
			} finally {
				try {
					DB.closeTransaction();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}

	}

	private Map getNextTasks(int now) throws SQLException {
		Map cron = table.fetchNextTask(now);
		return cron;
	}
	
	private int nextRun(Map cron, Date now) {

		if (cron == null || now == null) {
			return (int) (System.currentTimeMillis() /1000);
		}

		int now_int = (int) (now.getTime() / 1000);
		int now_minute = now.getMinutes();
		int now_hour = now.getHours();
		int now_day = now.getDay();
		int now_month = now.getMonth() + 1;
		int now_weekday = this.getWeekDay(now);

		String minute =  (String) cron.get("minute");
		int next_minute = this.next(minute, now_minute, 0, 59);
		System.out.println(next_minute);

		String hour =  (String) cron.get("hour");
		int next_hour = this.next(minute, now_hour, 0, 23);
		System.out.println(next_hour);

		String day =  (String) cron.get("day");
		int next_day = this.nextDay(minute, now_day, 1, 31);
		System.out.println(next_day);

		/*String month =  (String) cron.get("month");
		int next_month = this.next(minute, now_month, 1, 12);
		System.out.println(next_month);*/

		String weekday =  (String) cron.get("weekday");
		int next_weekday = this.next(minute, now_weekday, 0, 6);
		System.out.println(next_weekday);

		int nextrun = 0;
		nextrun = next_minute * 60 + next_hour * 3600;

		if (next_day != 0 && next_weekday== 0) {
			nextrun = next_day * 86400;
		}

		if (next_day != 0 && next_weekday== 0) {
			nextrun = next_weekday * 86400;
		}

		if (nextrun == 0) {
			nextrun = 60;
		}

		nextrun = now_int + nextrun;
		return nextrun;
	}

	private int next(String config, int now, int sdoor, int sfloor) {
		try {
			int config_int = Integer.valueOf(config);
			if (sdoor <= config_int && config_int <= sfloor) {
				if (now >= config_int) {
					return sfloor - now + config_int;
				}

				if (now < config_int) {
					return config_int - now;
				}
			} else {
				// Logger.error(config + " not in " + "[" + sdoor + "-" + sfloor +"]");
			}
		} catch (Exception e) {
			// Nothing to do
		}

		if (config != null) {
			if (config.equals("*")) {
				return 0;
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
							int next    = Integer.valueOf(now) + 1;
							if (door <= next && next <= floor) {
								return 1;
							}

							if (sdoor <= now && now < door) {
								return door - now;
							}

							if (floor <= now && now <= sfloor) {
								return sfloor - now + door;
							}
						}
					}

					if (config.contains("/") && !config.contains("-")) {
						String[] space = config.split("//");
						if (space != null && space.length == 2) {
							return Integer.valueOf(space[2]);
						}
					}
				}
			}
		}

		return 0;
	}

	private int nextDay(String config, int now, int sdoor, int sfloor) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		sfloor = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
		
		try {
			int config_int = Integer.valueOf(config);
			if (sdoor <= config_int && config_int <= sfloor) {
				if (now >= config_int) {
					return sfloor - now + config_int;
				}

				if (now < config_int) {
					return config_int - now;
				}
			} else {
				// Logger.error(config + " not in " + "[" + sdoor + "-" + sfloor +"]");
			}
		} catch (Exception e) {
			// Nothing to do
		}

		if (config != null) {
			if (config.equals("*")) {
				return 0;
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
							int next    = Integer.valueOf(now) + 1;
							if (door <= next && next <= floor) {
								return 1;
							}

							if (sdoor <= now && now < door) {
								return door - now;
							}

							if (floor <= now && now <= sfloor) {
								return sfloor - now + door;
							}
						}
					}

					if (config.contains("/") && !config.contains("-")) {
						String[] space = config.split("//");
						if (space != null && space.length == 2) {
							return Integer.valueOf(space[2]);
						}
					}
				}
			}
		}

		return 0;
	}

	private int getWeekDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1 ;
		return w;
	}
}
