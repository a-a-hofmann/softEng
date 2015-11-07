package com.uzh.gwt.softeng.shared;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

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
	public void testImportFilmData(){
		String pathToFile = "war/Resources/movies_80000.tsv";
		int lineToParse = 100;
		
		//Test import function on the whole file
		movies.importFilmData(pathToFile);
		
		for(FilmData film : movies.getFilms()){
			assertTrue(Long.toString(film.getID()).matches(patterns.get(0)));
			assertTrue(film.getTitle().matches(patterns.get(1)));
			assertTrue(Integer.toString(film.getDate()).matches(patterns.get(2)));
			assertTrue(Float.toString(film.getDuration()).matches(patterns.get(3)));
			assertTrue(film.getLanguages().toString().matches(patterns.get(4)));
			assertTrue(film.getCountries().toString().matches(patterns.get(5)));
			assertTrue(film.getGenres().toString().matches(patterns.get(6)));
		}
	
		try {
			assertEquals(movies.getFilms().size(), movies.getFileSizeByLine(pathToFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Test import with fewer lines
		movies = new FilmDataSet();
		movies.importFilmData(pathToFile, lineToParse);
		assertEquals(lineToParse, movies.getFilms().size());
	}
	
	@Test
	public void testGetFileSizeByLine() throws IOException{
		File testFile = new File("test.txt");
		PrintWriter writer = new PrintWriter("test.txt");
		for(int i = 0; i < 100; i++){
			writer.println(i);
		}
		writer.close();
		long lines = movies.getFileSizeByLine("test.txt");
		assertEquals(lines, 100);
		boolean deleted = testFile.delete();
		if(!deleted){
			System.err.println("Error: Failed to delete file test.txt");
		}	
	}
	
	@Test
	public void testPrintDataSet(){
		StringBuilder output = new StringBuilder();
		for(FilmData film: movies.getFilms()){
			output.append(film.toString());
		}
		assertEquals(output.toString(), outContent.toString());
	}
	
}
