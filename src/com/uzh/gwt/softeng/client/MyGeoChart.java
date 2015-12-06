package com.uzh.gwt.softeng.client;

import com.google.gwt.user.client.Window;
import com.googlecode.gwt.charts.client.geochart.GeoChart;

public class MyGeoChart extends GeoChart {
	
	
	public void exportJava() {
		String uri = chartObject.getImageURI();
		Window.open(uri, "heatmap", "status=0,toolbar=0,menubar=0,location=0");
	}
	
	public void exportJS() {
		String uri = getElement().toString();
		openPrintWindow(uri);
	}
	
	native void openPrintWindow(String contents) /*-{
	    var printWindow = window.open("", "PrintWin");
	    if (printWindow && printWindow.top) {
	        printWindow.document.write(contents);
	        printWindow.print();
	        printWindow.close();
	    } else {
	        alert("The print feature works by opening a popup window, but our popup window was blocked by your browser.  If you can disable the blocker temporarily, you'll be able to print here.  Sorry!");
	    }
	}-*/;
}
