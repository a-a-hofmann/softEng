package com.uzh.gwt.softeng.client;

import java.util.ArrayList;

import org.spiffyui.client.widgets.slider.RangeSlider;

import com.google.gwt.user.client.Window;
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
import com.uzh.gwt.softeng.shared.FilmDataSet;

public class FilterPanel extends Composite {

	FilmDataSet filmSet;
	//This widgets main panel
	private VerticalPanel vlp;
	private HorizontalPanel titlePanel;
	private HorizontalPanel datePanel;
	private HorizontalPanel genresPanel;
	private HorizontalPanel languagesPanel;
	private HorizontalPanel countriesPanel;

	private Label titleLabel;
	private TextBox titleSearchBox;

	private Label dateLabel;
	private RangeSlider dateSlider;

	private Label durationLabel;
	private RangeSlider durationSlider;
	
	private Label genresLabel;
	private SuggestBox genresBox;
	
	private Label languagesLabel;
	private SuggestBox languagesBox;
	
	private Label countriesLabel;
	private SuggestBox countriesBox;

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


		//Date
		dateLabel = new Label("Date: ");
		dateSlider = new RangeSlider("dateSlider", 1888, 2020, 1888, 2020);

		//Duration
		durationLabel = new Label("Duration: ");
		durationSlider = new RangeSlider("durationSlider", 0, 400, 0, 400);



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
				if( !getTitle().equals("") )
					filterString.append( "m.title = " + getTitle() + " ");
				
				filterString.append( "m.duration >= " + durationSlider.getMinimum() + " and m.duration <= " + durationSlider.getMaximum() + " " );
				filterString.append( "m.date >= " + dateSlider.getMinimum() + " and m.date <= " +dateSlider.getMaximum() + " " );
				
				//has to be last String in this chain
				filterString.append( "group by m.movieid;" );
				
				//TODO: send query to server
			}
		});

		//Genres
		genresLabel = new Label("Genres: ");
		genresBox = new SuggestBox();
		genresPanel = new HorizontalPanel();
		genresPanel.add(genresLabel);
		genresPanel.add(genresBox);
		
		//Languages
		languagesLabel = new Label("Languages: ");
		languagesBox = new SuggestBox();
		languagesPanel = new HorizontalPanel();
		languagesPanel.add(languagesLabel);
		languagesPanel.add(languagesBox);
		
		//Countries
		countriesLabel = new Label("Countries: ");
		countriesBox = new SuggestBox(getCountrySuggestion());
		countriesBox.setWidth("200px");
		countriesPanel = new HorizontalPanel();
		countriesPanel.add(countriesLabel);
		countriesPanel.add(countriesBox);
		
		
		vlp.add(titlePanel);
		vlp.add(dateLabel);
		vlp.add(dateSlider);
		vlp.add(durationLabel);
		vlp.add(durationSlider);
		vlp.add(genresPanel);
		vlp.add(languagesPanel);
		vlp.add(countriesPanel);
		vlp.add(submitButton);

		initWidget(vlp);

		setStyleName("filterPanel-composite");
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

	public String getDateRange() {
		return new String( dateSlider.getMinimum() + " " + dateSlider.getMaximum() );
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
