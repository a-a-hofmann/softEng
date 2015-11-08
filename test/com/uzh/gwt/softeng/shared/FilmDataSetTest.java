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
	
}
