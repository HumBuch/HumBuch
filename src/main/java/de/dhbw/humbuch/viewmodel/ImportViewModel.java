package de.dhbw.humbuch.viewmodel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

import au.com.bytecode.opencsv.CSVReader;

import com.google.inject.Inject;
import com.vaadin.ui.Upload;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Parent;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.util.CSVHandler;

public class ImportViewModel {

	public interface ImportResult extends State<String> {}
		
	public interface DoImportStudents extends ActionHandler {}
	
	@ProvidesState(ImportResult.class)
	public final BasicState<String> importResult = new BasicState<String>(String.class);

	
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
	public ImportViewModel(DAO<Student> daoStudent, DAO<Grade> daoGrade, DAO<Parent> daoParent) {
		this.daoStudent = daoStudent;
		this.daoGrade = daoGrade;
		this.daoParent = daoParent;
	}
	
	@HandlesAction(DoImportStudents.class)
	public void doImportStudents(Upload upload){
		
	}
	
	public DAO<Student> getDAOStudent(){
		return this.daoStudent;
	}
	
	public DAO<Grade> getDAOGrade(){
		return this.daoGrade;
	}
	
	public DAO<Parent> getDAOParent(){
		return this.daoParent;
	}
	
	public void setImportResult(String importResult){
		this.importResult.set(importResult);
	}
	/**
	 * Receives the OutputStream provided by an upload.
	 * @param outputStream
	 */
	public void receiveUploadByteOutputStream(ByteArrayOutputStream outputStream) {
		InputStreamReader inputStream = new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray()));
		CSVReader reader = new CSVReader(inputStream);
		CSVHandler.createStudentObjectsFromCSV(reader);
	}
}
