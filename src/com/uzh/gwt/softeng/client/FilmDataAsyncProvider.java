package com.uzh.gwt.softeng.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.DataGrid;
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
	
	/**
	 * Says whether the table is sorted.
	 */
	private boolean isSort;
	
	/**
	 * Sort by this column. Last column clicked on by the user.
	 */
	private String column;
	
	/**
	 * Sort in ascending or descending order. {@code true} is ascending, {@code false} otherwise.
	 */
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
	
	/**
	 * Called when the user clicks on a sortable header. Handles the sorting by either sorting locally 
	 * or through the database.
	 * @param display The display whose order has changed.
	 * @param isAscending Whether to sort in ascending or descending order.
	 * @param column Column to sort by.
	 */
	public void onColumnSort(HasData<FilmData> display, boolean isAscending, String column) {
		Range range = display.getVisibleRange();
		final int start = range.getStart();
		final int length = range.getLength();
		
		String order = isAscending ? "ASC" : "DESC";
		this.isAscending = isAscending;
		this.column = column;
		isSort = true;
		
		query = "select m.*, group_concat(DISTINCT g.genre) genres, group_concat(DISTINCT l.language) languages, "
				+ "group_concat(DISTINCT c.country) countries from (SELECT * FROM movies ORDER BY " + column + " " + order + " LIMIT " + start + ", " + length + ") as m"
				+ " left join moviegenres mg on m.movieid=mg.movieid left join genres g on g.genreid=mg.genreid"
				+ " left join movielanguages ml on m.movieid=ml.movieid"
				+ " left join languages l on l.languageid=ml.languageid"
				+ " left join moviecountries mc on m.movieid=mc.movieid"
				+ " left join countries c on c.countryid=mc.countryid group by m.movieid order by " + column + " " + order + ";";
		
		if (isFinishedLoading || isSearchResult) {
			FilmDataSet films = null;
			if (isSearchResult) {
				films = new FilmDataSet(filmSearchResults);
			} else {
				films = new FilmDataSet(filmData);
			}
			if (column.equals("movieid")) {
				films.sortByID(isAscending);
			} else if (column.equals("title")) {
				films.sortByTitle(isAscending);
			} else if (column.equals("date")) {
				films.sortByDate(isAscending);
			} else {
				films.sortByDuration(isAscending);
			}
		}
	    
	    onRangeChanged(display);
		
	}
	
	
	/**
	 * onRangeChanged is called any time the pager controls are activated.
	 * @param display The display whose range has changed.	
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
	
	/**
	 * Gets film data set size from the database.
	 */
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
	 * @param isSearch Says whether the list is the result of a search.
	 */
	public void setList(FilmDataSet filmDataSet, boolean isSearch) {
		if (filmDataSet != null) {
			ArrayList<FilmData> newData = filmDataSet.getFilms();
			if(isSearch) {
				filmSearchResults = newData;
				isSearchResult = isSearch;
			} else {
				filmData = newData;
				isFinishedLoading = true;
			}
			
			if(isSort) {
				FilmDataSet tmp = new FilmDataSet(filmSearchResults);
				if(column.equals("movieid")) {
					tmp.sortByID(isAscending);
				} else if (column.equals("title")) {
					tmp.sortByTitle(isAscending);
				} else if (column.equals("date")) {
					tmp.sortByDate(isAscending);
				} else {
					tmp.sortByDuration(isAscending);
				}
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
	 * @param count The new total row count
	 * @param exact True if the count is exact, false if it is an estimate
	 */
	@Override
	public void updateRowCount(int count, boolean exact) {
		super.updateRowCount(count, exact);
	}
	
	/**
	 * Gets the list this data provider is backed by. 
	 * @return film data set
	 */
	public ArrayList<FilmData> getList() {
		return filmData;
	}

	/**
	 * Says whether the application is finished loading data.
	 * @return {@code true} if the application is done loading, {@code false} otherwise.
	 */
	public boolean isFinishedLoading() {
		return isFinishedLoading;
	}
}
