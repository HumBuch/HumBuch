package de.dhbw.humbuch.viewmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

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
	
	private SchoolYear currentSchoolYear;
	
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
	private void afterVMBinding() {
		updateSchoolYear();
		updateTeachingMaterials();
		updateAllStudentsBorrowedMaterials();
	}
	
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
	
	@HandlesAction(SetBorrowedMaterialsReceived.class)
	public void setBorrowedMaterialsReceived(Collection<BorrowedMaterial> borrowedMaterials) {
		for (BorrowedMaterial borrowedMaterial : borrowedMaterials) {
			borrowedMaterial.setReceived(true);
			daoBorrowedMaterial.update(borrowedMaterial);
		}
		
		updateAllStudentsBorrowedMaterials();
	}
	
	@HandlesAction(DoManualLending.class)
	public void doManualLending(Student student, TeachingMaterial toLend, Date borrowUntil) {
		persistBorrowedMaterial(student, toLend, borrowUntil);
		updateAllStudentsBorrowedMaterials();
	}
	
	private void updateAllStudentsBorrowedMaterials() {
		for(Student student : daoStudent.findAllWithCriteria(Restrictions.eq("leavingSchool", false))) {
			persistBorrowedMaterials(student, getNewTeachingMaterials(student));
		}

		updateUnreceivedBorrowedMaterialsState();
	}

	private void updateTeachingMaterials() {
		teachingMaterials.set(daoTeachingMaterial.findAll());
	}
	
	private void updateSchoolYear() {
		Date today = new Date();
		currentSchoolYear = daoSchoolYear.findSingleWithCriteria(
				Restrictions.le("fromDate", today), 
				Restrictions.ge("toDate", today));
	}
	
	private void updateUnreceivedBorrowedMaterialsState() {
		Map<Grade, Map<Student, List<BorrowedMaterial>>> unreceivedMap = new TreeMap<Grade, Map<Student, List<BorrowedMaterial>>>();
		
		for (Grade grade : daoGrade.findAll()) {
			Map<Student, List<BorrowedMaterial>> studentsWithUnreceivedBorrowedMaterials = new TreeMap<Student, List<BorrowedMaterial>>();
			
			for (Student student : grade.getStudents()) {
				if(student.hasUnreceivedBorrowedMaterials()) {
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

	private List<TeachingMaterial> getNewTeachingMaterials(Student student) {
		Collection<TeachingMaterial> teachingMerterials = daoTeachingMaterial.findAllWithCriteria(
				Restrictions.and(
						Restrictions.le("fromGrade", student.getGrade().getGrade())
						, Restrictions.ge("toGrade", student.getGrade().getGrade())
						, Restrictions.le("validFrom", new Date())
						, Restrictions.le("fromTerm", currentSchoolYear.getCurrentTerm())
						, Restrictions.or(
								Restrictions.ge("validUntil", new Date())
								, Restrictions.isNull("validUntil"))
				));

		List<TeachingMaterial> owningTeachingMaterials = getOwningTeachingMaterials(student);
		List<TeachingMaterial> toLend = new ArrayList<TeachingMaterial>();

		for(TeachingMaterial teachingMaterial : teachingMerterials) {
			if(student.getProfile().containsAll(teachingMaterial.getProfile())
					&& !owningTeachingMaterials.contains(teachingMaterial)) {
				toLend.add(teachingMaterial);
			}
		}

		return toLend;
	}
	
	private void persistBorrowedMaterials(Student student, List<TeachingMaterial> teachingMaterials) {
		for (TeachingMaterial teachingMaterial : teachingMaterials) {
			BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(student, teachingMaterial, new Date()).build();
			daoBorrowedMaterial.insert(borrowedMaterial);
		}
	}
	
	private void persistBorrowedMaterial(Student student, TeachingMaterial teachingMaterial, Date borrowUntil) {
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(student, teachingMaterial, new Date()).borrowUntil(borrowUntil).build();
		daoBorrowedMaterial.insert(borrowedMaterial);
	}
	
	private List<TeachingMaterial> getOwningTeachingMaterials(Student student) {
		List<TeachingMaterial> owning = new ArrayList<TeachingMaterial>();
		for(BorrowedMaterial borrowedMaterial : student.getBorrowedList()) {
			owning.add(borrowedMaterial.getTeachingMaterial());
		}
		
		return owning;
	}
	
	@Subscribe
	public void handleImportEvent(ImportSuccessEvent importSuccessEvent) {
		updateAllStudentsBorrowedMaterials();
	}
}

