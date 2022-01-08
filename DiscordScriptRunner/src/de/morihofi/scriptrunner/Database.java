package de.morihofi.scriptrunner;

import java.sql.*;

public class Database {
	
	public static void db_init() {


		try (Connection conn = DriverManager.getConnection(Start.db_database, Start.db_user, Start.db_pass)) {

			// Class.forName("org.h2.Driver");
			Statement stmt = conn.createStatement();

			String createQ = "CREATE TABLE IF NOT EXISTS " + Start.db_table
					+ " (id INT NOT NULL AUTO_INCREMENT, user TEXT, time TEXT, codelang TEXT, code TEXT, output TEXT)";
			stmt.executeUpdate(createQ);

		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

	public static void db_insert(String user, String time, String codelang, String code, String output) {


		try (Connection conn = DriverManager.getConnection(Start.db_database, Start.db_user, Start.db_pass)) {

			// Class.forName("org.h2.Driver");
			Statement stmt = conn.createStatement();

			String insertQ = "INSERT INTO " + Start.db_table
					+ " (user,time,codelang,code,output) VALUES ('" + escapeSql(user) + "','"
					+ escapeSql(time) + "','" + escapeSql(codelang) + "','" + escapeSql(code) + "','" + escapeSql(output) + "')";
			stmt.executeUpdate(insertQ);

		} catch (SQLException e) {
			e.printStackTrace();

		}
	}

	public static String escapeSql(String str) {
		if (str == null) {
			return null;
		}
		return new String(str).replace("'", "''");
	}

}
