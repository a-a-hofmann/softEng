package com.uzh.gwt.softeng.shared;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.view.client.Range;


/**
 * The {@code FilmDataSet} class is responsible for managing a set of films.
 */
public class FilmDataSet implements Serializable{
	
	/**
	 * Serial UID.
	 */
	private static final long serialVersionUID = 5765170337279868452L;

	/**
	 * List of filmData.
	 */
    private ArrayList<FilmData> films;
    
    /**
     * Map of number of films per country.
     */
    private HashMap<String, Integer> filmsPerCountry;
    
    /**
     * Map (id, language).
     */
    private HashMap<String, String> languages;
    
    /**
     * ArrayList languagesList for client
     */
    private ArrayList<String> languagesList;
    
    /**
     * Map (id, genres).
     */
    private HashMap<String, String> genres;
   
    /**
     * ArrayList genresList for client
     */
    private ArrayList<String> genresList;
    
    /**
     * Map (id, countries).
     */
    private HashMap<String, String> countries;
    
    /**
     * ArrayList countries for client
     */
    private ArrayList<String> countriesList;
    
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
     * Get all languages from the FilmDataSet.
     * @return Map containing all languages and ids.
     */
    public HashMap<String, String> getLanguages(){
    	if (languages != null){
    		return languages;
    	}
    	
		HashMap<String, String> results = new HashMap<String, String>();
	    	String id;
	    	String language;
	    	for(FilmData film : films){
	    		Iterator<String> it = film.getLanguages().iterator();
	    		while(it.hasNext()){
	    			id = it.next();
	    			language = it.next();
	    			
	    			results.put(language, id);
	    		}
	    	}
    	return results;
    }
    
    /**
     * Get all languages from the FilmDataSet for the client
     */
    public ArrayList<String> getLanguagesList(){
    	if (languagesList != null){
    		return languagesList;
    	}
    	
		ArrayList<String> results = new ArrayList<String>();
	    	String language;
	    	for(FilmData film : films){
	    		Iterator<String> it = film.getLanguages().iterator();
	    		while(it.hasNext()){
	    			language = it.next();
	    			
	    			results.add(language);
	    		}
	    	}
    	return results;
    }
    
    /**
     * Get all genres from the FilmDataSet.
     * @return Map containing all genres and ids.
     */
    public HashMap<String, String> getGenres(){
    	if (genres != null){
    		return genres;
    	}
    	
		HashMap<String, String> results = new HashMap<String, String>();
	    	String id;
	    	String genre;
	    	for(FilmData film : films){
	    		Iterator<String> it = film.getGenres().iterator();
	    		while(it.hasNext()){
	    			id = it.next();
	    			genre = it.next();
	    			
	    			results.put(genre, id);
	    		}
	    	}
    	return results;
    }
    
    /**
     * Get all genres from the FilmDataSet for the client
     */
    public ArrayList<String> getGenresList(){
    	if (genresList != null){
    		return genresList;
    	}
    	
		ArrayList<String> results = new ArrayList<String>();
	    	String genre;
	    	for(FilmData film : films){
	    		Iterator<String> it = film.getGenres().iterator();
	    		while(it.hasNext()){
	    			genre = it.next();
	    			
	    			results.add(genre);
	    		}
	    	}
    	return results;
    }
    
    /**
     * Get all countries from the FilmDataSet.
     * @return Map containing all genres and ids.
     */
    public HashMap<String, String> getCountries(){
    	if (countries != null){
    		return countries;
    	}
    	
		HashMap<String, String> results = new HashMap<String, String>();
	    	String id;
	    	String country;
	    	for(FilmData film : films){
	    		Iterator<String> it = film.getCountries().iterator();
	    		while(it.hasNext()){
	    			id = it.next();
	    			country = it.next();
	    			
	    			results.put(country, id);
	    		}
	    	}
    	return results;
    }
    
    /**
     * Get all countries from the FilmDataSet for the client
     */
    public ArrayList<String> getCountriesList(){
    	if (countriesList != null){
    		return countriesList;
    	}
		ArrayList<String> results = new ArrayList<String>();
	    String country;
	    	for(FilmData film : films){
	    		Iterator<String> it = film.getCountries().iterator();
	    		while(it.hasNext()){
	    			country = it.next();
	    			if(!results.contains(country)){
	    				results.add(country);
	    			}
	    		}
	    	}
	    
	    countriesList = results;
    	return results;
    }
    
    /**
     * Gets number of Films per country.
     * @return Map containing number of films per country.
     */
    public HashMap<String, Integer> getFilmsPerCountry(){
		if (filmsPerCountry != null)
			return filmsPerCountry;
		
		filmsPerCountry = new HashMap<String, Integer>();
		for (FilmData film : films){
			for (String country : film.getCountries()){
				if (filmsPerCountry.containsKey(country) && !country.isEmpty()){
					filmsPerCountry.put(country, filmsPerCountry.get(country) + 1);
				}
				else{
					filmsPerCountry.put(country, 1);
				}
			}	
		}
    	return filmsPerCountry;
    }
    
