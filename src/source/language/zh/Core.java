package source.language.zh;

import source.kernel.base.Base;

import java.util.HashMap;

/**
 * 效率上，配置文件 + Map缓存 和 静态字符串没有区别.
 * WEB不像APP可以无缝切换语言配置。不同字长的语言对前端er是个巨大的难题。
 * 推荐使用模板切换实现国际化，简单，实用！
 * @author Hai Thomson
 */
public class Core extends Base {
	// Format: 定义一个私有的本类的静态属性
	private static final Core core = new Core();

	// Format: 私有化构造方法
	private Core() {}

	// Format: 定义一个返回单例的方法.方法名限定为 public static 类 instance
	public static Core instance() {
		return Core.core;
	}

	// Format: 定义单一字符串
	public static final String nextpage = "下一页";
	public static final String prevpage = "上一页";
	public static final String pageunit = "上一页";
	public static final String total = "上一页";
	public static final String k10 = "万";
	public static final String pagejumptip = "输入页码，按回车快速跳转";

	// Format: 对于复杂的语言块有两大类三种方式可选
	// 内部类不可不免创造没有名字的内部类！
	// 推荐使用  Style1-2.除了"{{}}"，没有槽点
	// Style1-1: 内部类
	public static final Action action = new Action();


	// EL不支持直接取属性值，必须通过getXXX方法。
	// OGNL支持直接取属性值
	public static class Action {
		public static final String signup = "登录";
		public static final String home = "空间";
		public static final String index = "首页";
		public static final String other = "其他";
	}

	// Style1-2: 内部类
	public static final HashMap<String, String> date = new HashMap<String, String >() {{
		put("before", "前");
		put("day", "天");
		put("yday", "昨天");
		put("bday", "前天");
		put("hour", "小时");
		put("half", "半");
		put("min", "分钟");
		put("sec", "秒");
		put("now", "刚刚");
	}};

	// Style2: Map
	public final static HashMap<String, String> weeks = new HashMap<String, String>();
	static {
		weeks.put("a", "周一");
		weeks.put("2", "周二");
		weeks.put("3", "周三");
		weeks.put("4", "周四");
		weeks.put("5", "周五");
		weeks.put("6", "周六");
		weeks.put("7", "周日");
	}
}
