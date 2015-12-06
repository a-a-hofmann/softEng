package com.uzh.gwt.softeng.client;

import java.util.ArrayList;

import org.spiffyui.client.widgets.slider.RangeSlider;
import org.spiffyui.client.widgets.slider.SliderEvent;
import org.spiffyui.client.widgets.slider.SliderListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.Range;
import com.uzh.gwt.softeng.shared.FilmDataSet;

public class FilterPanel extends Composite {
	
	/**
	 * RPC Object.s
	 */
	private FilmDataServiceAsync filmDataSvc = GWT.create(FilmDataService.class);
	
	/**
	 * Table reference. Filtered results will be displayed here.
	 */
	private Table table;
	
	/**
	 * This widgets main panel.
	 */
	private FocusPanel fp;
	
	/**
	 * Vertical panel containing all other panels.
	 */
	private VerticalPanel vlp;
	
	//Sub panels containing the different search widgets
	private HorizontalPanel titlePanel;
	private HorizontalPanel datePanel;
	private HorizontalPanel durationPanel;
	private HorizontalPanel genresPanel;
	private HorizontalPanel languagesPanel;
	private HorizontalPanel countriesPanel;
	private HorizontalPanel buttonsPanel;
	
	//Title filter
	private Label titleLabel;
	private TextBox titleSearchBox;

	//Date filter
	private Label dateLabel;
	private TextBox dateBox;
    private RangeSlider dateSlider;
    private int defaultMinDate = 1888;
    private int defaultMaxDate = 2020;

    //Duration filter
    private Label durationLabel;
    private TextBox durationBox;
    private RangeSlider durationSlider;
    private int defaultMinDuration = 0;
    private int defaultMaxDuration = 600;
	
    //Genres filter
	private Label genresLabel;
	private SuggestBox genresBox;
	
	//Languages filter
	private Label languagesLabel;
	private SuggestBox languagesBox;
	
	//Countries filter
	private Label countriesLabel;
	private SuggestBox countriesBox;

	//Buttons
	private Button submitButton;
	private Button resetButton;
	private Button exportSearchButton;
	private Button exportAllButton;
	
	/**
	 * Stringbuilder to build search query
	 */
	private StringBuilder filterString;
	
	/**
	 * Says whether the normal data set is visible or a search result.
	 */
	private boolean isSearch;
	
	/**
	 * Base url for HTTP GET requests.
	 */
	private String baseUrl = GWT.getModuleBaseURL();
	
	/**
	 * Default input text box css rule.
	 */
	private String defaultInputCSSRule = "defaultInputBox";
	
	/**
	 * Default duration input text.
	 */
	private String durationDefaultText = "e.g. " + defaultMinDuration + " or " + defaultMinDuration + "-" + defaultMaxDuration;

	/**
	 * Default date input text.
	 */
	private String dateDefaultText = "e.g. " + defaultMinDate + " or " + defaultMinDate + "-" + defaultMaxDate;
	
