package de.dhbw.humbuch.viewmodel;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Student;

public class ReturnViewModel {
	
	public interface getReturnList extends ActionHandler {};
	
	@Inject
	private DAO<Student> daoStudent;
	
	@Inject
	private DAO<BorrowedMaterial> daoBorrowedMaterial;
	
	@HandlesAction(getReturnList.class)
	public List<BorrowedMaterial> getRetunList(String studentId) {
		List<BorrowedMaterial> toReturn = new ArrayList<BorrowedMaterial>();

		Student student = daoStudent.find(Integer.parseInt(studentId));
		
		List<BorrowedMaterial> borrowedList = student.getBorrowedList();
		for (BorrowedMaterial borrowedMaterial : borrowedList) {
			if(borrowedMaterial.isReceived() //book is received 
					&& borrowedMaterial.getReturnDate() != null //book hasn't returned yet
					&& borrowedMaterial.getTeachingMaterial().getToGrade() <= student.getGrade().getGrade() + 1) { //book isn't needed next year
				toReturn.add(borrowedMaterial);
			}
		}
		
		return toReturn;
	}
}
