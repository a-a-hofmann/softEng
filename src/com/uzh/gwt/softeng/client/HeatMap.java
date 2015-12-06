package com.uzh.gwt.softeng.client;


import java.util.Map;

import org.spiffyui.client.widgets.slider.RangeSlider;
import org.spiffyui.client.widgets.slider.SliderEvent;
import org.spiffyui.client.widgets.slider.SliderListener;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.Range;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.geochart.GeoChart;
import com.googlecode.gwt.charts.client.geochart.GeoChartColorAxis;
import com.googlecode.gwt.charts.client.geochart.GeoChartOptions;
import com.uzh.gwt.softeng.shared.FilmDataSet;

/**
 * The {@code HeatMap} class handles the GeoChart object and the Slider and TextBox for the manipulation.
 * It is an extension to the GWT Composite class.
 * It is composed of GeoChart, Slider and TextBox.
 */
public class HeatMap extends Composite {

	/**
	 * The actual heatmap.
	 */
	private GeoChart geoChart;
	
	/**
	 * filmSet to be represented.
	 * Do not modify internally. Modify only from outside using the setter
	 * when a new data set is available.
	 */
	private FilmDataSet filmSet;
	
	/**
	 * Filtered data set.
	 */
	private FilmDataSet filteredSet;
	
	/**
	 * Panel to contain heatmap.
	 */
	private DockLayoutPanel dlp;
	
	/**
	 * Datatable to be used to draw heatmap.
	 */
	private DataTable dataTable;
	
	/**
	 * Range slider.
	 */
	private RangeSlider slider;
	
	/**
	 * Slider wrapper. 
	 */
	private FocusPanel yearControlsWrapper;
	
	/**
	 * Horizontal panel containing slider controls.
	 */
	private HorizontalPanel yearControls;
	
	/**
	 * "Choose Year" label.
	 */
	private Label yearLabel;
	
	/**
	 * Input text box to specify year or year range.
	 */
	private TextBox yearInputTextBox;
	
	/**
	 * Panel containing the slider and two label for min and max value.
	 */
	private DockLayoutPanel sliderPanel;
	
	/**
	 * Minimum slider value.
	 */
	private int sliderDefaultMin = 1888;
	
	/**
	 * Maximum slider value.
	 */
	private int sliderDefaultMax = 2020;
	
	/**
	 * Default slider input text.
	 */
	private String defaultText = "e.g. " + sliderDefaultMin + " or " + sliderDefaultMin + "-" + sliderDefaultMax;
	
	/**
	 * Default input text box css rule.
	 */
	private String defaultInputCSSRule = "defaultInputBox";
	
	/**
	 * Heatmap constructor.
	 */
	public HeatMap() {
		initialize();
	}
	
	/**
	 * Heatmap constructor.
	 * @param filmSet FilmDataSet to be drawn.
	 */
	public HeatMap(FilmDataSet filmSet){
		this();
		this.filmSet = filmSet;
		this.filteredSet = filmSet;
	}

	/**
	 * Sets heatmap parameters.
	 */
	private void initialize() {
		ChartLoader chartLoader = new ChartLoader(ChartPackage.GEOCHART);
		dlp = new DockLayoutPanel(Unit.PCT);
		dlp.setHeight("600px");
		initWidget(dlp);
		
		initializeSliderPanel();
		
		chartLoader.loadApi(new Runnable() {

			@Override
			public void run() {
				// Create and attach the chart
				geoChart = new GeoChart();
				
				dlp.addSouth(sliderPanel, 20);
				dlp.add(geoChart);
				fillDataTable();
				draw();
			}
		});
	}

	/**
	 * Initializes panel containing slider and labels.
	 */
	private void initializeSliderPanel() {
		//Slider Controls Part
		yearControls = new HorizontalPanel();
		yearControlsWrapper = new FocusPanel( yearControls );
		
		yearControlsWrapper.addKeyDownHandler(getKeyDownHandler());
		
		//Initialize Year Input Field
		yearLabel = new Label("Choose Year: ");
		createYearInputTextBox();
        
		
		yearControls.add(yearLabel);
		yearControls.add(yearInputTextBox);
		yearControls.setStyleName("SliderControls");
		
		//Slider Part
		sliderPanel = new DockLayoutPanel(Unit.PCT);
		sliderPanel.setWidth("600px");
		sliderPanel.addStyleName("SliderPanel");
		
		slider = new RangeSlider("slider", 1888, 2020, 1888, 2020);
		slider.addListener(getSliderListener());
		
		//explanation of arguments for widgets added to DockLayoutPanel
		//first arg: widget name
		//second arg: size in percent
		sliderPanel.addNorth(yearControlsWrapper, 70);
		sliderPanel.add(slider);
		//set CSS property directly so that slider handles are visible
		slider.getElement().getParentElement().getStyle().setOverflow(Overflow.VISIBLE);
		
		sliderPanel.setHeight("60px");
		sliderPanel.setWidth("500px");
		
	}

