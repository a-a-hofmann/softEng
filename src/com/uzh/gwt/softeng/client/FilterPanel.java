package com.uzh.gwt.softeng.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FilterPanel extends Composite {
	//This widgets main panel
	private VerticalPanel vlp;

	//dummy content
	private SimplePanel panel1;
	private SimplePanel panel2;

	private TextBox textBox = new TextBox();
	private CheckBox checkBox = new CheckBox();

	public FilterPanel() {
		//initialization
		vlp = new VerticalPanel();
		panel1 = new SimplePanel();
		panel2 = new SimplePanel();

		//add components to vertical panel
		panel1.add(textBox);
		panel2.add(checkBox);

		vlp.add(panel1);
		vlp.add(panel2);


		initWidget(vlp);

		setStyleName("filterPanel-composite");
	}

	@SuppressWarnings("deprecation")
	public void onClick(ClickEvent event) {
		Object sender = event.getSource();
		if (sender == checkBox) {
			// When the check box is clicked, update the text box's enabled state.
			textBox.setEnabled(checkBox.isChecked());
		}
	}

	/**
	 * Sets the caption associated with the check box.
	 * 
	 * @param caption the check box's caption
	 */
	public void setCaption(String caption) {
		// Note how we use the use composition of the contained widgets to provide
		// only the methods that we want to.
		checkBox.setText(caption);
	}

	/**
	 * Gets the caption associated with the check box.
	 * 
	 * @return the check box's caption
	 */
	public String getCaption() {
		return checkBox.getText();
	}
}
