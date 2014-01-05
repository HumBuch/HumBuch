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
		
		while(studentIterator.hasNext()){
			Student student = studentIterator.next();
			
			Student persistedStudent = daoStudent.find(student.getId());
			
			Collection<Grade> grades = daoGrade.findAllWithCriteria(
					Restrictions.and(
							Restrictions.like("grade", student.getGrade().getGrade()),
							Restrictions.like("suffix", student.getGrade().getSuffix())
					));

			if(grades.size() == 1){
				Iterator<Grade> gradesIterator = grades.iterator();
				Grade grade = gradesIterator.next();		
				student.setGrade(grade);
			}
			
			if(persistedStudent == null){			
				
	//			Collection<Parent> parents = daoParent.findAllWithCriteria(
	//					Restrictions.and(
	//							Restrictions.like("title", student.getParent().getTitle()),
	//							Restrictions.like("firstname", student.getParent().getFirstname()),
	//							Restrictions.like("lastname", student.getParent().getLastname()),
	//							Restrictions.like("street", student.getParent().getStreet()),
	//							Restrictions.eq("postcode", student.getParent().getPostcode()),
	//							Restrictions.like("city", student.getParent().getCity())
	//					));
	//
	//			if(parents.size() == 1){
	//				Iterator<Parent> parentsIterator = parents.iterator();
	//				Parent parent = parentsIterator.next();		
	//				student.setParent(parent);
	//			}
	//			
				Parent parent = daoParent.find(1);
				student.setParent(parent);
				daoStudent.insert(student);	
			}
			else{
				daoStudent.update(student);
			}
		}		
	}
}
