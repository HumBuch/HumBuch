package de.dhbw.humbuch.viewmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.AfterVMBinding;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;

public class ReturnViewModel {
	
	public interface GenerateStudentReturnList extends ActionHandler {}
	public interface SetBorrowedMaterialsReturned extends ActionHandler {}
	
	public interface ReturnListStudent extends State<Map<Grade, Map<Student, List<BorrowedMaterial>>>> {}
	
	@ProvidesState(ReturnListStudent.class)
	public State<Map<Grade, Map<Student, List<BorrowedMaterial>>>> returnListStudent = new BasicState<>(Map.class);
	
	private DAO<Grade> daoGrade;
	private DAO<BorrowedMaterial> daoBorrowedMaterial;
	
	
	@Inject
	public ReturnViewModel(DAO<Grade> daoGrade, DAO<BorrowedMaterial> daoBorrowedMaterial) {
		this.daoGrade = daoGrade;
		this.daoBorrowedMaterial = daoBorrowedMaterial;
	}
	
	@AfterVMBinding
	private void afterVMBinding() {
		updateReturnList();
	}
	
	@HandlesAction(GenerateStudentReturnList.class)
	public void generateStudentReturnList() {
		Map<Grade, Map<Student, List<BorrowedMaterial>>> toReturn = new HashMap<Grade, Map<Student,List<BorrowedMaterial>>>();
		
		for(Grade grade : daoGrade.findAll()) {
			Map<Student, List<BorrowedMaterial>> studentWithUnreturnedBorrowedMaterials = new HashMap<Student, List<BorrowedMaterial>>();
			
			for(Student student : grade.getStudents()) {

				List<BorrowedMaterial> unreturnedBorrowedMaterials = new ArrayList<BorrowedMaterial>();
				for (BorrowedMaterial borrowedMaterial : student.getBorrowedList()) {
					if(borrowedMaterial.isReceived() //book is received 
							&& borrowedMaterial.getReturnDate() == null //book hasn't returned yet
							&& borrowedMaterial.getTeachingMaterial().getToGrade() <= student.getGrade().getGrade() + 1) { //book isn't needed next year
						unreturnedBorrowedMaterials.add(borrowedMaterial);
					}
				}
				
				if(!unreturnedBorrowedMaterials.isEmpty()) {
					studentWithUnreturnedBorrowedMaterials.put(student, unreturnedBorrowedMaterials);
				}
			}
			
			if(!studentWithUnreturnedBorrowedMaterials.isEmpty()) {
				toReturn.put(grade, studentWithUnreturnedBorrowedMaterials);
			}
		}
		
		returnListStudent.set(toReturn);
	}
	
	@HandlesAction(SetBorrowedMaterialsReturned.class)
	public void setBorrowedMaterialsReturned(Collection<BorrowedMaterial> borrowedMaterials) {
		for (BorrowedMaterial borrowedMaterial : borrowedMaterials) {
			borrowedMaterial.setReturnDate(new Date());
			daoBorrowedMaterial.update(borrowedMaterial);
		}
		
		updateReturnList();
	}
	
	private void updateReturnList() {
		generateStudentReturnList();
	}
}
