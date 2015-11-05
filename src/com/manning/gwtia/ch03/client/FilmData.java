package com.manning.gwtia.ch03.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * The {@code FilmData} class is responsible for managing and displaying the
 * contents of a single Film.
 *
 */
public class FilmData implements Serializable {
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
//    private Calendar date;
    private int date;

    /**
	 * Film duration in minutes.
	 */
    private float duration;

    /**
	 * An ArrayList of Strings containing languages.
	 */
    private ArrayList<String> languages;

    /**
	 * An ArrayList of Strings containing countries.
	 */
    private ArrayList<String> countries;

    /**
	 * An ArrayList of Strings containing genres.
	 */
    private ArrayList<String> genres;

    /**
	 * Creates a new FilmData instance.
	 */
    public FilmData(){
    	ID = -1;
    	title = "No Title";
    	date = -1;
    	duration = 0;
    	languages = new ArrayList<String>();
    	countries = new ArrayList<String>();
    	countries.add("{}");
    	genres = new ArrayList<String>();
    }
    
    /**
	 * Creates a new FilmData instance.
	 * To use only for testing.
	 * @param ID The ID of a film.
	 * @param title The title of a film.
	 * @param date The release date of a film.
	 * @param duration The duration of a film.
	 * @param languages The languages of a film.
	 * @param countries The countries of a film.
	 * @param genres The genres of a film.
	 */
    public FilmData(long ID, String title, int date, 
    		float duration, ArrayList<String> languages, ArrayList<String> countries, ArrayList<String> genres){
    	this.ID = ID;
    	setTitle(title);
    	this.date = date;
    	this.duration = duration;
    	this.languages = languages;
    	this.countries = countries;
    	this.genres = genres;
    }
    
    /**
	 * Creates a new FilmData instance.
	 * To use only for testing.
	 * @param ID The ID of a film.
	 * @param title The title of a film.
	 * @param duration The duration of a film.
	 * @param countries The countries of a film.
	 */
    public FilmData(long ID, String title, 
    		float duration, ArrayList<String> countries){
    	this();
    	this.ID = ID;
    	setTitle(title);
    	this.duration = duration;
    	this.countries = countries;
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
     * Gets the release date of a film.
     * @return Release date.
     */
    public int getDate(){
    	return this.date;
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
    public ArrayList<String> getCountries(){
    	return this.countries;
    }
    
    /**
	 * Gets the languages of a film.
	 * @return ArrayList of languages.
	 */
    public ArrayList<String> getLanguages() {
		return this.languages;
	}
    
    /**
	 * Gets the genres of a film.
	 * @return ArrayList of genres.
	 */
	public ArrayList<String> getGenres() {
		return this.genres;
	}

	/**
	 * Returns a string representation of the film.
	 * @return a string representation of the film.
	 */
    @Override
    public String toString() {
        return ID + " " + title  + " " + date + " " + duration + " " + languages + " " + countries + "\nGenres: " + genres + "\n";
    }

    /**
	 * Sets a given field to a given value.
	 * @param field The field of FilmData to set to value.
	 * @param value The value to set to the given field.
	 */
    void set(int field, String value){
    	//Cast field to Instance enum for switch.
    	Instances instance = Instances.values()[field];

    	switch(instance){
	    	case ID:
	    		setID(Long.parseLong(value));
	    		break;
	    	case title:
				setTitle(value);
	    		break;
	    	case date:
				setDate(value);
	    		break;
	    	case duration:
				setDuration(Float.parseFloat(value));
	    		break;
	    	case languages:
				setLanguages(value);
	    		break;
	    	case countries:
	    		setCountries(value);
	    		break;
	    	case genres:
				setGenres(value);
	    		break;
			default:
				break;
    	}
    }

	/**
	 * Sets the id of a film.
	 * @param id The positive new id of a film.
	 * @throws IllegalArgumentException if id is negative.
	 */
    void setID(long id) {
    	if(id >= 0){
    		this.ID = id;
    	}
    	else{
    		throw new IllegalArgumentException();
    	}
	}
	
	/**
	 * Sets the title of a film.
	 * @param title The new title of a film.
	 */
	void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Sets the release date of a film.
	 * @param date The release date of a film.
	 */
	void setDate(String date) {
		int year = Integer.parseInt(date.split("-")[0]);
		this.date = year;
	}
	
	/**
	 * Sets the duration of a film.
	 * @param duration The positive new duration of a film.
	 * @throws IllegalArgumentException if duration is negative.
	 */
	void setDuration(float duration) {
		if(duration < 0){
			throw new IllegalArgumentException();
		}
		else{
			this.duration = duration;
		}
	}
	
	/**
	 * Sets the languages of a film.
	 * @param languages The languages of a film.
	 */
	void setLanguages(String languages) {
		this.languages = prepareLanguagesCountriesGenresTokens(languages);
	}
	
	/**
	 * Sets the countries of a film.
	 * @param countries The countries of a film.
	 */
	void setCountries(String countries) {
		this.countries = prepareLanguagesCountriesGenresTokens(countries);
	}
	
	/**
	 * Sets the genres of a film.
	 * @param genres The genres of a film.
	 */
	void setGenres(String genres) {
		this.genres = prepareLanguagesCountriesGenresTokens(genres);
	}
	
	/**
	 * Cleans the token containing the languages, countries or genres of a film.
	 * @param token String of the form {id1: data1, id2 data2, ...} to be cleaned.
	 * @return An <code>ArrayList<String></code> containing the cleaned data.
	 */
	ArrayList<String> prepareLanguagesCountriesGenresTokens(String token){
		ArrayList<String> results = new ArrayList<String>();
		if(token.equals("{}")){
			results.add(token);
		}
		else{
			results = filterLanguagesCountriesGenres(token);
		}
		return results;
	}
	
	/**
	 * Filters language, countries and genres tokens to extract the relevant information.
	 * @param token The token to clean.
	 * @return An ArrayList of Strings containing the relevant information.
	 */
    public ArrayList<String> filterLanguagesCountriesGenres(String token){
    	//Remove surrounding brackets.
    	token = token.replace("{", "").replace("}", "");

    	//Split token between commas ", " and save it in an ArrayList.
        ArrayList<String> genres = new ArrayList<String>(Arrays.asList(token.split("\", \"")));
        
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
//            	System.out.println(tmp);
                if(i % 2 != 0)
                    genres.add(tmp.replace("\"", ""));
                i++;
            }
        }
        
        return genres;
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
