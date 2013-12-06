package tests;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.junit.Test;

import de.dhbw.humbuch.dataImport.MyCSVHandler;
import de.dhbw.humbuch.model.GradeHandler;
import de.dhbw.humbuch.model.ProfileHandler;

public class CSVTest {
	
	@Test
	public void testCreateStudentObjectsFromCSV(){
		ArrayList<de.dhbw.humbuch.model.entity.Student> list = MyCSVHandler.createStudentObjectsFromCSV("./testfiles/schueler_stamm.csv");
		assertEquals(99, list.size());		
		assertEquals("Zivko", list.get(1).getLastname());
		assertEquals("5a", GradeHandler.getFullGrade(list.get(1).getGrade()));
		assertEquals("Adelina", list.get(1).getFirstname());		
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		assertEquals("02.01.1989", dateFormat.format(list.get(1).getBirthday()));
		assertEquals("m", list.get(1).getGender());
		assertEquals("E", ProfileHandler.getLanguageProfile(list.get(1).getProfil()));
	}

}
