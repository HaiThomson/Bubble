简介
-----------
Bubble是一个基于Java Servlet的全栈Web框架。<br/>
Bubble结合了性能和开发效率，只需具备基础的Java WEB开发知识就可快速上手。<br/>
Bubble高度模块化，采用无状态设计，具备多级缓存能力；是开发高性能，可伸缩Web Application的理想框架！<br/>

组成模块
-----------
<table border="1">
<tr>
	<td>Controller</td>
	<td>基于'Servlet 3.1'的控制器</td>
</tr>
<tr>
	<td>Global</td>
	<td>数据集装箱</td>
</tr>
<tr>
	<td>View</td>
	<td>视图适配器,支持JSP, JSON, Beetl, Velocity</td>
</tr>
<tr>
	<td>Container</td>
	<td>简易IOC容器</td>
</tr>
<tr>
	<td>AOP</td>
	<td>为开发者提供面向切面编程支持</td>
</tr>
<tr>
	<td>DB</td>
	<td>基于'Apache Commons DbUtils Version 1.6'重构的持久化模块</td>
</tr>
<tr>
	<td>Session</td>
	<td>无状态Session.提供In-Memory RDBMS Struts, In-Memory RDBMS, Redis三种版本</td>
</tr>
<tr>
	<td>Cache</td>
	<td>缓存系统.内置文件, 内存, 数据库缓存.内存支持Map, Redis, Memcache, Guava Cache, Ehcache</td>
</tr>
<tr>
	<td>Staticize</td>
	<td>页面静态化工具, 支持页面缓存到Cache模块</td>
</tr>
<tr>
	<td>Log</td>
	<td>日志记录模块,支持超高并发,丝毫不影响应用性能</td>
</tr>
<tr>
	<td>OGNL</td>
	<td>简易的对象导航访问工具</td>
</tr>
<tr>
	<td>Security</td>
	<td>安全工具套装：安全令牌, 验证码, SQL检查, XSS检查, 蜘蛛检查</td>
</tr>
<tr>
	<td>Plug-in</td>
	<td>插件系统,支持运行期间安装卸载</td>
</tr>
<tr>
	<td>Cron</td>
	<td>计划任务</td>
</tr>
<tr>
	<td>Miscellaneous</td>
	<td>解决WEB开发中经常碰到的问题</td>
</tr>
</table>

下载使用
-----------
### 环境准备  
JDK&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;：JDK1.8+<br/>
WEB容器：支持Servlet 3.1标准的WEB容器.推荐使用Apache Tomcat Version 8.0.23+<br/>
数据库&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;：推荐使用支持In-Memory技术的RDBMS.推荐使用MySQL 5.5+,  Oracle 12c,  SQL Server 2014<br/>
IDE&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;：推荐使用IntelliJ IDEA 14.1.5+<br/>
  
### 详细步骤  
1.创建WEB开发项目，项目编码格式UTF-8，修改WebContent目录为"web"，配置相关运行环境<br/>
2.将源码包中的文件拷贝到对应目录<br/>
3.创建数据库,导入sql文件内对应类型的SQL<br/>
4.修改配置文件/web/config/c3p0-config.xml中“jdbcUrl”，“user”，“password”配置项<br/>
  注意: Bubble默认开启FileCache和Log,所产生的文件保存在WEB容器启动目录.可通过/web/config/GlobalConfig.json更改文件保存位置<br/>
5.编译运行<br/>

快速入门
-----------
1.编写访问控制层代码<br/>
```
@WebServlet(name = "EconomyController", urlPatterns = "/economy/*")
public class EconomyController extends ActionSupport {
	public String index() {
		try {
			Economyindex.run();
			return "/economy/index.jsp";
		} catch (Exception e) {
			return Controller.ERROR;
		}
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
public class Economyindex {
	public static void run() throws NoSuchMethodException, InvocationTargetException {
		Container.app().Global.put("allsession", Container.table("common_session_struct").call("fetchAll"));
	}
}
```
4.编写展示层代码<br/>
以JSP为例<br/>
```
<div>All Session:</div>
<div>${Global["allsession"]}</div>
```