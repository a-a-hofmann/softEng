package com.uzh.gwt.softeng.shared;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

import com.uzh.gwt.softeng.shared.FilmData;
import com.uzh.gwt.softeng.shared.FilmDataSet;

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
        System.setOut(new PrintStream(outContent));
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
}
