package com.uzh.gwt.softeng.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.uzh.gwt.softeng.shared.FilmDataSet;

/**
 * Interface for RPC, client-side.
 *
 */
@RemoteServiceRelativePath("filmData")
public interface FilmDataService extends RemoteService {
	/**
	 * Sends request to server to retrieve film data.
	 * Results limited to 50.
	 * @return Query result. First 50 films in database.
	 */
	FilmDataSet getFilmData();
	
	/**
	 * Sends query to server to retrieve film data.
	 * @param query String containing a valid SQL query.
	 * @return Query result.
	 */
    FilmDataSet getFilmData(String query);
}
