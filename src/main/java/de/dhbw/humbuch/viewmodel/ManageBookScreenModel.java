package de.dhbw.humbuch.viewmodel;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;

public class ManageBookScreenModel {
	
	public interface Edit extends State<Boolean> {}
	
	public interface DoEdit extends ActionHandler {}
	
	@ProvidesState(Edit.class)
	public final BasicState<Boolean> edit = new BasicState<>(Boolean.class);
	
	@HandlesAction(DoEdit.class)
	public void doEdit() {
		edit.set(true);
	}

}
