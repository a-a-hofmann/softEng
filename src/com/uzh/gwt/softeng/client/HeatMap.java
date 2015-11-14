package com.uzh.gwt.softeng.client;


import java.util.Map;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
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
	 */
	private FilmDataSet filmSet;
	
	/**
	 * Panel to contain heatmap.
	 */
	private DockLayoutPanel dlp;
	
	/**
	 * Datatable to be used to draw heatmap.
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
		ChartLoader chartLoader = new ChartLoader(ChartPackage.GEOCHART);
		dlp = new DockLayoutPanel(Unit.PCT);
		dlp.setHeight("600px");
		initWidget(dlp);
		
		
		chartLoader.loadApi(new Runnable() {

			@Override
			public void run() {
				// Create and attach the chart
				geoChart = new GeoChart();
				dlp.add(geoChart);
				fillDataTable();
				draw();
			}
		});
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
	private void draw() {
		// Set options
		GeoChartOptions options = GeoChartOptions.create();
		GeoChartColorAxis geoChartColorAxis = GeoChartColorAxis.create();
		geoChartColorAxis.setColors("green", "yellow", "red");
		geoChartColorAxis.setMaxValue(maxValue);
		options.setColorAxis(geoChartColorAxis);
		options.setDatalessRegionColor("gray");
		options.setKeepAspectRatio(true);

		// Draw the chart
		geoChart.draw(dataTable, options);
	}
	
	/**
	 * Draw data with max value.
	 */
	public void setMaxValue(int maxValue){
		this.maxValue = maxValue;
	}
	
	/**
	 * Gets the Panel containing the heatmap.
	 * @return DockLayoutPanel containing heatmap.
	 */
	public Widget getMap(){
		return dlp;
	}
}