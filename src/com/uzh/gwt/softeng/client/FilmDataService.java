package com.uzh.gwt.softeng.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.uzh.gwt.softeng.shared.FilmDataSet;

@RemoteServiceRelativePath("filmData")
public interface FilmDataService extends RemoteService {
	
	/**
	 * Gets film data set.
	 * @param query DB query to be sent to get data.
	 * @param isSearch specifies whether the query is a search or not (for caching purposes).
	 * @return the film data set.
	 */
	FilmDataSet getFilmData(String query, boolean isSearch);
	
	/**
	 * Gets film data set size.
	 * @return film data set size.
	 */
	Integer getFilmDataSetSize();
	
	/**
	 * Gets all suggestions for langauges, genres and countries.
	 * @return String arrays containing all suggestions.
	 */
	String[][] getSuggestions();
}
