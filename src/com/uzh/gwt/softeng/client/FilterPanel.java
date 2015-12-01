package com.uzh.gwt.softeng.client;

import java.util.ArrayList;

import org.spiffyui.client.widgets.slider.RangeSlider;
import org.spiffyui.client.widgets.slider.SliderEvent;
import org.spiffyui.client.widgets.slider.SliderListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.uzh.gwt.softeng.shared.FilmDataSet;

public class FilterPanel extends Composite {
	
	/**
	 * RPC Object
	 */
	private FilmDataServiceAsync filmDataSvc = GWT.create(FilmDataService.class);
	
	private Table table;

	FilmDataSet filmSet;
	
	private FocusPanel fp;
	//This widgets main panel
	private VerticalPanel vlp;
	
	//Sub panels containing the different search widgets
	private HorizontalPanel titlePanel;
	private HorizontalPanel genresPanel;
	private HorizontalPanel languagesPanel;
	private HorizontalPanel countriesPanel;
	private HorizontalPanel buttonsPanel;
	
	//Title filter
	private Label titleLabel;
	private TextBox titleSearchBox;

	//Date filter
	private Label dateLabel;
    private RangeSlider dateSlider;
    private Label dateminValueLabel;
    private Label datemaxValueLabel;

    //Duration filter
    private Label durationLabel;
    private RangeSlider durationSlider;
    private Label durationminValueLabel;
    private Label durationmaxValueLabel;
	
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
	private Button exportButton;
	
	//Stringbuilder to build search query
	private StringBuilder filterString;
	
	//Says whether the normal data set is visible or a search result
	private boolean isSearch;

	/**
	 * Constructor.
	 */
	public FilterPanel() {
		fp = new FocusPanel();
		fp.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
					isSearch = true;
					
					createSearchQuery();
					
					sendSearchQuery();
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
		createTSVExportButton();
		

		//Genres
		genresLabel = new Label("Genres: ");
		genresBox = new SuggestBox();
		genresPanel = new HorizontalPanel();
		genresPanel.add(genresLabel);
		genresPanel.add(genresBox);
//		genresPanel.addStyleName("filter-genre-panel");
		genresPanel.setHeight("40px");
		
		//Languages
		languagesLabel = new Label("Languages: ");
		languagesBox = new SuggestBox();
		languagesPanel = new HorizontalPanel();
		languagesPanel.add(languagesLabel);
		languagesPanel.add(languagesBox);
//		languagesPanel.addStyleName("filter-language-panel");
		languagesPanel.setHeight("40px");
		
		//Countries
		countriesLabel = new Label("Countries: ");
		countriesBox = new SuggestBox(getCountrySuggestion());
		countriesPanel = new HorizontalPanel();
		countriesPanel.add(countriesLabel);
		countriesPanel.add(countriesBox);
//		countriesPanel.addStyleName("filter-countries-panel");
		countriesPanel.setHeight("40px");
		
		
		vlp.add(titlePanel);
        vlp.add(dateLabel);
        vlp.add(dateminValueLabel);
        vlp.add(datemaxValueLabel);
        vlp.add(dateSlider);
        vlp.add(durationLabel);
        vlp.add(durationminValueLabel);
        vlp.add(durationmaxValueLabel);
        vlp.add(durationSlider);
        vlp.add(genresPanel);
        vlp.add(languagesPanel);
        vlp.add(countriesPanel);
		
		buttonsPanel = new HorizontalPanel();
		buttonsPanel.add(submitButton);
		buttonsPanel.add(resetButton);
		buttonsPanel.add(exportButton);
		vlp.add(buttonsPanel);
		
		//Always call for composite widgets
		initWidget(fp);
		
		setStyleName("filterPanel-composite");
	}
	
