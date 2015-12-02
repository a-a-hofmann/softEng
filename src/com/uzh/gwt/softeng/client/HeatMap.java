package com.uzh.gwt.softeng.client;


import java.util.Map;

import org.spiffyui.client.widgets.slider.RangeSlider;
import org.spiffyui.client.widgets.slider.SliderEvent;
import org.spiffyui.client.widgets.slider.SliderListener;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
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
	 * Slider wrapper and controls. 
	 */
	private FocusPanel yearControlsWrapper;
	private HorizontalPanel yearControls;
	private Label yearLabel;
	private TextBox yearInputTextBox;
	
	/**
	 * Panel containing the slider and two label for min and max value.
	 */
	private DockLayoutPanel sliderPanel;
	
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
		
		//Added pressing Enter-Key support
		//If there is a number in either fromYear or toYear the slider will get updated accordingly on pressing Enter
		yearControlsWrapper.addKeyDownHandler(new KeyDownHandler() {
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
		});
		
		//Initialize Year Input Field
		yearLabel = new Label("Choose Year: ");
		yearInputTextBox = new TextBox();
		yearInputTextBox.setValue("1888-2020");
		
		yearControls.add(yearLabel);
		yearControls.add(yearInputTextBox);
		yearControls.setStyleName("SliderControls");
		
		//Slider Part
		sliderPanel = new DockLayoutPanel(Unit.PCT);
		sliderPanel.setWidth("600px");
		sliderPanel.addStyleName("SliderPanel");
		
		slider = new RangeSlider("slider", 1888, 2020, 1888, 2020);
		slider.addListener(new SliderListener(){
			
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
				int max = slider.getValueMax();
				int min = slider.getValueMin();

				setYearInput(min, max);
			
				filteredSet = new FilmDataSet(filmSet.filterByDateRange(new Range(min, max)));

				return true;
			}

			@Override
			public void onChange(SliderEvent e) {
				int max = slider.getValueMax();
				int min = slider.getValueMin();

				setYearInput(min, max);
				filteredSet = new FilmDataSet(filmSet.filterByDateRange(new Range(min, max)));

				fillDataTable();
				draw();
			}

			@Override
			public void onStop(SliderEvent e) {
				int max = slider.getValueMax();
				int min = slider.getValueMin();
				
				setYearInput(min, max);
			}
			
		});
		
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