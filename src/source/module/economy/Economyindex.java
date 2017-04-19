package source.module.economy;

import source.kernel.Container;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author Hai Thomson
 */
public class Economyindex {
	public static void run() throws NoSuchMethodException, InvocationTargetException {

		// ScienceController index 未初始化app.
		// 调用数据库肯定会报错
		// 但未捕获到错误信息.测试
		try {
			Container.app().Global.put("allsession", Container.table("common_session_struct").call("fetchAll"));
		} catch (InvocationTargetException e) {
			System.out.println(e.getMessage());
		}

		// Container.app().Global.put("allsession", Container.table("common_session_struct").call("fetchAll"));
	}
}
