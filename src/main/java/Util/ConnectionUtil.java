package Util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionUtil {

    private static final String rootDir = System.getProperty("user.dir");
	private static final String connectionString = "jdbc:sqlite:test.db"; //+ rootDir + "/db.sqlite";

	private static Connection conn;

	// How to do this pattern with a try-with-resources
	public static Connection getConnection() {
		if (conn == null) {
			try {
				conn = DriverManager.getConnection(connectionString);
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return conn;
	}

	public static void resetTestDatabase() {

		String connectionString = "jdbc:sqlite:test.db";  		  // Path to new SQLite database
		String sqlReader = "src/main/resources/SocialMedia.sql";  // Path to the SQL file

		try (Connection conn =  DriverManager.getConnection(connectionString)){
			String sql = readSqlFile(sqlReader);

			try (Statement stmt = conn.createStatement()) {
				stmt.executeUpdate(sql);
			} catch (SQLException e) {
				System.out.println("Error executing SQL: " + e.getMessage());
			}

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	public static String readSqlFile(String filePath) throws IOException {
		StringBuilder sql = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				sql.append(line).append("\n");
			}
		}
		return sql.toString();
	}
}




