package com.manning.gwtia.ch03.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FilmDataServiceAsync {
	
	void getFilmData(AsyncCallback<List<FilmData>> callback);
}
