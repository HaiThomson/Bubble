package source.kernel.db.pool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Hai Thomson
 */
public abstract class ConnectionPoolingDriver {

	abstract void init(String path);

	abstract void init(String path, String dbName);

	abstract Connection getConnection() throws SQLException;

	abstract void close();
}
