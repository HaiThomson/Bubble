简介
-----------
Bubble是一个基于Java Servlet的全栈Web框架。<br/>
Bubble结合了性能和开发效率，只需具备基础的Java WEB开发知识就可快速上手。<br/>
Bubble高度模块化，采用无状态设计，具备多级缓存能力；是开发高性能，可伸缩Web Application的理想框架！<br/>

组成模块
-----------
1.3-tier		:	三层架构.<br/>
2.ORM			:	持久化模块.<br/>
3.View			:	支持JSP, JSON, Beetl, Velocity<br/>
4.Container		:	简易IOC容器.<br/>
5.AOP			:   准面向切面编程.<br/>
6.Session		:	无状态Session.提供In-Memory RDBMS Struts, In-Memory RDBMS, Redis三种版本<br/>
7.Cache			:	缓存系统.支持文件, 内存, 数据库缓存.内存支持Map, Redis, Memcache, Guava Cache, Ehcache.<br/>
8.Staticize     :	页面静态化, 支持页面缓存到Cache模块.<br/>
9.Log			:	高并发日志记录模块.<br/>
10.OGNL			:	简易的对象导航访问工具.<br/>
11.security		:	安全令牌, 验证码, SQL检查, XSS检查, 蜘蛛检查.<br/>
12.plug-in		:	插件系统,支持运行期间安装卸载. <br/>
13.crontab		:	计划任务模块,还未完成技术验证.<br/>
14.Miscellaneous:	WEB开发中经常碰到的问题.<br/>

下载使用
-----------
### 环境准备  
JDK：JDK1.8+<br/>
WEB容器：支持Servlet 3.1标准的WEB容器.推荐使用Apache Tomcat Version 8.0.23+<br/>
数据库：推荐使用支持In-Memory技术的RDBMS。推荐使用MySQL 5.5+,  Oracle 12c,  SQL Server 2014<br/>
IDE：推荐使用IntelliJ IDEA 14.1.5+<br/>
  
### 详细步骤  
1.创建WEB开发项目，项目编码格式UTF-8，修改WebContent目录为"web"，配置相关运行环境<br/>
2.将源码包中的文件拷贝到对应目录<br/>
3.创建数据库,导入sql文件内对应类型的SQL<br/>
4.修改配置文件/web/config/c3p0-config.xml中“jdbcUrl”，“user”，“password”配置项<br/>
  注意: Bubble默认开启FileCache和Log，所产生的文件保存在WEB容器启动目录。可通过/web/config/GlobalConfig.json更改文件保存位置<br/>
5.编译运行<br/>

快速入门
-----------
Bubble采用三层架构解耦<br/>
1.编写访问控制层代码<br/>
```
@WebServlet(name = "SimpleServlet", urlPatterns = "/simple/*")
public class SimpleServlet extends HttpServlet {

	public static final String[] RES_ARRAY = {
			"", "index.htm", "show.htm",
	};

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Container.creatApp(request, response);
		String resName = (String) Container.app().Global.get("res");
		String moduleName = (ArraysHelper.inArrays(RES_ARRAY, resName) && !resName.equals("") ? resName : "index").replaceAll(GlobalConfig.RES_SUFFIX, "");

		try {
			Container.app().init();
			Class moduleClass = Class.forName(GlobalConfig.SOURCE_PATH  + ".module.simple.Simple" + moduleName);
			moduleClass.getMethod("run").invoke(null);
		} catch (Exception e) {
			ExceptionHandler.handling(e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
}
```
2.编写数据操作层代码<br/>
```
public class common_session_struct extends Table {

	public common_session_struct() {
		super("common_session_struct", "sessionid");
	}

	public Map fetchAll() throws SQLException {
		String sql = "SELECT * FROM " + DB.getRealTableName(this.tableName);
		return DB.queryAll(sql);
	}
}
```
3.编写业务模块<br/>
```
public class Simpleindex {
	public static void run() throws Exception {
		// 两种写法
		Container.app().Global.put("allsession", ((common_session_struct) Container.table("common_session_struct")).fetchAll());
		Container.app().Global.put("allsession", Container.table("common_session_struct").call("fetchAll"));

		Core.forward("/simple/index.jsp");
	}
}
```
4.编写展示层代码<br/>
以JSP为例<br/>
```
<div>All Session:</div>
<div>${Global["allsession"]}</div>
```