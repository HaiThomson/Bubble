package source.kernel.serialization.json.fastjson;

import com.alibaba.fastjson.JSON;
import source.kernel.serialization.json.JsonConverter;

import java.util.Map;

/**
 * @author Hai Thomson
 */
public class fastjsonConverter extends JsonConverter{
	@Override
	public String toJson(Object object) {
		return JSON.toJSON(object).toString();
	}

	@Override
	public Object parseObject(String json, Class oclass) {
		return JSON.parseObject(json, oclass);
	}

	@Override
	public Map parseMap(String json) {
		return JSON.parseObject(json);
	}
}
