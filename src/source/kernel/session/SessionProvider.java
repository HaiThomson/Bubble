package source.kernel.session;

import source.kernel.base.Base;

/**
 * @author Hai Thomson
 */
public abstract class SessionProvider extends Base {

	protected abstract void set(String key, Object value);

	protected abstract Object get(String key);

	protected abstract boolean isExistent(String sessionid, String ip, String userid);

	protected abstract void create(String sessionid, String ip, String userid);

	protected abstract long delete();

	protected abstract void update(boolean isnew);

	protected abstract long clear();
}
