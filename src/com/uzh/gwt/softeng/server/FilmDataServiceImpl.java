package com.uzh.gwt.softeng.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	 * Server side implementation of FilmDataService.
	 * Send query and returns the resulting FilmDataSet.
	 * @return Query result.
	 */
	@Override
	public FilmDataSet getFilmData(String query){
		log.log(Level.INFO, "about to read from db");
		ArrayList<FilmData> result = null;
		try {
			result = MySQLConnector.readFromDB(query);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			log.log(Level.SEVERE, e.toString());
		}
		log.log(Level.INFO, "Finished reading from db");
		FilmDataSet newDataSet = new FilmDataSet(result);
		return newDataSet;
	}

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
}
