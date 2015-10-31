package com.manning.gwtia.ch03.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

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
	 * Gets a list of type FilmData.
	 * @return A list of filmFata.
	 */
    public ArrayList<FilmData> getFilms(){
    	return this.films;
    }
    
    /**
	 * Creates a new FilmDataSet instance.
	 */
    public FilmDataSet(){
    	films = new ArrayList<FilmData>();
    }
    
    /**
	 * Filters language, countries and genres tokens to extract the relevant information.
	 * @param token The token to clean.
	 * @return An ArrayList of Strings containing the relevant information.
	 */
    public static ArrayList<String> filterLanguagesCountriesGenres(String token){
    	//Remove surrounding brackets.
    	token = token.replace("{", "").replace("}", "");

    	//Split token between commas ", " and save it in an ArrayList.
        ArrayList<String> genres = new ArrayList<String>(Arrays.asList(token.split(", ")));
        
        //Further split each token in id and genre.
        ArrayList<ArrayList<String> > g = new ArrayList<ArrayList<String> >();
        for (String str : genres) {
            g.add(new ArrayList<String>(Arrays.asList(str.split(": "))));
        }


        genres.clear();
        //Keep only genre.
        int i = 0;
        for (ArrayList<String> str : g) {
            for (String tmp : str) {
                if(i % 2 != 0)
                    genres.add(tmp.replace("\"", ""));
                    i++;
            }
        }
        
        return genres;
    }
    
    /**
	 * Imports Film data.
	 * @param fileName The path to the .tsv file to parse.
	 */
    public void importFilmData(String fileName){
    	//Open .tsv file to read.
    	BufferedReader TSVFile = null;
    	try {
			TSVFile = new BufferedReader(new FileReader(fileName));
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
			int i;

	        while(line != null){
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
	 * Prints information relative to each film.
	 */
    public void printDataSet(){
    	for(FilmData film: films){
    		System.out.println(film);
    	}
    }

    public static void main(String[] args) throws Exception{
    	FilmDataSet dataSet = new FilmDataSet();
    	dataSet.importFilmData("war/Resources/movies_80000.tsv");
//    	dataSet.printDataSet();
    	MySQLConnector.readFromDB();
//    	MySQLConnector.sendToDB(dataSet.getFilms()); 	
    }
}
