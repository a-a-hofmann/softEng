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
		FilmData film1 = new FilmData();
		film1.setTitle("Buffy adasds");
		FilmData film2 = new FilmData();
		film2.setTitle("Plan 9");
		FilmData film3 = new FilmData();
		film3.setTitle("Dirty");
		
		String[] testTitles = { "Buffy", "Plan 9", "Dirty" };
		
		ArrayList<FilmData> filteredTitles = new ArrayList<FilmData>(Arrays.asList(film1, film2, film3));
		movies.setDataSet(filteredTitles);
		
		ArrayList<FilmData> results;
		for(String title : testTitles) {
			results = movies.filterByTitle(title);
			
			for(FilmData result : results){
				assertTrue(result.getTitle().toLowerCase().contains(title.toLowerCase()));
			}
		}
	}
	
	@Test
	public void testSearchById() {
		FilmData film1 = new FilmData();
		film1.setID(144435);
		FilmData film2 = new FilmData();
		film2.setID(83669);
		FilmData film3 = new FilmData();
		film3.setID(17405);
		
		
		long[] testIds = { 144435, 83669, 17405 };
		
		ArrayList<FilmData> filteredIds = new ArrayList<FilmData>(Arrays.asList(film1, film2, film3));
		movies.setDataSet(filteredIds);
		
		
		for(long id : testIds) {
			FilmData result = movies.searchByID(id).get(0);
			assertEquals(id, result.getID());
		}
		
		long wrongID = 1;
		assertEquals(movies.searchByID(wrongID).size(), 0);
		
	}
	
	@Test
	public void testFilterByDuration() {
		FilmData film1 = new FilmData();
		film1.setDuration(90.0f);
		FilmData film2 = new FilmData();
		film2.setDuration(180.0f);
		FilmData film3 = new FilmData();
		film3.setDuration(120.0f);
		
		float[] testDurations = { 90.0f, 180.0f, 120.5f };
		
		ArrayList<FilmData> filteredDurations = new ArrayList<FilmData>(Arrays.asList(film1, film2, film3));
		movies.setDataSet(filteredDurations);
		
		
		for(float duration : testDurations) {
			ArrayList<FilmData> result = movies.filterByDuration(duration);
			
			for(FilmData film : result) {
				assertTrue(film.getDuration() >= duration);
			}
		}
		
		int minD = 90;
		int maxD = 120;
		ArrayList<FilmData> result = movies.filterByDurationRange(new Range(minD, maxD - minD));
		
		
		for(FilmData film : result){
			assertTrue(film.getDuration() >= minD && film.getDuration() <= maxD);
		}	
	}
	
	@Test
	public void testFilterByDate() {
		String[] testDates = { "2014", "2013-06-06", "2001-01" };
		int [] years = {2014, 2013, 2001};
		FilmData film1 = new FilmData();
		film1.setDate(testDates[0]);
		FilmData film2 = new FilmData();
		film2.setDate(testDates[1]);
		FilmData film3 = new FilmData();
		film3.setDate(testDates[2]);
		
		
		ArrayList<FilmData> filteredDate = new ArrayList<FilmData>(Arrays.asList(film1, film2, film3));
		movies.setDataSet(filteredDate);
		
		
		for(int date : years) {
			ArrayList<FilmData> result = movies.filterByDate(date);
			
			for(FilmData film : result) {
				assertTrue(film.getDate() <= date);
			}
		}
	}
	
	@Test
	public void testFilterByLanguage() {
		FilmData film1 = new FilmData();
		film1.setLanguages("Russian");
		FilmData film2 = new FilmData();
		film2.setLanguages("Spanish");
		FilmData film3 = new FilmData();
		film3.setLanguages("English");
		
		
		ArrayList<FilmData> filteredLanguages = new ArrayList<FilmData>(Arrays.asList(film1, film2, film3));
		movies.setDataSet(filteredLanguages);
		
		String[] testLanguages = { "Russian", "Spanish", "Italian" };
		
		for(String language : testLanguages) {
			ArrayList<FilmData> result = movies.filterByLanguage(language);
			for(FilmData film : result){
				assertTrue(film.getLanguages().contains(language));
			}
			assertTrue(result.size() == 1 || result.size() == 0);
		}	
	}
	
	@Test
	public void testFilterByCountry() {
		FilmData film1 = new FilmData();
		film1.setCountries("Spain");
		FilmData film2 = new FilmData();
		film2.setCountries("Italy");
		FilmData film3 = new FilmData();
		film3.setCountries("Argentina");
		
		String[] testCountries = { "Spain", "Italy", "Argentina" };
		
		ArrayList<FilmData> filteredCountries = new ArrayList<FilmData>(Arrays.asList(film1, film2, film3));
		movies.setDataSet(filteredCountries);
		
		
		for(String country : testCountries) {
			ArrayList<FilmData> result = movies.filterByCountry(country);
			for(FilmData film : result){
				assertTrue(film.getCountries().contains(country));
			}
		}
	}
	
	@Test
	public void testFilterByGenre() {
		String[] testGenres = { "Comedy", "Horror", "Action" };
		FilmData film1 = new FilmData();
		film1.setGenres(testGenres[0]);
		FilmData film2 = new FilmData();
		film2.setGenres(testGenres[1]);
		FilmData film3 = new FilmData();
		film3.setGenres(testGenres[2]);
		
		ArrayList<FilmData> filteredCountries = new ArrayList<FilmData>(Arrays.asList(film1, film2, film3));
		movies.setDataSet(filteredCountries);
		
		
		for(String genre : testGenres) {
			ArrayList<FilmData> result = movies.filterByGenre(genre);
			for(FilmData film : result){
				assertTrue(film.getGenres().contains(genre));
			}
		}
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
