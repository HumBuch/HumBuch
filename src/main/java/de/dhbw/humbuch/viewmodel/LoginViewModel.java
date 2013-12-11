package de.dhbw.humbuch.viewmodel;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;

public class LoginViewModel {

	public interface LoginSuccessful extends State<Boolean> {}

	public interface DoLogin extends ActionHandler {}

	@ProvidesState(LoginSuccessful.class)
	public final BasicState<String> loginSuccessful = new BasicState<>(String.class);

	@HandlesAction(DoLogin.class)
	public void doLogin(String username, String password) {
		System.out.println("login attempt...");
		System.out.println("userName: " + username);
		System.out.println("password: " + password);
		loginSuccessful.set("Login erfolgreich!");
	}
}
