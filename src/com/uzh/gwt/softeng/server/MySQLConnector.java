package com.uzh.gwt.softeng.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.appengine.api.utils.SystemProperty;
import com.uzh.gwt.softeng.shared.FilmData;
import com.uzh.gwt.softeng.shared.FilmDataSet;

/**
 * The {@code MYSQLConnector} class is responsible for connecting to a mySQL database 
 * and for reading and writing to it.
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
	 * Opens connection to MySQL database instance.
	 * @throws SQLException If it cannot open connection.
	 * @throws InstantiationException If the class or its nullary constructor is not accessible.
	 * @throws IllegalAccessException If this Class represents an abstract class, an interface, 
	 * an array class, a primitive type, or void; or if the class has no nullary constructor; or if the instantiation fails for some other reason.
	 * @throws ClassNotFoundException If the class cannot be located.
	 */
	public static void openConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{	
		conn = null;
		rs = null;
		String url = null;
		
//		String userName = "softEng";
//		String pwd = "softEng";
		
		if (SystemProperty.environment.value() ==
		    SystemProperty.Environment.Value.Production) {
			// Connecting from App Engine.
			// Load the class that provides the "jdbc:google:mysql://"
			// prefix.
			Class.forName("com.mysql.jdbc.GoogleDriver");
			url = "jdbc:google:mysql://gwtsofteng:moviedb/test_movie_db?user=softEng";
		} else {			
			// Connecting from an external network.
			//Load driver class from .jar file.
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			//Google SQL url.
			url = "jdbc:mysql://173.194.238.0:3306/test_movie_db?user=softEng";
				
			//RPI url.
//			url = "jdbc:mysql://77.56.2.160:3306/test_movie_db";
		}
	
//		conn = DriverManager.getConnection(url, userName, pwd);
		conn = DriverManager.getConnection(url);
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
	 * @param table The table of the database.
	 * @throws SQLException Database access error.
	 */
	public static void sendToDB(ArrayList<FilmData> data, String table) throws SQLException{
		conn = null;
		rs = null;
		stmt = null;
		
		//Build placeholder query for PreparedStatement.
		//Send 10000 filmData objects per query.
		String startOfQuery = "INSERT INTO " + table + " (movieid, title, duration, country) VALUES ";
		String placeholder = ("(?, ?, ?, ?), ");
		StringBuilder sb = new StringBuilder(placeholder);
		int counter = 9999;
		for(int i = 0; i < counter; i++)
			sb.append(placeholder);
		
		String query = startOfQuery + sb.toString();
		query = query.substring(0, query.length()- 2).concat(";");

		//Counter to use with preparedStmt.set*(int, Obj);
		int placeholderCounter = 0;
		try {
			//Open connection to db.
			openConnection();
			
			//For each movie in the film data set, prepare the query.
			//Each 10000 films send a single query.
			stmt = conn.createStatement();
			int i = 0;
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			for (FilmData movie: data){
				
				preparedStmt.setInt(++placeholderCounter, (int) movie.getID());
				preparedStmt.setString(++placeholderCounter, movie.getTitle());
				preparedStmt.setFloat(++placeholderCounter, movie.getDuration());
				preparedStmt.setString(++placeholderCounter, movie.getCountries().get(0));
				
				if(++i % 10000 == 0){
					System.out.println((i / 800.0) + "% completed.");
					preparedStmt.execute();
					preparedStmt.clearParameters();
					placeholderCounter = 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}
		
	/**
	 * Reads all data from the database table.
	 * @param table Table from which to read all data.
	 * @return A list containing all FilmData read from db/table.
	 */
	public static ArrayList<FilmData> readAllDataFromDB(String table){
		stmt = null;
		rs = null;
		
		ArrayList<FilmData> result = new ArrayList<FilmData>();
		FilmData film = null;
		
		try {
			openConnection();
			
			//Create a statement object to hold the query.
			stmt = conn.createStatement();
			
			//Executes and saves query result.
			rs = stmt.executeQuery("SELECT * FROM " + table + ";");
			
			while (rs.next()) {
				String id = rs.getString("movieid");
				String title = rs.getString("title");
				String duration = rs.getString("duration");
				String country = rs.getString("country");
				ArrayList<String> countries = new ArrayList<String>(Arrays.asList(country.replace("{", "").replace("}", "").split(",")));
				film = new FilmData(Long.parseLong(id), title, Float.parseFloat(duration), countries);
				result.add(film);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection();
			try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
		return result;
	}
	
//	private static FilmDataSet parseResultSet(ResultSet rs){
//		ArrayList<FilmData> result = new ArrayList<FilmData>();
//		FilmData film;
//		try {
//			while (rs.next()) {
//				String id = rs.getString("movieid");
//				String title = rs.getString("title");
//				String duration = rs.getString("duration");
//				String country = rs.getString("country");
//				ArrayList<String> countries = new ArrayList<String>(Arrays.asList(country.replace("{", "").replace("}", "").split(",")));
//				film = new FilmData(Long.parseLong(id), title, Float.parseFloat(duration), countries);
//				result.add(film);
//			}
//		} catch (NumberFormatException | SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return new FilmDataSet(result);
//	}
	
	/**
	 * Reads from the database.
	 * @param query Query to send to database.
	 * @return Query result.
	 */
	public static ArrayList<FilmData> readFromDB(String query){
		stmt = null;
		rs = null;
		
		ArrayList<FilmData> result = new ArrayList<FilmData>();
		
		try {
			openConnection();
			
			//Create a statement object to hold the query.
			stmt = conn.createStatement();
			
			//Executes and saves query result.
			rs = stmt.executeQuery(query);
			
			while (rs.next()) {
				String id = rs.getString("movieid");
				String title = rs.getString("title");
				String duration = rs.getString("duration");
				String country = rs.getString("country");
				ArrayList<String> countries = new ArrayList<String>(Arrays.asList(country.replace("{", "").replace("}", "").split(",")));
				FilmData film = new FilmData(Long.parseLong(id), title, Float.parseFloat(duration), countries);
				result.add(film);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeConnection();
			try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
		return result;
	} 
	
	/**
	 * Open tsv file and send all data to database.
	 * @param table Table to send data to.
	 */
	public static void sendAllDataFromFileToDB(String table){
		FilmDataSet films;
		try {
			films = TSVImporter.importFilmData("war/WEB-INF/Resources/movies_80000.tsv");
			sendToDB(films.getFilms(), table);
		} catch (IOException | SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	public static void main(String[] args){
		sendAllDataFromFileToDB("movies");
	}
}