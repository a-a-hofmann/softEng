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
			url = "jdbc:mysql://173.194.238.0:3306/moviedb?user=softEng";
				
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
	 * Creates query to use to insert into movies table.
	 * @return String containing query placeholder.
	 */
	private static String getMoviesPlaceholder(){
		String startOfQuery = "INSERT INTO movies (movieid, title, date, duration) VALUES ";
		String placeholder = "(?, ?, ?, ?), ";
		StringBuilder sb = new StringBuilder("");
		int counter = 10000;
		for(int i = 0; i < counter; i++)
			sb.append(placeholder);
		
		String query = startOfQuery + sb.toString();
		query = query.substring(0, query.length()- 2).concat(";");
		
		return query;
	}
	
	/**
	 * Creates query to use to insert into countries table.
	 * @param filmDataSet FilmDataSet to use to get all countries in the FilmDataSet.
	 * @return String containing query placeholder.
	 */
	private static String getCountriesPlaceholder(FilmDataSet filmDataSet){
		String startOfQuery = "INSERT INTO countries (countryid, country) VALUES ";
		String placeholder = "(?, ?), ";
		StringBuilder sb = new StringBuilder("");
		for(int i = 0; i < filmDataSet.getCountries().size(); i++)
			sb.append(placeholder);
		String query = startOfQuery + sb.toString();
		query = query.substring(0, query.length() - 2).concat(";");
		
		return query;
	}

	/**
	 * Creates query to use to insert into genres table.
	 * @param filmDataSet FilmDataSet to use to get all genres in the FilmDataSet.
	 * @return String containing query placeholder.
	 */
	private static String getGenresPlaceholder(FilmDataSet filmDataSet){
		String startOfQuery = "INSERT INTO genres (genreid, genre) VALUES ";
		String placeholder = "(?, ?), ";
		StringBuilder sb = new StringBuilder("");
		for(int i = 0; i < filmDataSet.getGenres().size(); i++)
			sb.append(placeholder);
		String query = startOfQuery + sb.toString();
		query = query.substring(0, query.length() - 2).concat(";");
		
		return query;
	}
	
	/**
	 * Creates query to use to insert into languages table.
	 * @param filmDataSet FilmDataSet to use to get all languages in the FilmDataSet.
	 * @return String containing query placeholder.
	 */
	private static String getLanguagesPlaceholder(FilmDataSet filmDataSet){
		String startOfQuery = "INSERT INTO languages (languageid, language) VALUES ";
		String placeholder = "(?, ?), ";
		StringBuilder sb = new StringBuilder("");
		for(int i = 0; i < filmDataSet.getLanguages().size(); i++)
			sb.append(placeholder);
		String query = startOfQuery + sb.toString();
		query = query.substring(0, query.length() - 2).concat(";");
		
		return query;
	}
	
	/**
	 * Sends data to the database.
	 * @param data The data set to send to the database.
	 * @param table The table of the database.
	 * @throws SQLException Database access error.
	 */
	public static void sendToDB(FilmDataSet data, String table) throws SQLException{
		conn = null;
		rs = null;
		stmt = null;
		
		//Build placeholder query for PreparedStatement.
		//Send 10000 filmData objects per query.
		String moviesTableQuery = getMoviesPlaceholder();
		String countriesTableQuery = getCountriesPlaceholder(data);
		String genresTableQuery = getGenresPlaceholder(data);
		String languagesTableQuery = getLanguagesPlaceholder(data);

		//Counter to use with preparedStmt.set*(int, Obj);
		int moviesPlaceholderCounter = 0;
		int countriesPlaceholderCounter = 0;
		int genresPlaceholderCounter = 0;
		int languagesPlaceholderCounter = 0;
		
		try {
			//Open connection to db.
			openConnection();
			
			
			//Send Countries.
			PreparedStatement countriesPreparedStatement = conn.prepareStatement(countriesTableQuery);
			for(Map.Entry<String, String> cursor : data.getCountries().entrySet()){
				countriesPreparedStatement.setString(++countriesPlaceholderCounter, cursor.getValue());
				countriesPreparedStatement.setString(++countriesPlaceholderCounter, cursor.getKey());
			}
			countriesPreparedStatement.execute();
			
			//Send moviecountries data.
			String moviesCountriesQuery = "INSERT INTO moviecountries (mcid, movieid, countryid) VALUES (?, ?, ?);";
			int j = 0;
			PreparedStatement movieCountriesPreparedStatement = conn.prepareStatement(moviesCountriesQuery);
			for(FilmData film : data.getFilms()){
				int movieID = film.getID();
				
				for(int i = 0; i < film.getCountries().size()/2; i+=2){
					movieCountriesPreparedStatement.setInt(1, ++j); 
					movieCountriesPreparedStatement.setInt(2, movieID);
					movieCountriesPreparedStatement.setString(3, film.getCountries().get(i));
					
					movieCountriesPreparedStatement.addBatch();
				}
				
				if (j % 1000 == 0 || j == data.getFilms().size()){
					System.out.println(j/800.0 + "% completed.");
					movieCountriesPreparedStatement.executeBatch();
				}
			}
			System.out.println("Finished sending countries data.");
			
			
			//Send Languages.
			PreparedStatement languagesPreparedStatement = conn.prepareStatement(languagesTableQuery);
			for(Map.Entry<String, String> cursor : data.getLanguages().entrySet()){
				languagesPreparedStatement.setString(++languagesPlaceholderCounter, cursor.getValue());
				languagesPreparedStatement.setString(++languagesPlaceholderCounter, cursor.getKey());
			}
			languagesPreparedStatement.execute();
			System.out.println("Finished sending languages data.");
			
			//Send movielanguages data.
			String movieLanguagesQuery = "INSERT INTO movielanguages (mlid, movieid, languageid) VALUES (?, ?, ?);";
			j = 0;
			PreparedStatement movieLanguagesPreparedStatement = conn.prepareStatement(movieLanguagesQuery);
			for(FilmData film : data.getFilms()){
				int movieID = film.getID();
				
				for(int i = 0; i < film.getLanguages().size()/2; i+=2){
					movieLanguagesPreparedStatement.setInt(1, ++j); 
					movieLanguagesPreparedStatement.setInt(2, movieID);
					movieLanguagesPreparedStatement.setString(3, film.getLanguages().get(i));
					
					movieLanguagesPreparedStatement.addBatch();
				}
				
				if (j % 10000 == 0 || j == data.getFilms().size()){
					System.out.println(j/800.0 + "% completed.");
					movieLanguagesPreparedStatement.executeBatch();
				}
			}
			System.out.println("Finished sending languages data.");
			
			//Send Genres.
			PreparedStatement genresPreparedStatement = conn.prepareStatement(genresTableQuery);
			for(Map.Entry<String, String> cursor : data.getGenres().entrySet()){
				genresPreparedStatement.setString(++genresPlaceholderCounter, cursor.getValue());
				genresPreparedStatement.setString(++genresPlaceholderCounter, cursor.getKey());
			}
			genresPreparedStatement.execute();
			System.out.println("Finished sending genres data.");
			
			//Send moviegenres data.
			String movieGenresQuery = "INSERT INTO moviegenres (mgid, movieid, genreid) VALUES (?, ?, ?);";
			j = 0;
			PreparedStatement movieGenresPreparedStatement = conn.prepareStatement(movieGenresQuery);
			for(FilmData film : data.getFilms()){
				int movieID = film.getID();
				
				for(int i = 0; i < film.getGenres().size()/2; i+=2){
					movieGenresPreparedStatement.setInt(1, ++j); 
					movieGenresPreparedStatement.setInt(2, movieID);
					movieGenresPreparedStatement.setString(3, film.getGenres().get(i));
					
					movieGenresPreparedStatement.addBatch();
				}
				
				if (j % 10000 == 0 || j == data.getFilms().size()){
					System.out.println(j/800.0 + "% completed.");
					movieGenresPreparedStatement.executeBatch();
				}
			}
			System.out.println("Finished sending genres data.");
			
			
			//For each movie in the film data set, prepare the query.
			//Each 10000 films send a single query.

			int i = 0;
			PreparedStatement preparedStmt = conn.prepareStatement(moviesTableQuery);
			for (FilmData movie: data.getFilms()){
				
				preparedStmt.setInt(++moviesPlaceholderCounter, movie.getID());
				preparedStmt.setString(++moviesPlaceholderCounter, movie.getTitle());
				preparedStmt.setFloat(++moviesPlaceholderCounter, movie.getDate());
				preparedStmt.setFloat(++moviesPlaceholderCounter, movie.getDuration());
				
				if(++i % 10000 == 0){
					System.out.println((i / 800.0) + "% completed.");
					preparedStmt.execute();
					preparedStmt.clearParameters();
					moviesPlaceholderCounter = 0;
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
				String duration = rs.getString("date");
				String country = rs.getString("duration");
				ArrayList<String> countries = new ArrayList<String>(Arrays.asList(country.split(",")));
				film = new FilmData(Integer.parseInt(id), title, Float.parseFloat(duration), countries);
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
				int id = rs.getInt("movieid");
				String title = rs.getString("title");
				int date = rs.getInt("date");
				Float duration = rs.getFloat("duration");
				
				String genre = rs.getString("genres");
				String language = rs.getString("languages");
				String country = rs.getString("countries");
				
				ArrayList<String> genres = null;
				if (genre != null){
					genres = new ArrayList<String>(Arrays.asList(genre));
				}
				else{
					genres = new ArrayList<String>();
					genres.add("null");
				}
				
				ArrayList<String> languages = null;
				if (language != null){
					languages = new ArrayList<String>(Arrays.asList(language));
				}
				else{
					languages = new ArrayList<String>();
					languages.add("null");
				}
				
				ArrayList<String> countries = null;
				if (country != null){
					countries = new ArrayList<String>(Arrays.asList(country));
				}
				else{
					countries = new ArrayList<String>();
					countries.add("null");
				}
	
				FilmData film = new FilmData(id, title, date, duration, languages, countries, genres);
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
			sendToDB(films, table);
		} catch (IOException | SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
	
	public static void main(String[] args){
//		sendAllDataFromFileToDB("movies");
		String query = "select m.*, group_concat(DISTINCT g.genre) genres, "
				+ "group_concat(DISTINCT l.language) languages, "
				+ "group_concat(DISTINCT c.country) countries "
				+ "from movies m left join moviegenres mg on m.movieid=mg.movieid "
				+ "left join genres g on g.genreid=mg.genreid "
				+ "left join movielanguages ml on m.movieid=ml.movieid "
				+ "left join languages l on l.languageid=ml.languageid "
				+ "left join moviecountries mc on m.movieid=mc.movieid "
				+ "left join countries c on c.countryid=mc.countryid "
				+ "group by m.movieid;";
		System.out.println(query);
		ArrayList<FilmData> result = readFromDB(query);
		for(FilmData film : result){
			System.out.println(film);
		}
	}
}