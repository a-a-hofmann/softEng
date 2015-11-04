package com.manning.gwtia.ch03.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	private static int i = 1;
	
	/**
	 * Parses access file containing log-in info to database instance.
	 * @param pathToFile A String containing the path (relative to the project).
	 * @return A list of tokens containing in this order: url, username, password.
	 */
	public static ArrayList<String> parseAccessFile(String pathToFile){
		ArrayList<String> accessInfo = new ArrayList<String>();
		//Read access info from file.
		try {
			//Open file.
			BufferedReader accessFile = new BufferedReader(new FileReader(pathToFile));
			
			//Read each line.
			String line = accessFile.readLine();
			while(line != null){
				//'#' is a comment line, ignore it.
				while(line.startsWith("#")) 
					line = accessFile.readLine();
				accessInfo.add(line);
				line = accessFile.readLine();
				
			}
			
			accessFile.close();
		} catch (IOException e) {
			System.err.println("Error: Unable to find log-in info.\n");
		}
		
		return accessInfo;
	}
	
	/**
	 * Opens connection to MySQL database instance.
	 * @throws SQLException If it cannot open connection.
	 * @throws InstantiationException If the class or its nullary constructor is not accessible.
	 * @throws IllegalAccessException If this Class represents an abstract class, an interface, 
	 * an array class, a primitive type, or void; or if the class has no nullary constructor; or if the instantiation fails for some other reason.
	 * @throws ClassNotFoundException If the class cannot be located.
	 */
	public static void openConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		//Read access info from file
		ArrayList<String> accessInfo = parseAccessFile("/Users/alexanderhofmann/Documents/workspace/StockWatcher/war/Resources/AccessInfo.txt");

		conn = null;
		rs = null;
		
		//Load driver class from .jar file.
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		
		//Create connection to database.
		conn = DriverManager.getConnection(accessInfo.get(0), accessInfo.get(1), accessInfo.get(2));

	}
	
	/**
	 * Closes Connection to MySQL database instance.
	 */
	public static void closeConnection(){
		try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
		try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
	}
	
	
	/**
	 * Sends data to the database.
	 * @param data The data set to send to the database.
	 */
//	public static void sendToDB(ArrayList<FilmData> data){
//		conn = null;
//		rs = null;
//		
//		try {
//			openConnection();
//			
//			//Create a placeholder query.
//			String query = " insert into movies (movieid, title, duration, country)"
//			        + " values (?, ?, ?, ?);";
//			
//			//For each movie in the film data set, prepare a query and send it
//			//to the database.
//			for (FilmData movie: data){
//				//Prepare statement to send afterwards.
//				PreparedStatement preparedStmt = conn.prepareStatement(query);
//				preparedStmt.setInt(1, (int) movie.getID());
//				preparedStmt.setString(2, movie.getTitle());
//				preparedStmt.setFloat(3, movie.getDuration());
//				preparedStmt.setString(4, movie.getCountries().get(0));
//				try{
//					preparedStmt.execute();
//				} catch (MySQLIntegrityConstraintViolationException mySQLe){
//					mySQLe.printStackTrace();
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			closeConnection();
//		}
//	}
	
	
	/**
	 * Reads from the database.
	 */
	public static String[] readFromDB(){
		stmt = null;
		rs = null;
		String[] data = new String[4];
		
		try {
			openConnection();
			
			//Create a statement object to hold the query.
			stmt = conn.createStatement();
			
			//Executes and saves query result.
			rs = stmt.executeQuery("SELECT * FROM movies");
			
//			while (rs.next()) {
			for(int j = 0; j < i; j++)
				rs.next();
				
			i++;
				String id = rs.getString("movieid");
				String title = rs.getString("title");
				String duration = rs.getString("duration");
				String country = rs.getString("country");
//				System.out.println("ID: " + id + ", Title: " + title
//						+ ", Duration: " + duration + ", Country: "+ country);
				data[0] = id;
				data[1] = title;
				data[2] = duration;
				data[3] = country;
				
//				ArrayList<String> countries = new ArrayList<String>();
//				countries.add(country);
//				film = new FilmData(Long.parseLong(id), title,  9999, 
//			    		Float.parseFloat(duration), new ArrayList<String>(), countries, new ArrayList<String>());
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection();
			try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
		return data;
	} 
	
	public static void main(String[] args){
		
		for(int i = 0; i < 10; i++){
			for(String tmp: readFromDB())
				System.out.print(tmp + " ");
			System.out.println();
		}
	}
}