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
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
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
	 * Logger.
	 */
	private static final Logger log = Logger.getLogger( MySQLConnector.class.getName() );
	
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
		
		if (SystemProperty.environment.value() ==
		    SystemProperty.Environment.Value.Production) {
			// Connecting from App Engine.
			// Use GSQL DB.
			Class.forName("com.mysql.jdbc.GoogleDriver");
			url ="jdbc:google:mysql://gwtsoftengtest:moviedb/newmoviedb?user=root";
			conn = DriverManager.getConnection(url);
		} else {			
			// Development DB.
			Class.forName("com.mysql.jdbc.Driver").newInstance();
				
			//RPI url.
			String userName = "softEng";
			String pwd = "softEng";
			url = "jdbc:mysql://77.56.2.160:3306/newmoviedb";
			conn = DriverManager.getConnection(url, userName, pwd);
		}
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
	 * @throws SQLException Database access error.
	 * @throws InstantiationException Exception.
	 * @throws IllegalAccessException Exception.
	 * @throws ClassNotFoundException Exception.
	 */
	public static void sendToDBExtendedFileSet(FilmDataSet data) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
			
			openConnection();
			conn.setAutoCommit(false);
			
			//Send Countries.
			String countriesQuery = "INSERT INTO countries (countryid, country) VALUE (?, ?);";
			PreparedStatement countriesPreparedStatement = conn.prepareStatement(countriesQuery);
			for(Map.Entry<String, String> cursor : data.getCountries().entrySet()){
				try{
					countriesPreparedStatement.setString(1, cursor.getValue());
					countriesPreparedStatement.setString(2, cursor.getKey());
					countriesPreparedStatement.addBatch();
				}catch(MySQLIntegrityConstraintViolationException e){
					//Happens when a key is already present in db. In this case just ignore error.
				}
			}
			countriesPreparedStatement.executeBatch();
			conn.commit();
			System.out.println("Finished sending countries data.");
			
			//Send Languages.
			String languagesQuery = "INSERT INTO languages (languageid, language) VALUE (?, ?);";
			PreparedStatement languagesPreparedStatement = conn.prepareStatement(languagesQuery);
			for(Map.Entry<String, String> cursor : data.getLanguages().entrySet()){
				try{
					languagesPreparedStatement.setString(1, cursor.getValue());
					languagesPreparedStatement.setString(2, cursor.getKey());
					languagesPreparedStatement.addBatch();
				}catch(MySQLIntegrityConstraintViolationException e){
					//Happens when a key is already present in db. In this case just ignore error.
				}
			}
			languagesPreparedStatement.executeBatch();
			conn.commit();
			System.out.println("Finished sending languages data.");
			
			//Send Genres.
			String genresQuery = "INSERT INTO genres (genreid, genre) VALUE (?, ?);";
			PreparedStatement genresPreparedStatement = conn.prepareStatement(genresQuery);
			for(Map.Entry<String, String> cursor : data.getGenres().entrySet()){
				try{
					genresPreparedStatement.setString(1, cursor.getValue());
					genresPreparedStatement.setString(2, cursor.getKey());
					genresPreparedStatement.addBatch();
				}catch(MySQLIntegrityConstraintViolationException e){
					//Happens when a key is already present in db. In this case just ignore error.
				}
			}
			genresPreparedStatement.executeBatch();
			conn.commit();
			System.out.println("Finished sending genres data.");
			
