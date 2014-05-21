package de.dhbw.humbuch.util;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.junit.Test;
import org.mozilla.universalchardet.UniversalDetector;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Test reading a csv file and creating student objects of its data.
 * 
 * @author Benjamin Räthlein
 *
 */
public class CSVTest {
	
	@Test
	public void testCreateStudentObjectsFromCSV(){
		CSVReader csvReader;
		try {
			String encoding = checkEncoding("./src/test/java/de/dhbw/humbuch/util/schueler_stamm.csv");

			if (encoding != null) {
				csvReader = new CSVReader(new InputStreamReader(new FileInputStream("./src/test/java/de/dhbw/humbuch/util/schueler_stamm.csv"), encoding), ';', '\'', 0);			
			}
			else{
				csvReader = new CSVReader(new FileReader("./src/test/java/de/dhbw/humbuch/util/schueler_stamm.csv"), ';', '\'', 0);
			}
			ArrayList<de.dhbw.humbuch.model.entity.Student> list = CSVHandler.createStudentObjectsFromCSV(csvReader);
			assertEquals(99, list.size());		
			assertEquals("Zivko", list.get(1).getLastname());
			assertEquals("Adelina", list.get(1).getFirstname());		
			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			assertEquals("02.01.1989", dateFormat.format(list.get(1).getBirthday()));
			assertEquals("m", list.get(1).getGender());
		}
		catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
	
	private String checkEncoding(String fileName) {
		byte[] buf = new byte[4096];
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(fileName);

			// (1)
			UniversalDetector detector = new UniversalDetector(null);

			// (2)
			int nread;
			while ((nread = fileInputStream.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
			// (3)
			detector.dataEnd();

			// (4)
			String encoding = detector.getDetectedCharset();

			// (5)
			detector.reset();
			fileInputStream.close();
			return encoding;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
