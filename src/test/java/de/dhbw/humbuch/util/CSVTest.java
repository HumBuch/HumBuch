package de.dhbw.humbuch.util;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.junit.Test;

import de.dhbw.humbuch.model.GradeHandler;
import de.dhbw.humbuch.model.entity.Parent;
import de.dhbw.humbuch.util.CSVHandler;

public class CSVTest {
	
	@Test
	public void testCreateStudentObjectsFromCSV(){
		ArrayList<de.dhbw.humbuch.model.entity.Student> list = CSVHandler.createStudentObjectsFromCSV("./src/test/java/de/dhbw/humbuch/util/schueler_stamm.csv");
		assertEquals(99, list.size());		
		assertEquals("Zivko", list.get(1).getLastname());
		assertEquals("5a", GradeHandler.getFullGrade(list.get(1).getGrade()));
		assertEquals("Adelina", list.get(1).getFirstname());		
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		assertEquals("02.01.1989", dateFormat.format(list.get(1).getBirthday()));
		assertEquals("m", list.get(1).getGender());
		//assertEquals("E", ProfileHandler.getLanguageProfile(list.get(1).getProfile()));
	}
	
	@Test
	public void testCreateParentObjectsFromCSV(){
		ArrayList<Parent> list = CSVHandler.createParentObjectsFromCSV("./src/test/java/de/dhbw/humbuch/util/eltern_test.csv");
		assertEquals(3, list.size());
		assertEquals("Palmer", list.get(1).getLastname());
		assertEquals("Deacon", list.get(1).getFirstname());
		assertEquals(54321, list.get(1).getPostcode());
		assertEquals("Herr", list.get(1).getTitle());
		assertEquals("Example Ave.", list.get(1).getStreet());
	}

}
