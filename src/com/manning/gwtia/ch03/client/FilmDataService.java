package com.manning.gwtia.ch03.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("filmData")
public interface FilmDataService extends RemoteService {

      String[] getFilmData();
}
