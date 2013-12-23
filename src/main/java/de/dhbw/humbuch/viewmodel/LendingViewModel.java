package de.dhbw.humbuch.viewmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

public class LendingViewModel {
	public interface GenerateStudentLendingList extends ActionHandler {}
//	public interface GenerateGradeLendingList extends ActionHandler {};
	
	public interface LendingListStudent extends State<List<TeachingMaterial>> {};
//	public interface LendingListGrades extends State<Map<Grade, Map<TeachingMaterial, Integer>>> {};
	
	@ProvidesState(LendingListStudent.class)
	public BasicState<List<TeachingMaterial>> lendingListStudent = new BasicState<>(List.class);
	
//	@ProvidesState(LendingListGrades.class)
//	public BasicState<Map<Grade, Map<TeachingMaterial, Integer>>> lendingListGrades = new BasicState<>(Map.class);

	@Inject
	private DAO<Student> daoStudent;
	
	@Inject
	private DAO<TeachingMaterial> daoTeachingMaterial;
	
	@HandlesAction(GenerateStudentLendingList.class)
	public void generateStudentLendingList(String studentId) {
		lendingListStudent.set(getStudentLendingList(Integer.parseInt(studentId)));
	}
	
	
//	@Inject
//	DAO<Grade> daoGrades;
//	//for the whole class TODO: add List parameter
//	//TODO: refactor both getLending methods --> same name, different parameter	
//	@HandlesAction(GenerateGradeLendingList.class)
//	public void generateGradeLendingList(String string) {
//		Collection<Grade> grades = daoGrades.findAll();
//
//		Map<Grade, Map<TeachingMaterial, Integer>> toLend = new LinkedHashMap<Grade, Map<TeachingMaterial, Integer>>();
//		
//		for (Grade grade : grades) {
//			Map<TeachingMaterial, Integer> gradeList = new LinkedHashMap<TeachingMaterial, Integer>();
//			
//			for(Student student : grade.getStudents()) {
//				List<TeachingMaterial> studentLendingList = getStudentLendingList(student.getId());
//				
//				for (TeachingMaterial teachingMaterial : studentLendingList) {
//					if(gradeList.containsKey(teachingMaterial)) {
//						gradeList.put(teachingMaterial, gradeList.get(teachingMaterial) + 1);
//					} else {
//						gradeList.put(teachingMaterial, 1);
//					}
//				}
//			}
//			
//			toLend.put(grade, gradeList);
//		}
//		
//		//TEST output
//		for(Grade grade : toLend.keySet()) {
//			System.out.println("GRADE: " + grade.getGrade() + grade.getSuffix());
//			Map<TeachingMaterial, Integer> map = toLend.get(grade);
//			for(TeachingMaterial teachingMaterial : map.keySet()) {
//				System.out.print("      " + teachingMaterial.getName() + ": ");
//				Integer count = map.get(teachingMaterial);
//				System.out.println(count + "x");
//			}
//		}
//		
//		lendingListGrades.set(toLend);
//	}

	private List<TeachingMaterial> getStudentLendingList(int studentId) {
		Student student = daoStudent.find(studentId);
		
		Collection<TeachingMaterial> teachingMerterials = daoTeachingMaterial.findAllWithCriteria(
				Restrictions.or(
						Restrictions.and(
								Restrictions.le("fromGrade", student.getGrade().getGrade())
								, Restrictions.ge("toGrade", student.getGrade().getGrade())
								, Restrictions.le("validFrom", new Date())
								, Restrictions.ge("validUntil", new Date())
								)
								, Restrictions.isNull("validUntil")
						));
		//TODO: restrictions with TM's term
		
		List<TeachingMaterial> toLend = new ArrayList<TeachingMaterial>();
		
		for(TeachingMaterial teachingMaterial : teachingMerterials) {
			if(student.getProfile().containsAll(teachingMaterial.getProfile())) {
				toLend.add(teachingMaterial);
			}
		}
		
		lendingListStudent.set(toLend);
		return toLend;
	}
}

