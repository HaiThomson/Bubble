package source.kernel.cron;

import source.kernel.Container;
import source.table.common_cron;

import java.sql.SQLException;
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

		while (true) {
			try {
				// 以操作系统时间为基准,每分钟执行一次
				// 误差 -10~0 毫秒
				long now = System.currentTimeMillis();
				long sleep = 60000 - now % 60000;
				System.out.println(sleep);
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				// Nothing to do
			}

			System.out.println(System.currentTimeMillis());

			this.getNextTask();
		}

	}

	// 查询消耗时间不要大于5000毫秒
	private boolean getNextTask() {
		try {
			Map map = table.fetchNextTask();

			Date now = new Date();
			int now_seconds = now.getSeconds();

			Set<String> keys = map.keySet();
			for (String key : keys) {

				String minute = (String) map.get("minute");




			}






		} catch (SQLException e) {
			e.printStackTrace();
		}

		return true;
	}

	private boolean minute(String minute, String now_seconds) {
		if (minute != null) {
			if (minute.equals("*") || minute.equals(now_seconds)) {
				return true;
			} else {
				if (minute.contains(",")) {
					String[] split = minute.split(",");
					if (split != null && split.length > 0) {
						for (int i = 0; i < split.length; i++) {
							if (split[i].contains("-")) {
								String[] split1 = split[i].split("-");
								if (split1 != null && split1.length == 2) {
									int door = Integer.valueOf(split1[0]);
									int floor  = Integer.valueOf(split1[1]);
									int minuteInt    = Integer.valueOf(minute);
									if (door <= minuteInt && minuteInt <= floor) {
										return true;
									}
								}
							} else {
								if (minute.equals(now_seconds)) {
									return true;
								}
							}
						}
					}
				} else {
					if (minute.contains("-")) {
						String[] split1 = minute.split("-");
						if (split1 != null && split1.length == 2) {
							int door = Integer.valueOf(split1[0]);
							int floor  = Integer.valueOf(split1[1]);
							int minuteInt    = Integer.valueOf(minute);
							if (door <= minuteInt && minuteInt <= floor) {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}
}
