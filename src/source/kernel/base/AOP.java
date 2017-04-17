package source.kernel.base;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 正在设计
 * @author Hai Thomson
 */
public abstract class AOP extends Base {

	// 类型声明决定泛型类型
	protected HashMap<String, ArrayList> beforeRun = new HashMap<String, ArrayList>();
	protected HashMap<String, ArrayList> inRun = new HashMap<String, ArrayList>();
	protected HashMap<String, ArrayList> afterRun  = new HashMap<String, ArrayList>();

	public void attachBeforeMethod(String aname, String classpath, String bname) {
		ArrayList<String[]> runlist = beforeRun.get(aname);
		if (runlist == null) {
			runlist = new ArrayList<String[]>();
			String[] run = {classpath, bname};
			runlist.add(run);
		} else {
			String[] run = {classpath, bname};
			runlist.add(run);
		}
	}

	public void attachInRunMethod(String aname, String classpath, String bname) {
		ArrayList<String[]> runlist = inRun.get(aname);
		if (runlist == null) {
			runlist = new ArrayList<String[]>();
			String[] run = {classpath, bname};
			runlist.add(run);
		} else {
			String[] run = {classpath, bname};
			runlist.add(run);
		}
	}

	public void attachAfterMethod(String aname, String classpath, String bname) {
		ArrayList<String[]> runlist = afterRun.get(aname);
		if (runlist == null) {
			runlist = new ArrayList<String[]>();
			String[] run = {classpath, bname};
			runlist.add(run);
		} else {
			String[] run = {classpath, bname};
			runlist.add(run);
		}
	}

	public Object call(String methodName, Object... params) {

		Object results = null;

		// 前置，运行

		results = super.call(methodName, params);

		// 后置
		// 如果调用方法所在的对象是单例或者调用方法是静态的，清空运行计划

		return results;
	}
}
