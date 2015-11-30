package com.uzh.gwt.softeng.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.uzh.gwt.softeng.shared.FilmDataSet;

public interface FilmDataServiceAsync {

	void getFilmData(String query, boolean isSearch, AsyncCallback<FilmDataSet> callback);
	void getFilmDataSetSize(AsyncCallback<Integer> callback);
}
