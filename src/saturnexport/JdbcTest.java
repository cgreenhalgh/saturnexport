/**
 * 
 */
package saturnexport;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

import java.util.logging.Level;


/**
 * @author cmg
 *
 */
public class JdbcTest {
	static Logger logger = Logger.getLogger(JdbcTest.class.getName());


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 *
		 */
		if (args.length!=1) {
			logger.log(Level.SEVERE, "Usage: <jdbc.properties>");
			System.exit(-1);
		}
		Connection conn = JdbcUtils.getConnection(args[0]);
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			// example: get known schools from saturn3 using stored procedure mod_get_schools
			stmt = conn.prepareStatement("{call mod_get_schools()}", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = stmt.executeQuery();
			while(rs.next()) {
				String code = rs.getString(1);
				String descr = rs.getString(2);
				System.out.println("Found "+code+" "+descr);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error", e);
		}
		finally {
			if (rs!=null)
				try { rs.close(); } catch (Throwable ignore) {}
			if (stmt!=null)
				try { stmt.close(); } catch (Throwable ignore) {}
			try { conn.close(); } catch (Throwable ignore) {}
		}
	}

}
