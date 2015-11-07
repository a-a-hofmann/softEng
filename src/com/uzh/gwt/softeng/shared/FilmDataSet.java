package com.uzh.gwt.softeng.shared;


import java.io.Serializable;
import java.util.ArrayList;

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
}
