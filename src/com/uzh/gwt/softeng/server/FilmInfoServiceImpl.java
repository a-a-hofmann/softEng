package com.uzh.gwt.softeng.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.uzh.gwt.softeng.client.FilmInfoService;


public class FilmInfoServiceImpl extends RemoteServiceServlet implements FilmInfoService{
	
	/**
	 * Logger.
	 */
	private static final Logger log = Logger.getLogger( FilmInfoServiceImpl.class.getName() );
	
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = -2506386207697746952L;
	
	/**
	 * Responds to HTTP GET requests.
	 * Queries the ThemovieDB API for movie info and sends it back to the client.
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		try {
			@SuppressWarnings("unused")
			String var0show = request.getParameter("showthis");
		} catch(Exception e) {
			log.log(Level.SEVERE, e.toString());
		}
		
		
		String urlString = "https://api.themoviedb.org/3/search/movie?api_key=f69f8579a414114fd51b382937aef6b3&query=";
		
		
		String title = request.getParameter("t");
		title = title.replaceAll(" ", "%20");
		
		String year = request.getParameter("y");
		
		urlString = urlString + title;// + "&year=" + year;
		
		URL url;
		try {
			url = new URL(urlString + "&year=" + year);
		} catch (MalformedURLException e1) {
			log.log(Level.SEVERE, e1.toString());
			return;
		}
			
			
		try {	
			String result = readFromUrl(url);
			
			
			//Check if it found a result
			JSONObject jsonObject;
		
			jsonObject = new JSONObject(result);
			int numberOfResults = Integer.parseInt(jsonObject.get("total_results").toString());
			
			if (numberOfResults == 0) {
				url = new URL(urlString);
				result = readFromUrl(url);
			}
			
			writeToClient(result, response);
			
		} catch (JSONException | IOException e) {
			log.log(Level.SEVERE, e.toString());
		}
		
	}
	
	/**
	 * Sends a query to the themoviedb. 
	 * @param url Query url.
	 * @return resulting json object.
	 * @throws IOException
	 */
	private String readFromUrl(URL url) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line;
		
		String result = "";
		
		while((line = reader.readLine())!= null){
			result = result + line;
		}
		
		reader.close();
		return result;
	}
	
	/**
	 * Writes json to client.
	 * @param json json file to be sent.
	 * @param response Response object.
	 * @throws IOException Failed to open stream to client.
	 */
	private void writeToClient(String json, HttpServletResponse response) throws IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();	
		out.println(json);
		out.close();
	}

}
