package source.aspect.transaction;

import source.kernel.DB;
import source.kernel.base.AOP;

import java.sql.SQLException;

/**
 * 正在设计
 * @author Hai Thomson
 */
public class TransactionAOP extends AOP {

	public Object call(String methodName, Object... params) {

		Object results = null;

		try {
			DB.beginTransaction();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}

		try {
			results = super.call(methodName, params);
		} catch (Exception e) {
			try {
				DB.rollBackTransaction();
				DB.closeConnection();
			} catch (SQLException sqlException) {
				throw new RuntimeException(sqlException.getMessage());
			}

			throw new RuntimeException(e.getMessage());
		}

		try {
			DB.commitTransaction();
		} catch (SQLException e) {
			try {
				DB.rollBackTransaction();
				DB.closeConnection();
			} catch (SQLException sqlException) {
				throw new RuntimeException(sqlException.getMessage());
			}

			throw new RuntimeException(e.getMessage());
		}

		try {
			DB.closeConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage());
		}

		return results;
	}
}
