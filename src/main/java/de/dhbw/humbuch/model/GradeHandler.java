package de.dhbw.humbuch.model;

import java.util.ArrayList;
import java.util.List;

import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;

public class GradeHandler {	
	public static String getFullGrade(Grade grade){
		return grade.getGrade() + grade.getSuffix();
	}
	
	public static List<MapperAmountAndBorrowedMaterial> getAllRentedBooksOfGrade(Grade grade){
		List<MapperAmountAndBorrowedMaterial> gradeRentalList = new ArrayList<MapperAmountAndBorrowedMaterial>();
		List<Student> students = grade.getStudents();
		
		for(int i = 0; i < students.size(); i++){
			List<BorrowedMaterial> borrowedMaterialList = students.get(i).getBorrowedList();
			for(int j = 0; j < borrowedMaterialList.size(); j++){
				BorrowedMaterial borrowedMaterial = borrowedMaterialList.get(j);
				boolean alreadyFound = false;
				for(int k = 0; k < gradeRentalList.size(); k++){
					BorrowedMaterial alreadyBorrowedMaterial = gradeRentalList.get(k).getBorrowedMaterial();					
					if(borrowedMaterial.getTeachingMaterial().getName().equals(alreadyBorrowedMaterial.getTeachingMaterial().getName())){
						alreadyFound = true;
						gradeRentalList.get(k).increaseAmount();
					}					
				}
				if(!alreadyFound){
					MapperAmountAndBorrowedMaterial gradeRental = new MapperAmountAndBorrowedMaterial();
					gradeRental.setBorrowedMaterial(borrowedMaterial);
					gradeRental.setAmount(1);
					gradeRentalList.add(gradeRental);
				}
			}
		}
		
		return gradeRentalList;
	}

}
