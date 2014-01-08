package de.dhbw.humbuch.model.entity;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;


public class GradeTest {
	
	@Test
	public void testGetAllRentedBooksOfGrade(){
		Map<Grade, Map<TeachingMaterial, Integer>> gradeMap = prepareGradeTest();
		for(Grade grade : gradeMap.keySet()){
			Map<TeachingMaterial, Integer> teachingMaterialMap = gradeMap.get(grade);
			Iterator<TeachingMaterial> iterator = teachingMaterialMap.keySet().iterator();
			TeachingMaterial teachingMaterial;
			if(iterator.hasNext()){
				teachingMaterial = iterator.next();
				assertEquals(4, (int) teachingMaterialMap.get(teachingMaterial));
				assertEquals("Bio1 - Bugs", teachingMaterial.getName());
			}
			if(iterator.hasNext()){
				teachingMaterial = iterator.next();
				assertEquals(2, (int) teachingMaterialMap.get(teachingMaterial));
				assertEquals("German1 - Faust", teachingMaterial.getName());
			}
			if(iterator.hasNext()){
				teachingMaterial = iterator.next();
				assertEquals(3, (int) teachingMaterialMap.get(teachingMaterial));
				assertEquals("Java rocks", teachingMaterial.getName());
			}
			if(iterator.hasNext()){
				teachingMaterial = iterator.next();
				assertEquals(7, (int) teachingMaterialMap.get(teachingMaterial));
				assertEquals("Geometrie for Dummies", teachingMaterial.getName());
			}
		}
	}
	
	public static Map<Grade, Map<TeachingMaterial, Integer>> prepareGradeTest(){
		Map<Grade, Map<TeachingMaterial, Integer>> gradeMap = new LinkedHashMap<>();
		Map<TeachingMaterial, Integer> teachingMaterialMap = new LinkedHashMap<>();
		Grade grade = new Grade.Builder("7b").teacher("Herr Bob").build();
		Category category = new Category.Builder("Book").build();
		Date date = null;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.04.1970");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}		
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(category, "Bio1 - Bugs", "1-2-3", date).price(79.75).toGrade(6).build();
		teachingMaterial.setId(1);
		teachingMaterialMap.put(teachingMaterial, 4);
		teachingMaterial = new TeachingMaterial.Builder(category, "German1 - Faust", "1-2-3", date).price(22.49).toGrade(11).build();
		teachingMaterial.setId(2);
		teachingMaterialMap.put(teachingMaterial, 2);
		teachingMaterial = new TeachingMaterial.Builder(category, "Java rocks", "1-2-3", date).price(22.49).toGrade(11).build();
		teachingMaterial.setId(3);
		teachingMaterialMap.put(teachingMaterial, 3);
		teachingMaterial = new TeachingMaterial.Builder(category, "Geometrie for Dummies", "1-2-3", date).price(22.49).toGrade(11).build();
		teachingMaterial.setId(4);
		teachingMaterialMap.put(teachingMaterial, 7);
		gradeMap.put(grade, teachingMaterialMap);
		
		return gradeMap;		
	}
	
	public static Map<Grade, Map<TeachingMaterial, Integer>> prepareMultipleGradeTest(){	
		Map<Grade, Map<TeachingMaterial, Integer>> gradeMap = new LinkedHashMap<>();
		Map<TeachingMaterial, Integer> teachingMaterialMap = new LinkedHashMap<>();
		Grade grade = new Grade.Builder("7b").teacher("Herr Bob").build();
		grade.setId(1);
		Category category = new Category.Builder("Book").build();
		Date date = null;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.04.1970");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}		
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(category, "Bio1 - Bugs", "1-2-3", date).price(79.75).toGrade(6).build();
		teachingMaterial.setId(1);
		teachingMaterialMap.put(teachingMaterial, 4);
		teachingMaterial = new TeachingMaterial.Builder(category, "German1 - Faust", "1-2-3", date).price(22.49).toGrade(11).build();
		teachingMaterial.setId(2);
		teachingMaterialMap.put(teachingMaterial, 2);
		teachingMaterial = new TeachingMaterial.Builder(category, "Java rocks", "1-2-3", date).price(22.49).toGrade(11).build();
		teachingMaterial.setId(3);
		teachingMaterialMap.put(teachingMaterial, 3);
		teachingMaterial = new TeachingMaterial.Builder(category, "Geometrie for Dummies", "1-2-3", date).price(22.49).toGrade(11).build();
		teachingMaterial.setId(4);
		teachingMaterialMap.put(teachingMaterial, 7);
		gradeMap.put(grade, teachingMaterialMap);
		
		grade = new Grade.Builder("11a").teacher("Frau Berta").build();
		grade.setId(2);
		category = new Category.Builder("Book").build();
		date = null;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.04.1970");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}		
		teachingMaterial = new TeachingMaterial.Builder(category, "Bio1 - Bugs", "1-2-3", date).price(79.75).toGrade(6).build();
		teachingMaterial.setId(1);
		teachingMaterialMap.put(teachingMaterial, 4);
		teachingMaterial = new TeachingMaterial.Builder(category, "German1 - Faust", "1-2-3", date).price(22.49).toGrade(11).build();
		teachingMaterial.setId(2);
		teachingMaterialMap.put(teachingMaterial, 2);
		teachingMaterial = new TeachingMaterial.Builder(category, "Java rocks", "1-2-3", date).price(22.49).toGrade(11).build();
		teachingMaterial.setId(3);
		teachingMaterialMap.put(teachingMaterial, 3);
		teachingMaterial = new TeachingMaterial.Builder(category, "Geometrie for Dummies", "1-2-3", date).price(22.49).toGrade(11).build();
		teachingMaterial.setId(4);
		teachingMaterialMap.put(teachingMaterial, 7);
		gradeMap.put(grade, teachingMaterialMap);
		
		grade = new Grade.Builder("5c").teacher("Herr Kaizen").build();
		grade.setId(3);

		category = new Category.Builder("Book").build();
		date = null;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.04.1970");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}		
		teachingMaterial = new TeachingMaterial.Builder(category, "Bio1 - Bugs", "1-2-3", date).price(79.75).toGrade(6).build();
		teachingMaterial.setId(1);
		teachingMaterialMap.put(teachingMaterial, 4);
		teachingMaterial = new TeachingMaterial.Builder(category, "German1 - Faust", "1-2-3", date).price(22.49).toGrade(11).build();
		teachingMaterial.setId(2);
		teachingMaterialMap.put(teachingMaterial, 2);
		teachingMaterial = new TeachingMaterial.Builder(category, "Java rocks", "1-2-3", date).price(22.49).toGrade(11).build();
		teachingMaterial.setId(3);
		teachingMaterialMap.put(teachingMaterial, 3);
		teachingMaterial = new TeachingMaterial.Builder(category, "Geometrie for Dummies", "1-2-3", date).price(22.49).toGrade(11).build();
		teachingMaterial.setId(4);
		teachingMaterialMap.put(teachingMaterial, 7);
		gradeMap.put(grade, teachingMaterialMap);
			
		return gradeMap;		
	}
}
