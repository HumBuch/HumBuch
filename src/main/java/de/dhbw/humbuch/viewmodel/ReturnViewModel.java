package de.dhbw.humbuch.viewmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

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
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.SchoolYear.Term;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

public class ReturnViewModel {
	
	public interface GenerateStudentReturnList extends ActionHandler {}
	public interface SetBorrowedMaterialsReturned extends ActionHandler {}
	public interface RefreshStudents extends ActionHandler {}
	
	public interface ReturnListStudent extends State<Map<Grade, Map<Student, List<BorrowedMaterial>>>> {}
	
	@ProvidesState(ReturnListStudent.class)
	public State<Map<Grade, Map<Student, List<BorrowedMaterial>>>> returnListStudent = new BasicState<>(Map.class);
	
	private DAO<Grade> daoGrade;
	private DAO<BorrowedMaterial> daoBorrowedMaterial;
	private DAO<SchoolYear> daoSchoolYear;
	private DAO<Student> daoStudents;
	
	private SchoolYear recentlyActiveSchoolYear;

	
	/**
	 * Constructor
	 * 
	 * @param daoGrade
	 * @param daoBorrowedMaterial
	 * @param daoSchoolYear
	 * @param daoStudents
	 */
	@Inject
	public ReturnViewModel(DAO<Grade> daoGrade, DAO<BorrowedMaterial> daoBorrowedMaterial, DAO<SchoolYear> daoSchoolYear, DAO<Student> daoStudents) {
		this.daoGrade = daoGrade;
		this.daoBorrowedMaterial = daoBorrowedMaterial;
		this.daoSchoolYear = daoSchoolYear;
		this.daoStudents = daoStudents;
	}
	
	@AfterVMBinding
	public void refresh() {
		updateSchoolYear();
		updateReturnList();
	}
	
	/**
	 * Generates "list" of all {@link BorrowedMaterial}s that have to returned by a {@link Student}.<br>
	 * the "list" is returned as {@code Map<Grade, Map<Student, List<BorrowedMaterial>>>} in the state {@link ReturnListStudent}
	 * 
	 */
	@HandlesAction(GenerateStudentReturnList.class)
	public void generateStudentReturnList() {
		Map<Grade, Map<Student, List<BorrowedMaterial>>> toReturn = new TreeMap<Grade, Map<Student,List<BorrowedMaterial>>>();
		
		for(Grade grade : daoGrade.findAll()) {
			Map<Student, List<BorrowedMaterial>> studentWithUnreturnedBorrowedMaterials = new TreeMap<Student, List<BorrowedMaterial>>();
			
			for(Student student : grade.getStudents()) {
				List<BorrowedMaterial> unreturnedBorrowedMaterials = new ArrayList<BorrowedMaterial>();
				for (BorrowedMaterial borrowedMaterial : student.getReceivedBorrowedMaterials()) {
					boolean isAfterCurrentTerm = recentlyActiveSchoolYear.getEndOf(recentlyActiveSchoolYear.getRecentlyActiveTerm()).before(new Date());
//					boolean notNeededNextTerm = borrowedMaterial.isReceived() && borrowedMaterial.getReturnDate() == null && !isNeededNextTerm(borrowedMaterial);
					boolean notNeededNextTerm = borrowedMaterial.getReturnDate() == null && !isNeededNextTerm(borrowedMaterial);
					boolean borrowUntilExceeded = borrowedMaterial.getBorrowUntil() == null ? false : borrowedMaterial.getBorrowUntil().before(new Date());
					boolean isManualLended = borrowedMaterial.getBorrowUntil() == null ? false : true;
					if(!isManualLended && isAfterCurrentTerm && notNeededNextTerm) {
						unreturnedBorrowedMaterials.add(borrowedMaterial);
					} else if (borrowedMaterial.getReturnDate() == null && borrowUntilExceeded) {
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
	
	/**
	 * Marks the given {@link BorrowedMaterial}s as {@code returned}
	 * 
	 * @param borrowedMaterials that should be marked as {@code returned}
	 */
	@HandlesAction(SetBorrowedMaterialsReturned.class)
	public void setBorrowedMaterialsReturned(Collection<BorrowedMaterial> borrowedMaterials) {
		for (BorrowedMaterial borrowedMaterial : borrowedMaterials) {
			borrowedMaterial.setReturnDate(new Date());
			daoBorrowedMaterial.update(borrowedMaterial);
		}
		
		updateReturnList();
	}
	
	@HandlesAction(RefreshStudents.class)
	private void refreshStudents() {
		daoStudents.findAll();
	}
	
	/**
	 * Checks if the given {@link BorrowedMaterial} is needed in the next {@link Term}.
	 * 
	 * @param borrowedMaterial
	 * @return <code>true</code> if needed, <code>false</code> otherwise
	 */
	private boolean isNeededNextTerm(BorrowedMaterial borrowedMaterial) {
		TeachingMaterial teachingMaterial = borrowedMaterial.getTeachingMaterial();

		Integer toGrade = teachingMaterial.getToGrade();
		int currentGrade = borrowedMaterial.getStudent().getGrade().getGrade();
		Term toTerm = teachingMaterial.getToTerm();
		Term currentTerm = recentlyActiveSchoolYear.getRecentlyActiveTerm();

		if(toGrade == null)
			return false;
					
		return (toGrade > currentGrade || (toGrade == currentGrade && (toTerm.compareTo(currentTerm) > 0)));
	}

	private void updateReturnList() {
		generateStudentReturnList();
	}
	
	/**
	 * Updates the recently actice {@link SchoolYear}
	 */
	private void updateSchoolYear() {
		recentlyActiveSchoolYear = daoSchoolYear.findSingleWithCriteria(
				Order.desc("toDate"),
				Restrictions.le("fromDate", new Date()));
	}
}
