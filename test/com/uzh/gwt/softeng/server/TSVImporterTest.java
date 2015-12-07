package com.uzh.gwt.softeng.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.uzh.gwt.softeng.shared.FilmData;
import com.uzh.gwt.softeng.shared.FilmDataSet;

public class TSVImporterTest {
	
	ArrayList<String> patterns;

	@Before
	public void setUp() throws Exception {
		patterns = new ArrayList<String>();
		String idPattern = "[0-9]++";
        String titlePattern = ".*";
        String datePattern = "(\\d{4}-\\d{2}-\\d{2})|(\\d{4})|(\\d{4}-\\d{2})|(-1)";
        String durationPattern = "\\d++.\\d++";
        String languageCountryGenrePattern = "\\[.*[\\s\\w+]*[,\\s\\w+]*\\]";
        patterns.add(idPattern);
        patterns.add(titlePattern);
        patterns.add(datePattern);
        patterns.add(durationPattern);
        patterns.add(languageCountryGenrePattern);
        patterns.add(languageCountryGenrePattern);
        patterns.add(languageCountryGenrePattern);
        
	}
	
	@Test
	public void testGetFileSizeByLine() throws IOException{
		File testFile = new File("test.txt");
		PrintWriter writer = new PrintWriter("test.txt");
		for(int i = 0; i < 100; i++){
			writer.println(i);
		}
		writer.close();
		long lines = TSVImporter.getFileSizeByLine("test.txt");
		assertEquals(lines, 100);
		boolean deleted = testFile.delete();
		if(!deleted){
			System.err.println("Error: Failed to delete file test.txt");
		}	
	}
	
	/**
	 * Tests both importFilmData methods.
	 */
	@Test
	public void testImportFilmData(){
		String pathToFile = "war/WEB-INF/Resources/movies_80000.tsv";
		
		//Test wrong path to file.
		//Should fail
		try {
			TSVImporter.importFilmDataFromFile(pathToFile + "a");
		} catch (FileNotFoundException e) {
			// Nothing to do.
		} catch (IOException e) {
			fail("TSVImporter.importFilmData(pathTofile) IOException");
		}
		
		//Test import function on the whole file
		FilmDataSet result;
		try {
			result = TSVImporter.importFilmDataFromFile(pathToFile);
			for(FilmData film : result.getFilms()){
				System.out.println(film);
				assertTrue(Long.toString(film.getID()).matches(patterns.get(0)));
				assertTrue(film.getTitle().matches(patterns.get(1)));
				assertTrue(Integer.toString(film.getDate()).matches(patterns.get(2)));
				assertTrue(Float.toString(film.getDuration()).matches(patterns.get(3)));
				assertTrue(film.getLanguages().toString().matches(patterns.get(4)));
				assertTrue(film.getCountries().toString().matches(patterns.get(5)));
				assertTrue(film.getGenres().toString().matches(patterns.get(6)));			
			}
			assertEquals(result.getFilms().size(), 80000);
		} catch (FileNotFoundException e1) {
			fail("TSVImporter.importFilmData(pathToFile) Failed to open file");
		} catch (IOException e1) {
			fail("TSVImporter.importFilmData(pathToFile) IOException");
		}
	}
}
