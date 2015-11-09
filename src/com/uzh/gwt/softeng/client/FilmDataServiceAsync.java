package com.uzh.gwt.softeng.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.uzh.gwt.softeng.shared.FilmDataSet;

/**
 * Async FilmDataService interface.
 *
 */
public interface FilmDataServiceAsync {
	
	/**
	 * Sends request to server to retrieve film data.
	 * Results limited to 50.
	 * @return Query result. First 50 films in database.
	 */
	void getFilmData(AsyncCallback<FilmDataSet> callback);
	
	/**
	 * Sends query to server to retrieve film data.
	 * @param query String containing a valid SQL query.
	 * @return Query result.
	 */
	void getFilmData(String query, AsyncCallback<FilmDataSet> callback);
}
