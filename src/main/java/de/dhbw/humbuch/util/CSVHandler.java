package de.dhbw.humbuch.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;
import de.dhbw.humbuch.model.SubjectHandler;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Parent;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.Subject;


public final class CSVHandler {
	
	/**
	 * Reads a csv file and creates student objects of it's records.
	 * 
	 * @param path
	 *            a path to the csv which contains information about students
	 * @return an ArrayList that contains student objects
	 * @see ArrayList
	 */
	public static ArrayList<Student> createStudentObjectsFromCSV(CSVReader csvReaderParam) {
		ArrayList<Student> studentArrayList = new ArrayList<Student>();

		try {
			//csvReader - separator is ';';
			CSVReader csvReader = csvReaderParam;

			Properties csvHeaderProperties = readCSVConfigurationFile();

			List<String[]> allRecords = csvReader.readAll();
			Iterator<String[]> allRecordsIterator = allRecords.iterator();
			HashMap<String, Integer> headerIndexMap = new HashMap<String, Integer>();

			if(allRecordsIterator.hasNext()) {
				String[] headerRecord = allRecordsIterator.next();
	
				for (int i = 0; i < headerRecord.length; i++) {
					headerIndexMap.put(headerRecord[i], i);
				}
			}

			while (allRecordsIterator.hasNext()) {
				String[] record = allRecordsIterator.next();

				Student student = createStudentObject(record, csvHeaderProperties, headerIndexMap);
				if(student != null){
					studentArrayList.add(student);
				}				
			}

			csvReader.close();
		}
		catch (IOException e) {
			System.err.println("Could not read student's csv records. " + e.getStackTrace());
		}

		return studentArrayList;
	}

	private static Properties readCSVConfigurationFile() {
		Properties csvHeaderProperties = new Properties();
		try {
			csvHeaderProperties.load(new InputStreamReader(new FileInputStream("src/main/resources/csvConfiguration.properties"), "UTF-8"));
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return csvHeaderProperties;
	}

	/**
	 * Creates a student object with the information in the record.
	 * 
	 * @param record is one line of the loaded csv-file
	 * @return Student
	 */
	private static Student createStudentObject(String[] record, Properties properties, HashMap<String, Integer> index) {
		String foreignLanguage1 = record[getAttributeNameToHeaderIndex(properties, index, "foreignLanguage1")];
		String foreignLanguage2 = record[getAttributeNameToHeaderIndex(properties, index, "foreignLanguage2")];
		String foreignLanguage3 = record[getAttributeNameToHeaderIndex(properties, index, "foreignLanguage3")];
		String gradeString = record[getAttributeNameToHeaderIndex(properties, index, "grade")];
		String firstName = record[getAttributeNameToHeaderIndex(properties, index, "firstName")];
		String lastName = record[getAttributeNameToHeaderIndex(properties, index, "lastName")];
		String gender = record[getAttributeNameToHeaderIndex(properties, index, "gender")];
		String birthDay = record[getAttributeNameToHeaderIndex(properties, index, "birthDay")];
		int id = Integer.parseInt(record[getAttributeNameToHeaderIndex(properties, index, "id")]);
		String religion = record[getAttributeNameToHeaderIndex(properties, index, "religion")];
		
		Parent parent = null;
		try{
			String parentTitle = record[getAttributeNameToHeaderIndex(properties, index, "parentTitle")];
			String parentLastName = record[getAttributeNameToHeaderIndex(properties, index, "parentLastName")];
			String parentFirstName = record[getAttributeNameToHeaderIndex(properties, index, "parentFirstName")];
			String parentStreet = record[getAttributeNameToHeaderIndex(properties, index, "parentStreet")];
			int parentPostalcode = Integer.parseInt(record[getAttributeNameToHeaderIndex(properties, index, "parentPostalcode")]);
			String parentPlace = record[getAttributeNameToHeaderIndex(properties, index, "parentPlace")];
					
			parent = new Parent.Builder(parentFirstName, parentLastName).title(parentTitle)
					.street(parentStreet).postcode(parentPostalcode).city(parentPlace).build();
		}
		catch(NullPointerException npe){
			System.err.println("Could not create parent object to student");
		}

		
		ArrayList<String> checkValidityList = new ArrayList<String>();
		checkValidityList.add(foreignLanguage1);
		checkValidityList.add(foreignLanguage2);
		checkValidityList.add(foreignLanguage3);
		checkValidityList.add(gradeString);
		checkValidityList.add(firstName);
		checkValidityList.add(lastName);
		checkValidityList.add(gender);
		checkValidityList.add(birthDay);
		checkValidityList.add(""+id);
		checkValidityList.add(religion);
		
		if(!checkForValidityOfAttributes(checkValidityList)){
			return null;
		}
		
		Date date = null;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse(birthDay);
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
			return null;
		}

		Grade grade = new Grade.Builder(gradeString).build();

		String[] foreignLanguage = new String[3];
		foreignLanguage[0] = foreignLanguage1;
		foreignLanguage[1] = foreignLanguage2;
		foreignLanguage[2] = foreignLanguage3;
		Set<Subject> subjectSet = SubjectHandler.createProfile(foreignLanguage, religion);
		
		return new Student.Builder(id, firstName, lastName, date, grade).profile(subjectSet).gender(gender).parent(parent).leavingSchool(false).build();
	}
	
	private static int getAttributeNameToHeaderIndex(Properties properties, HashMap<String, Integer> indexMap, String attributeName) {
		String headerValue = (String)properties.getProperty(attributeName);
		if(headerValue != null){
			int indexHeader = indexMap.get(headerValue);
			return indexHeader;
		}
		
		return -1;
	}
	
	private static boolean checkForValidityOfAttributes(ArrayList<String> attributeList){
		for(String str : attributeList){
			if(str == "-1"){
				return false;
			}
		}
		return true;
	}
}
