package de.dhbw.humbuch.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.hibernate.criterion.Restrictions;

import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Parent;
import de.dhbw.humbuch.model.entity.Student;


public class StudentPersistenceHandler {
	
	public static void persistStudents(ArrayList<Student> students, DAO<Student> daoStudent, 
			DAO<Grade> daoGrade, DAO<Parent> daoParent){
		
		Iterator<Student> studentIterator = students.iterator();
		int gradeId = 0;
		
		while(studentIterator.hasNext()){
			Student student = studentIterator.next();

			Collection<Grade> grades = daoGrade.findAllWithCriteria(
					Restrictions.and(
							Restrictions.like("grade", student.getGrade().getGrade()),
							Restrictions.like("suffix", student.getGrade().getSuffix())
					));
			
			Collection<Grade> allGrades = daoGrade.findAll();
			Iterator<Grade> allGradesIterator = allGrades.iterator();
			while(allGradesIterator.hasNext()){
				Grade grade = allGradesIterator.next();
				gradeId = grade.getId() + 1;
			}
						
			//if grades is not empty (size != 0), use the already existing grade for student
			if(grades.size() == 0){
				System.out.println("Here!1");
				
				Grade grade = student.getGrade();
				grade.setId(gradeId);
				daoGrade.insert(grade);

				Collection<Grade> justInsertedGrades = daoGrade.findAllWithCriteria(
						Restrictions.and(
								Restrictions.like("grade", student.getGrade().getGrade()),
								Restrictions.like("suffix", student.getGrade().getSuffix())
						));
				
				Iterator<Grade> justInsertedGradesIterator = justInsertedGrades.iterator();

				grade = justInsertedGradesIterator.next();
				student.setGrade(grade);
			}
			else if(grades.size() == 1){
				Iterator<Grade> gradesIterator = grades.iterator();
				Grade grade = gradesIterator.next();		
				student.setGrade(grade);
			}
				
			Parent parent = daoParent.find(1);
			student.setParent(parent);
			student.setProfile(null);
			daoStudent.insert(student);		
		}		
	}
}
