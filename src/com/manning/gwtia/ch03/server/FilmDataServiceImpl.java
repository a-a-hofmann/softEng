package com.manning.gwtia.ch03.server;

import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.manning.gwtia.ch03.client.FilmData;
import com.manning.gwtia.ch03.client.FilmDataService;

public class FilmDataServiceImpl extends RemoteServiceServlet implements FilmDataService{
	
	public List<FilmData> getFilmData(){
		List<FilmData> result = MySQLConnector.readFromDB("SELECT * FROM movies LIMIT 10");
		return result;
	}
	
//	public static void main(String[] args){
//		FilmDataServiceImpl fd = new FilmDataServiceImpl();
//		for(int i = 0; i < 10; i++){
//			for(String tmp: fd.getFilmData())
//				System.out.print(tmp + " ");
//			System.out.println();
//		}
//	}
}
