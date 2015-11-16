package com.uzh.gwt.softeng.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.uzh.gwt.softeng.shared.FilmData;
import com.uzh.gwt.softeng.shared.FilmDataSet;

/**
 * The {@code TSVImporter} class is responsible for reading the data from the tsv file.
 */
public class TSVImporter {
	
	 /**
     * Gets file line count.
     * @param pathToFile Path to file.
     * @return Number of lines in file. 
     * @throws IOException if file was not found.
     */
    static long getFileSizeByLine(String pathToFile) throws IOException{
    	BufferedReader reader = new BufferedReader(new FileReader(pathToFile));
    	int lines = 0;
    	while (reader.readLine() != null) lines++;
    	reader.close();
		return lines;
    }
    
    /**
	 * Imports Film data from file.
	 * @param filePath The path to the .tsv file to parse.
	 * @return Imported FilmDataSet.
     * @throws IOException IOException.
     * @throws FileNotFoundException File not found.
	 */
    public static FilmDataSet importFilmData(String filePath) throws FileNotFoundException, IOException{
		return importFilmData(filePath, getFileSizeByLine(filePath));
    }
     
    /**
     * Imports film data from file.
     * @param filePath The path to the file to parse.
     * @param numberOfLinesToParse The number of lines to parse from the file.
     * @throws FileNotFoundException File not found.
     * @return Imported FilmDataSet.
     */
    public static FilmDataSet importFilmData(String filePath, long numberOfLinesToParse) throws FileNotFoundException{
    	//Open .tsv file to read.
    	BufferedReader TSVFile = null;	
		TSVFile = new BufferedReader(new FileReader(filePath));
    	
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
        
        ArrayList<FilmData> films = new ArrayList<FilmData>((int) numberOfLinesToParse);
  
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
	            				if(token.equals("{}")){
	            					c++;
	            					i = c;
	            				}
	            				else{
	            					m.set(c++, token);
	            					i = c;
	            					break;
	            				}
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
		
		return new FilmDataSet(films);
    }
}
