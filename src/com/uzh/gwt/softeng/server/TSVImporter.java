package com.uzh.gwt.softeng.server;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

import com.uzh.gwt.softeng.shared.FilmData;
import com.uzh.gwt.softeng.shared.FilmDataSet;

/**
 * The {@code TSVImporter} class is responsible for reading the data from the tsv file.
 * To upload new data to server:
 * 	1) run importFilmData to parse .tsv file.
 * 	2) call sendToDBExtendedFileSet to send the new data to the mysql db.
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
     * Imports film data from file. Easier implementation
     * @param filePath The path to the file to parse.
     * @param numberOfLinesToParse The number of lines to parse from the file.
     * @throws FileNotFoundException File not found.
     * @return Imported FilmDataSet.
     */
    public static FilmDataSet importFilmData(String filePath) throws IOException{
    	//Open .tsv file to read.
    	BufferedReader TSVFile = null;	
		TSVFile = new BufferedReader(new FileReader(filePath));
    	
		ArrayList<FilmData> films = new ArrayList<FilmData>();
		
		String line = TSVFile.readLine();
		
		while(line != null){
			FilmData m = new FilmData();
			
			String[] tmp = line.split("\t");
			
			String id = tmp[0];
			String title = tmp[2];
			String date = tmp[3];
			String duration = tmp[5];
			String languages = tmp[6];
			String countries = tmp[7];
			String genres = tmp[8];
			
			m.set(0, id);
			m.set(1, title);
			
			if(!date.isEmpty())
				m.set(2, date);
			
		
			if(!duration.isEmpty())
				m.set(3, duration);
			

			if(!languages.equals("{}"));
				m.set(4, languages);
			
		
			if(!countries.equals("{}"))
				m.set(5, countries);
			
			if(!genres.equals("{}"))
				m.set(6, genres);
			
			films.add(m);
			
			line = TSVFile.readLine();
		}
		TSVFile.close();
		
    	return new FilmDataSet(films);
    }
    
    public static void writeFilmDataToFile(FilmDataSet films, String newFileName) throws FileNotFoundException{
    	PrintWriter out = new PrintWriter(newFileName);
    		
    	out.println(films.formatToTSV());
    	
    	out.close();
    }
    
    
    public static void main(String[] args) throws FileNotFoundException, IOException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
    	
//    	FilmDataSet result = importFilmDataNew("war/WEB-INF/Resources/movies_80000.tsv");
//    	FilmDataSet newData = importFilmDataNew("war/WEB-INF/Resources/movies_1471.tsv");
//    	result.getFilms().addAll(newData.getFilms());
//    	FilmDataSet superDataSet = new FilmDataSet(result.getFilms());
//    	
//		String query = "select m.*, group_concat(DISTINCT g.genre) genres, "
//				+ "group_concat(DISTINCT l.language) languages, "
//				+ "group_concat(DISTINCT c.country) countries "
//				+ "from movies m left join moviegenres mg on m.movieid=mg.movieid "
//				+ "left join genres g on g.genreid=mg.genreid "
//				+ "left join movielanguages ml on m.movieid=ml.movieid "
//				+ "left join languages l on l.languageid=ml.languageid "
//				+ "left join moviecountries mc on m.movieid=mc.movieid "
//				+ "left join countries c on c.countryid=mc.countryid "
//				+ "group by m.movieid ";
//    	
//		FilmDataSet movies = new FilmDataSet(MySQLConnector.readFromDB(query));
//    	
//    	writeFilmDataToFile(movies, "war/WEB-INF/Resources/movies_all.tsv");
//    	MySQLConnector.sendToDBExtendedFileSet(superDataSet);
    	

////    	newData.printDataSet();
//    	try {
//			MySQLConnector.sendToDBExtendedFileSet(newData);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    	
//    	FileWriter fileWriter = new FileWriter("war/WEB-INF/Resources/tmp.txt");
//
//            // Always wrap FileWriter in BufferedWriter.
//            BufferedWriter bufferedWriter =
//                new BufferedWriter(fileWriter);
//            
//        for (FilmData film : superDataSet.getFilms())
//        	bufferedWriter.write(film.toString());
//    	result.printDataSet();

    }
}
