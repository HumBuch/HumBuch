package de.dhbw.humbuch.viewmodel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

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
import de.dhbw.humbuch.model.entity.Subject;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

/**
 * Provides {@link State}s and methods for automatic and manual lending of {@link TeachingMaterial}s.
 * 
 * @author David Vitt
 *
 */
public class LendingViewModel {

	public interface GenerateMaterialListGrades extends ActionHandler {};
	public interface SetBorrowedMaterialsReceived extends ActionHandler {};
	public interface DoManualLending extends ActionHandler {};
	
	public interface StudentsWithUnreceivedBorrowedMaterials extends State<Map<Grade, Map<Student, List<BorrowedMaterial>>>> {};
	public interface MaterialListGrades extends State<Map<Grade, Map<TeachingMaterial, Integer>>> {};
	public interface TeachingMaterials extends State<Collection<TeachingMaterial>> {};

	@ProvidesState(StudentsWithUnreceivedBorrowedMaterials.class)
	public State<Map<Grade, Map<Student, List<BorrowedMaterial>>>> studentsWithUnreceivedBorrowedMaterials = new BasicState<>(Map.class);
	
	@ProvidesState(MaterialListGrades.class)
	public State<Map<Grade, Map<TeachingMaterial, Integer>>> materialListGrades = new BasicState<>(Map.class);

	@ProvidesState(TeachingMaterials.class)
	public State<Collection<TeachingMaterial>> teachingMaterials = new BasicState<>(Collection.class);
	
	private DAO<Grade> daoGrade;
	private DAO<Student> daoStudent;
	private DAO<TeachingMaterial> daoTeachingMaterial;
	private DAO<BorrowedMaterial> daoBorrowedMaterial;
	private DAO<SchoolYear> daoSchoolYear; 
	
	private SchoolYear recentlyActiveSchoolYear;
	
	/**
	 * Constructor
	 * 
	 * @param daoStudent
	 * @param daoTeachingMaterial
	 * @param daoGrade
	 * @param daoBorrowedMaterial
	 * @param daoSchoolYear
	 */
	@Inject
	public LendingViewModel(DAO<Student> daoStudent, DAO<TeachingMaterial> daoTeachingMaterial, DAO<Grade> daoGrade, 
			DAO<BorrowedMaterial> daoBorrowedMaterial, DAO<SchoolYear> daoSchoolYear) {
		this.daoStudent = daoStudent;
		this.daoTeachingMaterial = daoTeachingMaterial;
		this.daoGrade = daoGrade;
		this.daoBorrowedMaterial = daoBorrowedMaterial;
		this.daoSchoolYear = daoSchoolYear;
	}
	
	@AfterVMBinding
	public void initialiseStates() {
		studentsWithUnreceivedBorrowedMaterials.set(new HashMap<Grade, Map<Student, List<BorrowedMaterial>>>());
		materialListGrades.set(new HashMap<Grade, Map<TeachingMaterial, Integer>>());
		teachingMaterials.set(new ArrayList<TeachingMaterial>());
	}
	
	public void refresh() {
		updateSchoolYear();
		updateTeachingMaterials();	
		updateAllStudentsBorrowedMaterials();
	}
	
	/**
	 * Generates "list" of all {@link TeachingMaterial}s required by the given {@link Grade}s.<br>
	 * The "list" is returned as {@code Map<Grade, Map<TeachingMaterial, Integer>>} in the state {@link MaterialListGrades}
	 * 
	 * @param selectedGrades {@link Set} of {@link Grade}s
	 */
	@HandlesAction(GenerateMaterialListGrades.class)
	public void generateMaterialListGrades(Set<Grade> selectedGrades) {
		Map<Grade, Map<TeachingMaterial, Integer>> materialList = new TreeMap<Grade, Map<TeachingMaterial, Integer>>();
		
		for (Grade grade : selectedGrades) {
			Map<TeachingMaterial, Integer> gradeMap = new TreeMap<TeachingMaterial, Integer>();
			
			for(Student student : daoStudent.findAllWithCriteria(Restrictions.eq("leavingSchool", false), Restrictions.eq("grade", grade))) {
				for(BorrowedMaterial borrowedMaterial : student.getUnreceivedBorrowedList()) {
					TeachingMaterial teachingMaterial = borrowedMaterial.getTeachingMaterial();
					if(gradeMap.containsKey(teachingMaterial)) {
						gradeMap.put(teachingMaterial, gradeMap.get(teachingMaterial) + 1);
					} else {
						gradeMap.put(teachingMaterial, 1);
					}
				}
			}
			
			materialList.put(grade, gradeMap);
		}
		
		materialListGrades.set(materialList);
	}
	
