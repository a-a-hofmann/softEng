package com.manning.gwtia.ch03.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.manning.gwtia.ch03.shared.FilmDataSet;

public interface FilmDataServiceAsync {
	
	void getFilmData(AsyncCallback<FilmDataSet> callback);
}
