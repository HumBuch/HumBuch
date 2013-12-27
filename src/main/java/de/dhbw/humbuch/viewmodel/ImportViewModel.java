package de.dhbw.humbuch.viewmodel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.vaadin.ui.Upload;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;

public class ImportViewModel {

	public interface ImportResult extends State<String> {}
	
	public interface UploadButton extends State<Upload> {}
	
	public interface DoImportStudents extends ActionHandler {}
	
	@ProvidesState(ImportResult.class)
	public final BasicState<String> importResult = new BasicState<String>(String.class);
	
	@ProvidesState(UploadButton.class)
	private BasicState<Upload> uploadPanel = new BasicState<Upload>(Upload.class);
	
	@HandlesAction(DoImportStudents.class)
	public void doImportStudents(String str){
	

	}
	
	public void setImportResult(String importResult){
		this.importResult.set(importResult);
	}
}
