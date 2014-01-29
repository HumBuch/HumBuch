package de.dhbw.humbuch.viewmodel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.mozilla.universalchardet.UniversalDetector;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.AfterVMBinding;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.event.EntityUpdateEvent;
import de.dhbw.humbuch.event.MessageEvent;
import de.dhbw.humbuch.event.MessageEvent.Type;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.DAO.FireUpdateEvent;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Parent;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.util.CSVHandler;


public class StudentInformationViewModel {

	public interface Students extends State<Collection<Student>> {
	}


	public interface PersistStudents extends ActionHandler {
	}

	@ProvidesState(Students.class)
	public State<Collection<Student>> students = new BasicState<>(Collection.class);

	private EventBus eventBus;
	private DAO<Student> daoStudent;
	private DAO<Grade> daoGrade;
	private DAO<Parent> daoParent;
	private DAO<BorrowedMaterial> daoBorrowedMaterial;

	/**
	 * Constructor
	 * 
	 * @param daoTeachingMaterial
	 *            DAO implementation to access TeachingMaterial entities
	 */
	@Inject
	public StudentInformationViewModel(DAO<Student> daoStudent,
			DAO<Grade> daoGrade, DAO<Parent> daoParent,
			DAO<BorrowedMaterial> daoBorrowedMaterial, EventBus eventBus) {
		this.daoStudent = daoStudent;
		this.daoGrade = daoGrade;
		this.daoParent = daoParent;
		this.daoBorrowedMaterial = daoBorrowedMaterial;
		this.eventBus = eventBus;
		
		eventBus.register(this);
	}

	@AfterVMBinding
	private void afterVMBinding() {
		updateStudents();
	}

	private void updateStudents() {
		students.set(daoStudent.findAll());
	}

	@HandlesAction(PersistStudents.class)
	public void persistStudents(List<Student> students, boolean fullImport) {

		int updatedStudents = 0;
		int insertedStudents = 0;
		int deletedStudents = 0;

		// Full import of all students in the csv
		if (fullImport) {
			Collection<Student> deltaStudents = new ArrayList<Student>();
			// All students in DB
			Collection<Student> allStudents = this.daoStudent.findAll();

			// Calculate the delta between the students in the database and in
			// the csv
			for (Student student : allStudents) {
				boolean isDelta = true;
				for (Student csvStudent : students) {
					// If the csv-student is in the database remove it from
					// delta and move to the next student in the database
					if (student.getId() == csvStudent.getId()) {
						isDelta = false;
						break;
					}
				}
				// Add it to delta
				if (isDelta) {
					deltaStudents.add(student);
				}
			}

			// Get all unreturned borrowed materials
			Collection<BorrowedMaterial> unreturnedBorrowedMaterials = this.daoBorrowedMaterial
					.findAllWithCriteria(Restrictions.eq("received", true),
							Restrictions.isNull("returnDate"));

			// Iterate over all delta students
			for (Student student : deltaStudents) {
				Collection<Student> studentsToNotDelete = new HashSet<>();

				// Checks each material in the unreturned materials collection
				for (BorrowedMaterial borrowedMaterial : unreturnedBorrowedMaterials) {
					// If the student of the unreturned borrowed material is a
					// delta student, exclude him from deletion and set the
					// flag "leavingSchool"
					if (borrowedMaterial.getStudent().equals(student)) {
						Student a = borrowedMaterial.getStudent();
						a.setLeavingSchool(true);
						studentsToNotDelete.add(a);
					}
				}

				// Delete the student if it isn't an element of the
				// do-not-delete-list
				if (!studentsToNotDelete.contains(student)) {
					this.daoStudent.delete(student);
					deletedStudents++;
				}
			}
		}
		// ====== End of full import ====== //

		// Iterator over all csv-students
		Iterator<Student> studentIterator = students.iterator();

		while (studentIterator.hasNext()) {
			Student student = studentIterator.next();

			Student persistedStudent = this.daoStudent.find(student.getId());

			Collection<Grade> grades = this.daoGrade.findAllWithCriteria(
					Restrictions.and(
							Restrictions.like("grade", student.getGrade().getGrade()),
							Restrictions.like("suffix", student.getGrade().getSuffix())
							));

			if (grades.size() == 1) {
				Iterator<Grade> gradesIterator = grades.iterator();
				Grade grade = gradesIterator.next();
				student.setGrade(grade);
			}

			if (persistedStudent == null) {
				if (student.getParent() != null) {
					Collection<Parent> parents = daoParent.findAllWithCriteria(
							Restrictions.and(
									Restrictions.like("title", student.getParent().getTitle()),
									Restrictions.like("firstname", student.getParent().getFirstname()),
									Restrictions.like("lastname", student.getParent().getLastname()),
									Restrictions.like("street", student.getParent().getStreet()),
									Restrictions.eq("postcode", student.getParent().getPostcode()),
									Restrictions.like("city", student.getParent().getCity())
									));

					if (parents.size() == 1) {
						Iterator<Parent> parentsIterator = parents.iterator();
						Parent parent = parentsIterator.next();
						student.setParent(parent);
					}
				}

				daoStudent.insert(student);
				insertedStudents++;
			} else {
				daoStudent.update(student);
				updatedStudents++;
			}
		}
		eventBus.post(new MessageEvent("Import erfolgreich", "Es wurden "
				+ insertedStudents + " Schüler hinzugefügt, " + updatedStudents
				+ " aktualisiert und " + deletedStudents + " gelöscht.",
				Type.TRAYINFO));
		eventBus.post(new ImportSuccessEvent());
		updateStudents();
	}

	/**
	 * Receives the OutputStream provided by an upload.
	 * 
	 * @param outputStream
	 * @param fullImport 
	 */
	public void receiveUploadByteOutputStream(ByteArrayOutputStream outputStream, boolean fullImport) {
		try {
			String encoding = checkEncoding(outputStream);
			CSVReader reader;
			//leave System.out in for test purposes on other systems
			System.out.println(encoding);
			if (encoding != null) {
				reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray()), encoding), ';', '\'', 0);
				
			}
			else{
				reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray())), ';', '\'', 0);
			}
			List<Student> students = CSVHandler.createStudentObjectsFromCSV(reader);
			persistStudents(students, fullImport);
		} catch (UnsupportedOperationException uoe) {
			eventBus.post(new MessageEvent("Import nicht möglich.", uoe.getMessage(), Type.ERROR));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private String checkEncoding(ByteArrayOutputStream outputStream) {
		byte[] buf = new byte[4096];
		ByteArrayInputStream bais;
		try {
			bais = new ByteArrayInputStream(outputStream.toByteArray());

			// (1)
			UniversalDetector detector = new UniversalDetector(null);

			// (2)
			int nread;
			while ((nread = bais.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
			// (3)
			detector.dataEnd();

			// (4)
			String encoding = detector.getDetectedCharset();

			// (5)
			detector.reset();
			bais.close();
			return encoding;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	@Subscribe
	public void handleEntityUpdateEvent(EntityUpdateEvent entityUpdateEvent) {
		if(entityUpdateEvent.contains(Student.class)) {
			updateStudents();
		}
	}
}