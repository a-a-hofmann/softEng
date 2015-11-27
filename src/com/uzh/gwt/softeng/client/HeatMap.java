package com.uzh.gwt.softeng.client;


import java.util.Map;

import org.spiffyui.client.widgets.slider.RangeSlider;
import org.spiffyui.client.widgets.slider.SliderEvent;
import org.spiffyui.client.widgets.slider.SliderListener;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
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
	 * Max range for heatmap. Otherwise it's kinda useless.
	 */
	private int maxSize = 10000;
	
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
				
				dlp.addSouth(sliderPanel, 10);
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
		sliderPanel = new DockLayoutPanel(Unit.PCT);
		sliderPanel.setWidth("600px");
		sliderPanel.addStyleName("SliderPanel");
		
		minValueLabel = new Label("Min: 1850");
		minValueLabel.setHeight("20px");
		maxValueLabel = new Label("Max: 2020");
		maxValueLabel.setHeight("20px");
		
		slider = new RangeSlider("slider", 1880, 2020, 1888, 2020);
		slider.addListener(new SliderListener(){

			@Override
			public void onStart(SliderEvent e) {
			}

			@Override
			public boolean onSlide(SliderEvent e) {
				int max = slider.getValueMax();
				int min = slider.getValueMin();
				filteredSet = new FilmDataSet(filmSet.filterByDateRange(min, max));
				
				minValueLabel.setText("Min: " + min);
				maxValueLabel.setText("Max: " + max);
				return true;
			}

			@Override
			public void onChange(SliderEvent e) {
				int max = slider.getValueMax();
				int min = slider.getValueMin();
				filteredSet = new FilmDataSet(filmSet.filterByDateRange(min, max));
				
				minValueLabel.setText("Min: " + min);
				maxValueLabel.setText("Max: " + max);
				fillDataTable();
				draw();
			}

			@Override
			public void onStop(SliderEvent e) {
			}
			
		});
		
		sliderPanel.addWest(minValueLabel, 7);
		sliderPanel.addEast(maxValueLabel, 15);
		sliderPanel.add(slider);
		sliderPanel.setHeight("20px");
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
//		if(size > maxSize)
//			size = maxSize;
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