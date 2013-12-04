package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import de.dhbw.humbuch.dataImport.MyCSVHandler;
import de.dhbw.humbuch.model.Student;

public class CSVTest {
	
	@Test
	public void testCreateStudentObjectsFromCSV(){
		ArrayList<Student> list = MyCSVHandler.createStudentObjectsFromCSV("./testfiles/schueler_stamm.csv");
		assertEquals(99, list.size());		
		assertEquals("Zivko", list.get(1).getLastName());
	}

}
