package com.uzh.gwt.softeng.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.uzh.gwt.softeng.shared.FilmDataSet;

@RemoteServiceRelativePath("filmData")
public interface FilmDataService extends RemoteService {
	
      FilmDataSet getFilmData(String query);
      Integer getFilmDataSetSize();
}
