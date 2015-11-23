package com.uzh.gwt.softeng.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.uzh.gwt.softeng.shared.FilmData;
import com.uzh.gwt.softeng.shared.FilmDataSet;

/**
 * Extension of asyncDataProvider. Retrieves new FilmData when the SimplePager
 * controls are used. Send search queries
 */
public class FilmDataAsyncProvider extends AsyncDataProvider<FilmData>{

	/**
	 * FilmDataServiceAsync object for RPC.
	 */
	private FilmDataServiceAsync filmDataSvc = GWT.create(FilmDataService.class);
	
	/**
	 * Display table reference.
	 */
	private CellTable<FilmData> table;
	
	/**
	 * List for search requests.
	 */
	private List<FilmData> filmDataWrapper;
	
	/**
	 * Says if the application is finished loading all film data.
	 */
	private boolean isFinishedLoading;
	
	/**
	 * Query for the database. Needs to be complemented with a limit for the pager
	 * and a WHERE condition in case of a async search.
	 */
	private String query;
	
	/**
	 * Data provider constructor.
	 * @param table Table reference to add to the list of displays to monitor.
	 */
	public FilmDataAsyncProvider(CellTable<FilmData> table){
		query = "select m.*, group_concat(DISTINCT g.genre) genres, "
				+ "group_concat(DISTINCT l.language) languages, "
				+ "group_concat(DISTINCT c.country) countries "
				+ "from movies m left join moviegenres mg on m.movieid=mg.movieid "
				+ "left join genres g on g.genreid=mg.genreid "
				+ "left join movielanguages ml on m.movieid=ml.movieid "
				+ "left join languages l on l.languageid=ml.languageid "
				+ "left join moviecountries mc on m.movieid=mc.movieid "
				+ "left join countries c on c.countryid=mc.countryid "
				+ "group by m.movieid ";
		this.table = table;
		addDataDisplay(table);
	}
	
	/**
	 * onRangeChanged is called any time the pager controls are activated.
	 */
	@Override
	protected void onRangeChanged(HasData<FilmData> display) {
		//New range to be displayed.
		Range range = display.getVisibleRange();
		final int start = range.getStart();
		int length = range.getLength();
	
		
		if (!isFinishedLoading){
			// Set up the callback object.
		    AsyncCallback<FilmDataSet> callback = new AsyncCallback<FilmDataSet>() {
		    	public void onFailure(Throwable caught) {
		    		Window.alert("onRangeChanged failed");
		    		caught.printStackTrace();
		    	}
	
		    	public void onSuccess(FilmDataSet result) {
		            List<FilmData> newData = result.getFilms();
		            updateRowData(start, newData);
		    	}
		    };
		  
			String getAllQuery = query + " limit " + Integer.toString(start) + "," + Integer.toString(length) + ";";
	    	filmDataSvc.getFilmData(getAllQuery, callback);
		}
		else{
			Window.alert("Finished loading, using local data");
			List<FilmData> tmp = new ArrayList<FilmData>();
			for(int i = start; i < start + length && i < filmDataWrapper.size(); i++){
				tmp.add(filmDataWrapper.get(i));
			}
			updateRowData(start, tmp);
		}
	}
	
	/**
	 * Filter data on table.
	 * @param filmDataSet Result from filtering to show on table.
	 * @param isSearch If it is a search or reset (not working).
	 */
	public void filter(FilmDataSet filmDataSet, boolean isSearch){
		setList(filmDataSet);
	}
	
	/**
	 * Filter by name
	 * @param search Search query.
	 */
	public void filterByName(String search){
		String titleQuery = "select m.*, group_concat(DISTINCT g.genre) genres, "
					+ "group_concat(DISTINCT l.language) languages, "
					+ "group_concat(DISTINCT c.country) countries "
					+ "from movies m left join moviegenres mg on m.movieid=mg.movieid "
					+ "left join genres g on g.genreid=mg.genreid "
					+ "left join movielanguages ml on m.movieid=ml.movieid "
					+ "left join languages l on l.languageid=ml.languageid "
					+ "left join moviecountries mc on m.movieid=mc.movieid "
					+ "left join countries c on c.countryid=mc.countryid "
					+ "where LOWER(m.title) like LOWER(\"%" + search + "%\") "
					+ "group by m.movieid;";
			
		AsyncCallback<FilmDataSet> callback = new AsyncCallback<FilmDataSet>() {
	    	public void onFailure(Throwable caught) {
	    		Window.alert("onRangeChanged failed");
	    		caught.printStackTrace();
	    	}

	    	public void onSuccess(FilmDataSet result) {
	            setList(result);
	    	}
	    };
	    
    	filmDataSvc.getFilmData(titleQuery, callback);
	}

	/**
	 * Set a list for the data provider.
	 * If possible the data provider will use this list instead of sending
	 * remote procedure calls.
	 * @param filmDataSet FilmDataSet to save in the data provider.
	 */
	public void setList(FilmDataSet filmDataSet) {
		if (filmDataSet != null){
			filmDataWrapper = filmDataSet.getFilms();
			isFinishedLoading = true;
			updateRowCount(filmDataWrapper.size(), true);
			onRangeChanged(table);
		}
	}
}
