package de.dhbw.humbuch.viewmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.criterion.Restrictions;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.AfterVMBinding;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.event.ImportSuccessEvent;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.SchoolYear.Term;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

public class ReturnViewModel {
	
	public interface GenerateStudentReturnList extends ActionHandler {}
	public interface SetBorrowedMaterialsReturned extends ActionHandler {}
	
	public interface ReturnListStudent extends State<Map<Grade, Map<Student, List<BorrowedMaterial>>>> {}
	
	@ProvidesState(ReturnListStudent.class)
	public State<Map<Grade, Map<Student, List<BorrowedMaterial>>>> returnListStudent = new BasicState<>(Map.class);
	
	private DAO<Grade> daoGrade;
	private DAO<BorrowedMaterial> daoBorrowedMaterial;
	private DAO<SchoolYear> daoSchoolYear;
	
	private SchoolYear currentSchoolYear;
	
	@Inject
	public ReturnViewModel(DAO<Grade> daoGrade, DAO<BorrowedMaterial> daoBorrowedMaterial, DAO<SchoolYear> daoSchoolYear) {
		this.daoGrade = daoGrade;
		this.daoBorrowedMaterial = daoBorrowedMaterial;
		this.daoSchoolYear = daoSchoolYear;
	}
	
	@AfterVMBinding
	private void afterVMBinding() {
		updateSchoolYear();
		updateReturnList();
	}
	
	@HandlesAction(GenerateStudentReturnList.class)
	public void generateStudentReturnList() {
		Map<Grade, Map<Student, List<BorrowedMaterial>>> toReturn = new TreeMap<Grade, Map<Student,List<BorrowedMaterial>>>();
		
		for(Grade grade : daoGrade.findAll()) {
			Map<Student, List<BorrowedMaterial>> studentWithUnreturnedBorrowedMaterials = new TreeMap<Student, List<BorrowedMaterial>>();
			
			for(Student student : grade.getStudents()) {

				List<BorrowedMaterial> unreturnedBorrowedMaterials = new ArrayList<BorrowedMaterial>();
				for (BorrowedMaterial borrowedMaterial : student.getBorrowedList()) {
					if(borrowedMaterial.isReceived() //book is received 
							&& borrowedMaterial.getReturnDate() == null //book hasn't returned yet
							&& !isNeededNextTerm(borrowedMaterial)) { //book isn't needed next term
						unreturnedBorrowedMaterials.add(borrowedMaterial);
					}
				}
				
				if(!unreturnedBorrowedMaterials.isEmpty()) {
					Collections.sort(unreturnedBorrowedMaterials);
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
	
	private boolean isNeededNextTerm(BorrowedMaterial borrowedMaterial) {
		TeachingMaterial teachingMaterial = borrowedMaterial.getTeachingMaterial();

		int toGrade = teachingMaterial.getToGrade();
		int currentGrade = borrowedMaterial.getStudent().getGrade().getGrade();
		Term toTerm = teachingMaterial.getToTerm();
		Term currentTerm = currentSchoolYear.getCurrentTerm();
		
		return (toGrade > currentGrade || (toGrade == currentGrade && (toTerm.compareTo(currentTerm) > 0)));
	}

	private void updateReturnList() {
		generateStudentReturnList();
	}
	
	private void updateSchoolYear() {
		currentSchoolYear = daoSchoolYear.findSingleWithCriteria(
				Restrictions.le("fromDate", new Date()), 
				Restrictions.ge("toDate", new Date()));
	}
	
	@Subscribe
	public void handleImportEvent(ImportSuccessEvent importSuccessEvent) {
		generateStudentReturnList();
	}
}
