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
    
    /**
    * Returns an ArrayList containing the filmData where titlePart occurs case ignored in the title.
    * @param titlePart Title to be searched.
    * @return ArrayList containing FilmDataSet.
    */
    public ArrayList<FilmData> filterByTitle(String titlePart){
    	ArrayList<FilmData> filteredSet = new ArrayList<FilmData>();
    	//distinction of occurrence of titlePart in beginning, middle or end of Title
    	//so only eg. titlePart = "the" containing data is in ArrayList and not "They"
    	for(FilmData film: films){
    		if(film.getTitle().toUpperCase().startsWith(titlePart.toUpperCase())){  
    			filteredSet.add(film);
    		} else if(film.getTitle().toUpperCase().contains(titlePart.toUpperCase())){
    			filteredSet.add(film);
    		} else if(film.getTitle().toUpperCase().endsWith(titlePart.toUpperCase())){
    			filteredSet.add(film);
		   }
    	}
    	return filteredSet;  
    }
    
   /**
    * Returns an ArrayList containing the filmData belonging to the search ID.
    * @param id ID to be searched.
    * @return ArrayList containing FilmDataSet.
    */
    ArrayList<FilmData> searchByID(long id){
        ArrayList<FilmData> filteredSet = new ArrayList<FilmData>();
        for(FilmData film: films){
            if(film.getID() == id){
                filteredSet.add(film);
            }
        }
        return filteredSet;
    }
       
    /**
     * Returns an ArrayList containing the filmData of all films with given duration.
     * @param duration Duration to be searched.
     * @return ArrayList containing FilmDataSet.
     */
    ArrayList<FilmData> filterByDuration(float duration){
       ArrayList<FilmData> filteredSet = new ArrayList<FilmData>();
       for(FilmData film: films){
           if(film.getDuration() == duration){
               filteredSet.add(film);
           }
       }
       return filteredSet;
    }
    
    /**
     * Returns an ArrayList containing the filmData of all films with a given language.
     * @param language Language to be searched.
     * @return ArrayList containing FilmDataSet.
     */
    ArrayList<FilmData> filterByLanguage(String language){
    	ArrayList<FilmData> filteredSet = new ArrayList<FilmData>();
    	for(FilmData film: films){
    		if(film.getLanguages().contains(language + " Language") || film.getLanguages().contains(language)){
    			filteredSet.add(film);
    		}
    	}
    	return filteredSet;
    }
    
    /**
     * Returns an ArrayList containing the filmData of all films released in a given year.
     * @param date Year to be searched.
     * @return ArrayList containing FilmDataSet.
     */
    ArrayList<FilmData> filterByDate(String date){
    	ArrayList<FilmData> filteredSet = new ArrayList<FilmData>();
    	int year = Integer.parseInt(date.split("-")[0]);
    	for(FilmData film: films){
    		if(film.getDate() == year){
    			filteredSet.add(film);
    		}
    	}
    	return filteredSet;
    }

    /**
     * Returns an ArrayList containing the filmData of all films filmed (partially) in a given country.
     * @param country Country to be searched.
     * @return ArrayList containing FilmDataSet.
     */
    ArrayList<FilmData> filterByCountry(String country){
    	ArrayList<FilmData> filteredSet = new ArrayList<FilmData>();
    	for(FilmData film: films){
    		if(film.getCountries().contains(country)){
    			filteredSet.add(film);
    		}
    	}
    	return filteredSet;
    }
    
    /**
     * Returns an ArrayList containing the filmData of all films assigned to a given genre.
     * @param genre Genre to be searched.
     * @return ArrayList containing FilmDataSet.
     */
    ArrayList<FilmData> filterByGenre(String genre){
    	ArrayList<FilmData> filteredSet = new ArrayList<FilmData>();
    	for(FilmData film: films){
    		if(film.getGenres().contains(genre)){
    			filteredSet.add(film);
    		}
    	}
    return filteredSet;
    }
    
    public static void main(String[] args){
//    	FilmDataSet dataSet;
//    	ArrayList<FilmData> films;
//		try {
//			dataSet = TSVImporter.importFilmData("war/WEB-INF/Resources/movies_80000.tsv");
//			dataSet.printDataSet();
//	    	
//	    	films = dataSet.searchByID(474750);
//	    	System.out.println(films);
//	    	
//	    	System.out.println("-------------------------------");
//	    	
//	    	films = dataSet.filterByDuration(100);
//	    	for(FilmData film : films)
//	    		System.out.println(film);
//	    	
//	    	System.out.println("-------------------------------");
//	    	
//	    	films = dataSet.filterByLanguage("Italian");
//	    	for(FilmData film : films)
//	    		System.out.println(film);
//	    	
//	    	System.out.println("-------------------------------");
//	    	
//	    	films = dataSet.filterByDate("1950-10-04");
//	    	for(FilmData film : films)
//	    		System.out.println(film);
//	    	
//	    	System.out.println("-------------------------------");
//	    	
//	    	films = dataSet.filterByCountry("Switzerland");
//	    	for(FilmData film : films)
//	    		System.out.println(film);
//	    	
//	    	System.out.println("-------------------------------");
//	    	
//	    	films = dataSet.filterByGenre("Comedy");
//	    	for(FilmData film : films)
//	    		System.out.println(film);
//	    	
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
}
