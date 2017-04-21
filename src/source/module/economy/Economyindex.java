package source.module.economy;

import source.kernel.Container;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Hai Thomson
 */
public class Economyindex {
	public static void run() throws NoSuchMethodException, InvocationTargetException {

		/*try {
			Container.app().Global.put("allsession", Container.table("common_session_struct").call("fetchAll"));
		} catch (InvocationTargetException e) {
			// 有信息
			e.printStackTrace();
			// 无信息
			// 异常经常无Message
			System.out.println(e.getMessage());
		} catch (NoSuchMethodException e) {
			System.out.println(e.getMessage());
		}*/

		Container.app().Global.put("allsession", Container.table("common_session_struct").call("fetchAll"));
	}
}
