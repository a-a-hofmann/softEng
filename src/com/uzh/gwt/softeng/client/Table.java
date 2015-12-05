package com.uzh.gwt.softeng.client;


import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.cellview.client.ColumnSortEvent.AsyncHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.uzh.gwt.softeng.shared.FilmData;
import com.uzh.gwt.softeng.shared.FilmDataSet;

/**
 * The {@code Table} class handles the Table object and the data retrieval for it.
 * It is an extension to the GWT Composite class.
 * It is composed from a CellTable, a SimplePager and an AsynchronousDataProvider.
 */
public class Table extends Composite {
	
	/**
	 * Table.
	 */
	private DataGrid<FilmData> table = new DataGrid<FilmData> (15);
	
	/**
	 * Table and pager container.
	 */
	private DockLayoutPanel lp;
	
	/**
	 * Asynchronous data provider.
	 */
	FilmDataAsyncProvider asyncDataProvider;
	
	/**
	 * Pager for the table.
	 */
	SimplePager pager;
	
	/**
	 * Table constructor.
	 * Creates container panel and adds table and pager to it.
	 * Initializes data provider.
	 */
	public Table() {
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		table.setWidth("100%");
		table.setRowCount(81741, true);
		
		asyncDataProvider = new FilmDataAsyncProvider(table);
		
		lp = new DockLayoutPanel(Unit.PCT);
		lp.setHeight("800px");
		
		SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
		pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
		pager.setDisplay(table);
		pager.addStyleName("pager");
		lp.addSouth(pager, 25);
		lp.add(table);
		initTable();
		initWidget(lp);
		
		
	}
	
	/**
	 * Table initialization.
	 * Creates columns and sets selection handler.
	 */
	public void initTable() {
		
		AsyncHandler columnSortHandler = new AsyncHandler(table);
		table.addColumnSortHandler(columnSortHandler);
		
		TextColumn<FilmData> movieID = new TextColumn<FilmData>() {
			@Override
			public String getValue(FilmData object) {
				return Long.toString( object.getID() );
			}};
		table.addColumn(movieID, "Movie ID");
		movieID.setSortable(true);
		table.getColumnSortList().push(movieID);
		
		TextColumn<FilmData> title = new TextColumn<FilmData>() {
			@Override
			public String getValue(FilmData object) {
				return object.getTitle();
			}};

		table.addColumn(title, "Title");
		
		TextColumn<FilmData> movieDate = new TextColumn<FilmData>() {
			@Override
			public String getValue(FilmData object) {
				int date = object.getDate();
				if (date == 0){
					return "";
				}
				else{
					return Integer.toString(date);
				}
			}};
			
		table.addColumn(movieDate, "Date");

		TextColumn<FilmData> movieDuration = new TextColumn<FilmData>() {
			@Override
			public String getValue(FilmData object) {
				float duration = object.getDuration();
				if (duration < 0.4){
					return "";
				}
				else{
					return Float.toString(duration);
				}
			}};
			
		table.addColumn(movieDuration, "Duration");
		
		TextColumn<FilmData> movieGenres = new TextColumn<FilmData>() {
			@Override
			public String getValue(FilmData object) {
				String genres = "";
				for(String genre : object.getGenres())
					genres = genres.concat(genre + ", ");
				genres = genres.substring(0, genres.length() - 2);
				return genres;
			}};

		table.addColumn(movieGenres, "Genres");
		
		TextColumn<FilmData> movieLanguages = new TextColumn<FilmData>() {
			@Override
			public String getValue(FilmData object) {
				String languages = "";
				for(String language : object.getLanguages())
					languages = languages.concat(language + ", ");
				languages = languages.substring(0, languages.length() - 2);
				return languages;
			}};

		table.addColumn(movieLanguages, "Languages");

		TextColumn<FilmData> movieCountry = new TextColumn<FilmData>() {
			@Override
			public String getValue(FilmData object) {
				String countries = "";
				for(String country : object.getCountries())
					countries = countries.concat(country + ", ");
				countries = countries.substring(0, countries.length() - 2);
				return countries;
			}};

		table.addColumn(movieCountry, "Country");
		
		//Handler for single selection of one row
		final NoSelectionModel<FilmData> selectionModel = new NoSelectionModel<FilmData>();
		table.setSelectionModel(selectionModel);
		
		
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			
			public void onSelectionChange(SelectionChangeEvent event) {

				final FilmData selected = selectionModel.getLastSelectedObject();
				if (selected != null) {
					
					String url = GWT.getModuleBaseURL() + "filmInfo?t=" + selected.getTitle() + "&y=" + selected.getDate();
					RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
					
					try {
						// Create a HTTP GET request
						builder.sendRequest(null, new RequestCallback() {
							
							public void onError(Request request, Throwable exception) {
								// Couldn't connect to server (could be timeout, SOP violation, etc.)
								Window.alert(exception.toString());
						    }

						    public void onResponseReceived(Request request, Response response) {
						    	if (200 == response.getStatusCode()) {
						    		// Process the response in response.getText()
						    		JSONObject jsonObject = JSONParser.parseStrict(response.getText()).isObject();
						    		JSONArray results = jsonObject.get("results").isArray();
						    		jsonObject = results.get(0).isObject();
						    		
						    		String url = "http://image.tmdb.org/t/p/original/";
						    		
						    		String poster = jsonObject.get("poster_path").toString().replaceAll("\"", "");
						    		String plot = jsonObject.get("overview").toString();
					    		
						    		// Show popup with poster and movie plot.
						    		PopupPanel popup = new PopupPanel();
						    		HorizontalPanel hp = new HorizontalPanel();
						    		VerticalPanel vp = new VerticalPanel();
						    		Label titleLabel = new Label(selected.getTitle());
						    		Label plotLabel = new Label(plot);
						    		
						    		final Image image = new Image(url + poster);
						    		image.setSize("300px", "500px");
						    		
						    		vp.add(titleLabel);
						    		vp.add(plotLabel);
						    		
						    		hp.add(image);
						    		hp.add(vp);
						    		
						    		popup.setAutoHideEnabled(true);
						    		popup.add(hp);
						    		popup.setSize("500px", "500px");
						    		popup.show();
						    		popup.center();
						    		
						    	} else {
						    		// Handle the error.  Can get the status text from response.getStatusText()
						    	}
						    }
						});
					} catch (RequestException e) {
						// Couldn't connect to server
					}
					
					
					
//					Window.alert("You selected: " + selected.getID() + " " + selected.getTitle() + " " + selected.getCountries().toString()
//									+ " " + selected.getDuration());
				}
			}
		});	
	}
	
	/**
	 * Sets a list in the data provider to be used as cache.
	 * @param filmDataSet FilmDataSet containing list to be set.
	 */
	public void setList(FilmDataSet filmDataSet, boolean isSearch){
		asyncDataProvider.setList(filmDataSet, isSearch);
	}
	
	/**
	 * Returns list from data provider.
	 * @return list containing film data.
	 */
	public ArrayList<FilmData> getList(){
		return asyncDataProvider.getList();
	}
	
	/**
	 * Resets table view.
	 */
	public void reset(){
		asyncDataProvider.reset();
	}
	
	/**
	 * Updates table row count.
	 */
	public void updateRowCount(int size){
		asyncDataProvider.updateRowCount(size, true);
	}
	
	/**
	 * Checks if the application is finished loading all data.
	 * @return {@code true} if application finished loading. {@code false} otherwise.
	 */
	public boolean isFinishedLoading(){
		return asyncDataProvider.isFinishedLoading();
	}
}
