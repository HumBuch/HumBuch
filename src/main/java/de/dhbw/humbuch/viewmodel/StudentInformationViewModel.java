package de.dhbw.humbuch.viewmodel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.AfterVMBinding;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.event.ImportSuccessEvent;
import de.dhbw.humbuch.event.MessageEvent;
import de.dhbw.humbuch.event.MessageEvent.Type;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Parent;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.util.CSVHandler;

public class StudentInformationViewModel {

	public interface Students extends State<Collection<Student>> {}
		
	public interface PersistStudents extends ActionHandler {}

	@ProvidesState(Students.class)
	public State<Collection<Student>> students = new BasicState<>(Collection.class);
	
	private EventBus eventBus;
	private DAO<Student> daoStudent;
	private DAO<Grade> daoGrade;
	private DAO<Parent> daoParent;

	/**
	 * Constructor
	 * 
	 * @param daoTeachingMaterial
	 *            DAO implementation to access TeachingMaterial entities
	 */
	@Inject
	public StudentInformationViewModel(DAO<Student> daoStudent, DAO<Grade> daoGrade, DAO<Parent> daoParent, EventBus eventBus) {
		this.daoStudent = daoStudent;
		this.daoGrade = daoGrade;
		this.daoParent = daoParent;
		this.eventBus = eventBus;
	}
	
	@AfterVMBinding
	private void afterVMBinding() {
		updateStudents();
	}
	
	private void updateStudents() {
		students.set(daoStudent.findAll());
	}

	@HandlesAction(PersistStudents.class)
	public void persistStudents(List<Student> students) {
		Iterator<Student> studentIterator = students.iterator();
		
		while(studentIterator.hasNext()) {
			Student student = studentIterator.next();
			
			Student persistedStudent = this.daoStudent.find(student.getId());
			
			Collection<Grade> grades = this.daoGrade.findAllWithCriteria(
					Restrictions.and(
							Restrictions.like("grade", student.getGrade().getGrade()),
							Restrictions.like("suffix", student.getGrade().getSuffix())
					));
	
			if(grades.size() == 1) {
				Iterator<Grade> gradesIterator = grades.iterator();
				Grade grade = gradesIterator.next();		
				student.setGrade(grade);
			}
			
			if(persistedStudent == null) {			
				if(student.getParent() != null){
					Collection<Parent> parents = daoParent.findAllWithCriteria(
							Restrictions.and(
									Restrictions.like("title", student.getParent().getTitle()),
									Restrictions.like("firstname", student.getParent().getFirstname()),
									Restrictions.like("lastname", student.getParent().getLastname()),
									Restrictions.like("street", student.getParent().getStreet()),
									Restrictions.eq("postcode", student.getParent().getPostcode()),
									Restrictions.like("city", student.getParent().getCity())
							));
		
					if(parents.size() == 1){
						Iterator<Parent> parentsIterator = parents.iterator();
						Parent parent = parentsIterator.next();		
						student.setParent(parent);
					}
				}				
		
				daoStudent.insert(student);	
			} else {
				daoStudent.update(student);
			}
		}
		eventBus.post(new MessageEvent("Import erfolgreich", "Alle Schüler wurden erfolgreich importiert", Type.TRAYINFO));
		eventBus.post(new ImportSuccessEvent());
		updateStudents();
	}

	/**
	 * Receives the OutputStream provided by an upload.
	 * 
	 * @param outputStream
	 */
	public void receiveUploadByteOutputStream(ByteArrayOutputStream outputStream) {
		CSVReader reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray())), ';', '\'', 0);
		try {
			List<Student> students = CSVHandler.createStudentObjectsFromCSV(reader);
			persistStudents(students);
		} catch (UnsupportedOperationException uoe) {
			eventBus.post(new MessageEvent("Import nicht möglich.", uoe.getMessage(), Type.ERROR));
		}
	}

}