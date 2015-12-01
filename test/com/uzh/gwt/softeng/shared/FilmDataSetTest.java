package com.uzh.gwt.softeng.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.google.gwt.view.client.Range;

public class FilmDataSetTest {
	ArrayList<String> patterns;
	FilmDataSet movies;
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	
	@Before
	public void setUp() throws Exception {
		movies = new FilmDataSet();
		patterns = new ArrayList<String>();
		String idPattern = "[0-9]++";
        String titlePattern = "\\w.*";
        String datePattern = "(\\d{4}-\\d{2}-\\d{2})|(\\d{4})|(\\d{4}-\\d{2})";
        String durationPattern = "\\d++.\\d++";
        String languageCountryGenrePattern = "\\[.*[\\s\\w+]*[,\\s\\w+]*\\]";
        patterns.add(idPattern);
        patterns.add(titlePattern);
        patterns.add(datePattern);
        patterns.add(durationPattern);
        patterns.add(languageCountryGenrePattern);
        patterns.add(languageCountryGenrePattern);
        patterns.add(languageCountryGenrePattern);
//        System.setOut(new PrintStream(outContent));
	}

	@Test
	public void testFilmDataSetConstructor() {
		assertNotEquals(movies, null);
		assertNotEquals(movies.getFilms(), null);
	}
	
	@Test
	public void testGetFilms(){
		assertNotEquals(movies.getFilms(), null);
		assertEquals(movies.getFilms().size(), 0);
	}
	
//	@Test
//	public void testGetCountriesList(){
//		FilmData f1 = new FilmData(1, "a", 180, new ArrayList<String>(Arrays.asList("Switzerland", "USA")));
//		FilmData f2 = new FilmData(2, "b", 180, new ArrayList<String>(Arrays.asList("Italy", "Japan")));
//		FilmData f3 = new FilmData(3, "c", 180, new ArrayList<String>(Arrays.asList("Japan")));
//		FilmDataSet movies = new FilmDataSet(new ArrayList<FilmData>(Arrays.asList(f1, f2, f3)));
//		
//		ArrayList<String> result = movies.getCountriesList();
//		for(String tmp : result){
//			System.out.println(tmp);
//		}
//	}

	@Test
	public void testPrintDataSet(){
		StringBuilder output = new StringBuilder();
		for(FilmData film: movies.getFilms()){
			output.append(film.toString());
			System.out.println(film);
		}
		assertEquals(output.toString(), outContent.toString());
	}
	
	@Test
	public void testFilterByTitle() {
		String[] testTitles = { "Buffy", "Plan 9", "Dirty" };
		ArrayList<FilmData> filteredTitles = new ArrayList<FilmData>();
		
		for(String title : testTitles) {
			filteredTitles.addAll( movies.filterByTitle(title) );
		}
		
		assertTrue( movies.getFilms().containsAll(filteredTitles) );
	}
	
	@Test
	public void testSearchById() {
		long[] testIds = { 144435, 83669, 17405 };
		ArrayList<FilmData> filteredIds = new ArrayList<FilmData>();
		
		for(long id : testIds) {
			filteredIds.addAll( movies.searchByID(id) );
		}
		
		assertTrue( movies.getFilms().containsAll(filteredIds) );
	}
	
	@Test
	public void testFilterByDuration() {
		float[] testDurations = { 90.0f, 180.3f, 120.5f };
		ArrayList<FilmData> filteredDurations = new ArrayList<FilmData>();
		
		for(float duration : testDurations) {
			filteredDurations.addAll( movies.filterByDuration(duration) );
		}
		
		assertTrue( movies.getFilms().containsAll(filteredDurations) );
	}
	
	@Test
	public void testFilterByLanguage() {
		String[] testLanguages = { "Russian", "Spanish", "Italian" };
		ArrayList<FilmData> filteredLanguages = new ArrayList<FilmData>();
		
		for(String language : testLanguages) {
			filteredLanguages.addAll( movies.filterByLanguage(language) );
		}
		
		assertTrue( movies.getFilms().containsAll(filteredLanguages) );
	}
	
	@Test
	public void testFilterByDate() {
		String[] testDates = { "2014", "2013-06-06", "2001-01" };
		ArrayList<FilmData> filteredDates = new ArrayList<FilmData>();
		
		for(String date : testDates) {
			filteredDates.addAll( movies.filterByDate(date) );
		}
		
		assertTrue( movies.getFilms().containsAll(filteredDates) );
	}
	
	@Test
	public void testFilterByCountry() {
		String[] testCountries = { "Spain", "Italy", "Argentina" };
		ArrayList<FilmData> filteredCountries = new ArrayList<FilmData>();
		
		for(String country : testCountries) {
			filteredCountries.addAll( movies.filterByCountry(country) );
		}
		
		assertTrue( movies.getFilms().containsAll(filteredCountries) );
	}
	
	@Test
	public void testFilterByGenre() {
		String[] testGenre = { "Comedy", "Horror", "Action" };
		ArrayList<FilmData> filteredGenres = new ArrayList<FilmData>();
		
		for(String genre : testGenre) {
			filteredGenres.addAll( movies.filterByGenre(genre) );
		}
		
		assertTrue( movies.getFilms().containsAll(filteredGenres) );
	}
	
	@Test
	public void testFilterByDateRange() {
		ArrayList<FilmData> films = new ArrayList<FilmData>(Arrays.asList(new FilmData(), new FilmData(1, "a", 5), 
				new FilmData(2, "b", 10), new FilmData(3, "c", 1000)));
		
		FilmDataSet movies = new FilmDataSet(films);
		boolean thrown = false;
		try{
			movies.filterByDateRange(new Range(-1, 1));
		} catch (IllegalArgumentException e){
			thrown = true;
		}
		assertTrue(thrown);
		
		ArrayList<FilmData> tmp = movies.filterByDateRange(new Range(2, 6));
		assertEquals(tmp.get(0).getDate(), 5);
		
		tmp = movies.filterByDateRange(new Range(2, 1000));
		for (FilmData film : tmp){
			assertTrue(film.getDate() <= 1000 && film.getDate() >= 2);
		}
		
	}
}
