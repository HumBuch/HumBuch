package de.dhbw.humbuch.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;
import de.dhbw.humbuch.model.SubjectHandler;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Parent;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.Subject;

/**
 * Create student objects of data in csv file.
 * Checks cells for validity.
 * 
 * @author Benjamin Räthlein
 * @author David Vitt
 *
 */
public final class CSVHandler {

	/**
	 * Reads a csv file and creates student objects of it's records.
	 * 
	 * @param path
	 *            a path to the csv which contains information about students
	 * @return an ArrayList that contains student objects
	 * @exception throws an UnsupportedOperationException if an error occurred
	 * @see ArrayList
	 */
	public static ArrayList<Student> createStudentObjectsFromCSV(CSVReader csvReaderParam) throws UnsupportedOperationException {
		ArrayList<Student> studentArrayList = new ArrayList<Student>();

		try {
			//csvReader - separator is ';';
			CSVReader csvReader = csvReaderParam;

			Properties csvHeaderProperties = readCSVConfigurationFile();
			Map<String, String> csvHeaderPropertyStrings = new LinkedHashMap<>();

			for (Object property : csvHeaderProperties.keySet()) {
				csvHeaderPropertyStrings.put(((String) property).replaceAll("\\p{C}", ""), csvHeaderProperties.getProperty((String) property));
			}

			List<String[]> allRecords = csvReader.readAll();
			Iterator<String[]> allRecordsIterator = allRecords.iterator();
			HashMap<String, Integer> headerIndexMap = new HashMap<String, Integer>();

			if (allRecordsIterator.hasNext()) {
				String[] headerRecord = allRecordsIterator.next();

				for (int i = 0; i < headerRecord.length; i++) {
					// removes all non-printable characters 
					headerIndexMap.put(headerRecord[i].replaceAll("\\p{C}", ""), i);
				}
			}

			int index = 0;
			while (allRecordsIterator.hasNext()) {
				String[] record = allRecordsIterator.next();
				
				index++;
				Student student = createStudentObject(record, csvHeaderPropertyStrings, headerIndexMap, index);
				if (student != null) {
					studentArrayList.add(student);
				}
				else {
					throw new UnsupportedOperationException("Mindestens ein Studenten-Datensatz war korrumpiert");
				}
			}

			csvReader.close();
		}
		catch (IOException e) {
			throw new UnsupportedOperationException("Die Studentendaten konnten nicht eingelesen werden");
		}

		return studentArrayList;
	}

