package de.dhbw.humbuch.viewmodel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.AfterVMBinding;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.event.MessageEvent;
import de.dhbw.humbuch.event.MessageEvent.Type;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.util.CSVHandler;
import de.dhbw.humbuch.view.StudentInformationView;


/**
 * Provides a {@link State} for all {@link Student}s.<br>
 * Imports {@link Student}.
 * 
 * @author David Vitt
 * @author Benjamin Räthlein
 * @author Johannes Idelhauser
 *
 */
public class StudentInformationViewModel {
	private final static Logger LOG = LoggerFactory.getLogger(StudentInformationView.class);

	public interface PersistStudents extends ActionHandler {}
	public interface Students extends State<Collection<Student>> {}

	@ProvidesState(Students.class)
	public State<Collection<Student>> students = new BasicState<>(Collection.class);

	private EventBus eventBus;
	private DAO<Student> daoStudent;
	private DAO<Grade> daoGrade;
	private DAO<BorrowedMaterial> daoBorrowedMaterial;

	/**
	 * Constructor
	 * 
	 * @param daoTeachingMaterial
	 *            DAO implementation to access TeachingMaterial entities
	 */
	@Inject
	public StudentInformationViewModel(DAO<Student> daoStudent,	DAO<Grade> daoGrade, DAO<BorrowedMaterial> daoBorrowedMaterial, 
			EventBus eventBus) {
		this.daoStudent = daoStudent;
		this.daoGrade = daoGrade;
		this.daoBorrowedMaterial = daoBorrowedMaterial;
		this.eventBus = eventBus;
	}

	@AfterVMBinding
	public void initialiseStates() {
		students.set(new ArrayList<Student>());
	}
	
	public void refresh() {
		updateStudents();
	}

	private void updateStudents() {
		students.set(daoStudent.findAll());
	}

	/**
	 * Imports the given {@link List} of {@link Student}s.<br>
	 * If {@code fullImport} is set, all {@link Student}s in the database will be deleted which aren't included in the {@link List}.
	 * Existing {@link Student}s will be updated.<br>
	 * If {@code fullImport} is not set, new {@link Student}s will be persisted and the existing {@link Student}s will be updated.  
	 * 
	 * @param csvStudents
	 * @param fullImport
	 */
	@HandlesAction(PersistStudents.class)
	public void persistStudents(List<Student> csvStudents, boolean fullImport) {

		int deletedStudents = 0;
		int changedStudents = 0;

		if (fullImport) {
			deletedStudents = deleteReturnedMarkUnreturned(csvStudents);
		}
		
		changedStudents = updateInsertStudents(csvStudents);
		
		eventBus.post(new MessageEvent("Import erfolgreich", 
				"Es wurden " + changedStudents + " Schüler hinzugefügt oder aktualisiert und " + deletedStudents + " gelöscht.",
				Type.TRAYINFO));
		
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
			LOG.warn(encoding);
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

	/**
	 * Checks the encoding of the given {@link ByteArrayOutputStream}.
	 * 
	 * @param outputStream
	 * @return The encoding of the {@link ByteArrayOutputStream}. If no encoding can be deteced: <code>null</code>. 
	 */
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
	
	/**
	 * @return {@link List} of {@link Student}s which have {@code received} {@link BorrowedMaterial}s with {@code isReturned == false}
	 */
	private List<Student> getStudentsWithUnreturnedBorrowedMaterials() {
		Collection<BorrowedMaterial> unreturnedBorrowedMaterials = this.daoBorrowedMaterial.findAllWithCriteria(
				Restrictions.eq("received", true),
				Restrictions.isNull("returnDate"));
		
		List<Student> studentsWithUnreturnedBorrowedMaterials = new ArrayList<>();
		for (BorrowedMaterial borrowedMaterial : unreturnedBorrowedMaterials) {
			studentsWithUnreturnedBorrowedMaterials.add(borrowedMaterial.getStudent());
		}
		
		return studentsWithUnreturnedBorrowedMaterials;
	}
	
	/**
	 * Deletes the given {@link Student}s if they don't have any {@code unreturned} {@link BorrowedMaterial}s left.<br>
	 * A {@link Student} will be marked as {@code leavingSchool} if he has {@code unreturned} {@link BorrowedMaterial}s.
	 * 
	 * @param csvStudents
	 * @return Number of deleted or updated {@link Student}s
	 */
	private int deleteReturnedMarkUnreturned(List<Student> csvStudents) {
		List<Student> toDelete = new ArrayList<>(daoStudent.findAll());
		toDelete.removeAll(csvStudents);
		
		List<Student> studentsWithUnreturnedBorrowedMaterials = getStudentsWithUnreturnedBorrowedMaterials();
		studentsWithUnreturnedBorrowedMaterials.retainAll(toDelete);
		for (Student student : studentsWithUnreturnedBorrowedMaterials) {
			student.setLeavingSchool(true);
		}
		daoStudent.update(studentsWithUnreturnedBorrowedMaterials);
		
		toDelete.removeAll(studentsWithUnreturnedBorrowedMaterials);
		daoStudent.delete(toDelete);
		return toDelete.size();
	}
	
	/**
	 * Checks if one of the given {@link Student}s is already existing in the database.
	 * In this case, the {@link Student} will be updated, otherwise it will be persisted. 
	 * 
	 * @param csvStudents
	 * @return Number of updated or inserted {@link Student}s
	 */
	private int updateInsertStudents(List<Student> csvStudents) {
		int changedStudents = 0;
		for(Student student : csvStudents) {
			Grade grade = daoGrade.findSingleWithCriteria(
					Restrictions.and(
							Restrictions.like("grade", student.getGrade().getGrade()),
							Restrictions.like("suffix", student.getGrade().getSuffix())
						));

			if (grade == null) {
				grade = daoGrade.insert(student.getGrade());
			}

			student.setGrade(grade);
			
			Student existingStudent = daoStudent.find(student.getId());
			if(existingStudent != null) {
				existingStudent.copyDataFrom(student);
				daoStudent.update(existingStudent);
			} else {	
				daoStudent.insert(student);
			}
			
			changedStudents++;
		}
		
		return changedStudents;
	}
}
