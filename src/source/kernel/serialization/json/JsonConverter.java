package source.kernel.serialization.json;

import java.util.Map;

/**
 * @author Hai Thomson
 */
public abstract class JsonConverter {
	public abstract String toJson(Object object);

	public abstract Object parseObject(String json, Class oclass);

	public abstract Map parseMap(String json);
}
