package source.kernel.session.oracle;

import source.kernel.session.SessionProvider;

import java.sql.SQLException;

/**
 * @author Hai Thomson
 */
public class WebContainerSessionProvider extends SessionProvider {
	@Override
	protected void set(String key, Object value) {

	}

	@Override
	protected Object get(String key) {
		return null;
	}

	@Override
	protected boolean isExistent(String sessionid, String ip, String userid) throws SQLException {
		return false;
	}

	@Override
	protected void create(String sessionid, String ip, String userid) throws SQLException {

	}

	@Override
	protected long delete() {
		return 0;
	}

	@Override
	protected void update(boolean isnew) throws SQLException {
		// Nothing to do
	}

	@Override
	public long clear() {
		// 此方法对HttpSession没有意义.永远返回0
		return 0;
	}
}
