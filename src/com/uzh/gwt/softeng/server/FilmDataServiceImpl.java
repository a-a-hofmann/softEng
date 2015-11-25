package com.uzh.gwt.softeng.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.uzh.gwt.softeng.client.FilmDataService;
import com.uzh.gwt.softeng.shared.FilmData;
import com.uzh.gwt.softeng.shared.FilmDataSet;

@SuppressWarnings("serial")
public class FilmDataServiceImpl extends RemoteServiceServlet implements FilmDataService{
	private static final Logger log = Logger.getLogger( FilmDataServiceImpl.class.getName() );
	/**
	 * Server side implementation of FilmDataService.
	 * Send query and returns the resulting FilmDataSet limited to 50 results.
	 * @return Query result.
	 */
	public FilmDataSet getFilmData(){
		String query = "select m.*, group_concat(DISTINCT g.genre) genres, "
				+ "group_concat(DISTINCT l.language) languages, "
				+ "group_concat(DISTINCT c.country) countries "
				+ "from movies m left join moviegenres mg on m.movieid=mg.movieid "
				+ "left join genres g on g.genreid=mg.genreid "
				+ "left join movielanguages ml on m.movieid=ml.movieid "
				+ "left join languages l on l.languageid=ml.languageid "
				+ "left join moviecountries mc on m.movieid=mc.movieid "
				+ "left join countries c on c.countryid=mc.countryid "
				+ "group by m.movieid "
				+ "limit 50;";
		return getFilmData(query);
	}
	
	/**
	 * Server side implementation of FilmDataService.
	 * Send query and returns the resulting FilmDataSet.
	 * @return Query result.
	 */
	public FilmDataSet getFilmData(String query){
		log.log(Level.INFO, "about to read from db");
		ArrayList<FilmData> result = null;
		try {
			result = MySQLConnector.readFromDB(query);
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			log.log(Level.SEVERE, e.toString());
		}
		FilmDataSet newDataSet = new FilmDataSet(result);
		System.out.println("Finished getting data");
		return newDataSet;
	}
}
