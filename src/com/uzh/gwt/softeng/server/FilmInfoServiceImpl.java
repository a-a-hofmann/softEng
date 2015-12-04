package com.uzh.gwt.softeng.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.uzh.gwt.softeng.client.FilmInfoService;


public class FilmInfoServiceImpl extends RemoteServiceServlet implements FilmInfoService{
	
	/**
	 * Logger.
	 */
	private static final Logger log = Logger.getLogger( FilmInfoServiceImpl.class.getName() );
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2506386207697746952L;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			@SuppressWarnings("unused")
			String var0show = request.getParameter("showthis");
		} catch(Exception e) {
			log.log(Level.SEVERE, e.toString());
		}
		
		try{
			
//			String urlString = "http://www.omdbapi.com/?t=";
			String urlString = "https://api.themoviedb.org/3/search/movie?api_key=f69f8579a414114fd51b382937aef6b3&query=";
			
			
			String title = request.getParameter("t");
			title = title.replaceAll(" ", "%20");
			
			String year = request.getParameter("y");
			
			urlString = urlString + title + "&year=" + year;
			
			URL url = new URL(urlString);
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			
			String result = "";
			
			while((line = reader.readLine())!= null){
				result = result + line;
			}
			
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			
			out.println(result);
			out.close();
			reader.close();
			
			log.log(Level.INFO, result);
			} catch(Exception e){
			
			}	
		
	}

}