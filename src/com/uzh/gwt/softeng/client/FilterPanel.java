package com.uzh.gwt.softeng.client;

import java.util.ArrayList;
import java.util.List;

import org.spiffyui.client.widgets.slider.RangeSlider;
import org.spiffyui.client.widgets.slider.SliderEvent;
import org.spiffyui.client.widgets.slider.SliderListener;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.uzh.gwt.softeng.shared.FilmData;
import com.uzh.gwt.softeng.shared.FilmDataSet;

public class FilterPanel extends Composite {
	
	/**
	 * RPC Object
	 */
	private FilmDataServiceAsync filmDataSvc = GWT.create(FilmDataService.class);
	
	private Table table;

	FilmDataSet filmSet;
	//This widgets main panel
	private VerticalPanel vlp;
	private HorizontalPanel titlePanel;
	private HorizontalPanel datePanel;
	private HorizontalPanel genresPanel;
	private HorizontalPanel languagesPanel;
	private HorizontalPanel countriesPanel;
	private HorizontalPanel buttonsPanel;

	private Label titleLabel;
	private TextBox titleSearchBox;

	private Label dateLabel;
    private RangeSlider dateSlider;
    private Label dateminValueLabel;
    private Label datemaxValueLabel;

    private Label durationLabel;
    private RangeSlider durationSlider;
    private Label durationminValueLabel;
    private Label durationmaxValueLabel;
	
	private Label genresLabel;
	private SuggestBox genresBox;
	
	private Label languagesLabel;
	private SuggestBox languagesBox;
	
	private Label countriesLabel;
	private SuggestBox countriesBox;

	private Button resetButton;
	private Button submitButton;
	private StringBuilder filterString;

	public FilterPanel() {
		//initialization
		vlp = new VerticalPanel();

		//Title
		titleLabel = new Label("Title: ");
		titleSearchBox = new TextBox();

		titlePanel = new HorizontalPanel();
		titlePanel.add(titleLabel);
		titlePanel.add(titleSearchBox);
		titlePanel.addStyleName("filter-title-panel");


		//Date
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


        //Duration
        durationLabel = new Label("Duration: ");
        durationSlider = new RangeSlider("durationSlider", 0, 400, 0, 400);
        durationminValueLabel = new Label("Min: 400");
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



		//Button
		submitButton  = new Button("Filter...", new ClickHandler() {
			public void onClick(ClickEvent event) {

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
				
				
				AsyncCallback<FilmDataSet> callback = new AsyncCallback<FilmDataSet>() {
			    	public void onFailure(Throwable caught) {
			    		Window.alert("Search Query failed");
			    		caught.printStackTrace();
			    	}
		
			    	public void onSuccess(FilmDataSet result) {
			            table.setList(result);	            
			    	}
			    };
		    	filmDataSvc.getFilmData(filterString.toString(), callback);
			}
		});
		
		resetButton = new Button("Reset", new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				table.reset();
			}
			
		});

		//Genres
		genresLabel = new Label("Genres: ");
		genresBox = new SuggestBox();
		genresPanel = new HorizontalPanel();
		genresPanel.add(genresLabel);
		genresPanel.add(genresBox);
		genresPanel.addStyleName("filter-genre-panel");
		
		//Languages
		languagesLabel = new Label("Languages: ");
		languagesBox = new SuggestBox();
		languagesPanel = new HorizontalPanel();
		languagesPanel.add(languagesLabel);
		languagesPanel.add(languagesBox);
		languagesPanel.addStyleName("filter-language-panel");
		
		//Countries
		countriesLabel = new Label("Countries: ");
		countriesBox = new SuggestBox(getCountrySuggestion());
		countriesBox.setWidth("200px");
		countriesPanel = new HorizontalPanel();
		countriesPanel.add(countriesLabel);
		countriesPanel.add(countriesBox);
		countriesPanel.addStyleName("filter-countries-panel");
		
		
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
		vlp.add(buttonsPanel);

		initWidget(vlp);

		setStyleName("filterPanel-composite");
	}
	
	FilterPanel(Table table){
		this();
		this.table = table;
	}
	
	public MultiWordSuggestOracle getCountrySuggestion(){
		MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
		
//		String a = "Afghanistan";
//		String b = "Albania";
//		String c= "Algeria";
//		String d = "Argentina";
//		String e = "Armenia";
//		
//		oracle.add(a);
//		oracle.add(b);
//		oracle.add(c);
//		oracle.add(d);
//		oracle.add(e);
		
		return oracle;
	}
	
	public void setCountrySuggestion(ArrayList<String> countries){
		MultiWordSuggestOracle oracle = (MultiWordSuggestOracle) countriesBox.getSuggestOracle();
		
		for (String country : countries){
			Window.alert("Country: " + country);
			oracle.add(country);
		}
	}

	public String getSearchBoxCaption() {
		return titleSearchBox.getText();
	}


	//	@SuppressWarnings("deprecation")
	//	public void onClick(ClickEvent event) {
	//		Object sender = event.getSource();
	//		if (sender == checkBox) {
	//			// When the check box is clicked, update the text box's enabled state.
	//			textBox.setEnabled(checkBox.isChecked());
	//		}
	//	}
	//
	//	/**
	//	 * Sets the caption associated with the check box.
	//	 * 
	//	 * @param caption the check box's caption
	//	 */
	//	public void setCaption(String caption) {
	//		// Note how we use the use composition of the contained widgets to provide
	//		// only the methods that we want to.
	//		checkBox.setText(caption);
	//	}
	//
	//	/**
	//	 * Gets the caption associated with the check box.
	//	 * 
	//	 * @return the check box's caption
	//	 */
	//	public String getCaption() {
	//		return checkBox.getText();
	//	}
}
