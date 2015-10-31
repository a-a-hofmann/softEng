package com.manning.gwtia.ch03.server;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * The {@code FilmData} class is responsible for managing and displaying the
 * contents of a single Film.
 *
 */
public class FilmData {
	/**
	 * ID.
	 */
	private long ID;
	
	/**
	 * Film title.
	 */
    private String title;
    
    /**
	 * Film release date.
	 */
    private Calendar date;
    
    /**
	 * Film duration in minutes.
	 */
    private float duration;
    
    /**
	 * An ArrayList of Strings containing languages.
	 */
    private ArrayList<String> languages;
    
    /**
	 * An ArrayList of Strings contraining countries.
	 */
    private ArrayList<String> countries;
    
    /**
	 * An ArrayList of Strings contraining genres.
	 */
    private ArrayList<String> genres;
    
    /**
	 * Creates a new FilmData instance.
	 */
    public FilmData(){
    	ID = -1;
    	title = "No Title";
    	date = Calendar.getInstance();
    	date.set(Calendar.YEAR, 99999);
    	duration = 0;
    	languages = new ArrayList<String>();
    	countries = new ArrayList<String>();
    	genres = new ArrayList<String>();
    }
    
    /**
	 * Gets the ID of a film.
	 * @return Film ID.
	 */
    public long getID(){
    	return this.ID;
    }
    
    /**
	 * Gets the title of a film.
	 * @return Film title.
	 */
    public String getTitle(){
    	return this.title;
    }
    
    /**
	 * Gets the duration of a film.
	 * @return Film duration.
	 */
    public float getDuration(){
    	return this.duration;
    }
    
    /**
	 * Gets the countries of a film.
	 * @return ArrayList of countries.
	 */
    public String getCountries(){
    	return (this.countries.size() > 0) ? this.countries.get(0) : "";
    }

    /**
	 * Returns a string representation of the film.
	 * @return a string representation of the film.
	 */
    @Override
    public String toString() {
        return ID + " " + title  + " " + date.get(Calendar.YEAR) + " " + duration + " " + languages + " " + countries + "\nGenres: " + genres + "\n";
    }
    
    /**
	 * Sets a given field to a given value.
	 * @param field The field of FilmData to set to value. 
	 * @param value The value to set to the given field.
	 */
    public void set(int field, String value){
    	//Cast field to Instance enum for switch.
    	Instances instance = Instances.values()[field];
    	
    	switch(instance){
	    	case ID:
	    		this.ID = Long.parseLong(value);
	    		break;
	    	case title:
	    		this.title = value;
	    		break;
	    	case duration:
	    		this.duration = Float.parseFloat(value);
	    		break;
	    	case countries:
	    		if(value.equals("{}")){
	    			this.countries.add(value);
	    		}
	    		else{
		    		this.countries = FilmDataSet.filterLanguagesCountriesGenres(value);
	    		}
	    		break;
	    	case languages:
	    		if(value.equals("{}")){
	    			this.languages.add(value);
	    		}
	    		else{
	    			this.languages = FilmDataSet.filterLanguagesCountriesGenres(value);
	    		}
	    		break;
	    	case date:
	    		int year = Integer.parseInt(value.split("-")[0]);
	        	this.date.set(Calendar.YEAR, year);
	    		break;
	    	case genres:
	    		if(value.equals("{}")){
	    			this.genres.add(value);
	    		}
	    		else{
	    			this.genres = FilmDataSet.filterLanguagesCountriesGenres(value);
	    		}
	    		break;
			default:
				break;		
    	}
    }
    
    /**
     *  The possible fields to set for FilmData.
     */
    private enum Instances{
    	ID,
    	title,
    	date,
    	duration,
    	languages,
    	countries,
    	genres;
    }
}
