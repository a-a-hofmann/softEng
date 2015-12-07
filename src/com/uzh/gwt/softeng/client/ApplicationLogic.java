package com.uzh.gwt.softeng.client;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.uzh.gwt.softeng.shared.FilmDataSet;


/**
 * The {@code ApplicationLogic} class handles all RPC calls and panels that build the UI of the project.
 * 
 */
public class ApplicationLogic implements EntryPoint {
	
	/**
	 * The film data set.
	 */
	private FilmDataSet dataSet = new FilmDataSet();
	
	/**
	 * FilmDataServiceAsync object for RPC.
	 */
	private FilmDataServiceAsync filmDataSvc = GWT.create(FilmDataService.class);
	
	/**
	 * The table containing data.
	 */
	private Table table;
	
	/**
	 * The heatmap.
	 */
	private HeatMap map;
	
	/**
	 * The Filter panel.
	 */
	private FilterPanel filterPanel;
	
	/**
	 * The image ad.
	 */
	private Image ad;
	
	/**
	 * FileUploader.
	 */
	private FileUploader fileUploader;
	
	/**
	 * The filename of our ad image
	 */
	private final String AD_IMAGE_NAME = "banana.gif";
		
	/**
	 * Create new Image widget.
	 */
	private void insertAd(){
		ad = new Image(GWT.getModuleBaseURL() + "../" + AD_IMAGE_NAME);
		
		RootPanel logoSlot = RootPanel.get("ad");
		if (logoSlot != null)
			logoSlot.add(ad);
	}
	
	/**
	 * Sends RPC to server with specific query to fetch film data and refreshes table.
	 * @param query The query to send to the database.
	 */
	private void getFilmDataSetAsync(String query){
		if (filmDataSvc == null) {
		      filmDataSvc = GWT.create(FilmDataService.class);
		    }

		    // Set up the callback object.
		    AsyncCallback<FilmDataSet> callback = new AsyncCallback<FilmDataSet>() {
		    	public void onFailure(Throwable caught) {
		    		caught.printStackTrace();
		    	}

		    	public void onSuccess(FilmDataSet result) {
		    		dataSet = result;
		    		buildMap();
		    		filterPanel.setCountrySuggestion(result.getCountriesList());
		    		filterPanel.setGenresSuggestion(result.getGenresList());
		    		filterPanel.setLanguagesSuggestion(result.getLanguagesList());
		    		table.setList(dataSet, false);
		    	}
		    };
		    // Make the call to the film data service.
		    filmDataSvc.getFilmData(query, false, callback);
	}
	
	/**
	 * Load suggestions lists from the server.
	 */
	private void getSuggestionsAsync(){
		if (filmDataSvc == null) {
		      filmDataSvc = GWT.create(FilmDataService.class);
		    }

		    // Set up the countries callback object.
		    AsyncCallback<String[][]> callback = new AsyncCallback<String[][]>() {
		    	public void onFailure(Throwable caught) {
		    		Window.alert(caught.toString());
		    	}

		    	public void onSuccess(String[][] result) {    		
		    		ArrayList<String> suggG = new ArrayList<String>(Arrays.asList(result[0]));
		    		ArrayList<String> suggL = new ArrayList<String>(Arrays.asList(result[1]));
		    		ArrayList<String> suggC = new ArrayList<String>(Arrays.asList(result[2]));
		    		
		    		filterPanel.setCountrySuggestion(suggC);
		    		filterPanel.setGenresSuggestion(suggG);
		    		filterPanel.setLanguagesSuggestion(suggL);
		    	}
		    };
		    // Make the call to the film data service.
		    filmDataSvc.getSuggestions(callback);
	}
	
	/**
	 * Sets up the GUI components used in the application
	 * 
	 * 1. A heatmap.
	 * 2. A Table to contain the dataSet.
	 * 3. A filter panel containing various filtering options.
	 * 4. An image for the ad.
	 * 
	 */
	private void setUpGui() {
		//Build Map
		buildMap();
		//Build Table
		buildTable();
		//Build FilterPanel
		buildFilters();
		//Insert an ad
		insertAd();
		
		insertUploadForm();
	}
	
	private void insertUploadForm() {
		fileUploader = new FileUploader();
		RootPanel contentSlot = RootPanel.get("fileUploader");
		if (contentSlot!=null) 
			contentSlot.add(fileUploader);
	}

	/**
	 * Create a new map object with updated FilmDataSet.
	 */
	private void buildMap() {
		RootPanel mapSlot = RootPanel.get("heatmap");
		if(map != null){
			mapSlot.remove(map);
		}
		map = new HeatMap(dataSet);
		if (mapSlot != null) {
			mapSlot.add(map);
		} else {
			System.out.println("Heatmap id not found!");
		}
	}

	/**
	 * Creates table.
	 */
	private void buildTable() {
		table = new Table();
		RootPanel contentSlot = RootPanel.get("table");
		if (contentSlot!=null) 
			contentSlot.add(table);
	}
	
	/**
	 * Creates filter panel.
	 */
	private void buildFilters() {
		filterPanel = new FilterPanel(table);
		
		RootPanel filterSlot = RootPanel.get("filterPanel");
		if(filterSlot != null)
			filterSlot.add(filterPanel);
		
		// Load suggestions after building the filter panel.
		getSuggestionsAsync();
	}
	
	/**
	 * This is the entry point method which will load the data and create the GUI.
	 */
	public void onModuleLoad() {	
		// Get film data set
		String query = "select m.*, group_concat(DISTINCT g.genre) genres, "
				+ "group_concat(DISTINCT l.language) languages, "
				+ "group_concat(DISTINCT c.country) countries "
				+ "from movies m left join moviegenres mg on m.movieid=mg.movieid "
				+ "left join genres g on g.genreid=mg.genreid "
				+ "left join movielanguages ml on m.movieid=ml.movieid "
				+ "left join languages l on l.languageid=ml.languageid "
				+ "left join moviecountries mc on m.movieid=mc.movieid "
				+ "left join countries c on c.countryid=mc.countryid "
				+ "group by m.movieid;";
		getFilmDataSetAsync(query);
		
		// Create the user interface
		setUpGui();				
	}
}
