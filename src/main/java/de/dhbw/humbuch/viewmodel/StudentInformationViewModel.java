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
	public StudentInformationViewModel(DAO<Student> daoStudent, DAO<Grade> daoGrade, DAO<Parent> daoParent,
			DAO<BorrowedMaterial> daoBorrowedMaterial, EventBus eventBus) {
		this.daoStudent = daoStudent;
		this.daoGrade = daoGrade;
		this.daoParent = daoParent;
		this.daoBorrowedMaterial = daoBorrowedMaterial;
		this.eventBus = eventBus;
	}

	@AfterVMBinding
	private void afterVMBinding() {
		updateStudents();
	}

	private void updateStudents() {
		students.set(daoStudent.findAllWithCriteria(Restrictions.eq("leavingSchool", false)));
	}

	@HandlesAction(PersistStudents.class)
	public void persistStudents(List<Student> students, boolean fullImport) {

		if (fullImport) {			
			Collection<Student> allStudents = this.daoStudent.findAll();
			Collection<Integer> deltaStudentIDs = new ArrayList<Integer>();
			for(Student student : allStudents){
				boolean isDelta = true;
				for(Student csvStudent : students){
					if(student.getId() == csvStudent.getId()){
						isDelta = false;
					}
				}
				if(isDelta){
					deltaStudentIDs.add(student.getId());
				}
			}
			
			for(Integer studentID : deltaStudentIDs){
				Collection<BorrowedMaterial> borrowedMaterials = this.daoBorrowedMaterial.findAllWithCriteria(
						Restrictions.eq("received", true),
						Restrictions.isNull("returnDate"),
						Restrictions.eq("studentId", studentID)
						);
				Collection<BorrowedMaterial> deltaBorrowed = this.daoBorrowedMaterial.findAll();				
				deltaBorrowed.removeAll(borrowedMaterials);
				Collection<Student> studentsCol = new HashSet<>();
				for(BorrowedMaterial borrowedMaterial : deltaBorrowed){
					studentsCol.add(borrowedMaterial.getStudent());
				}
				for(Student student : studentsCol){
					this.daoStudent.delete(student);
				}
				
				for(BorrowedMaterial borrowedMaterial : borrowedMaterials){
					borrowedMaterial.getStudent().setLeavingSchool(true);
				}
			}

			Collection<BorrowedMaterial> borrowedMaterials = this.daoBorrowedMaterial.findAll();
			Collection<Integer> borrowedMaterialIDs = new ArrayList<Integer>();
			borrowedMaterialIDs.add(-1);
			for (BorrowedMaterial borrowedMaterial : borrowedMaterials) {
				borrowedMaterialIDs.add(borrowedMaterial.getStudent().getId());
			}

			/**
			 * BorrowedMaterials that are lend to a student and not returned yet
			 **/
			Collection<BorrowedMaterial> borrowedMaterialsUnreturned = this.daoBorrowedMaterial.findAllWithCriteria(
					Restrictions.isNull("returnDate"),
					Restrictions.eq("received", true)
					);
			Collection<Integer> studentIDsWithUnreturnedMaterials = new HashSet<Integer>();
			studentIDsWithUnreturnedMaterials.add(-1);
			for (BorrowedMaterial borrowedMaterial : borrowedMaterialsUnreturned) {
				studentIDsWithUnreturnedMaterials.add(borrowedMaterial.getStudent().getId());
			}
			System.out.println("Size: " + studentIDsWithUnreturnedMaterials.size());

			/** All students that have no entries in borrowed materials **/
			Collection<Student> studentsToDelete = this.daoStudent.findAllWithCriteria(
					Restrictions.or(
							Restrictions.not(Restrictions.in("id", borrowedMaterialIDs)),
							Restrictions.and(
									Restrictions.in("id", borrowedMaterialIDs),
									Restrictions.not(Restrictions.in("id", studentIDsWithUnreturnedMaterials))
									)
							)
					);
	
			/** All students that have entries in borrowed materials **/
			//			Collection<Student> allStudents = this.daoStudent.findAllWithCriteria(
			//					Restrictions.in("id", borrowedMaterialIDs)
			//					);
			//			Collection<Student> allStudents = this.daoStudent.findAll();
			//			Collection<Student> studentsToDelete = new ArrayList<Student>();
			//			/** Find all students whose borrowed materials are returned (if all returnDates are not null) **/
			//			for (Student student : allStudents) {
			//				boolean canBeDeleted = true;
			//				for (BorrowedMaterial borrowedMaterial : student.getBorrowedList()) {
			//					//if (borrowedMaterial.getStudent().getId() == student.getId()) {
			//						if (borrowedMaterial.getReturnDate() == null) {
			//							canBeDeleted = false;
			//							break;
			//						}
			//					//}
			//				}
			//				if (canBeDeleted) {
			//					studentsToDelete.add(student);
			//				}
			//			}

			for (Student student : studentsToDelete) {
				this.daoStudent.delete(student);
			}
		}

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
			} 
			else {
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
			else {
				reader = new CSVReader(new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray())), ';', '\'', 0);
			}
			List<Student> students = CSVHandler.createStudentObjectsFromCSV(reader);
			persistStudents(students, fullImport);
		}
		catch (UnsupportedOperationException uoe) {
			eventBus.post(new MessageEvent("Import nicht möglich.", uoe.getMessage(), Type.ERROR));
		}
		catch (UnsupportedEncodingException e) {
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
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}