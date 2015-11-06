package com.manning.gwtia.ch03.server;

import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.manning.gwtia.ch03.client.FilmDataService;
import com.manning.gwtia.ch03.shared.FilmData;
import com.manning.gwtia.ch03.shared.FilmDataSet;

@SuppressWarnings("serial")
public class FilmDataServiceImpl extends RemoteServiceServlet implements FilmDataService{
	
	/**
	 * Server side implementation of FilmDataService.
	 * Send query and returns the resulting FilmDataSet.
	 * @return Query result.
	 */
	public FilmDataSet getFilmData(){
		ArrayList<FilmData> result = MySQLConnector.readFromDB("SELECT * FROM movies LIMIT 1000;");
		FilmDataSet newDataSet = new FilmDataSet();
		newDataSet.setDataSet(result);
		
		System.out.println(result.size());
		return newDataSet;
	}
}
