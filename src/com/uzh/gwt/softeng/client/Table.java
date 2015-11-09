package com.uzh.gwt.softeng.client;


import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.uzh.gwt.softeng.shared.FilmData;
import com.uzh.gwt.softeng.shared.FilmDataSet;

public class Table extends Composite {
	//all films to be imported into table
	private ArrayList<FilmData> filmSet;
	
	//table reference
	private CellTable<FilmData> table = new CellTable<FilmData> ();
	
	private DockLayoutPanel lp;
	
	ListDataProvider<FilmData> dataProvider;
	
	SimplePager pager;
	

	public Table() {
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		//TODO: add ArrayList with imported data to fill table
		filmSet = new ArrayList<FilmData>();
		filmSet.add(new FilmData());
		filmSet.add(new FilmData());
		
		table.setWidth("100%");
		
		dataProvider = new ListDataProvider<FilmData>(filmSet);
		dataProvider.addDataDisplay(table);
		
		lp = new DockLayoutPanel(Unit.PCT);
		lp.setHeight("600px");
	
		pager = new SimplePager(TextLocation.CENTER, true, true);
		pager.setDisplay(table);
		lp.addSouth(pager, 25);
		lp.add(table);
		initTable();
		initWidget(lp);
	}
	
	public Table(FilmDataSet films) {
		this();
		this.filmSet = films.getFilms();
	}
	
	//TODO: initialize Table
	public void initTable() {
		
		TextColumn<FilmData> movieID = new TextColumn<FilmData>() {
			@Override
			public String getValue(FilmData object) {
				return Long.toString( object.getID() );
			}};
		table.addColumn(movieID, "Movie ID");
		
		TextColumn<FilmData> movieName = new TextColumn<FilmData>() {
			@Override
			public String getValue(FilmData object) {
				return object.getTitle();
			}};

		table.addColumn(movieName, "Movie Name");

		TextColumn<FilmData> movieDuration = new TextColumn<FilmData>() {
			@Override
			public String getValue(FilmData object) {
				return Float.toString( object.getDuration() );
			}};
			
		table.addColumn(movieDuration, "Duration");

		TextColumn<FilmData> movieCountry = new TextColumn<FilmData>() {
			@Override
			public String getValue(FilmData object) {
				return object.getCountries().toString();
			}};

		table.addColumn(movieCountry, "Country");
		
		//Handler for single selection of one row
		final SingleSelectionModel<FilmData> selectionModel = new SingleSelectionModel<FilmData>();
		table.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				FilmData selected = selectionModel.getSelectedObject();
				if (selected != null) {
					Window.alert("You selected: " + selected.getID() + " " + selected.getTitle() + " " + selected.getCountries().toString()
									+ " " + selected.getDuration());
				}
			}
		});
	}
	
	public void fillTable(){
		table.setWidth("100%");
	}
	
	public void fillTable(ArrayList<FilmData> filmSet) {
		dataProvider.setList(filmSet);	
		table.setWidth("100%");
	}
}