	/**
	 * Creates duration labels, slider and adds listeners.
	 */
	private void createDurationFilter() {
		//Duration
        durationLabel = new Label("Duration: ");
        durationSlider = new RangeSlider("durationSlider", 0, 400, 0, 400);
        durationminValueLabel = new Label("Min: 0");
        durationminValueLabel.setHeight("20px");
        durationmaxValueLabel = new Label("Max: 400");
        durationmaxValueLabel.setHeight("20px");

        durationSlider.addListener(new SliderListener(){
            @Override
            public void onStart(SliderEvent e) {
            }
            @Override
            public boolean onSlide(SliderEvent e) {
                int max = durationSlider.getValueMax();
                int min = durationSlider.getValueMin();

                durationminValueLabel.setText("Min: " + min);
                durationmaxValueLabel.setText("Max: " + max);
                return true;
            }

            @Override
            public void onChange(SliderEvent e) {
                int max = durationSlider.getValueMax();
                int min = durationSlider.getValueMin();

                durationminValueLabel.setText("Min: " + min);
                durationmaxValueLabel.setText("Max: " + max);
            }

            @Override
            public void onStop(SliderEvent e) {
            }
        });
	}

	/**
	 * Creates date labels, slider and adds listener.
	 */
	private void createDateFilter() {
		dateLabel = new Label("Date: ");
        dateSlider = new RangeSlider("dateSlider", 1888, 2020, 1888, 2020);
        dateminValueLabel = new Label("Min: 1888");
        dateminValueLabel.setHeight("20px");
        datemaxValueLabel = new Label("Max: 2020");
        datemaxValueLabel.setHeight("20px");

        dateSlider.addListener(new SliderListener(){
            @Override
            public void onStart(SliderEvent e) {
            }
            @Override
            public boolean onSlide(SliderEvent e) {
                int max = dateSlider.getValueMax();
                int min = dateSlider.getValueMin();

                dateminValueLabel.setText("Min: " + min);
                datemaxValueLabel.setText("Max: " + max);
                return true;
            }

            @Override
            public void onChange(SliderEvent e) {
                int max = dateSlider.getValueMax();
                int min = dateSlider.getValueMin();

                dateminValueLabel.setText("Min: " + min);
                datemaxValueLabel.setText("Max: " + max);
            }

            @Override
            public void onStop(SliderEvent e) {
            }

        });
	}
	
	FilterPanel(Table table){
		this();
		this.table = table;
	}
	
	/**
	 * Empty all textboxes and reset slider labels and values.
	 */
	private void emptySearchQuery() {
		titleSearchBox.setText("");
		dateSlider.setValues(1888, 2020);
		durationSlider.setValues(0, 400);
		genresBox.setText("");
		languagesBox.setText("");
		countriesBox.setText("");
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
				isSearch = true;
				
				createSearchQuery();
				
				sendSearchQuery();
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
		
		filterString.append( "m.duration >= " + durationSlider.getValueMin() + " and m.duration <= " + durationSlider.getValueMax() + " and " );
		filterString.append( "m.date >= " + dateSlider.getValueMin() + " and m.date <= " +dateSlider.getValueMax() + " " );
		//has to be last String in this chain
		filterString.append( "group by m.movieid;" );
	}
	
	/**
	 * Sends the search query.
	 */
	private void sendSearchQuery() {
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
				emptySearchQuery();
				
				isSearch = false;
			}
		});
	}
	

	/**
	 * Creates a button and attaches a clickhandler to export data set to tsv.
	 * @return
	 */
	private void createTSVExportButton() {
		exportButton = new Button("Export", new ClickHandler(){
			// On click send a get request
			@Override
			public void onClick(ClickEvent event) {
				// Servlet URL
				final String url = GWT.getModuleBaseURL() + "filmData?type=TSV&search=" + isSearch;
				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
				
				try {
					// Create a HTTP GET request
					builder.sendRequest(null, new RequestCallback() {
						public void onError(Request request, Throwable exception) {
							// Couldn't connect to server (could be timeout, SOP violation, etc.)
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
		});
	}
	
	public MultiWordSuggestOracle getCountrySuggestion(){
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
		
		return oracle;
	}
	
	public void setCountrySuggestion(ArrayList<String> countries){
		MultiWordSuggestOracle oracle = (MultiWordSuggestOracle) countriesBox.getSuggestOracle();
		
		for (String country : countries){
			oracle.add(country);
		}
	}

	public String getSearchBoxCaption() {
		return titleSearchBox.getText();
	}
}
