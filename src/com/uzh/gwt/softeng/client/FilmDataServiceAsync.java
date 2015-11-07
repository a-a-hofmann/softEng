package com.uzh.gwt.softeng.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.uzh.gwt.softeng.shared.FilmDataSet;

public interface FilmDataServiceAsync {
	
	void getFilmData(AsyncCallback<FilmDataSet> callback);
}
