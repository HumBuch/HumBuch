package de.dhbw.humbuch.viewmodel;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;

public class ImportViewModel {
	
	public interface ImportResult extends State<String> {}
	
	public interface DoImportStudents extends ActionHandler {}
	
	@ProvidesState(ImportResult.class)
	public final BasicState<String> importResult = new BasicState<String>(String.class);
	
	@HandlesAction(DoImportStudents.class)
	public void doImportStudents(String str){
		importResult.set("Test");
		//System.out.println("In ImportStudentsActionHandler");
		//Upload upload = new Upload();
	}

}