    public int size(){
    	return films.size();
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
    		if (film.getTitle().contains(titlePart)){
    			filteredSet.add(film);
    		}
//    		if(film.getTitle().toUpperCase().startsWith(titlePart.toUpperCase())){  
//    			filteredSet.add(film);
//    		} else if(film.getTitle().toUpperCase().contains(titlePart.toUpperCase())){
//    			filteredSet.add(film);
//    		} else if(film.getTitle().toUpperCase().endsWith(titlePart.toUpperCase())){
//    			filteredSet.add(film);
//		   }
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
     * /**
     * Returns an ArrayList containing the filmData filtered over a given date range.
     * @param low Lower limit.
     * @param high Upper limit.
     * @return ArrayList containing filtered film data set.
	**/
    public ArrayList<FilmData> filterByDateRange(Range range){
    	int low = range.getStart();
    	int high = low + range.getLength();
    	
    	if(low < 0){
    		throw new IllegalArgumentException("Low < 0");
    	}
    	else if(low > high){
    		throw new IllegalArgumentException("Low > High");
    	}
    	ArrayList<FilmData> filteredSet = new ArrayList<FilmData>();
    	
    	for(FilmData film: films){
    		if(film.getDate() >= low && film.getDate() <= high){
    			filteredSet.add(film);
    		}
    	}
    	return filteredSet;
    }
    
    /**
     * /**
     * Returns an ArrayList containing the filmData filtered over a given date range.
     * @param low Lower limit.
     * @param high Upper limit.
     * @return ArrayList containing filtered film data set.
	**/
    public ArrayList<FilmData> filterByDurationRange(Range range){
    	int low = range.getStart();
    	int high = low + range.getLength();
    	
    	if(low < 0){
    		throw new IllegalArgumentException("Low < 0");
    	}
    	else if(low > high){
    		throw new IllegalArgumentException("Low > High");
    	}
    	ArrayList<FilmData> filteredSet = new ArrayList<FilmData>();
    	
    	for(FilmData film: films){
    		if(film.getDuration() >= low && film.getDuration() <= high){
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
    
    /**
     * Filter data set according to given parameters.
     * @param title Title to search for.
     * @param country Country to search for.
     * @param genre Genre to search for.
     * @param language Language to search for.
     * @param durationRange Duration range to search for.
     * @param dateRange Date range to search for.
     * @return ArrayList filtered data set.
     */
    public ArrayList<FilmData> filter(String title, String country, String genre, String language,
    		Range durationRange, Range dateRange){
    	
    	
    	FilmDataSet result = new FilmDataSet(films);
    	ArrayList<FilmData> tmp;
    	if(title != null && !title.isEmpty()){
    		tmp = result.filterByTitle(title);
    		result = new FilmDataSet(tmp);
    	}
    	
    	if(country != null && !country.isEmpty()){
    		tmp = result.filterByCountry(country);
    		result = new FilmDataSet(tmp);
    	}
    	
    	if(genre != null && !genre.isEmpty()){
    		tmp = result.filterByGenre(genre);
    		result = new FilmDataSet(tmp);
    	}
    	
    	if(language != null && !language.isEmpty()){
    		tmp = result.filterByLanguage(language);
    		result = new FilmDataSet(tmp);
    	}
    	
    	
    	if(durationRange != null){
    	
    		tmp = result.filterByDurationRange(durationRange);
    		result = new FilmDataSet(tmp);
    	}
    	if(dateRange != null){
    		tmp = result.filterByDateRange(dateRange);
    		result = new FilmDataSet(tmp);
    	}

    	return result.getFilms();
    }
    
    /**
     * Filter data set according to given parameters.
     * @param title Title to search for.
     * @param country Country to search for.
     * @param genre Genre to search for.
     * @param language Language to search for.
     * @param durationRange Duration range to search for.
     * @param dateRange Date range to search for.
     * @return ArrayList filtered data set.
     */
    public ArrayList<FilmData> filter(String title, String country, String genre, String language,
    		int durationMin, int durationMax, int dateMin, int dateMax){
    	
    	return filter(title, country, genre, language, new Range(durationMin, durationMax - durationMin)
    			, new Range(dateMin, dateMax - dateMin));
    }
    
    /**
     * Format the data set to TSV.
     * @return a string representation of the entire data set in tsv format.
     */
    public String formatToTSV() {
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < films.size(); i++) {
    		sb.append(films.get(i).formatToTSV() + "\n");
    	}
    	
    	return sb.toString();
    }

    
//    public static void main(String[] args){
//    	FilmDataSet dataSet;
////    	ArrayList<FilmData> films;
//		try {
//			dataSet = TSVImporter.importFilmData("war/WEB-INF/Resources/movies_80000.tsv");
//			
//			ArrayList<FilmData> result = dataSet.filter("Batman", null, null, null, new Range(0, 400), new Range(1888, 2020));
//			
//			for(FilmData film : result)
//				System.out.println(film.getTitle());
//		} catch(Exception e){
//		
//			
////			for (String country : dataSet.getCountriesList()){
//				System.out.println(country);
//			}
//			dataSet.printDataSet();
			
//			dataSet = new FilmDataSet(dataSet.filterByDateRange(2010, 2015));
//			dataSet.printDataSet();
//			System.out.println(dataSet.getFilms().size());
			
//			HashMap<String, String> data = dataSet.getCountries();
//			for (Map.Entry<String, String> cursor : data.entrySet()){
//				System.out.println(cursor.getKey() + " " + cursor.getValue());
//			}
//			
//			System.out.println("\n");
//			for(String tmp : dataSet.getFilms().get(0).getCountries())
//				System.out.println(tmp);
			
//			HashMap<String, Integer> data = dataSet.getFilmsPerCountry();
//			for (Map.Entry<String, Integer> cursor : data.entrySet()){
//				System.out.println(cursor.getKey() + " " + cursor.getValue());
//			}
			
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
//			e.printStackTrace();
//		}
//    }
}
