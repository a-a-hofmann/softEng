package com.uzh.gwt.softeng.client;

import org.spiffyui.client.widgets.slider.RangeSlider;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
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
		
		vlp.add(titlePanel);
		vlp.add(dateLabel);
		vlp.add(dateSlider);
		vlp.add(durationLabel);
		vlp.add(durationSlider);
		initWidget(vlp);

		setStyleName("filterPanel-composite");
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
