package de.dhbw.humbuch.dataImport;

import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;
import de.dhbw.humbuch.model.ProfileTypeHandler;
import de.dhbw.humbuch.model.StudentHandler;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.ProfileType;
import de.dhbw.humbuch.model.entity.Student;

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
		String[] foreignLanguage = new String[3];			
		foreignLanguage[0] = record[0];
		foreignLanguage[1] = record[1];
		foreignLanguage[2] = record[2];
		
		Set<ProfileType> profileTypeSet = ProfileTypeHandler.createProfile(foreignLanguage, record[15]);
				
		//Profile profile = ProfileHandler.createProfile(record[0], record[1], record[2]);
		//return StudentHandler.createStudentObject(record[16], record[9], record[5], record[6], record[8], profileTypeSet);
		Date date = null;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse(record[5]);
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}
		Grade grade = new Grade();
		String[] splittedString = splitBetweenCharsAndDigits(record[8]);
		grade.setGrade(Integer.parseInt(splittedString[0]));
		grade.setSuffix(splittedString[1]);
		return new Student.Builder(0, record[16], record[9],  date, grade).profileTypes(profileTypeSet).gender(record[6]).build();
	}
	
	private static String[] splitBetweenCharsAndDigits(String str){	
		return str.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
	}
}