	/**
	 * Constructor.
	 */
	public FilterPanel() {
		fp = new FocusPanel();
		fp.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
					search();
				}
			}
			
		});
		
		//initialization
		vlp = new VerticalPanel();
		fp.add(vlp);

		//Title
		titleLabel = new Label("Title: ");
		titleSearchBox = new TextBox();

		titlePanel = new HorizontalPanel();
		titlePanel.add(titleLabel);
		titlePanel.add(titleSearchBox);
		titlePanel.addStyleName("filter-title-panel");


		//Date
		createDateFilter();

		//Duration
        createDurationFilter();

		//Button
		createSubmitButton();
		createResetButton();
		createExportSearchButton();
		createExportAllButton();
		

		//Genres
		genresLabel = new Label("Genres: ");
		genresBox = new SuggestBox(new MultiWordSuggestOracle());
		genresPanel = new HorizontalPanel();
		genresPanel.add(genresLabel);
		genresPanel.add(genresBox);
		genresPanel.setHeight("40px");
		
		//Languages
		languagesLabel = new Label("Languages: ");
		languagesBox = new SuggestBox(new MultiWordSuggestOracle());
		languagesPanel = new HorizontalPanel();
		languagesPanel.add(languagesLabel);
		languagesPanel.add(languagesBox);
		languagesPanel.setHeight("40px");
		
		//Countries
		countriesLabel = new Label("Countries: ");
		countriesBox = new SuggestBox(new MultiWordSuggestOracle());
		countriesPanel = new HorizontalPanel();
		countriesPanel.add(countriesLabel);
		countriesPanel.add(countriesBox);
		countriesPanel.setHeight("40px");
		
		
		vlp.add(titlePanel);
        vlp.add(datePanel);
        vlp.add(dateSlider);
        vlp.add(durationPanel);
        vlp.add(durationSlider);
        vlp.add(genresPanel);
        vlp.add(languagesPanel);
        vlp.add(countriesPanel);
		
		buttonsPanel = new HorizontalPanel();
		buttonsPanel.add(submitButton);
		buttonsPanel.add(resetButton);
		buttonsPanel.add(exportSearchButton);
		buttonsPanel.add(exportAllButton);
		vlp.add(buttonsPanel);
		
		//Always call for composite widgets
		initWidget(fp);
		
		setStyleName("filterPanel-composite");
	}
	
	/**
	 * Constructor and saves table reference.
	 * @param table Table reference.
	 */
	public FilterPanel(Table table){
		this();
		this.table = table;
	}
	
	/**
	 * Starts the searching process.
	 */
	private void search() {
		if(!isEmpty()){
			isSearch = true;
			if(!table.isFinishedLoading())
				createSearchQuery();
			sendSearchQuery();
		}
	}
	
	/**
	 * Gets a new KeyDownHandler for the duration filter.
	 * @return KeyDownHandler
	 */
	private KeyDownHandler getDurationKeyDownHandler() {
		KeyDownHandler handler = new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER ){
					String durationInput = durationBox.getText();
								
					//year was entered
					if( durationInput.matches("\\d{1,3}") ) {
						//if background was changed due to error, reset it
						durationBox.getElement().getStyle().setBackgroundColor("white");
						
						int min = Integer.valueOf( durationInput );
						int max = min;
						
						durationSlider.setValues(min, max);
						return;
						
					} else if( durationInput.matches("\\d{1,3}-\\d{1,3}") ) {
						//if background was changed due to error, reset it
						durationBox.getElement().getStyle().setBackgroundColor("white");
						
						//year range was entered
						String[] duration = durationInput.split("-");
						
						int min = Integer.valueOf( duration[0] );
						int max = Integer.valueOf( duration[1] );
						
						//if minvalue is greater than max value or smaller than minimum of slider indicate error with red TextBox
						if( (min <= max) && !(min < durationSlider.getMinimum()) && !(max > durationSlider.getMaximum()) ){
							durationSlider.setValues(min, max);
							return;
						}
						
					} 
					
					//non valid input
					//set background of input box to red and remove wrong input
					durationBox.setValue("");
					durationBox.getElement().getStyle().setBackgroundColor("red");
				}	
			}
		};
		
		return handler;
	}
	
	/**
	 * Gets a new KeyDownHandler for the date filter.
	 * @return KeyDownHandler
	 */
	private KeyDownHandler getDateKeyDownHandler() {
		KeyDownHandler handler = new KeyDownHandler(){
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER ){
				String dateInput = dateBox.getText();
				
				//year was entered
				if( dateInput.matches("\\d{4}") ) {
					//if background was changed due to error, reset it
					dateBox.getElement().getStyle().setBackgroundColor("white");
					
					int min = Integer.valueOf( dateInput );
					int max = min;
					
					dateSlider.setValues(min, max);
					return;
					
				} else if( dateInput.matches("\\d{4}-\\d{4}") ) {
					//if background was changed due to error, reset it
					dateBox.getElement().getStyle().setBackgroundColor("white");
					
					//year range was entered
					String[] dates = dateInput.split("-");
					
					int min = Integer.valueOf( dates[0] );
					int max = Integer.valueOf( dates[1] );
					
					//if minvalue is greater than max value or smaller than minimum of slider indicate error with red TextBox
					if( (min <= max) && !(min < dateSlider.getMinimum()) && !(max > dateSlider.getMaximum()) ){
						dateSlider.setValues(min, max);
						return;
					}
					
				} 
				
				//non valid input
				//set background of input box to red and remove wrong input
				dateBox.setValue("");
				dateBox.getElement().getStyle().setBackgroundColor("red");
				}	
			}	
		};
		
		return handler;
	}
	
	/**
	 * Gets a new SliderListener for the duration slider.
	 * @return SliderListener.
	 */
	private SliderListener getDateSliderListener() {
		SliderListener listener = new SliderListener() {
			//helper function
			private void setDateInput(int min, int max) {
				if( min == max ) {
					//if values are equal just write one value
					dateBox.setValue( Integer.toString(min) );
				} else {
					//if values are different write both
					dateBox.setValue( Integer.toString(min) + "-" + Integer.toString(max) );
				}
			}
        	
            @Override
            public void onStart(SliderEvent e) {
            	//reset input field in case it was changed due to an error
            	dateBox.getElement().getStyle().setBackgroundColor("white");
            }
            
            @Override
            public boolean onSlide(SliderEvent e) {
            	dateBox.removeStyleName(defaultInputCSSRule);
            	
                int max = dateSlider.getValueMax();
                int min = dateSlider.getValueMin();

                setDateInput(min, max);
                return true;
            }

            @Override
            public void onChange(SliderEvent e) {
                int max = dateSlider.getValueMax();
                int min = dateSlider.getValueMin();
                
                setDateInput(min, max);
            }

            @Override
            public void onStop(SliderEvent e) {
            }
		};
		
		return listener;
	}
	
	/**
	 * Gets a new SliderListener for the date filter.
	 * @return SliderListener.
	 */
	private SliderListener getDurationSliderListener() {
		SliderListener listener = new SliderListener() {
			//helper function
			private void setDurationInput(int min, int max) {
				if( min == max ) {
					//if values are equal just write one value
					durationBox.setValue( Integer.toString(min) );
				} else {
					//if values are different write both
					durationBox.setValue( Integer.toString(min) + "-" + Integer.toString(max) );
				}
			}
        	
            @Override
            public void onStart(SliderEvent e) {
            	//reset input field in case it was changed due to an error
            	durationBox.getElement().getStyle().setBackgroundColor("white");
            }
            @Override
            public boolean onSlide(SliderEvent e) {
            	durationBox.removeStyleName(defaultInputCSSRule);
            	
                int max = durationSlider.getValueMax();
                int min = durationSlider.getValueMin();
                
                setDurationInput(min, max);
                return true;
            }

            @Override
            public void onChange(SliderEvent e) {
                int max = durationSlider.getValueMax();
                int min = durationSlider.getValueMin();

                setDurationInput(min, max);
            }

            @Override
            public void onStop(SliderEvent e) {
            }
		};
		
		return listener;
	}
	
	/**
	 * Creates date labels, slider and adds listener.
	 */
	private void createDateFilter() {
		datePanel = new HorizontalPanel();
		dateLabel = new Label("Date: ");
        dateSlider = new RangeSlider("dateSlider", defaultMinDate, defaultMaxDate, defaultMinDate, defaultMaxDate);
        dateBox = new TextBox();
        
        dateBox.setText(dateDefaultText);
        
        dateBox.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				dateBox.setText("");
				dateBox.removeStyleName(defaultInputCSSRule);
			}
        	
        });
        
        dateBox.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				if(dateBox.getText().isEmpty()) {
					dateBox.setText(dateDefaultText);
					dateBox.addStyleName(defaultInputCSSRule);
				} 
			}
        	
        });
        dateBox.addStyleName(defaultInputCSSRule);
        
        datePanel.add(dateLabel);
        datePanel.add(dateBox);
        
        dateBox.addKeyDownHandler(getDateKeyDownHandler());
        dateSlider.addListener(getDateSliderListener());
	}

	/**
	 * Creates duration labels, slider and adds listeners.
	 */
	private void createDurationFilter() {
		durationPanel = new HorizontalPanel();
		durationLabel = new Label("Duration: ");
		durationSlider = new RangeSlider("durationSlider", defaultMinDuration, defaultMaxDuration, defaultMinDuration, defaultMaxDuration);
        durationBox = new TextBox();
        
        durationBox.setText(durationDefaultText);
        durationPanel.add(durationLabel);
        durationPanel.add(durationBox);
        
        
        durationBox.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				durationBox.setText("");
				durationBox.removeStyleName(defaultInputCSSRule);
			}
        	
        });
        
        durationBox.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				if(durationBox.getText().isEmpty()) {
					durationBox.setText(durationDefaultText);
					durationBox.addStyleName(defaultInputCSSRule);
				} 
			}
        	
        });
        durationBox.addStyleName(defaultInputCSSRule);
        
        durationBox.addKeyDownHandler(getDurationKeyDownHandler());
        durationSlider.addListener(getDurationSliderListener());
     
        
        durationPanel.add(durationLabel);
        durationPanel.add(durationBox);
        durationPanel.add(durationSlider);
	}
	
	/**
	 * Empty all textboxes and reset slider labels and values.
	 */
	private void emptySearchParameters() {
		titleSearchBox.setText("");
		dateSlider.setValues(defaultMinDate, defaultMaxDate);
		durationSlider.setValues(defaultMinDuration, defaultMaxDuration);
		genresBox.setText("");
		languagesBox.setText("");
		countriesBox.setText("");
		
		dateBox.setText(dateDefaultText);
		dateBox.addStyleName(defaultInputCSSRule);
		
		durationBox.setText(durationDefaultText);
		durationBox.addStyleName(defaultInputCSSRule);
	}
	
	/**
	 * Checks if all input boxes are empty and sliders are set to default value.
	 * @return {@code true} if text boxes are empty and sliders are set to default values, {@code false} otherwise.
	 * 
	 */
	private boolean isEmpty() {
		return titleSearchBox.getText().isEmpty() && (dateSlider.getValueMin() == dateSlider.getMinimum()) &&
				(dateSlider.getValueMax() == dateSlider.getMaximum()) && durationSlider.getValueMin() == durationSlider.getMinimum() &&
				(durationSlider.getValueMax() == durationSlider.getMaximum()) && genresBox.getText().isEmpty() &&
				languagesBox.getText().isEmpty() && countriesBox.getText().isEmpty();
	}

	/**
	 * Creates a submit button and adds clicklistener.
	 */
	private void createSubmitButton() {
		if (submitButton != null){
			return;
		}
		submitButton  = new Button("Filter...", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				search();
			}
		});
	}
	
	/**
	 * Creates a search query.
	 */
	private void createSearchQuery() {
		//send query
		//create filter query
		filterString = new StringBuilder(
				"select m.*, group_concat(DISTINCT g.genre) genres, "
						+ "group_concat(DISTINCT l.language) languages, "
						+ "group_concat(DISTINCT c.country) countries "
						+ "from movies m left join moviegenres mg on m.movieid=mg.movieid "
						+ "left join genres g on g.genreid=mg.genreid "
						+ "left join movielanguages ml on m.movieid=ml.movieid "
						+ "left join languages l on l.languageid=ml.languageid "
						+ "left join moviecountries mc on m.movieid=mc.movieid "
						+ "left join countries c on c.countryid=mc.countryid "
						+ "where "
				);
		
		//add different filters to query
		//if title is empty do not include it in query
		if( !getSearchBoxCaption().equals("") )
			//the " and " at the end 
			filterString.append( "LOWER(m.title) like \"%" + getSearchBoxCaption().toLowerCase() + "%\" and " );
		
		//countries
		if( !countriesBox.getText().equals("") )
			filterString.append( "LOWER(c.country) like \"%" + countriesBox.getText().toLowerCase() + "%\" and " );
		
		//genres
		if( !genresBox.getText().equals("") )
			filterString.append( "LOWER(g.genre) like \"%" + genresBox.getText().toLowerCase() + "%\" and " );
		
		//languages
		if( !languagesBox.getText().equals("") )
			filterString.append( "LOWER(l.language) like \"%" + languagesBox.getText().toLowerCase() + "%\" and " );
		
		if(shouldNotLimitDurationUpwards()){
			filterString.append( "m.duration >= " + durationSlider.getValueMin() + " and " );
			
		} else {
			filterString.append( "m.duration >= " + durationSlider.getValueMin() + " and m.duration <= " + durationSlider.getValueMax() + " and " );
		}
		
		if(shouldNotLimitDateDownwards()) {
			filterString.append( "m.date <= " +dateSlider.getValueMax() + " " );
		} else {
			filterString.append( "m.date >= " + dateSlider.getValueMin() + " and m.date <= " + dateSlider.getValueMax() + " " );
		}
		
		//has to be last String in this chain
		filterString.append( "group by m.movieid;" );	
		
	}
	
	/**
	 * Sends the search query.
	 */
	private void sendSearchQuery() {
		if(!table.isFinishedLoading()){
			AsyncCallback<FilmDataSet> callback = new AsyncCallback<FilmDataSet>() {
		    	public void onFailure(Throwable caught) {
		    		Window.alert("Search Query failed");
		    		caught.printStackTrace();
		    	}
	
		    	public void onSuccess(FilmDataSet result) {
		            table.setList(result, true);	            
		    	}
		    };
	    	filmDataSvc.getFilmData(filterString.toString(), true, callback);
	    	
		} else {
			//Filter locally.
			
			FilmDataSet result = new FilmDataSet(table.getList());
				
			String title = titleSearchBox.getText();
			String country = countriesBox.getText();
			String genre = genresBox.getText();
			String language = languagesBox.getText();
			
			Range durationRange = new Range(durationSlider.getValueMin(), durationSlider.getValueMax() - durationSlider.getValueMin());
			Range dateRange = new Range(dateSlider.getValueMin(), dateSlider.getValueMax() - dateSlider.getValueMin());
			result = new FilmDataSet(result.filter(title, country, genre, language, durationRange, dateRange, shouldNotLimitDurationUpwards(), shouldNotLimitDateDownwards()));
			
			table.setList(result, true);
		}
	}
	
	/**
	 * Removes search result from table and refreshes table with unfiltered results.
	 */
	private void createResetButton() {
		if (resetButton != null){
			return;
		}
		resetButton = new Button("Reset", new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				//removes filtered results
				table.reset();
				
				//empties search panel
				emptySearchParameters();
				
				isSearch = false;
			}
		});
	}
	
	/**
	 * Gets all search parameters.
	 * @return A String array containing all search parameters.
	 */
	private String[] getSearchParameters() {
		String[] result = new String[8];
		result[0] = titleSearchBox.getText();
		result[1] = countriesBox.getText();
		result[2] = genresBox.getText();
		result[3] = languagesBox.getText();
		result[4] = Integer.toString(durationSlider.getValueMin());
		result[5] = Integer.toString(durationSlider.getValueMax());
		result[6] = Integer.toString(dateSlider.getValueMin());
		result[7] = Integer.toString(dateSlider.getValueMax());
		
		return result;
	}
	
	/**
	 * Create GET query from search parameters.
	 * @return HTTP GET query to be added to the base url.
	 */
	private String getSearchParametersUrl() {
		String[] parameters = getSearchParameters();
		String result = "&title=" + parameters[0] + 
				"&country=" + parameters[1] + "&genre=" + parameters[2] + "&language=" + parameters[3] +
				"&durationMin=" + parameters[4] + "&durationMax=" + parameters[5] + 
				"&dateMin=" + parameters[6] + "&dateMax=" + parameters[7];
		
		return result;
	}

	/**
	 * Creates a button and attaches a clickhandler to export data set to tsv.
	 */
	private void createExportSearchButton() {
		exportSearchButton = new Button("Export Search", new ClickHandler(){
			// On click send a get request
			@Override
			public void onClick(ClickEvent event) {
				
				if(!isEmpty() && isSearch){
					// Servlet URL
					String url = baseUrl + "filmData?search=true" + "&extended=" + table.isFinishedLoading();
					
					if(table.isFinishedLoading()) {
						url = url + getSearchParametersUrl();
						}
					doGet(url);
				} else {
					Window.alert("Please choose at least one search option to use this functionality");
				}
			}
		});
	}
	
	/**
	 * Creates a button and attaches a clickhandler to export data set to tsv.
	 */
	private void createExportAllButton() {
		exportAllButton = new Button("Export All", new ClickHandler(){
			// On click send a get request
			@Override
			public void onClick(ClickEvent event) {
				// Servlet URL
				String url = baseUrl + "filmData?search=false";
				doGet(url);
			}
		});
	}
	
	/**
	 * Sends HTTP GET request.
	 */
	private void doGet(final String url) {
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
			    		Window.open(url, "_self", "status=0,toolbar=0,menubar=0,location=0");
			    	} else {
			    		// Handle the error.  Can get the status text from response.getStatusText()
			    	}
			    }
			});
		} catch (RequestException e) {
			// Couldn't connect to server
		}
	}
	
	/**
	 * Set country suggestion list
	 * @param countries Countries list to be set as suggestions.
	 */
	void setCountrySuggestion(ArrayList<String> countries){
		MultiWordSuggestOracle oracle = (MultiWordSuggestOracle) countriesBox.getSuggestOracle();
		
		for(int i = 0; i < countries.size(); i++){
			oracle.add(countries.get(i));
		}
	}
	
	/**
	 * Set genre suggestion list
	 * @param genres Genres list to be set as suggestions.
	 */
	void setGenresSuggestion(ArrayList<String> genres) {
		MultiWordSuggestOracle oracle = (MultiWordSuggestOracle) genresBox.getSuggestOracle();
		
		for(int i = 0; i < genres.size(); i++){
			oracle.add(genres.get(i));
		}
	}
	
	/**
	 * Set language suggestion list
	 * @param languages Languages list to be set as suggestions.
	 */
	void setLanguagesSuggestion(ArrayList<String> languages) {
		MultiWordSuggestOracle oracle = (MultiWordSuggestOracle) languagesBox.getSuggestOracle();
		
		for(int i = 0; i < languages.size(); i++){
			oracle.add(languages.get(i));
		}
	}
		
	public String getSearchBoxCaption() {
		return titleSearchBox.getText();
	}
	
	/**
	 * Returns true if we should omit upper limit for the search query
	 * @return true if we should omit upper limit for the duration
	 * TODO: Find better name
	 */
	private boolean shouldNotLimitDurationUpwards() {
		return durationSlider.getValueMax() == durationSlider.getMaximum();
	}
	
	/**
	 * Returns true if we should omit lower limit for the search query
	 * @return true if we should omit lower limit for the date
	 * TODO: Find better name
	 */
	private boolean shouldNotLimitDateDownwards() {
		return dateSlider.getValueMin() == dateSlider.getMinimum();
	}
}
