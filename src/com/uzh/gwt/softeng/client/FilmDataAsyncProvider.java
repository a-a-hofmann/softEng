package com.uzh.gwt.softeng.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortList;
import com.google.gwt.user.cellview.client.ColumnSortList.ColumnSortInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.Range;
import com.uzh.gwt.softeng.shared.FilmData;
import com.uzh.gwt.softeng.shared.FilmDataSet;

/**
 * Extension of asyncDataProvider. Retrieves new FilmData when the SimplePager
 * controls are used.
 * 
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
	 * Flag for the pager.
	 */
	private boolean isSearch = false;
	
	/**
	 * List for search requests.
	 */
	private List<FilmData> filmDataWrapper;
	
	/**
	 * Data provider constructor.
	 * @param table Table reference to add to the list of displays to monitor.
	 */
	public FilmDataAsyncProvider(CellTable<FilmData> table){
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
	    
	    // Make the call to the film data service.
	    String query = "SELECT * FROM movies LIMIT " + Integer.toString(start) + "," + Integer.toString(length) + ";";
	    
	    if(isSearch == false)
	    	filmDataSvc.getFilmData(query, callback);
	    else{
	    	List<FilmData> tmp = new ArrayList<FilmData>();
	    	for(int i = start; i < start + length; i++){
	    		tmp.add(filmDataWrapper.get(i));
	    	}
	    	updateRowData(start, tmp);
	    }
	}
	
	/**
	 * Reset table with all values.
	 */
	public void reset(){
		isSearch = false;
		onRangeChanged(table);
	}
	
	/**
	 * Filter data on table.
	 * @param filmDataSet Result from filtering to show on table.
	 */
	public void filter(FilmDataSet filmDataSet){
		isSearch = true;
		this.filmDataWrapper = filmDataSet.getFilms();
		updateRowData(0, filmDataSet.getFilms());
		updateRowCount(filmDataSet.getFilms().size(), false);
	}
}
