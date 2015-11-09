package com.uzh.gwt.softeng.server;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.uzh.gwt.softeng.client.FilmDataService;
import com.uzh.gwt.softeng.shared.FilmData;
import com.uzh.gwt.softeng.shared.FilmDataSet;

@SuppressWarnings("serial")
public class FilmDataServiceImpl extends RemoteServiceServlet implements FilmDataService{
	
	Logger log;
	/**
	 * Server side implementation of FilmDataService.
	 * Send query and returns the resulting FilmDataSet limited to 50 results.
	 * @return Query result.
	 */
	public FilmDataSet getFilmData(){
		return getFilmData("SELECT * FROM movies LIMIT 50;");
	}
	
	/**
	 * Server side implementation of FilmDataService.
	 * Send query and returns the resulting FilmDataSet.
	 * @return Query result.
	 */
	public FilmDataSet getFilmData(String query){
		ArrayList<FilmData> result = MySQLConnector.readFromDB(query);
		log.log(Level.INFO, "Query result: " + result.size());
		FilmDataSet newDataSet = new FilmDataSet(result);
		return newDataSet;
	}
}
