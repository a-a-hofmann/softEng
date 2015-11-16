package com.uzh.gwt.softeng.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
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
	 * Filtered data set.
	 */
	private FilmDataSet filteredDataSet = new FilmDataSet();
	
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
	 * This button  will wrap the existing HTML button defined in the HTML page and 
	 * is used for the dummy search capability.
	 */
	Button search;
	
	/**
	 * The image logo.
	 */
	Image logo;
	
	/**
	 * The filename of our logo image
	 */
	private static final String LOGO_IMAGE_NAME = "ads.png";
	
	/**
	 * A popup panel that will be displayed if the search button is selected. 
	 */
	 PopupPanel searchRequest;
		
	/**
	 * Create new Image widget.
	 */
	private void insertLogo(){
		// Create the logo image and prevent being able to drag it to browser location bar
		// by overriding its onBrowserEvent method.
		logo = new Image(GWT.getModuleBaseURL() + "../" + LOGO_IMAGE_NAME){
			public void onBrowserEvent(Event evt){
				evt.preventDefault();
			}
		};
		
		RootPanel logoSlot = RootPanel.get("ad");
		if (logoSlot != null)
			logoSlot.add(logo);
		logo.setWidth("60em");
	}
	
	/**
	 * Wrap the search button that already exists on the HTML page and store it as the
	 * previously declared search Button widget.  If the button isn't found create it.
	 */
	private void wrapExisitngSearchButton(){
		// Try and find the DOM element
		Element el = DOM.getElementById("search");
		
		// If the element is not null, then we've found it, so let's wrap it
		if(el!=null){
			search = Button.wrap(el);
		} else {
			search = new Button("search");
			RootPanel.get().add(search);
		}
	}

	/**
	 * Here we set up the event handling that we will drive user interaction.
	 * 
	 * 1.  A ClickHandler for the search button.
	 * 
	 */
	private void setUpEventHandling(){
		
		/**
		 *  If the search button is clicked, display pop-up panel which allows
		 *  the user to type in a search term. The TextBox where the user types search terms should 
		 *  automatically gain focus to make it more user friendly.
		 */ 
		search.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				FlowPanel qAnswer;
				final TextBox searchTerm = new TextBox();
				
				// If search button is clicked for the first time then the searchRequest Pop-up panel does not yet exist
				// so we'll build it first as follows:
				if (searchRequest==null){
					// Create the PopupPanel widget
					searchRequest = new PopupPanel();
					
					// Create a FlowPanel to hold the question and answer for the search term
					qAnswer = new FlowPanel();
					// Add a Label to the Flow Panel that represents the "Search For" text
					qAnswer.add(new Label("Search For:"));
					// Add the answer TextBox (which we declared above) to the FlowPanel
					qAnswer.add(searchTerm);
					
					// Add a change handler to the TextBox so that when there is a change to search term 
					// we start the search.
					searchTerm.addChangeHandler(new ChangeHandler(){
						public void onChange(ChangeEvent event) {
							// Hide the popup panel from the screen
							searchRequest.hide();
							
							String searchTitle = searchTerm.getText();
							filteredDataSet = new FilmDataSet(dataSet.filterByTitle(searchTitle));
							table.filter(filteredDataSet);
							
							searchTerm.setText("");
						}
					});

					// Add the question/answer to the search pop-up.
					searchRequest.add(qAnswer);
					searchRequest.setAnimationEnabled(true);
					searchRequest.showRelativeTo(search);
					searchRequest.setAutoHideEnabled(true);
				} else {
					searchRequest.show();
				}
				// Set the TextBox of the popup Panel to have focus.
				searchTerm.setFocus(true);
			}			
		});
	}
	
	/**
	 * Sends RPC to server to fetch film data and refreshes table.
	 */
	private void getFilmDataSetAsync(){
		if (filmDataSvc == null) {
		      filmDataSvc = GWT.create(FilmDataService.class);
		    }

		     // Set up the callback object.
		    AsyncCallback<FilmDataSet> callback = new AsyncCallback<FilmDataSet>() {
		      public void onFailure(Throwable caught) {
		    	  Window.alert("I failed");
		    	  caught.printStackTrace();
		      }

		      public void onSuccess(FilmDataSet result) {
		    	  dataSet = result;
		    	  buildMap();
		      }
		    };

		     // Make the call to the stock price service.
		    filmDataSvc.getFilmData(callback);
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
		    		Window.alert("I failed");
		    		caught.printStackTrace();
		    	}

		    	public void onSuccess(FilmDataSet result) {
		    		dataSet = result;
		    		buildMap();
		    		map.setMaxValue(10000);
		    	}
		    };

		    // Make the call to the film data service.
		    filmDataSvc.getFilmData(query, callback);
	}
	
	/**
	 * Style the search button.
	 */
	private void styleButtonUsingDOM(){
		// Set up some styling on the button
		search.getElement().getStyle().setProperty("backgroundColor", "#ff0000");
		search.getElement().getStyle().setProperty("border", "2px solid");
		search.getElement().getStyle().setOpacity(0.7);
	}
	
	/**
	 * Sets up the GUI components used in the application
	 * 
	 * 1. A heatmap.
	 * 1. A Table to contain the dataSet.
	 * 2. A search button that is from the original HTML page.
	 * 3. An image for the logo.
	 * 
	 */
	private void setUpGui() {
		//Build Map
//		buildMap();
		//Build Table
		buildTable();
		// Wrap the existing search button
		wrapExisitngSearchButton();
		// Insert a logo into a defined slot in the HTML page
		insertLogo();
		// Style the Button using low level DOM access
		styleButtonUsingDOM();
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
		if (mapSlot != null)
			mapSlot.add(map);
		else{
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
	 * This is the entry point method which will load the data, create the GUI and set up the event handling.
	 */
	public void onModuleLoad() {
		// Load only first 50 film data objects (for speed).
		getFilmDataSetAsync();
		// Load the rest.
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
		// Set up all the event handling required for the application.
		setUpEventHandling();
	}
}
