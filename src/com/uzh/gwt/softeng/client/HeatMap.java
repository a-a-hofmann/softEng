package com.uzh.gwt.softeng.client;


import java.util.Map;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ChartType;
import com.googlecode.gwt.charts.client.ChartWrapper;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.controls.Dashboard;
import com.googlecode.gwt.charts.client.controls.filter.DateRangeFilter;
import com.googlecode.gwt.charts.client.controls.filter.NumberRangeFilter;
import com.googlecode.gwt.charts.client.controls.filter.NumberRangeFilterOptions;
import com.googlecode.gwt.charts.client.geochart.GeoChart;
import com.googlecode.gwt.charts.client.geochart.GeoChartColorAxis;
import com.googlecode.gwt.charts.client.geochart.GeoChartOptions;
import com.uzh.gwt.softeng.shared.FilmDataSet;

/**
 * The {@code HeatMap} class handles the HeatMap object and the data retrieval for it.
 * It is an extension to the GWT Composite class.
 * It is composed from a GeoChart, and a DataTable to hold the data.
 */
public class HeatMap extends Composite {
	
	/**
	 * The actual heatmap.
	 */
	private GeoChart geoChart;
	
	/*
	 * References for the time bar
	 */
	private Dashboard dashboard;
	private ChartWrapper<GeoChartOptions> mapWrapper;
	private DateRangeFilter dateRangeFilter; 
	private NumberRangeFilter numberRangeFilter;
	
	/**
	 * FilmDataSet to be represented.
	 */
	private FilmDataSet filmSet;
	
	/**
	 * Panel to contain heatmap.
	 */
	private DockLayoutPanel dlp;
	
	/**
	 * DataTable to be used to draw heatmap.
	 */
	DataTable dataTable;
	
	/**
	 * Max value.
	 */
	private int maxValue;

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
		maxValue = filmSet.getFilms().size();
	}

	/**
	 * Set heatmap parameters.
	 */
	private void initialize() {
		ChartLoader chartLoader = new ChartLoader(ChartPackage.GEOCHART, ChartPackage.CONTROLS);
		dlp = new DockLayoutPanel(Unit.PCT);
		dlp.setHeight("600px");
		initWidget(dlp);
		
		
		chartLoader.loadApi(new Runnable() {

			@Override
			public void run() {
				// Create and attach the chart
				geoChart = new GeoChart();
				dlp.addSouth(getDashboardWidget(), 0);
				dlp.addSouth(getNumberRangeFilter(), 20);
				dlp.add(getGeoChartOptions());
				draw();
			}
		});
	}
	
	private DateRangeFilter getDateRangeFilter() {
		if (dateRangeFilter == null) {
			dateRangeFilter = new DateRangeFilter();
		}
		return dateRangeFilter;
	}
	
	private NumberRangeFilter getNumberRangeFilter() {
		if (numberRangeFilter == null) {
			numberRangeFilter = new NumberRangeFilter();
		}
		return numberRangeFilter;
	}
	
	private Dashboard getDashboardWidget() {
		if (dashboard == null) {
			dashboard = new Dashboard();
		}
		return dashboard;
	}
	
	private ChartWrapper<GeoChartOptions> getGeoChartOptions() {
		if (mapWrapper == null) {
			mapWrapper = new ChartWrapper<GeoChartOptions>();
			mapWrapper.setChartType(ChartType.GEO_CHART);
		}
		return mapWrapper;
	}
	
	/**
	 * Set new FilmDataSet
	 * @param filmSet New FilmDataSet.
	 */
	public void setFilmDataSet(FilmDataSet filmSet){
		this.filmSet = filmSet;
		geoChart.draw(dataTable);
	}
	
	/**
	 * Fill data table.
	 */
	private void fillDataTable(){
		// Prepare the data
		dataTable = DataTable.create();
		dataTable.addRows(filmSet.getFilmsPerCountry().size());
		dataTable.addColumn(ColumnType.STRING, "Country");
		dataTable.addColumn(ColumnType.NUMBER, "Number of films");
		dataTable.addColumn(ColumnType.DATE, "Year");
		int i = 0;
		for (Map.Entry<String, Integer> cursor : filmSet.getFilmsPerCountry().entrySet()){
			System.out.println(cursor.getKey() + " " + cursor.getValue());
			dataTable.setValue(i, 0, cursor.getKey());
			dataTable.setValue(i++, 1, cursor.getValue());
		}
	}
	
	/**
	 * Draw data
	 */
	public void draw() {
		// Set options
		GeoChartOptions options = GeoChartOptions.create();
		GeoChartColorAxis geoChartColorAxis = GeoChartColorAxis.create();
		geoChartColorAxis.setColors("green", "yellow", "red");
		geoChartColorAxis.setMaxValue(maxValue);
		
		options.setBackgroundColor("#f0f0f0");
		options.setColorAxis(geoChartColorAxis);
		options.setDatalessRegionColor("gray");
		options.setKeepAspectRatio(true);

//		// Set control options
//		DateRangeFilterOptions dateRangeFilterOptions = DateRangeFilterOptions.create();
//		dateRangeFilterOptions.setFilterColumnLabel("Number of films");
//		DateRangeFilterUi dateRangeFilterUi = DateRangeFilterUi.create();
//		//dateRangeFilterUi.setFormat(DateFormatOptions.create("yyyy"));
//		dateRangeFilterUi.setFormat(DateFormatOptions.create());
//		dateRangeFilterOptions.setUi(dateRangeFilterUi);
//		dateRangeFilter.setOptions(dateRangeFilterOptions);

		// Set control options
		NumberRangeFilterOptions numberRangeFilterOptions = NumberRangeFilterOptions.create();
		numberRangeFilterOptions.setFilterColumnLabel("Number of films");
		numberRangeFilterOptions.setMinValue(0);
		numberRangeFilterOptions.setMaxValue(300);
		numberRangeFilter.setOptions(numberRangeFilterOptions);

		mapWrapper.setOptions(options);
		
		fillDataTable();
		
		// Draw the chart
		//geoChart.draw(dataTable, options);
		
		// Draw the chart
		dashboard.bind(numberRangeFilter, mapWrapper);
		dashboard.draw(dataTable);
	}
	
	/**
	 * Draw data with max value.
	 * @param maxValue New Maximum value to be set.
	 */
	public void setMaxValue(int maxValue){
		this.maxValue = maxValue;
	}
	
	/**
	 * Gets the Panel containing the heatmap.
	 * @return DockLayoutPanel containing heatmap.
	 */
	public DockLayoutPanel getMap(){
		return dlp;
	}
}