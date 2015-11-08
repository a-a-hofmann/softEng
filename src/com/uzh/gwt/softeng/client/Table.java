package com.uzh.gwt.softeng.client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.uzh.gwt.softeng.shared.FilmData;
import com.uzh.gwt.softeng.shared.FilmDataSet;

public class Table extends Composite {
	//all films to be imported into table
	private ArrayList<FilmData> filmSet;
	
	//table reference
	private DataGrid<FilmData> table = new DataGrid<FilmData> ();
	
	//Simple Layout Panel for return to Main
	private SimpleLayoutPanel slp;
	
	public Table() {
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		//TODO: add ArrayList with imported data to fill table
		filmSet = new ArrayList<FilmData>();
		filmSet.add(new FilmData());
		filmSet.add(new FilmData());
		
		SimpleLayoutPanel slp = new SimpleLayoutPanel();
		slp.add(table);
		slp.setHeight("1000px");
		
		initTable();
		fillTable();
		
		initWidget(slp);
	}
	
	public Table(FilmDataSet films) {
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		//TODO: add ArrayList with imported data to fill table
		this.filmSet = films.getFilms();
		
		SimpleLayoutPanel slp = new SimpleLayoutPanel();
		slp.add(table);
		slp.setHeight("1000px");
		
		initTable();
		fillTable();
		
		initWidget(slp);
	}
	
	//TODO: initialize Table
	public void initTable() {
		TextColumn<FilmData> movieID = new TextColumn<FilmData>() {
			@Override
			public String getValue(FilmData object) {
				return Long.toString( object.getID() );
			}};
		table.addColumn(movieID, "Movie ID");
		movieID.setSortable(true);
		
		movieID.setFieldUpdater(new FieldUpdater<FilmData, String>() {
		    @Override
		    public void update(int index, FilmData object, String value) {
		        table.redraw();
		    }
		});
		
		TextColumn<FilmData> movieName = new TextColumn<FilmData>() {
			@Override
			public String getValue(FilmData object) {
				return object.getTitle();
			}};

		table.addColumn(movieName, "Movie Name");
		movieName.setSortable(true);
		
		movieName.setFieldUpdater(new FieldUpdater<FilmData, String>() {
		    @Override
		    public void update(int index, FilmData object, String value) {
		        table.redraw();
		    }
		});

		TextColumn<FilmData> movieDuration = new TextColumn<FilmData>() {
			@Override
			public String getValue(FilmData object) {
				return Float.toString( object.getDuration() );
			}};
			
		table.addColumn(movieDuration, "Duration");
		movieDuration.setSortable(true);
		
		movieDuration.setFieldUpdater(new FieldUpdater<FilmData, String>() {
		    @Override
		    public void update(int index, FilmData object, String value) {
		        table.redraw();
		    }
		});

		TextColumn<FilmData> movieCountry = new TextColumn<FilmData>() {
			@Override
			public String getValue(FilmData object) {
				return object.getCountries().toString();
			}};

		table.addColumn(movieCountry, "Country");
		
		movieCountry.setFieldUpdater(new FieldUpdater<FilmData, String>() {
		    @Override
		    public void update(int index, FilmData object, String value) {
		        table.redraw();
		    }
		});
		
		ListDataProvider<FilmData> dataProvider = new ListDataProvider<FilmData>();
		dataProvider.addDataDisplay(table);
		List<FilmData> list = dataProvider.getList();
		
		for(FilmData film: filmSet){
			list.add(film);
		}
		
		
		ListHandler<FilmData> columnSortHandler = new ListHandler<FilmData>(list);
		
		columnSortHandler.setComparator(movieID, new Comparator<FilmData>() {
			public int compare(FilmData film1, FilmData film2){
				if(film1.getID() == film2.getID())
					return 0;
				
				return (film1.getID() < film2.getID()) ? 1 : -1;
			}
		});
		
		columnSortHandler.setComparator(movieDuration, new Comparator<FilmData>() {
			public int compare(FilmData film1, FilmData film2){
				if(film1.getDuration() == film2.getDuration())
					return 0;
				
				return (film1.getDuration() < film2.getDuration()) ? 1 : -1;
			}
		});
		
		
		table.addColumnSortHandler(columnSortHandler);
		
		table.getColumnSortList().push(movieID);
		table.getColumnSortList().push(movieDuration);
		
		
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
	
	//TODO: fill Table
	public void fillTable() {
		table.setRowCount(filmSet.size(), true);
		table.setRowData(0, filmSet);
		table.setWidth("100%");
		table.redraw();
	}
	
	//TODO: fill Table
	public void fillTable(ArrayList<FilmData> filmSet) {
		this.filmSet = filmSet;
		table.setRowCount(filmSet.size(), true);
		table.setRowData(0, filmSet);
		table.setWidth("100%");
//		table.redraw();
//		table.setVisible(true);
	}
	
	//TODO: get Table
	public SimpleLayoutPanel getTable() {	
		return slp;
	}
	public DataGrid<FilmData> getGrid(){
		return table;
	}
}
