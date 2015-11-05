package com.manning.gwtia.ch03.client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import com.manning.gwtia.ch03.server.MySQLConnector;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code FilmDataSet} class is responsible for managing a set of films
 * and importing the data from a .tsv file.
 */
public class FilmDataSet {
	/**
	 * List of filmData.
	 */
    private ArrayList<FilmData> films;
    
    /**
	 * Creates a new FilmDataSet instance.
	 */
    public FilmDataSet(){
    	films = new ArrayList<FilmData>();
    }
    
    /**
	 * Gets a list of type FilmData.
	 * @return A list of filmFata.
	 */
    public ArrayList<FilmData> getFilms(){
    	return this.films;
    }
    
    /**
     * Imports film data from file.
     * @param filePath The path to the file to parse.
     * @param numberOfLinesToParse The number of lines to parse from the file.
     */
    public void importFilmData(String filePath, long numberOfLinesToParse){
    	//Open .tsv file to read.
    	BufferedReader TSVFile = null;
    	try {
			TSVFile = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			//File not found.
			System.err.println("Error: File not found");
			return;
		}
    	
    	//Define patterns to parse text and add them to ArrayList for easier iterations.
        String idPattern = "[0-9]++";
        String titlePattern = "\\w.*";
        String datePattern = "(\\d{4}-\\d{2}-\\d{2})|(\\d{4})|(\\d{4}-\\d{2})";
        String durationPattern = "\\d++.\\d++";
        String languageCountryGenrePattern = "\\{\"/m/\\w+_*\": \".+[\\s\\w+-]*?\"[, \"/m/\\w+_*\": \"\\w+[\\s\\w+-]*?\"]*\\}";
        
        ArrayList<String> patterns = new ArrayList<String>();
        patterns.add(idPattern);
        patterns.add(titlePattern);
        patterns.add(datePattern);
        patterns.add(durationPattern);
        patterns.add(languageCountryGenrePattern);
        patterns.add(languageCountryGenrePattern);
        patterns.add(languageCountryGenrePattern);
        
  
		try {
			//line from .tsv file.
			String line = TSVFile.readLine();
			
			//pattern iterator.
			int i, numberOfLinesParsed = 0;

	        while(numberOfLinesParsed++ < numberOfLinesToParse && line != null){
	        	//For each line in file, tokenize line.
	        	StringTokenizer tok = new StringTokenizer(line, "\t");
	        	
	        	//Create film data container.
	            FilmData m = new FilmData();
	            
	            i = 0;
	            
	            String token;
	            
	            //While there are still tokens to parse and we still haven't found all patterns.
	            while(tok.hasMoreTokens() && i < patterns.size()){
	            	
	            	token = tok.nextToken();
	            	
	            	//If the token matches the given pattern then save it.
	            	if(token.matches(patterns.get(i))){
	            		m.set(i++, token);
	            	}
	            	else{ 
	            		//If the token doesn't match the given pattern then some data is missing
	            		//search for next matching pattern.
	            		for(int c = i + 1; c < patterns.size(); c++){
	            			if((c > 3 && token.equals("{}")) || token.matches(patterns.get(c))){
	                    		m.set(c++, token);
	                    		i = c;
	                    		break;
	                    	}
	            		}
	            	}
	            }
	            
	            //Add film data to data set.
	            films.add(m);
	            
	            //Read next line in file.
	            line = TSVFile.readLine();
	        }
	        TSVFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
	 * Imports Film data.
	 * @param filePath The path to the .tsv file to parse.
	 */
    public void importFilmData(String filePath){
    	try {
			importFilmData(filePath, getFileSizeByLine(filePath));
		} catch (IOException e) {
			System.err.println("Error: File not found");
		}
    }
    
    /**
     * Gets file line count.
     * @param pathToFile Path to file.
     * @return Number of lines in file. 
     * @throws IOException if file was not found.
     */
    long getFileSizeByLine(String pathToFile) throws IOException{
    	BufferedReader reader = new BufferedReader(new FileReader(pathToFile));
    	int lines = 0;
    	while (reader.readLine() != null) lines++;
    	reader.close();
		return lines;
    }

    /**
	 * Prints information relative to each film.
	 */
    public void printDataSet(){
    	for(FilmData film: films){
    		System.out.println(film);
    	}
    }

    public static void main(String[] args) throws Exception{
    	FilmDataSet dataSet = new FilmDataSet();
    	dataSet.importFilmData("war/WEB-INF/Resources/movies_80000.tsv");
    	System.out.println(dataSet.getFilms().size());
//    	dataSet.printDataSet();
    	
    	//Will throw exception if film already in db.
    	//Just text me and i'll empty the database :).
    	long start = System.currentTimeMillis();
    	MySQLConnector.sendToDB(dataSet.getFilms(), "movies");
    	List<FilmData> result = MySQLConnector.readAllDataFromDB("movies");
    	long end = System.currentTimeMillis();
    	System.out.println("Time elapsed: " + (end - start) / 1000.0);
//    	MySQLConnector.readAllDataFromDB();
    	System.out.println(result.size());

    }
}
