package source.kernel.view.data.json;

import source.kernel.serialization.json.Json;

/**
 * @author Hai Thomson
 */
public class JsonView {
	public static String toJson(Object object) {
		return Json.toJson(object);
	}
}
