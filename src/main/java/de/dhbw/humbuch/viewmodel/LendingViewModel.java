package de.dhbw.humbuch.viewmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

public class LendingViewModel {

	public interface GenerateMaterialListGrades extends ActionHandler {};
	public interface SetBorrowedMaterialsReceived extends ActionHandler {};
	
	public interface StudentsWithUnreceivedBorrowedMaterials extends State<Map<Grade, List<Student>>> {};
	public interface MaterialListGrades extends State<Map<Grade, Map<TeachingMaterial, Integer>>> {};
	
	@Deprecated
	public interface Students extends State<Collection<Student>> {};
	@Deprecated
	public interface Grades extends State<Collection<Grade>> {};

	@ProvidesState(StudentsWithUnreceivedBorrowedMaterials.class)
	public State<Map<Grade, List<Student>>> studentsWithUnreceivedBorrowedMaterials = new BasicState<>(Map.class);
	
	@ProvidesState(MaterialListGrades.class)
	public State<Map<Grade, Map<TeachingMaterial, Integer>>> materialListGrades = new BasicState<>(Map.class);

	private DAO<Grade> daoGrade;
	private DAO<Student> daoStudent;
	private DAO<TeachingMaterial> daoTeachingMaterial;
	private DAO<BorrowedMaterial> daoBorrowedMaterial; 
	
	@Inject
	public LendingViewModel(DAO<Student> daoStudent, DAO<TeachingMaterial> daoTeachingMaterial, DAO<Grade> daoGrade, DAO<BorrowedMaterial> daoBorrowedMaterial) {
		this.daoStudent = daoStudent;
		this.daoTeachingMaterial = daoTeachingMaterial;
		this.daoGrade = daoGrade;
		this.daoBorrowedMaterial = daoBorrowedMaterial;
	}
	
	@AfterVMBinding
	private void afterVMBinding() {
		updateAllStudentsBorrowedMaterials();
	}
	
	@HandlesAction(GenerateMaterialListGrades.class)
	public void generateMaterialListGrades(Set<Grade> selectedGrades) {
		Map<Grade, Map<TeachingMaterial, Integer>> materialList = new LinkedHashMap<Grade, Map<TeachingMaterial, Integer>>();
		
		for (Grade grade : selectedGrades) {
			Map<TeachingMaterial, Integer> gradeMap = new LinkedHashMap<TeachingMaterial, Integer>();
			
			for(Student student : grade.getStudents()) {
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

	private void updateAllStudentsBorrowedMaterials() {
		for(Student student : daoStudent.findAll()) {
			persistBorrowedMaterials(student, getNewTeachingMaterials(student));
			updateUnreceivedBorrowedMaterialsState();
		}
	}

	private void updateUnreceivedBorrowedMaterialsState() {
		Map<Grade, List<Student>> unreceivedMap = new HashMap<Grade, List<Student>>();
		
		for (Grade grade : daoGrade.findAll()) {
			List<Student> studentsWithUnreceivedBorrowedMaterials = new ArrayList<Student>();
			
			for (Student student : grade.getStudents()) {
				if(student.hasUnreceivedBorrowedMaterials()) {
					studentsWithUnreceivedBorrowedMaterials.add(student);
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
						, Restrictions.or(
								Restrictions.ge("validUntil", new Date())
								, Restrictions.isNull("validUntil")
						)
				));
		//TODO: restrictions with TM's term

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
	
	private List<TeachingMaterial> getOwningTeachingMaterials(Student student) {
		List<TeachingMaterial> owning = new ArrayList<TeachingMaterial>();
		for(BorrowedMaterial borrowedMaterial : student.getBorrowedList()) {
			owning.add(borrowedMaterial.getTeachingMaterial());
		}
		return owning;
	}
}

