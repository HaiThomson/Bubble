package source.kernel.session;

import source.kernel.base.Base;

import java.util.Map;

/**
 * @author Hai Thomson
 */
public abstract class SessionProvider extends Base {

	protected SessionProvider() {

	}

	protected SessionProvider(String sessionid, String ip, String userid) {

	}

	protected abstract void set(String key, Object value);

	protected abstract Object get(String key);

	protected abstract boolean isExistent(String sessionid, String ip, String userid);

	protected abstract Map<String, Object> create(String sessionid, String ip, String userid);

	protected abstract long delete();

	protected abstract void update();

	protected abstract long clear();
}
