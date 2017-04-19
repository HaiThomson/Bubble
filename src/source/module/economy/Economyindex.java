package source.module.economy;

import source.kernel.Container;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Hai Thomson
 */
public class Economyindex {
	public static void run() throws NoSuchMethodException, InvocationTargetException {
		Container.app().Global.put("allsession", Container.table("common_session_struct").call("fetchAll"));
	}
}
