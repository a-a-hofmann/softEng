package com.uzh.gwt.softeng.client;


import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.uzh.gwt.softeng.shared.FilmData;
import com.uzh.gwt.softeng.shared.FilmDataSet;

/**
 * The {@code Table} class handles the Table object and the data retrieval for it.
 * It is an extension to the GWT Composite class.
 * It is composed from a CellTable, a SimplePager and an AsynchronousDataProvider.
 */
public class Table extends Composite {
	
	/**
	 * Table.
	 */
	private CellTable<FilmData> table = new CellTable<FilmData> ();
	
	/**
	 * Table and pager container.
	 */
	private DockLayoutPanel lp;
	
	/**
	 * Asynchronous data provider.
	 */
	FilmDataAsyncProvider asyncDataProvider;
	
	/**
	 * Pager for the table.
	 */
	SimplePager pager;
	
	/**
	 * Table constructor.
	 * Creates container panel and adds table and pager to it.
	 * Initializes data provider.
	 */
	public Table() {
		table.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		table.setWidth("100%");
		table.setRowCount(80000, false);
		
		asyncDataProvider = new FilmDataAsyncProvider(table);
		
		lp = new DockLayoutPanel(Unit.PCT);
		lp.setHeight("600px");
	
		pager = new SimplePager(TextLocation.CENTER, true, true);
		pager.setDisplay(table);
		lp.addSouth(pager, 25);
		lp.add(table);
		initTable();
		initWidget(lp);
	}
	
	/**
	 * Table initialization.
	 * Creates columns and sets selection handler.
	 */
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
	
	/**
	 * Reset data on table.
	 */
	public void reset(){
		asyncDataProvider.reset();
	}
	
	/**
	 * Shows filtered results.
	 * @param filmDataSet Filtered FilmDataSet.
	 */
	public void filter(FilmDataSet filmDataSet){
		asyncDataProvider.filter(filmDataSet);
	}
}
