package com.manning.gwtia.ch03.client;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;

//import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FilmDataTest {
	private FilmData emptyFilmTest;
	private FilmData filmTest;
	private long testID = 1;
	private String testTitle = "Star Wars Episode IV: A New Hope";
	private float testDuration = 121.0f;
	private int testDate = 1977;
	private ArrayList<String> testCountries;
	private ArrayList<String> testLanguages;
	private ArrayList<String> testGenres;
	private String testTokens = "{\"/m/06ppq\": \"Silent film\", \"/m/02h40lc\": \"English Language\"}";
	private ArrayList<String> testTokensCleaned = new ArrayList<String>();
	
	
	@Before
	public void setUp() throws Exception {
		emptyFilmTest = new FilmData();
		
		testCountries = new ArrayList<String>();
		testCountries.add("USA");
		
		testLanguages = new ArrayList<String>();
		testLanguages.add("English");
		
		testGenres = new ArrayList<String>();
		testGenres.add("Action");
		testGenres.add("Adventure");
		testGenres.add("Fantasy");
		filmTest = new FilmData(testID, testTitle, testDate, 
				testDuration, testLanguages, testCountries, testGenres);
		
		testTokensCleaned.add("Silent film");
		testTokensCleaned.add("English Language");
	}


	@Test
	public void testFilmDataConstructor() {
		assertNotEquals(null, emptyFilmTest);
		assertNotEquals(null, filmTest);
	}
	
	@Test
	public void testGetID(){
		assertEquals(-1, emptyFilmTest.getID());
		assertEquals(1, filmTest.getID());
	}
	
	@Test
	public void testGetTitle(){
		assertEquals("No Title", emptyFilmTest.getTitle());
		assertEquals("Star Wars Episode IV: A New Hope", filmTest.getTitle());
	}
	
	@Test
	public void testGetDate(){
		assertTrue(testDate == filmTest.getDate());
	}
	
	@Test
	public void testGetDuration(){
		assertEquals(-1, emptyFilmTest.getID());
		assertEquals(121.0f, filmTest.getDuration(), 0.01);
	}
	
	@Test
	public void testGetLanguages(){
		assertEquals(0, emptyFilmTest.getLanguages().size());
		int i = 0;
		for(String language: filmTest.getLanguages()){
			assertEquals(testLanguages.get(i++), language);
		}
	}
	
	@Test
	public void testGetCountries(){
		assertEquals(1, emptyFilmTest.getCountries().size());
		assertEquals("{}", emptyFilmTest.getCountries().get(0));
		
		int i = 0;
		for(String country: filmTest.getCountries()){
			assertEquals(testCountries.get(i++), country);
		}
	}
	
	@Test
	public void testGetGenres(){
		assertEquals(0, emptyFilmTest.getGenres().size());
		
		int i = 0;
		for(String genre: filmTest.getGenres()){
			assertEquals(testGenres.get(i++), genre);
		}
	}
	
	@Test
	public void testSetID(){
		//Invalid input.
		boolean thrown = false;
		try{
			filmTest.setID(-2);
		} catch(IllegalArgumentException e){
			thrown = true;
		}
		//Assert setID threw exception on invalid parameter.
		assertTrue(thrown);
		//Assert id is still set to the old id.
		assertEquals(testID, filmTest.getID());
		
		//Valid input.
		filmTest.setID(10);
		assertEquals(10, filmTest.getID());
	}
	
	@Test
	public void testSetTitle(){
		testTitle = "Star Wars";
		filmTest.setTitle(testTitle);
		assertEquals(testTitle, filmTest.getTitle());
	}
	
	@Test
	public void testSetDate(){
		//Valid parameters.
		String date1 = "1977-10-10";
		String date2 = "1977-10";
		String date3 = "1977";
		
		filmTest.setDate(date1);
		assertEquals(testDate, filmTest.getDate());
		filmTest.setDate(date2);
		assertEquals(testDate, filmTest.getDate());
		filmTest.setDate(date3);
		assertEquals(testDate, filmTest.getDate());
		
		//Invalid parameter.
		//Should throw exception
		String date4 = "-1";
		boolean thrown = false;
		try{
			filmTest.setDate(date4);
		} catch(NumberFormatException e){
			thrown = true;
			assertEquals(testDate, filmTest.getDate());
		}
		assertTrue(thrown);
		assertEquals(testDate, filmTest.getDate());
	}
	
	@Test
	public void testSetDuration(){
		//Invalid input.
		boolean thrown = false;
		try{
			filmTest.setDuration(-1);
		} catch(IllegalArgumentException e){
			thrown = true;
		}
		//Assert setID threw exception on invalid parameter.
		assertTrue(thrown);
		//Assert id is still set to the old id.
		assertEquals(testDuration, filmTest.getDuration(), 0.01);
		
		//Valid input.
		filmTest.setDuration(10);
		assertEquals(10.0, filmTest.getDuration(), 0.01);
	}
	
	@Test
	public void testPrepareLanguagesCountriesGenresTokens(){
		ArrayList<String> cleanedTokens = filmTest.prepareLanguagesCountriesGenresTokens(testTokens);
		int i = 0;
		for(String token: cleanedTokens){
			assertEquals(token, testTokensCleaned.get(i++));
		}
	}
	
	@Test
	public void testSetLanguages(){		
		filmTest.setLanguages(testTokens);
		int i = 0;
		for(String language: filmTest.getLanguages()){
			assertEquals(testTokensCleaned.get(i++), language);
		}
	}
	
	@Test
	public void testToString(){
		String expected = testID + " " + testTitle  + " " + testDate 
		+ " " + testDuration + " " + testLanguages + " " + testCountries +
		"\nGenres: " + testGenres + "\n";
		
		assertEquals(expected, filmTest.toString());
	}

}
