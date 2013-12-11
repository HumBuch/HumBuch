package de.dhbw.humbuch.viewmodel;

import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

public class LoginViewModel {

	public interface LoginSuccessful extends State<Boolean> {}

	public interface DoLogin extends ActionHandler {}
	
	@Inject
	private DAO<Student> daoStudent;
	
	@Inject
	private DAO<TeachingMaterial> daoTeachingMaterial;

	@ProvidesState(LoginSuccessful.class)
	public final BasicState<String> loginWasSuccessful = new BasicState<>(String.class);

	@HandlesAction(DoLogin.class)
	public void doLogin(String username, String password) {
		System.out.println("login attempt...");
		System.out.println("userName: " + username);
		System.out.println("password: " + password);
		loginWasSuccessful.set("Login erfolgreich!");
	}
}
