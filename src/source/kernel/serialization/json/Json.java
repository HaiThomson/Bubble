package source.kernel.serialization.json;

import source.kernel.base.ExceptionHandler;

import java.util.Map;

/**
 * @author Hai Thomson
 */
public class Json {

	public static JsonConverter jsonConverter = null;

	static {
		try {
			// 修改为加载配置
			Class<?> dbDriverClass = Class.forName("source.kernel.serialization.json.fastjson.fastjsonConverter");
			jsonConverter = (JsonConverter) dbDriverClass.newInstance();
		} catch (Exception e) {
			ExceptionHandler.handling(e);
		}
	}

	public static String toJson(Object object) {
		return jsonConverter.toJson(object);
	}

	public static Object parseObject(String json, Class oclass) {
		return jsonConverter.parseObject(json, oclass);
	}

	public static Map parseMap(String json) {
		return jsonConverter.parseMap(json);
	}
}
