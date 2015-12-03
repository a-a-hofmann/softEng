package com.uzh.gwt.softeng.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.uzh.gwt.softeng.client.FilmDataService;
import com.uzh.gwt.softeng.shared.FilmData;
import com.uzh.gwt.softeng.shared.FilmDataSet;


public class FilmDataServiceImpl extends RemoteServiceServlet implements FilmDataService{
	
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 4162121558926044280L;
	
	/**
	 * Logger.
	 */
	private static final Logger log = Logger.getLogger( FilmDataServiceImpl.class.getName() );
	
	/**
	 * FilmDataSet as cache.
	 * Will be served to client on click on export button.
	 */
	private FilmDataSet dataSet;
	
	/**
	 * FilmDataSet as cache. Search results.
	 * Will be served to client on click on export button.
	 */
	private FilmDataSet searchSet;
	
	/**
	 * MIME type for TSV.
	 */
	private static final String TSV_CONTENT_TYPE = "text/tab-separated-values";
	
	/**
	 * Server side implementation of FilmDataService.
	 * Send query and returns the resulting FilmDataSet.
	 * @return Query result.
	 */
	@Override
	public FilmDataSet getFilmData(String query, boolean isSearch){
		ArrayList<FilmData> result = null;
		try {
			result = MySQLConnector.readFromDB(query);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			log.log(Level.SEVERE, e.toString());
		}
		
		//Save last loaded dataset as export candidate
		FilmDataSet newDataSet = new FilmDataSet(result);
		if (isSearch){
			log.log(Level.INFO, "Sending search results to client");
			searchSet = newDataSet;
		} else {
			log.log(Level.INFO, "Sending data set to client");
			dataSet = newDataSet;
		}
		
		return newDataSet;
	}

	/**
	 * Fetches data set size.
	 * @return size of data set.
	 */
	@Override
	public Integer getFilmDataSetSize() {
		log.log(Level.INFO, "Fetching data set size");
		Integer result = -1;
		try {
			result = MySQLConnector.getFilmDataSetSize();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			log.log(Level.SEVERE, e.toString());
		}
		
		return result;
	}
	
	@Override
	public String[][] getSuggestions() {
		String[][] result = new String[3][];
		
		String[] genres = null;
		String[] languages = null;
		String[] countries = null;
		try {
			String query = "SELECT country FROM countries;";
			String sizeQuery = "SELECT COUNT(*) FROM countries;";
			countries = MySQLConnector.getSuggestions(query, sizeQuery);
			
			query = "SELECT genre FROM genres;";
			sizeQuery = "SELECT COUNT(*) FROM genres;";
			genres = MySQLConnector.getSuggestions(query, sizeQuery);
			
			query = "SELECT language FROM languages;";
			sizeQuery = "SELECT COUNT(*) FROM languages;";
			languages = MySQLConnector.getSuggestions(query, sizeQuery);
			
			result[0] = genres;
			result[1] = languages;
			result[2] = countries;
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			log.log(Level.SEVERE, e.toString());
		}
		
		return result;
	}
	
	/**
	 * Process the HTTP doGet request and serves a file as response.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			@SuppressWarnings("unused")
			String var0show = request.getParameter("showthis");
		} catch(Exception e) {
			log.log(Level.SEVERE, e.toString());
		}
		
		log.log(Level.INFO, request.getQueryString());
		
		String formatDataSet = "";
		
		response.setContentType(TSV_CONTENT_TYPE);
		
		PrintWriter out = response.getWriter();
		
		if (request.getParameter("search").equals("true")){
			if (request.getParameter("extended").equals("true")){
				String title = request.getParameter("title");
				String country = request.getParameter("country");
				String genre = request.getParameter("genre");
				String language = request.getParameter("language");
								
				int durationMin = Integer.parseInt(request.getParameter("durationMin"));
				int durationMax = Integer.parseInt(request.getParameter("durationMax"));
	
				int dateMin = Integer.parseInt(request.getParameter("dateMin"));
				int dateMax = Integer.parseInt(request.getParameter("dateMax"));
	
				formatDataSet = new FilmDataSet(dataSet.filter(title, country, genre, language, durationMin, durationMax, dateMin, dateMax)).formatToTSV();
				log.log(Level.SEVERE, "" + dataSet.size());
			} else {
				formatDataSet = searchSet.formatToTSV();
			}
			
		    out.println(formatDataSet);
		    out.close();
		    
		} else {
			log.log(Level.INFO, "Export entire dataset");
			
			BufferedReader bufferedReader = new BufferedReader(new FileReader("WEB-INF/Resources/movies_all.tsv"));
			
			while((formatDataSet = bufferedReader.readLine()) != null){
				out.println(formatDataSet);
			}
			
			out.close();
			bufferedReader.close();
		}
	}
}
