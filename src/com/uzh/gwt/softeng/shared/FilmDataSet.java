package com.uzh.gwt.softeng.shared;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import com.uzh.gwt.softeng.server.TSVImporter;



/**
 * The {@code FilmDataSet} class is responsible for managing a set of films.
 */
@SuppressWarnings("serial")
public class FilmDataSet implements Serializable{
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
     * Creates a new FilmDataSet instance with a given FilmData list.
     * @param films FilmData list to pass to newly created FilmDataSet instance.
     */
    public FilmDataSet(ArrayList<FilmData> films){
    	this.films = films;
    }
    
    /**
	 * Gets a list of type FilmData.
	 * @return A list of filmFata.
	 */
    public ArrayList<FilmData> getFilms(){
    	return this.films;
    }
    
    /**
     * Set new FilmDataSet.
     * @param movies New film data set to be saved in filmDataSet object.
     */
    public void setDataSet(ArrayList<FilmData> movies){
    	this.films = movies;
    }

    /**
	 * Prints information relative to each film.
	 */
    public void printDataSet(){
    	for(FilmData film: films){
    		System.out.println(film);
    	}
    }
    
  //returns an ArrayList containing the filmData where titlePart occurs case ignored in the title
    public ArrayList<FilmData> filterByTitle(String titlePart){
	   ArrayList<FilmData> filteredSet = new ArrayList<FilmData>();
	   
	   //distinction of occurence of titlePart in beginning, middle or end of Title
	   //so only eg. titlePart = "the" containing data is in ArrayList and not "They"
	   for(FilmData film: films){
		   if(film.getTitle().toUpperCase().startsWith(titlePart.toUpperCase())){  
			   filteredSet.add(film);
		   }else if(film.getTitle().toUpperCase().contains(titlePart.toUpperCase())){
			   filteredSet.add(film);
		   }else if(film.getTitle().toUpperCase().endsWith(titlePart.toUpperCase())){
			   filteredSet.add(film);
		   }
	   }
	 return filteredSet;  
   }
    
    public static void main(String[] args){
    	FilmDataSet dataSet;
		try {
			dataSet = TSVImporter.importFilmData("war/WEB-INF/Resources/movies_80000.tsv");
			dataSet.printDataSet();
	    	ArrayList<FilmData> films = dataSet.filterByTitle("Batman");
	    	for(FilmData tmp : films)
	    		System.out.println(tmp);
	    	System.out.println(films.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
