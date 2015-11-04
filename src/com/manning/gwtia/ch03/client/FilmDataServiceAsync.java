package com.manning.gwtia.ch03.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FilmDataServiceAsync {
	
	void getFilmData(AsyncCallback<String[]> callback);
}