//			Send moviecountries data.
			String moviesCountriesQuery = "INSERT INTO moviecountries (movieid, countryid) VALUES (?, ?);";
			int j = 0;
			PreparedStatement movieCountriesPreparedStatement = conn.prepareStatement(moviesCountriesQuery);
			for(FilmData film : data.getFilms()){
				int movieID = film.getID();
				
				for(int i = 0; i < film.getCountries().size(); i+=2){
					movieCountriesPreparedStatement.setInt(1, movieID);
					movieCountriesPreparedStatement.setString(2, film.getCountries().get(i));
					
					movieCountriesPreparedStatement.addBatch();
				}
				if(++j % 500 == 0){
					System.out.println(j/800.0 + "% completed");
					movieCountriesPreparedStatement.executeBatch();
					conn.commit();
				}
			}
			movieCountriesPreparedStatement.executeBatch();
			conn.commit();
			System.out.println("Finished sending moviecountries data.");
			
			//Send movielanguages data.
			String movieLanguagesQuery = "INSERT INTO movielanguages (movieid, languageid) VALUES (?, ?);";
			j = 0;
			PreparedStatement movieLanguagesPreparedStatement = conn.prepareStatement(movieLanguagesQuery);
			for(FilmData film : data.getFilms()){
				int movieID = film.getID();
				
				for(int i = 0; i < film.getLanguages().size(); i+=2){
					movieLanguagesPreparedStatement.setInt(1, movieID);
					movieLanguagesPreparedStatement.setString(2, film.getLanguages().get(i));
					
					movieLanguagesPreparedStatement.addBatch();
				}
				if(++j % 500 == 0){
					System.out.println(j/800.0 + "% completed");
					movieLanguagesPreparedStatement.executeBatch();
					conn.commit();
				}
			}
			movieLanguagesPreparedStatement.executeBatch();
			conn.commit();
			System.out.println("Finished sending movielanguages data.");
			
			//Send moviegenres data.
			String movieGenresQuery = "INSERT INTO moviegenres (movieid, genreid) VALUES (?, ?);";
			j = 0;
			PreparedStatement movieGenresPreparedStatement = conn.prepareStatement(movieGenresQuery);
			for(FilmData film : data.getFilms()){
				int movieID = film.getID();
				
				for(int i = 0; i < film.getGenres().size(); i+=2){
					movieGenresPreparedStatement.setInt(1, movieID);
					movieGenresPreparedStatement.setString(2, film.getGenres().get(i));
					
					movieGenresPreparedStatement.addBatch();
				}
				if(++j % 500 == 0){
					System.out.println(j/800.0 + "% completed");
					movieGenresPreparedStatement.executeBatch();
					conn.commit();
				}
			}
			movieGenresPreparedStatement.executeBatch();
			conn.commit();
			System.out.println("Finished sending moviegenres data.");
		
			//Movies
			j = 0;
			String moviesTableQuery = "INSERT INTO movies (movieid, title, date, duration) VALUE(?, ?, ?, ?);";
			PreparedStatement preparedStmt = conn.prepareStatement(moviesTableQuery);
			for (FilmData movie: data.getFilms()){
				preparedStmt.setInt(1, movie.getID());
				preparedStmt.setString(2, movie.getTitle());
				preparedStmt.setInt(3, movie.getDate());
				preparedStmt.setFloat(4, movie.getDuration());
				preparedStmt.addBatch();
				if(++j % 5000 == 0){
					System.out.println(j/800.0 + "% completed");
					preparedStmt.executeBatch();
					conn.commit();
				}
			}
			preparedStmt.executeBatch();
			conn.commit();
			System.out.println("Finished sending movies data.");
		
			closeConnection();	
	}
	
	/**
	 * Reads from the database.
	 * @param query Query to send to database.
	 * @return Query result.
	 * @throws SQLException Exception.
	 * @throws ClassNotFoundException Exception. 
	 * @throws IllegalAccessException Exception.
	 * @throws InstantiationException Exception.
	 */
	public static ArrayList<FilmData> readFromDB(String query) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		stmt = null;
		rs = null;
		
		ArrayList<FilmData> result = new ArrayList<FilmData>();
		
		openConnection();
		
		//Create a statement object to hold the query.
		stmt = conn.createStatement();
		
		//Executes and saves query result.
		rs = stmt.executeQuery(query);
		
		while (rs.next()) {
			int id = rs.getInt("movieid");
			String title = rs.getString("title");
			int date = rs.getInt("date");
			Float duration = rs.getFloat("duration");
			
			String genre = rs.getString("genres");
			String language = rs.getString("languages");
			String country = rs.getString("countries");
			
			ArrayList<String> genres = null;
			if (genre != null){
				genres = new ArrayList<String>(Arrays.asList(genre.split(",")));
			}
			else{
				genres = new ArrayList<String>();
				genres.add("");
			}
			
			ArrayList<String> languages = null;
			if (language != null){
				languages = new ArrayList<String>(Arrays.asList(language.split(",")));
			}
			else{
				languages = new ArrayList<String>();
				languages.add("");
			}
			
			ArrayList<String> countries = null;
			if (country != null){
				countries = new ArrayList<String>(Arrays.asList(country.split(",")));
			}
			else{
				countries = new ArrayList<String>();
				countries.add("");
			}

			FilmData film = new FilmData(id, title, date, duration, languages, countries, genres);
			result.add(film);
		}
		closeConnection();
		try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
		return result;
	} 
	
	/**
	 * Get film data set size.
	 * @return size of data set.
	 * @throws InstantiationException Exception.
	 * @throws IllegalAccessException Exception.
	 * @throws ClassNotFoundException Exception.
	 * @throws SQLException Exception.
	 */
	public static Integer getFilmDataSetSize() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		Integer result = -1;
		String query = "SELECT COUNT(*) FROM movies;";
		
		openConnection();
		
		stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		
		if(rs.next())
			result = rs.getInt(1);
		
		
		return result;
	}
	
	/**
	 * Open tsv file and send all data to database.
	 */
	public static void sendAllDataFromFileToDB(){
		FilmDataSet films;
		try {
			//Original data set.
			films = TSVImporter.importFilmData("war/WEB-INF/Resources/movies_80000.tsv");
			ArrayList<FilmData> tmp = films.getFilms();
			//Extra data set.
			films = TSVImporter.importFilmData("war/WEB-INF/Resources/movies_1471.tsv");
			tmp.addAll(films.getFilms());
			
			films.setDataSet(tmp);
			
			System.out.println("Sending to db");
			
			sendToDBExtendedFileSet(films);
		} catch (IOException | SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e1) {
			log.log(Level.SEVERE, e1.toString());
		}

	}
	
	/**
	 * Get all genres, languages or countries of data set for the suggestions list.
	 * @param query Query to get all genres, languages or countries.
	 * @param sizeQuery Query to get size of desired list.
	 * @return a string array containing all genres, languages or countries.
	 * @throws InstantiationException Exception.
	 * @throws IllegalAccessException Exception.
	 * @throws ClassNotFoundException Exception.
	 * @throws SQLException Exception.
	 */
	public static String[] getSuggestions(String query, String sizeQuery) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		int size = 0;
		
		openConnection();
		
		stmt = conn.createStatement();
		
		ResultSet rs = stmt.executeQuery(sizeQuery);
		
		if(rs.next())
			size = rs.getInt(1);
		
		log.log(Level.INFO, "" + size);
		
		String[] result = new String[size];
		int i = 0;
		ResultSet rs2 = stmt.executeQuery(query);
		while(rs2.next()){
			result[i++] = rs2.getString(1);
		}
		
		closeConnection();
		
		return result;
	}
}