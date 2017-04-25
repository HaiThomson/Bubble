package source.kernel.cron;

/**
 * @author Hai Thomson
 */
public class Cron {

	public static void init() {
		new CronDaemonThread().start();
	}
}
