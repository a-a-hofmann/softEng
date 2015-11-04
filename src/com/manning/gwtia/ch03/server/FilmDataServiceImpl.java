package com.manning.gwtia.ch03.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.manning.gwtia.ch03.client.FilmDataService;

public class FilmDataServiceImpl extends RemoteServiceServlet implements FilmDataService{
	
	public String[] getFilmData(){
		String[] movie = MySQLConnector.readFromDB();
		return movie;
	}
	
	public static void main(String[] args){
		FilmDataServiceImpl fd = new FilmDataServiceImpl();
		for(int i = 0; i < 10; i++){
			for(String tmp: fd.getFilmData())
				System.out.print(tmp + " ");
			System.out.println();
		}
	}
}
