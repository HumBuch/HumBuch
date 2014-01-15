package de.dhbw.humbuch.viewmodel;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.dhbw.humbuch.model.entity.User;

public class Properties {
	
	public final State<User> currentUser = new BasicState<>(User.class);

}
