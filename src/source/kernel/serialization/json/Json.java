package source.kernel.serialization.json;

import source.kernel.base.ExceptionHandler;

import java.sql.SQLException;
import java.util.Map;

/**
 * @author Hai Thomson
 */
public class Json {

	public static JsonConverter jsonConverter = null;

	public static void init() {
		// 修改为加载配置
		Class<?> dbDriverClass = null;
		try {
			dbDriverClass = Class.forName("source.kernel.serialization.json.fastjson.fastjsonConverter");
			jsonConverter = (JsonConverter) dbDriverClass.newInstance();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("没有加载到指定的JsonConverter: " + "" + " 原始信息: " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new RuntimeException("无法访问JsonConverter: " + "" + "的构造函数! " + " 原始信息: " + e.getMessage());
		} catch (InstantiationException e) {
			throw new RuntimeException("指定的JsonConverter: " + "" + " 不是一个实现类! " + " 原始信息: " + e.getMessage());
		}
	}

	public static String toJson(Object object) {
		if (jsonConverter == null) {
			Json.init();
		}
		return jsonConverter.toJson(object);
	}

	public static Object parseObject(String json, Class oclass) {
		if (jsonConverter == null) {
			Json.init();
		}
		return jsonConverter.parseObject(json, oclass);
	}

	public static Map parseMap(String json) {
		if (jsonConverter == null) {
			Json.init();
		}
		return jsonConverter.parseMap(json);
	}
}
