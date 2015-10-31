package com.manning.gwtia.ch03.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

/**
 * The {@code MYSQLConnector} class is responsible for connecting to a mySQL database 
 * and for reading and writing to it.
 *
 */
public class MySQLConnector {
	/**
	 * Connection to database.
	 */
	private static Connection conn;
	
	/**
	 * Statement to send to database.
	 */
	private static Statement stmt;
	
	/**
	 * Query result.
	 */
	private static ResultSet rs;
	
	/**
	 * url to database.
	 */
	private static String connectionUrl = "jdbc:mysql://raspy.remote:3306/test_movie_db";
	
	/**
	 * Access info to database.
	 */
	private static String connectionUser = "softEng";
	
	/**
	 * Access info to database.
	 */
	private static String connectionPassword = "softEng";
	
	
	/**
	 * Sends data to the database.
	 * @param data The data set to send to the database.
	 */
	public static void sendToDB(ArrayList<FilmData> data){
		conn = null;
		rs = null;
		
		try {
			//Load driver class from .jar file.
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			//Create connection to database.
			conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
			
			//Create a placeholder query.
			String query = " insert into movies (movieid, title, duration, country)"
			        + " values (?, ?, ?, ?);";
			
			//For each movie in the film data set, prepare a query and send it
			//to the database.
			for (FilmData movie: data){
				//Prepare statement to send afterwards.
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				preparedStmt.setInt(1, (int) movie.getID());
				preparedStmt.setString(2, movie.getTitle());
				preparedStmt.setFloat(3, movie.getDuration());
				preparedStmt.setString(4, movie.getCountries());
				try{
				preparedStmt.execute();
				} catch (MySQLIntegrityConstraintViolationException mySQLe){
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
	}
	
	
	/**
	 * Reads from the database.
	 */
	public static void readFromDB(){
		conn = null;
		stmt = null;
		rs = null;
		
		try {
			//Load driver class from .jar file.
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			//Create connection to database.
			conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
			
			//Create a statement object to hold the query.
			stmt = conn.createStatement();
			
			//Executes and saves query result.
			rs = stmt.executeQuery("SELECT * FROM movies");
			
			while (rs.next()) {
				String id = rs.getString("movieid");
				String title = rs.getString("title");
				String duration = rs.getString("duration");
				String country = rs.getString("country");
				System.out.println("ID: " + id + ", Title: " + title
						+ ", Duration: " + duration + ", Country: "+ country);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
	}
}