	/**
	 * Read a properties file that contains information about the names of the header fields in the csv.
	 * If the name of a header in the csv changes, the new name can be mapped in the properties file. As a result,
	 * changes of csv headers can be adopted easily without the need of touching the code.
	 * Also, the properties file enables the program to handle csv files even in case the order of the header fields
	 * changed.
	 * 
	 * @return properties that fit the csv header line
	 */
	private static Properties readCSVConfigurationFile() {
		Properties csvHeaderProperties = new Properties();
		try {
			csvHeaderProperties.load(new InputStreamReader(new ResourceLoader("csvConfiguration.properties").getStream()));
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
	 * @param record
	 *            is one line of the loaded csv-file
	 * @param dataSetIndex 
	 * @return Student
	 */
	private static Student createStudentObject(String[] record, Map<String, String> properties, HashMap<String, Integer> index, int dataSetIndex) throws UnsupportedOperationException {
		String foreignLanguage1, foreignLanguage2, foreignLanguage3, gradeString, firstName, lastName, gender, birthDay, religion;
		String parentTitle, parentLastName, parentFirstName, parentStreet, parentPlace;
		int id;
		int parentPostalcode;

		try {
			foreignLanguage1 = record[getAttributeNameToHeaderIndex(properties, index, "foreignLanguage1")];
			foreignLanguage2 = record[getAttributeNameToHeaderIndex(properties, index, "foreignLanguage2")];
			foreignLanguage3 = record[getAttributeNameToHeaderIndex(properties, index, "foreignLanguage3")];
			gradeString = record[getAttributeNameToHeaderIndex(properties, index, "grade")];
			firstName = record[getAttributeNameToHeaderIndex(properties, index, "firstName")];
			lastName = record[getAttributeNameToHeaderIndex(properties, index, "lastName")];
			gender = record[getAttributeNameToHeaderIndex(properties, index, "gender")];
			birthDay = record[getAttributeNameToHeaderIndex(properties, index, "birthDay")];
			id = Integer.parseInt(record[getAttributeNameToHeaderIndex(properties, index, "id")]);
			religion = record[getAttributeNameToHeaderIndex(properties, index, "religion")];
		}
		catch (ArrayIndexOutOfBoundsException a) {
			throw new UnsupportedOperationException("Ein Wert im Studentendatensatz konnte nicht gelesen werden. Fehler in Datensatz: " + dataSetIndex);
		}
		catch (NumberFormatException e) {
			throw new UnsupportedOperationException("Mindestens eine Postleitzahl ist keine gültige Nummer. Fehler in Datensatz: " + dataSetIndex);
		}

		Parent parent = null;
		try {
			parentTitle = record[getAttributeNameToHeaderIndex(properties, index, "parentTitle")];
			parentLastName = record[getAttributeNameToHeaderIndex(properties, index, "parentLastName")];
			parentFirstName = record[getAttributeNameToHeaderIndex(properties, index, "parentFirstName")];
			parentStreet = record[getAttributeNameToHeaderIndex(properties, index, "parentStreet")];
			parentPostalcode = Integer.parseInt(record[getAttributeNameToHeaderIndex(properties, index, "parentPostalcode")]);
			parentPlace = record[getAttributeNameToHeaderIndex(properties, index, "parentPlace")];

			parent = new Parent.Builder(parentFirstName, parentLastName).title(parentTitle)
					.street(parentStreet).postcode(parentPostalcode).city(parentPlace).build();
		}
		catch (NullPointerException e) {
			throw new UnsupportedOperationException("Die Elterndaten enthalten an mindestens einer Stelle einen Fehler. Fehler in Datensatz: " + dataSetIndex);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			throw new UnsupportedOperationException("Mindestens ein Datensatz enthält keine Eltern-Informationen. Fehler in Datensatz: " + dataSetIndex);
		}
		catch (NumberFormatException e) {
			throw new UnsupportedOperationException("Mindestens eine Postleitzahl ist keine gültige Nummer.Fehler in Datensatz: " + dataSetIndex);
		}

		Map<String, Boolean> checkValidityMap = new LinkedHashMap<>();
		checkValidityMap.put(foreignLanguage1, true);
		checkValidityMap.put(foreignLanguage2, true);
		checkValidityMap.put(foreignLanguage3, true);
		checkValidityMap.put(gradeString, false);
		checkValidityMap.put(firstName, false);
		checkValidityMap.put(lastName, false);
		checkValidityMap.put(gender, false);
		checkValidityMap.put(birthDay, false);
		checkValidityMap.put("" + id, false);
		checkValidityMap.put(religion, false);
		checkValidityMap.put(parentTitle, false);
		checkValidityMap.put(parentLastName, false);
		checkValidityMap.put(parentFirstName, false);
		checkValidityMap.put(parentStreet, false);
		checkValidityMap.put("" + parentPostalcode, false);
		checkValidityMap.put(parentPlace, false);

		String check = checkForValidityOfAttributes(checkValidityMap);
		if (!check.equals("okay")) {
			if(check.equals("error")){
				throw new UnsupportedOperationException("Mindestens ein Studenten-Datensatz war korrumpiert. Fehler in Datensatz: " + dataSetIndex);
			}
			else if(check.equals("empty")){
				throw new UnsupportedOperationException("Ein Datensatz ist nicht vollständig gefüllt. Fehler in Datensatz: " + dataSetIndex);
			}
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

	private static int getAttributeNameToHeaderIndex(Map<String, String> properties, HashMap<String, Integer> indexMap, String attributeName) throws UnsupportedOperationException {
		String headerValue = (String) properties.get(attributeName);

		if (headerValue != null) {
			int indexHeader = -1;
			if (indexMap.get(headerValue) != null) {
				indexHeader = indexMap.get(headerValue);
			}
			else {
				throw new UnsupportedOperationException("Ein CSV-Spaltenname konnte nicht zugeordnet werden. "
						+ "Bitte die Einstellungsdatei mit der CSV-Datei abgleichen. Spaltenname: " + headerValue);
			}
			return indexHeader;
		}

		return -1;
	}

	/**
	 * If one string of the map is -1 an error occurred previously and the
	 * method will return false. If one string is empty that is not to allowed to
	 * be empty (indicated by 'false' in the map) the method will return false.
	 * If this method returns false, an exception will be thrown that the CSV lacks important data.
	 * If this method returns true, the data set of the CSV is correct.
	 * 
	 * @param attributes
	 * @return boolean that indicates whether the data set of the CSV is correct or not.
	 */
	private static String checkForValidityOfAttributes(Map<String, Boolean> attributes) {
		for (String str : attributes.keySet()) {
			if (str.equals("-1")) {
				return "error";
			}
			if (!attributes.get(str)) {
				if (str.equals("")) {
					return "empty";
				}
			}
		}
		return "okay";
	}
}
