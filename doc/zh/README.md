简介
-----------
Bubble是一个基于Java Servlet的全栈Web框架。<br/>
Bubble结合了性能和开发效率，只需具备基础的Java WEB开发知识就可快速上手。<br/>
Bubble高度模块化，采用无状态设计，具备多级缓存能力；是开发高性能，可伸缩Web Application的理想框架！<br/>

下载使用
-----------
### 环境准备  
   JDK：JDK1.8<br/>
   WEB容器：支持Servlet 3.1标准的WEB容器。推荐使用Apache Tomcat Version 8.0.23及更高版本<br/>
   数据库：推荐使用支持In-Memory技术的RDBMS。如MySQL 5.5.15，Oracle 12c, SQL Server 2014<br/>
   IDE：推荐使用IntelliJ IDEA 14.1.5及更高版本<br/>
  
### 详细步骤  
   1.创建WEB开发项目，项目编码格式UTF-8，修改WebContent目录为"web"，配置相关运行环境<br/>
   2.将源码包中的文件拷贝到对应目录<br/>
   3.创建数据库,导入sql文件内对应类型的SQL<br/>
   4.修改配置文件/web/config/c3p0-config.xml中“jdbcUrl”，“user”，“password”配置项<br/>
     注意: Bubble默认开启FileCache和Log，所产生的文件保存在WEB容器启动目录。可通过/web/config/GlobalConfig.json更改文件保存位置<br/>
   5.编译运行<br/>

快速入门
-----------
Bubble解耦策略和其它SSH不同<br/>
1.编写访问控制层代码<br/>
2.编写业务模块<br/>
3.编写数据操作层代码<br/>
4.编写展示层代码<br/>
先选JSP这个老朋友<br/>