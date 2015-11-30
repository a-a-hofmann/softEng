package com.uzh.gwt.softeng.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
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
	
	/**
	 * Process the HTTP doGet request and serves a file as response.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		log.log(Level.INFO, "Exporting");
		
		try {
			@SuppressWarnings("unused")
			String var0show = request.getParameter("showthis");
		} catch(Exception e) {
			log.log(Level.SEVERE, e.toString());
		}
		
		String formatDataSet = "";
		if (request.getParameter("type").equals("TSV")){
			if (request.getParameter("search").equals("true")){
				formatDataSet = searchSet.formatToTSV();
			} else {
				formatDataSet = dataSet.formatToTSV();
			}
			
			response.setContentType(TSV_CONTENT_TYPE);
		}
	    PrintWriter out = response.getWriter();
	    out.println(formatDataSet);
	    out.close();
	}
}