	/**
	 * Marks the given {@link BorrowedMaterial}s as {@code received}.
	 * 
	 * @param borrowedMaterials {@link Collection} of {@link BorrowedMaterial}s that should be marked {@code received} 
	 */
	@HandlesAction(SetBorrowedMaterialsReceived.class)
	public void setBorrowedMaterialsReceived(Collection<BorrowedMaterial> borrowedMaterials) {
		for (BorrowedMaterial borrowedMaterial : borrowedMaterials) {
			borrowedMaterial.setReceived(true);
		}
		daoBorrowedMaterial.update(borrowedMaterials);
		
		updateAllStudentsBorrowedMaterials();
	}
	
	/**
	 * Lends the given {@link TeachingMaterial} for the given {@link Student} until the given {@link Date}.
	 * 
	 * @param student {@link Student} to lend this...
	 * @param toLend {@link TeachingMaterial} ...
	 * @param borrowUntil until this {@link Date}
	 */
	@HandlesAction(DoManualLending.class)
	public void doManualLending(Student student, TeachingMaterial toLend, Date borrowUntil) {
		persistBorrowedMaterial(student, toLend, borrowUntil);
		updateAllStudentsBorrowedMaterials();
	}
	
	/**
	 * Updates the list of {@link BorrowedMaterial}s for all {@link Student}s
	 */
	private void updateAllStudentsBorrowedMaterials() {
		persistBorrowedMaterials(getNewTeachingMaterials(daoGrade.findAll()));
		updateUnreceivedBorrowedMaterialsState();
	}

