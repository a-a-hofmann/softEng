package com.uzh.gwt.softeng.client;

import org.spiffyui.client.widgets.slider.RangeSlider;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FilterPanel extends Composite {

	//This widgets main panel
	private VerticalPanel vlp;
	private HorizontalPanel titlePanel;
	private VerticalPanel datePanel;

	private Label titleLabel;
	private TextBox titleSearchBox;

	private Label dateLabel;
	private RangeSlider dateSlider;

	private Label durationLabel;
	private RangeSlider durationSlider;

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

		vlp.add(titlePanel);
		vlp.add(dateLabel);
		vlp.add(dateSlider);
		vlp.add(durationLabel);
		vlp.add(durationSlider);
		vlp.add(submitButton);

		initWidget(vlp);

		setStyleName("filterPanel-composite");
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
