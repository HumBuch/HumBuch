package de.dhbw.humbuch.dataImport;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import de.dhbw.humbuch.model.Student;

public final class MyCSVHandler {
	
	/**
	 * Reads a csv file and creates student objects of it's records.
	 * 
	 * @param path a path to the csv which contains information about students
	 * @return an ArrayList that contains student objects
	 * @see ArrayList
	 */
	public static ArrayList<Student> createStudentObjectsFromCSV(String path){
		ArrayList<Student> studentArrayList = new ArrayList<Student>();	

		try {
			//csvReader - separator is ';'; first line is skipped (header)
			CSVReader csvReader = new CSVReader(new FileReader(path), ';', '\'', 1);			
			
			List<String[]> allRecords = csvReader.readAll();
			Iterator<String[]> allRecordsIterator = allRecords.iterator();
		
			while(allRecordsIterator.hasNext()){
				String[] record = allRecordsIterator.next();
					
				studentArrayList.add(createStudentObject(record));					
			}	
			
			csvReader.close();
		}catch (IOException e) {
			System.err.println("Could not read student's csv records. " + e.getStackTrace());
		}
		
		return studentArrayList;
	}
	
	/**
	 * Creates a student object with the information in the record.
	 * 
	 * @param record is one line of the loaded csv-file
	 * @return Student
	 */
	private static Student createStudentObject(String[] record){
		String[] foreignLanguage = new String[4];			
		foreignLanguage[0] = record[0];
		foreignLanguage[1] = record[1];
		foreignLanguage[2] = record[2];
		foreignLanguage[3] = record[3];

							
		return new Student(foreignLanguage, record[5], record[6], Integer.parseInt(record[7]), //foreignLanguages, birthDay, gender, id
							record[8],  record[9], record[16], record[10], //tutorGroup, lastName, firstName, place
							Integer.parseInt(record[11]), record[14], record[15]); //postalCode, streetName, profile
	}
}
