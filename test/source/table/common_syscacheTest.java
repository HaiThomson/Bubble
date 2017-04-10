package source.table;

import source.kernel.Container;
import source.kernel.DB;
import source.kernel.config.GlobalConfig;
import source.kernel.db.pool.ConnectionPooling;
import source.kernel.helper.MapHelper;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author Hai Thomson
 */
public class common_syscacheTest {
	public static void main(String[] args) throws SQLException {
		ConnectionPooling.init("c3p0", "classloader:" + ("./config/c3p0-config.xml"));
		DB.init(GlobalConfig.DBDRIVER_PATH, GlobalConfig.TABLE_PREFIX);
		common_syscache common_syscache = (source.table.common_syscache) Container.table("common_syscache");

		HashMap<String, Object> setting = new HashMap<String, Object>();
		setting.put("oltimespan", 10L);

		common_syscache.insert("setting", MapHelper.serializableToBytes(setting));
	}

	public static void justtest() {
		// servletContext.getResource();获取的资源路径完整,不转义，不使用短路经

		// 失败,new File只是为了除去"file:/"
		// ConnectionPooling.init(new File(common_syscacheTest.class.getClassLoader().getResource("./config/c3p0-config.xml").getPath()).getPath());
		// System.out.println(new File(common_syscacheTest.class.getClassLoader().getResource("./config/c3p0-config.xml").getPath()).getPath());

		// 失败,中文路径出现%XX代码
		//ConnectionPooling.init(common_syscacheTest.class.getClassLoader().getResource("./config/c3p0-config.xml").getPath());
		//System.out.println(common_syscacheTest.class.getClassLoader().getResource("./config/c3p0-config.xml").getPath());

		// 失败,中文路径出现%XX代码
		//ConnectionPooling.init("/" + new File(common_syscacheTest.class.getClassLoader().getResource("./config/c3p0-config.xml").getPath()).getPath());
		//System.out.println("/" + new File(common_syscacheTest.class.getClassLoader().getResource("./config/c3p0-config.xml").getPath()).getPath());

		// 失败，文件名、目录名或卷标语法不正确
		//ConnectionPooling.init(common_syscacheTest.class.getClassLoader().getResource("./config/c3p0-config.xml").toString());
		//System.out.println(common_syscacheTest.class.getClassLoader().getResource("./config/c3p0-config.xml").toString());

		// 失败, C3P0无法加载到资源
		// ConnectionPooling.init("classloader:" + common_syscacheTest.class.getClassLoader().getResource("./config/c3p0-config.xml").toString());
		// System.out.println(common_syscacheTest.class.getClassLoader().getResource("./config/c3p0-config.xml").toString());}
	}
}
