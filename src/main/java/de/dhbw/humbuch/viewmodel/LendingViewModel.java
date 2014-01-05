package de.dhbw.humbuch.viewmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
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

	public interface GenerateStudentLendingList extends ActionHandler {}
	public interface GenerateGradeLendingList extends ActionHandler {};
	public interface GetStudentsBorrowedMaterial extends ActionHandler {};
	
	public interface LendingListStudent extends State<List<TeachingMaterial>> {};
	public interface LendingListGrades extends State<Map<Grade, Map<TeachingMaterial, Integer>>> {};
	public interface StudentBorrowedMaterials extends State<List<BorrowedMaterial>> {};
	public interface Students extends State<Collection<Student>> {};
	public interface Grades extends State<Collection<Grade>> {};

	@ProvidesState(LendingListStudent.class)
	public BasicState<List<TeachingMaterial>> lendingListStudent = new BasicState<>(List.class);
	
	@ProvidesState(LendingListGrades.class)
	public BasicState<Map<Grade, Map<TeachingMaterial, Integer>>> lendingListGrades = new BasicState<>(Map.class);

	@ProvidesState(StudentBorrowedMaterials.class)
	public BasicState<List<BorrowedMaterial>> studentBorrowedMaterials = new BasicState<>(List.class);
	
	@ProvidesState(Students.class)
	public BasicState<Collection<Student>> students = new BasicState<>(Collection.class);
	
	@ProvidesState(Grades.class)
	public BasicState<Collection<Grade>> grades = new BasicState<>(Collection.class);
	
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
		updateStudents();
		updateGrades();
	}
	
	private void updateStudents() {
		students.set(daoStudent.findAll());
	}
	
	private void updateGrades() {
		grades.set(daoGrade.findAll());
	}
	
	@HandlesAction(GenerateStudentLendingList.class)
	public void generateStudentLendingList(String studentId) {
		Student student = daoStudent.find(Integer.parseInt(studentId));
		
		List<TeachingMaterial> studentLendingList = getStudentLendingList(student);
		
		persistBorrowedMaterials(student, studentLendingList);
		lendingListStudent.set(studentLendingList);
	}
	
	@HandlesAction(GenerateGradeLendingList.class)
	public void generateGradeLendingList(State<Set<Grade>> selectedGrades) {
		Map<Grade, Map<TeachingMaterial, Integer>> toLend = new LinkedHashMap<Grade, Map<TeachingMaterial, Integer>>();
		
		for (Grade grade : selectedGrades.get()) {
			Map<TeachingMaterial, Integer> gradeList = new LinkedHashMap<TeachingMaterial, Integer>();
			
			for(Student student : grade.getStudents()) {
				List<TeachingMaterial> studentLendingList = getStudentLendingList(student);
				
				for (TeachingMaterial teachingMaterial : studentLendingList) {
					if(gradeList.containsKey(teachingMaterial)) {
						gradeList.put(teachingMaterial, gradeList.get(teachingMaterial) + 1);
					} else {
						gradeList.put(teachingMaterial, 1);
					}
				}
			}
			
			toLend.put(grade, gradeList);
		}
		
		//TEST output
		/*for(Grade grade : toLend.keySet()) {
			System.out.println("GRADE: " + grade.getGrade() + grade.getSuffix());
			Map<TeachingMaterial, Integer> map = toLend.get(grade);
			for(TeachingMaterial teachingMaterial : map.keySet()) {
				System.out.print("      " + teachingMaterial.getName() + ": ");
				Integer count = map.get(teachingMaterial);
				System.out.println(count + "x");
			}
		}*/
		
		lendingListGrades.set(toLend);
	}
	
	@HandlesAction(GetStudentsBorrowedMaterial.class)
	public void getStudentBorrowedMaterials(String studentId) {
//		Student student = daoStudent.find(Integer.parseInt(studentId));
//		List<BorrowedMaterial> borrowedList = student.getBorrowedList();
//		for (BorrowedMaterial borrowedMaterial : borrowedList) {
//			if(borrowedMaterial.isReceived() == true) {
//				borrowedList.remove(borrowedMaterial);
//			}
//		}
		
		
		List<BorrowedMaterial> borrowedList = (List<BorrowedMaterial>) daoBorrowedMaterial.findAllWithCriteria(
				Restrictions.eq("studentId", Integer.parseInt(studentId))
				, Restrictions.eq("received", false));
		studentBorrowedMaterials.set(borrowedList);
	}

	private List<TeachingMaterial> getStudentLendingList(Student student) {
		
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

