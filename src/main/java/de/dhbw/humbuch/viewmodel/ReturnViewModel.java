package de.dhbw.humbuch.viewmodel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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

/**
 * Provides {@link State}s and methods for returning {@link BorrowedMaterial}s
 * 
 * @author David Vitt
 *
 */
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
	public void initialiseStates() {
		returnListStudent.set(new HashMap<Grade, Map<Student, List<BorrowedMaterial>>>());
	}
	
	public void refresh() {
		updateSchoolYear();
		generateStudentReturnList();
	}
	
	/**
	 * Generates "list" of all {@link BorrowedMaterial}s that have to returned by a {@link Student}.<br>
	 * the "list" is returned as {@code Map<Grade, Map<Student, List<BorrowedMaterial>>>} in the state {@link ReturnListStudent}
	 * 
	 */
	@HandlesAction(GenerateStudentReturnList.class)
	public void generateStudentReturnList() {
		Date today = new Date();
		Term recentlyActiveTerm = recentlyActiveSchoolYear.getRecentlyActiveTerm();
		boolean isAfterCurrentTerm = recentlyActiveSchoolYear.getEndOf(recentlyActiveTerm).before(today);

		Map<Grade, Map<Student, List<BorrowedMaterial>>> toReturn = new TreeMap<>();
		for(Grade gradeEntity : daoGrade.findAll()) {
			Map<Student, List<BorrowedMaterial>> studentWithUnreturnedBorrowedMaterials = new TreeMap<>();

			for(Student student : gradeEntity.getStudents()) {
				int studentsGrade = student.getGrade().getGrade();
				List<BorrowedMaterial> unreturnedBorrowedMaterials = new ArrayList<>();
				for(BorrowedMaterial borrowedMaterial : student.getReceivedBorrowedMaterials()) {
					TeachingMaterial teachingMaterial = borrowedMaterial.getTeachingMaterial();
					Date borrowUntilDate = borrowedMaterial.getBorrowUntil();

					boolean notNeededNextTerm = borrowedMaterial.getReturnDate() == null && !isNeededNextTerm(borrowedMaterial);
					boolean borrowUntilExceeded = borrowUntilDate == null ? false : borrowUntilDate.before(today);
					boolean isManualLended = borrowUntilDate == null ? false : true;
					boolean toTermEqualsRecentlyActiceTerm = teachingMaterial.getToGrade() == studentsGrade	
							&& teachingMaterial.getToTerm() == recentlyActiveTerm;

					if(!isManualLended	&& notNeededNextTerm && (toTermEqualsRecentlyActiceTerm ? isAfterCurrentTerm : true)) {
						unreturnedBorrowedMaterials.add(borrowedMaterial);
					} else if (!borrowedMaterial.isReturned() && borrowUntilExceeded) {
						unreturnedBorrowedMaterials.add(borrowedMaterial);
					}
				}

				if(!unreturnedBorrowedMaterials.isEmpty()) {
					Collections.sort(unreturnedBorrowedMaterials);
					studentWithUnreturnedBorrowedMaterials.put(student,	unreturnedBorrowedMaterials);
				}
			}

			if(!studentWithUnreturnedBorrowedMaterials.isEmpty()) {
				toReturn.put(gradeEntity, studentWithUnreturnedBorrowedMaterials);
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
		Date today = new Date();
		for (BorrowedMaterial borrowedMaterial : borrowedMaterials) {
			borrowedMaterial.setReturnDate(today);
		}
		
		daoBorrowedMaterial.update(borrowedMaterials);
		generateStudentReturnList();
	}
	
	@HandlesAction(RefreshStudents.class)
	public void refreshStudents() {
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

	/**
	 * Updates the recently actice {@link SchoolYear}
	 */
	private void updateSchoolYear() {
		recentlyActiveSchoolYear = daoSchoolYear.findSingleWithCriteria(
				Order.desc("toDate"),
				Restrictions.le("fromDate", new Date()));
		if(recentlyActiveSchoolYear == null) {
			recentlyActiveSchoolYear = new SchoolYear.Builder(
					"now", getDate(Calendar.AUGUST, 1), getDate(Calendar.JUNE, 31))
			.endFirstTerm(getDate(Calendar.JANUARY, 31))
			.beginSecondTerm(getDate(Calendar.FEBRUARY, 1))
			.build();
		}
	}
	
	/**
	 * Returns {@link Date} object with the current year and the given month and day.
	 * 
	 * @param month
	 * @param day
	 * @return {@link Date} object with given information
	 */
	private Date getDate(int month, int day) {
		Calendar  calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), month, day);
		return calendar.getTime();
	}
}
