package de.dhbw.humbuch.model;

import de.dhbw.humbuch.model.entity.TeachingMaterial;


public class TeachingMaterialHandler {
	
	public static TeachingMaterial createTeachingMaterial(int toGrade, String name, double price){
		TeachingMaterial teachingMaterial = new TeachingMaterial();		
		teachingMaterial.setToGrade(toGrade);
		teachingMaterial.setName(name);
		teachingMaterial.setPrice(price);
		
		return teachingMaterial;
	}

}
