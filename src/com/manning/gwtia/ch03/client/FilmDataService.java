package com.manning.gwtia.ch03.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.manning.gwtia.ch03.shared.FilmDataSet;

@RemoteServiceRelativePath("filmData")
public interface FilmDataService extends RemoteService {

      FilmDataSet getFilmData();
}
