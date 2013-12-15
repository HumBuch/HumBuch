package de.dhbw.humbuch.viewmodel;

import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
<<<<<<< HEAD
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.Student;
=======
import de.dhbw.humbuch.ui.NavigationUI;
>>>>>>> refs/heads/developNew

public class LoginViewModel {

	public interface LoginSuccessful extends State<Boolean> {}

	public interface DoLogin extends ActionHandler {}
	
	@Inject
	private DAO<Student> daoStudent;
	
	@ProvidesState(LoginSuccessful.class)
	public final BasicState<String> loginSuccessful = new BasicState<>(String.class);

	@HandlesAction(DoLogin.class)
	public void doLogin(String username, String password) {
<<<<<<< HEAD
		loginSuccessful.set(daoStudent.find(1).getFirstname());
=======
		System.out.println("login attempt...");
		System.out.println("userName: " + username);
		System.out.println("password: " + password);
//		loginSuccessful.set("Login Erfolgreich!");
>>>>>>> refs/heads/developNew
	}
}
