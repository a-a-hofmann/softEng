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
import com.google.gwt.user.client.ui.Widget;
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
	 * Textbox to target 
	 */
	private FocusPanel fromToYearControlsWrapper;
	private HorizontalPanel fromToYearControls;
	private Label fromYearLabel;
	private Label toYearLabel;
	private TextBox fromYearTextBox;
	private TextBox toYearTextBox;
	
	/**
	 * Panel containing the slider and two label for min and max value.
	 */
	private DockLayoutPanel sliderPanel;
	
	/**
	 * Min value label of slider.
	 */
	private Label minValueLabel;
	
	/**
	 * Max value label of slider.
	 */
	private Label maxValueLabel;
	
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
		fromToYearControls = new HorizontalPanel();
		fromToYearControlsWrapper = new FocusPanel( fromToYearControls );
		
		//Added pressing Enter-Key support
		//If there is a number in either fromYear or toYear the slider will get updated accordingly on pressing Enter
		fromToYearControlsWrapper.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent event) {
				if( event.getNativeKeyCode() == KeyCodes.KEY_ENTER ) {
					
					//fromYear TextBox
					if( fromYearTextBox.getText().equals("") ) {
						
					} else {
						//TODO: check for valid input
						minValueLabel.setText( fromYearTextBox.getText() );

						//adjust min value, do not change max value
						//TODO: max can not be smaller than min!
						int max = slider.getValueMax();
						int min = Integer.valueOf( fromYearTextBox.getText() );
						
						slider.setValues(min, max);
					}
					
					//toYearTextBox
					if( toYearTextBox.getText().equals("") ) {
						
					} else {
						//TODO: check for valid input
						maxValueLabel.setText( toYearTextBox.getText() );

						//adjust min value, do not change max value
						//TODO: max can not be smaller than min!
						int max = Integer.valueOf( toYearTextBox.getText() );
						int min = slider.getValueMin();

						slider.setValues(min, max);
					}
					
				}
			}
		});
		
		//fromToYearControlsWrapper.setFocus(true);
		
		
		//TODO: If just one box is filled out, use it to target 1 specific year
		fromYearLabel = new Label("Min: ");
		fromYearTextBox = new TextBox();
		
		toYearLabel = new Label("Max: ");
		toYearTextBox = new TextBox();
		
		fromToYearControls.add(fromYearLabel);
		fromToYearControls.add(fromYearTextBox);
		fromToYearControls.add(toYearLabel);
		fromToYearControls.add(toYearTextBox);
		fromToYearControls.setStyleName("SliderControls");
		
		//Slider Part
		sliderPanel = new DockLayoutPanel(Unit.PCT);
		sliderPanel.setWidth("600px");
		sliderPanel.addStyleName("SliderPanel");
		
//		minValueLabel = new Label("Min: 1850");
//		minValueLabel.setHeight("20px");
//		maxValueLabel = new Label("Max: 2020");
//		maxValueLabel.setHeight("20px");
		
		slider = new RangeSlider("slider", 1880, 2020, 1888, 2020);
		slider.addListener(new SliderListener(){

			@Override
			public void onStart(SliderEvent e) {
				//initialize from-to-year-controls
				fromYearTextBox.setValue( Integer.toString( slider.getValueMin() ) );
				toYearTextBox.setValue( Integer.toString( slider.getValueMax() ) );
			}

			@Override
			public boolean onSlide(SliderEvent e) {
				int max = slider.getValueMax();
				int min = slider.getValueMin();
				filteredSet = new FilmDataSet(filmSet.filterByDateRange(min, max));
				
				//minValueLabel.setText("Min: " + min);
				//maxValueLabel.setText("Max: " + max);
				return true;
			}

			@Override
			public void onChange(SliderEvent e) {
				int max = slider.getValueMax();
				int min = slider.getValueMin();
				filteredSet = new FilmDataSet(filmSet.filterByDateRange(min, max));
				
				//minValueLabel.setText("Min: " + min);
				//maxValueLabel.setText("Max: " + max);
				fillDataTable();
				draw();
			}

			@Override
			public void onStop(SliderEvent e) {
			}
			
		});
		
		//initialize Slider Controls TextBoxes
		if( fromYearTextBox.getText().equals("") && toYearTextBox.getText().equals("") ) {

			fromYearTextBox.setValue("1888");
			toYearTextBox.setValue("2020");
		}
				
		//explanation of arguments for widgets added to DockLayoutPanel
		//first arg: widget name
		//second arg: size in percent
		sliderPanel.addNorth(fromToYearControlsWrapper, 70);
		//sliderPanel.addWest(minValueLabel, 15);
		//sliderPanel.addEast(maxValueLabel, 15);
		sliderPanel.add(slider);
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
	
	/**
	 * Gets the Panel containing the heatmap.
	 * @return DockLayoutPanel containing heatmap.
	 */
	public Widget getMap(){
		return dlp;
	}
}