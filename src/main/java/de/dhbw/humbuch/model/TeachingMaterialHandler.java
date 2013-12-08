package de.dhbw.humbuch.model;

import de.dhbw.humbuch.model.entity.Subject;
import de.dhbw.humbuch.model.entity.TeachingMaterial;


public class TeachingMaterialHandler {
	
	public static TeachingMaterial createTeachingMaterial(Subject subject, int toGrade, String name, double price){
		TeachingMaterial teachingMaterial = new TeachingMaterial();		
		teachingMaterial.setSubject(subject);
		teachingMaterial.setToGrade(toGrade);
		teachingMaterial.setName(name);
		teachingMaterial.setPrice(price);
		
		return teachingMaterial;
	}

}
