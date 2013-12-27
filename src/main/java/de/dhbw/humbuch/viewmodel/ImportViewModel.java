package de.dhbw.humbuch.viewmodel;

import com.google.inject.Inject;
import com.vaadin.ui.Upload;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;

public class ImportViewModel {

	public interface ImportResult extends State<String> {}
		
	public interface DoImportStudents extends ActionHandler {}
	
	@ProvidesState(ImportResult.class)
	public final BasicState<String> importResult = new BasicState<String>(String.class);

	
	private DAO<Student> daoStudent;
	private DAO<Grade> daoGrade;

	/**
	 * Constructor
	 * 
	 * @param daoTeachingMaterial
	 *            DAO implementation to access TeachingMaterial entities
	 */
	@Inject
	public ImportViewModel(DAO<Student> daoStudent, DAO<Grade> daoGrade) {
		this.daoStudent = daoStudent;
		this.daoGrade = daoGrade;
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
	
	public void setImportResult(String importResult){
		this.importResult.set(importResult);
	}
}