	/**
	 * Creates a new keyboard handler.
	 * @return a new keyboard handler.
	 */
	private KeyDownHandler getKeyDownHandler() {
		
		//Added pressing Enter-Key support
		//If there is a number in either fromYear or toYear the slider will get updated accordingly on pressing Enter
		KeyDownHandler handler = new KeyDownHandler() {
		
			public void onKeyDown(KeyDownEvent event) {
				if( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
					
					String yearInput = yearInputTextBox.getText();
					
					//year was entered
					if( yearInput.matches("\\d{4}") ) {
						//if background was changed due to error, reset it
						yearInputTextBox.getElement().getStyle().setBackgroundColor("white");
						
						int min = Integer.valueOf( yearInput );
						int max = min;
						
						slider.setValues(min, max);
						return;
						
					} else if( yearInput.matches("\\d{4}-\\d{4}") ) {
						//if background was changed due to error, reset it
						yearInputTextBox.getElement().getStyle().setBackgroundColor("white");
						
						//year range was entered
						String[] years = yearInput.split("-");
						
						int min = Integer.valueOf( years[0] );
						int max = Integer.valueOf( years[1] );
						
						//if minvalue is greater than max value or smaller than minimum of slider indicate error with red TextBox
						if( (min <= max) && !(min < slider.getMinimum()) && !(max > slider.getMaximum()) ){
							slider.setValues(min, max);
							return;
						}
						
					} 
					
					//non valid input
					//set background of input box to red and remove wrong input
					yearInputTextBox.setValue("");
					yearInputTextBox.getElement().getStyle().setBackgroundColor("red");
					
				}
			}
		};
		
		return handler;
	}

	/**
	 * Creates a new Text Box with onFocus and onBlur events.
	 */
	private void createYearInputTextBox() {
		yearInputTextBox = new TextBox();
		yearInputTextBox.setValue(defaultText);
		yearInputTextBox.addStyleName(defaultInputCSSRule);
		
		yearInputTextBox.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				yearInputTextBox.setText("");
				yearInputTextBox.removeStyleName(defaultInputCSSRule);
			}
        	
        });
        
		yearInputTextBox.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				if(yearInputTextBox.getText().isEmpty()) {
					yearInputTextBox.setText(defaultText);
					yearInputTextBox.addStyleName(defaultInputCSSRule);
				} 
			}
        	
        });
	}
	
	/**
	 * Creates a new slider listener.
	 * @return a new slider listener.
	 */
	private SliderListener getSliderListener() {
		SliderListener listener = new SliderListener() {
			//helper function
			private void setYearInput(int min, int max) {
				if( min == max ) {
					//if values are equal just write one value
					yearInputTextBox.setValue( Integer.toString(min) );
				} else {
					//if values are different write both
					yearInputTextBox.setValue( Integer.toString(min) + "-" + Integer.toString(max) );
				}
			}
			
			@Override
			public void onStart(SliderEvent e) {
				//reset input field in case it was changed due to an error
				yearInputTextBox.getElement().getStyle().setBackgroundColor("white");
			}

			@Override
			public boolean onSlide(SliderEvent e) {
				yearInputTextBox.removeStyleName(defaultInputCSSRule);
				int max = slider.getValueMax();
				int min = slider.getValueMin();

				setYearInput(min, max);
				return true;
			}

			@Override
			public void onChange(SliderEvent e) {
				int max = slider.getValueMax();
				int min = slider.getValueMin();

				setYearInput(min, max);
				
				if (isDefault()) {
					yearInputTextBox.setText(defaultText);
					yearInputTextBox.addStyleName(defaultInputCSSRule);
				}
				
				if (filmSet.size() != 0) {
					filteredSet = new FilmDataSet(filmSet.filterByDateRange(new Range(min, max - min)));
					fillDataTable();
					draw();
				}
			}

			@Override
			public void onStop(SliderEvent e) {
				
				int max = slider.getValueMax();
				int min = slider.getValueMin();
				
				filteredSet = new FilmDataSet(filmSet.filterByDateRange(new Range(min, max - min)));
				setYearInput(min, max);
			}
		};
		
		return listener;
	}
	
	/**
	 * Says wheter the slider is in its default position.
	 * @return true if upper limit == max value and lower limit == min value.
	 */
	private boolean isDefault() {
		return (slider.getValueMin() == sliderDefaultMin) && (slider.getValueMax() == sliderDefaultMax);
	}
	
	/**
	 * Sets new FilmDataSet.
	 * @param filmSet New FilmDataSet.
	 */
	public void setFilmDataSet(FilmDataSet filmSet){
		this.filmSet = filmSet;
		fillDataTable();
		draw();
	}
	
	/**
	 * Fills data table.
	 */
	private void fillDataTable(){
		int size = filteredSet.getFilmsPerCountry().size();
		
		// Prepare the data
		dataTable = DataTable.create();	
		dataTable.addRows(size);
		dataTable.addColumn(ColumnType.STRING, "Country");
		dataTable.addColumn(ColumnType.NUMBER, "Number of films");
		int i = 0;
		for (Map.Entry<String, Integer> cursor : filteredSet.getFilmsPerCountry().entrySet()){
			dataTable.setValue(i, 0, cursor.getKey());
			dataTable.setValue(i++, 1, cursor.getValue());
		}
	}
	
	/**
	 * Computes size of data set to be represented.
	 * @return number of films in set.
	 */
	private int computeDataSetSize(){
		int result = 0;
		Map<String, Integer> data = filteredSet.getFilmsPerCountry();
		for (Map.Entry<String, Integer> cursor : data.entrySet()){
			result += cursor.getValue();
		}
		return result;
	}
	
	/**
	 * Draw data.
	 */
	private void draw() {
		// Set options
		GeoChartOptions options = GeoChartOptions.create();
		GeoChartColorAxis geoChartColorAxis = GeoChartColorAxis.create();
		geoChartColorAxis.setColors("green", "yellow", "red");
		
		int size = computeDataSetSize();
		geoChartColorAxis.setMaxValue(size);
		
		//Update new color axis
		options.setColorAxis(geoChartColorAxis);
		options.setDatalessRegionColor("gray");

		// Draw the chart
		geoChart.draw(dataTable, options);
	}
}