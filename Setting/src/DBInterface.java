import java.sql.Statement;

import javax.swing.JOptionPane;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBInterface {
	public static Connection conn;
	public static Statement stmt;
	
	public static void init() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");	// "org.mariadb.jdbc.Driver"
		conn = DriverManager.getConnection("jdbc:mysql://localhost/", "root", "abc123");
		stmt = conn.createStatement();
		
		System.out.println("DB Connect");
		
		
	}
}
