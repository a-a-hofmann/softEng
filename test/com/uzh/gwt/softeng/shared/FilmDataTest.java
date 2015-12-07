package com.uzh.gwt.softeng.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class FilmDataTest {
	private FilmData emptyFilmTest;
	private FilmData filmTest;
	private int testID = 1;
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
		
		testTokensCleaned.add("/m/06ppq");
		testTokensCleaned.add("Silent film");
		testTokensCleaned.add("/m/02h40lc");
		testTokensCleaned.add("English Language");
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
		String date5 = "abc";
		String date6 = "";
		boolean thrown = false;
		try{
			filmTest.setDate(date4);
		} catch(NumberFormatException e){
			thrown = true;
			assertEquals(testDate, filmTest.getDate());
		}
		
		try{
			filmTest.setDate(date5);
		} catch(NumberFormatException e){
			thrown = true;
			assertEquals(testDate, filmTest.getDate());
		}
		
		try{
			filmTest.setDate(date6);
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
		//Assert setDuration threw exception on invalid parameter.
		assertTrue(thrown);
		//Assert duration is still set to the old duration.
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
}