	/**
	 * Updates the {@link State} {@link TeachingMaterials}
	 */
	private void updateTeachingMaterials() {
		teachingMaterials.set(daoTeachingMaterial.findAll());
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
	
	/**
	 * Updates the {@link State} of all {@link Student}s unreceived {@link BorrowedMaterial}s.<br>
	 * Returned as {@code Map<Grade, Map<Student, List<BorrowedMaterial>>>} in {@link StudentsWithUnreceivedBorrowedMaterials}
	 * 
	 */
	private void updateUnreceivedBorrowedMaterialsState() {
		Map<Grade, Map<Student, List<BorrowedMaterial>>> unreceivedMap = new TreeMap<Grade, Map<Student, List<BorrowedMaterial>>>();
		
		for (Grade grade : daoGrade.findAll()) {
			Map<Student, List<BorrowedMaterial>> studentsWithUnreceivedBorrowedMaterials = new TreeMap<Student, List<BorrowedMaterial>>();
			for (Student student : grade.getStudents()) {
				if (student.hasUnreceivedBorrowedMaterials()) {
					List<BorrowedMaterial> unreceivedBorrowedList = student.getUnreceivedBorrowedList();
					Collections.sort(unreceivedBorrowedList);
					studentsWithUnreceivedBorrowedMaterials.put(student, unreceivedBorrowedList);
				}
			}
			
			if(!studentsWithUnreceivedBorrowedMaterials.isEmpty()) {
				unreceivedMap.put(grade, studentsWithUnreceivedBorrowedMaterials);
			}
		}

		studentsWithUnreceivedBorrowedMaterials.set(unreceivedMap);
	}
	
	/**
	 * Returns map of {@link TeachingMaterial}s that have to be lended by the {@link Student}s of the given {@link Grade}s.
	 * 
	 * @param grades
	 * @return {@link Map} of {@link Student}s and their {@link TeachingMaterial}s that have to be lended
	 */
	private Map<Student, List<TeachingMaterial>> getNewTeachingMaterials(List<Grade> grades) {
		Map<Student, List<TeachingMaterial>> studentsNewTeachingMaterialMap = new HashMap<Student, List<TeachingMaterial>>();

		Date today = new Date();
		
		//No TeachingMaterials to lend when not in an active SchoolYear
		if(recentlyActiveSchoolYear.getToDate().before(today)) {
			return studentsNewTeachingMaterialMap;
		}
		
		Term recentlyActiveTerm = recentlyActiveSchoolYear.getRecentlyActiveTerm();
		
		SimpleExpression restrictionValidFrom = Restrictions.le("validFrom", today);
		SimpleExpression restrictionFromTerm = Restrictions.le("fromTerm", recentlyActiveTerm);
		SimpleExpression restrictionToTerm = Restrictions.ge("toTerm", recentlyActiveTerm);
		LogicalExpression restrictionValidUntil = Restrictions.or(
				Restrictions.ge("validUntil", today), 
				Restrictions.isNull("validUntil"));
		
		for (Grade gradeEntity : grades) {
			int grade = gradeEntity.getGrade();
			Collection<TeachingMaterial> teachingMaterials = daoTeachingMaterial.findAllWithCriteria(
					Restrictions.and(
							Restrictions.le("fromGrade", grade)
							, Restrictions.ge("toGrade", grade)
							, restrictionValidFrom
							, restrictionFromTerm
							, restrictionToTerm
							, restrictionValidUntil
					));
			
			for (Student student : gradeEntity.getStudents()) {
				if (!student.isLeavingSchool()) {
					List<TeachingMaterial> teachingMaterialsOfStudent = new ArrayList<>(student.getTeachingMaterials());
					List<TeachingMaterial> toLend = new ArrayList<TeachingMaterial>();

					Set<Subject> studentsProfile = student.getProfile();
					for (TeachingMaterial teachingMaterial : teachingMaterials) {
						if (studentsProfile.containsAll(teachingMaterial.getProfile())
								&& !teachingMaterialsOfStudent.contains(teachingMaterial)) {
							toLend.add(teachingMaterial);
						}
					}
					
					studentsNewTeachingMaterialMap.put(student, toLend);
				}
			}
		}
		
		return studentsNewTeachingMaterialMap;
	}
	
	/**
	 * Persists the {@link TeachingMaterial}s for the {@link Student}s as {@link BorrowedMaterial}s in the database.
	 * 
	 * @param newTeachingMaterials {@link Map} of {@link Student}s and their new {@link TeachingMaterial}s
	 */
	private void persistBorrowedMaterials(Map<Student, List<TeachingMaterial>> newTeachingMaterials) {
		Collection<BorrowedMaterial> borrowedMaterials = new ArrayList<>();
		for(Student student : newTeachingMaterials.keySet()) {
			borrowedMaterials.addAll(buildBorrowedMaterials(student, newTeachingMaterials.get(student)));
		}
		
		daoBorrowedMaterial.insert(borrowedMaterials);
	}
	
	/**
	 * Builds a {@link Collection} of {@link BorrowedMaterial}s
	 * 
	 * @param student
	 * @param teachingMaterials
	 * @return {@link Collection} of {@link BorrowedMaterial}s
	 */
	private Collection<BorrowedMaterial> buildBorrowedMaterials(Student student, List<TeachingMaterial> teachingMaterials) {
		Date borrowFrom = new Date();
		Collection<BorrowedMaterial> borrowedMaterials = new ArrayList<>();
		for (TeachingMaterial teachingMaterial : teachingMaterials) {
			borrowedMaterials.add(new BorrowedMaterial.Builder(student, teachingMaterial, borrowFrom).build());
		}

		return borrowedMaterials;
	}
	
	/**
	 * Persists a single {@link TeachingMaterial} as {@link BorrowedMaterial}.<br>
	 * Is needed for manual lending.
	 * 
	 * @param student
	 * @param teachingMaterial
	 * @param borrowUntil
	 */
	private void persistBorrowedMaterial(Student student, TeachingMaterial teachingMaterial, Date borrowUntil) {
		daoBorrowedMaterial.insert(new BorrowedMaterial.Builder(student, teachingMaterial, new Date()).borrowUntil(borrowUntil).build());
	}
}
