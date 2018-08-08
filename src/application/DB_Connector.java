package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB_Connector {
	private static Connection conn;

	public static Connection getConnection() throws SQLException {
		String user = "sa";
		String pass = "ashraf123";
		String url = "jdbc:sqlserver://AHO;databaseName=SCADA_Power_Generation_Report";
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			conn = DriverManager.getConnection(url, user, pass);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}

}
