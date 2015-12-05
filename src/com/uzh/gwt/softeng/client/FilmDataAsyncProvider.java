package com.uzh.gwt.softeng.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.DataGrid;
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
	 * Film data set size.
	 */
	private int size;
	
	private int searchSize;
	
	/**
	 * FilmDataServiceAsync object for RPC.
	 */
	private FilmDataServiceAsync filmDataSvc = GWT.create(FilmDataService.class);
	
	/**
	 * Display table reference.
	 */
	private DataGrid<FilmData> table;
	
	/**
	 * List with all movies to use when it is finished loading.
	 */
	private ArrayList<FilmData> filmData;
	
	/**
	 * List for search requests.
	 */
	private ArrayList<FilmData> filmSearchResults;
	
	/**
	 * Says if the application is finished loading all film data.
	 */
	private boolean isFinishedLoading;
	
	/**
	 * Says if the application is showing search results.
	 */
	private boolean isSearchResult;
	
	/**
	 * Query for the database. Needs to be complemented with a limit for the pager
	 * and a WHERE condition in case of a async search.
	 */
	private String query;
	
	
	private boolean isSort;
	
	private String column;
	
	private boolean isAscending;
	
	/**
	 * Data provider constructor.
	 * @param table Table reference to add to the list of displays to monitor.
	 */
	public FilmDataAsyncProvider(DataGrid<FilmData> table){
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
	
	public void onColumnSort(HasData<FilmData> display, boolean isAscending, String column) {
		Range range = display.getVisibleRange();
		final int start = range.getStart();
		final int length = range.getLength();
		
		String order = isAscending ? "ASC" : "DESC";
		this.column = column;
		isSort = true;
		query = "select m.*, group_concat(DISTINCT g.genre) genres, group_concat(DISTINCT l.language) languages, "
				+ "group_concat(DISTINCT c.country) countries from (SELECT * FROM movies ORDER BY " + column + " " + order + " LIMIT " + start + ", " + length + ") as m"
				+ " left join moviegenres mg on m.movieid=mg.movieid left join genres g on g.genreid=mg.genreid"
				+ " left join movielanguages ml on m.movieid=ml.movieid"
				+ " left join languages l on l.languageid=ml.languageid"
				+ " left join moviecountries mc on m.movieid=mc.movieid"
				+ " left join countries c on c.countryid=mc.countryid group by m.movieid order by " + column + " " + order + ";";
		
		AsyncCallback<FilmDataSet> callback = new AsyncCallback<FilmDataSet>() {
	    	public void onFailure(Throwable caught) {
	    		caught.printStackTrace();
	    	}

	    	public void onSuccess(FilmDataSet result) {
	            List<FilmData> newData = result.getFilms();
	            updateRowData(start, newData);
	    	}
	    };
	    
	    onRangeChanged(display);
//    	filmDataSvc.getFilmData(sortQuery, false, callback);
		
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
	
		
		if (!isFinishedLoading && !isSearchResult){
			// Set up the callback object.
		    AsyncCallback<FilmDataSet> callback = new AsyncCallback<FilmDataSet>() {
		    	public void onFailure(Throwable caught) {
		    		caught.printStackTrace();
		    	}
	
		    	public void onSuccess(FilmDataSet result) {
		            List<FilmData> newData = result.getFilms();
		            updateRowData(start, newData);
		    	}
		    };
		    
		    if(isSort) {
		    	query = "select m.*, group_concat(DISTINCT g.genre) genres, group_concat(DISTINCT l.language) languages, "
						+ "group_concat(DISTINCT c.country) countries from (SELECT * FROM movies ORDER BY " + column + " " + (isAscending ? "ASC" : "DESC") + " LIMIT " + start + ", " + length + ") as m"
						+ " left join moviegenres mg on m.movieid=mg.movieid left join genres g on g.genreid=mg.genreid"
						+ " left join movielanguages ml on m.movieid=ml.movieid"
						+ " left join languages l on l.languageid=ml.languageid"
						+ " left join moviecountries mc on m.movieid=mc.movieid"
						+ " left join countries c on c.countryid=mc.countryid group by m.movieid"
						+ " order by m." + column + " " + (isAscending ? "ASC" : "DESC") + ";";
		    	filmDataSvc.getFilmData(query, false, callback);
		    } else {
		    	String getAllQuery = query + " limit " + Integer.toString(start) + "," + Integer.toString(length) + ";";
		    	filmDataSvc.getFilmData(getAllQuery, false, callback);
		    }
		} else {
			List<FilmData> tmp = new ArrayList<FilmData>();
			
			if(isSearchResult){
				for(int i = start; i < start + length && i < filmSearchResults.size(); i++){
					tmp.add(filmSearchResults.get(i));
				}
			}
			else{
				for(int i = start; i < start + length && i < filmData.size(); i++){
					tmp.add(filmData.get(i));
				}
			}
			updateRowData(start, tmp);
		}
	}
	
	public void getFilmDataSetSize(){
		if (filmDataSvc == null) {
		      filmDataSvc = GWT.create(FilmDataService.class);
		    }

		    // Set up the callback object.
		    AsyncCallback<Integer> callback = new AsyncCallback<Integer>() {
		    	public void onFailure(Throwable caught) {
		    		caught.printStackTrace();
		    	}

		    	public void onSuccess(Integer result) {
		    		updateRowCount(result, true);
		    	}
		    };
		    // Make the call to the film data service.
		    filmDataSvc.getFilmDataSetSize(callback);
	}

	/**
	 * Set a list for the data provider.
	 * If possible the data provider will use this list instead of sending
	 * remote procedure calls.
	 * 
	 * @param filmDataSet FilmDataSet to save in the data provider.
	 */
	public void setList(FilmDataSet filmDataSet, boolean isSearch) {
		if (filmDataSet != null){
			ArrayList<FilmData> newData = filmDataSet.getFilms();
			if(isSearch){
				filmSearchResults = newData;
				isSearchResult = isSearch;
				this.searchSize = newData.size();
			}
			else{
				filmData = newData;
				isFinishedLoading = true;
				this.size = newData.size();
			}
			
			updateRowCount(newData.size(), true);
			onRangeChanged(table);
		}
	}
	
	/**
	 * Removes search results and show all data available.
	 */
	public void reset() {
		isSearchResult = false;
		if(isFinishedLoading){
			updateRowCount(filmData.size(), true);
		}
		else{
			getFilmDataSetSize();
		}
		onRangeChanged(table);		
	}
	
	/**
	 * Inform the displays of the total number of items that are available.
	 * 
	 * @param count the new total row count
	 * @param exact true if the count is exact, false if it is an estimate
	 */
	@Override
	public void updateRowCount(int size, boolean exact) {
		super.updateRowCount(size, exact);
		this.size = size;
	}
	
	
	public ArrayList<FilmData> getList() {
		return filmData;
	}

	public boolean isFinishedLoading() {
		return isFinishedLoading;
	}
}
