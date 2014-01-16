package de.dhbw.humbuch.viewmodel;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.event.LoginEvent;
import de.dhbw.humbuch.event.MessageEvent;
import de.dhbw.humbuch.event.MessageEvent.Type;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.User;

public class LoginViewModel {

	public interface IsLoggedIn extends State<Boolean> {
	}
	
	public interface LoginError extends State<String> {
	}

	public interface DoLogout extends ActionHandler {
	}

	public interface DoLogin extends ActionHandler {
	}
	
	private DAO<User> daoUser;
	private EventBus eventBus;

	@ProvidesState(IsLoggedIn.class)
	public final BasicState<Boolean> isLoggedIn = new BasicState<Boolean>(Boolean.class);

	@ProvidesState(LoginError.class)
	public final BasicState<String> loginError = new BasicState<String>(String.class);
	
	@Inject
	public LoginViewModel(DAO<User> daoUser, EventBus eventBus) {
		this.eventBus = eventBus;
		this.daoUser = daoUser;
		isLoggedIn.set(new Boolean(false));
	}

	@HandlesAction(DoLogin.class)
	public void doLogin(String username, String password) {
		// loginSuccessful.set(daoStudent.find(1).getFirstname());
		
		// Es muss sichergestellt sein, dass es keine zwei User mit selber Name/Passwort Kombination gibt
		if (username.equals("") || password.equals("")) {
			// Set the loginError to empty string --> state change --> user feedback in the view
			loginError.set("");
			loginError.set("Bitte geben Sie einen Nutzernamen und Passwort an.");
			eventBus.post(new MessageEvent("Name + Passwort angeben", Type.ERROR));
			return;
		} else {
			List<User> user = (List<User>) daoUser.findAllWithCriteria(Restrictions.eq("username", username), Restrictions.eq("password", password));
			if(!user.isEmpty()) {
				isLoggedIn.set(new Boolean(true));
			} else {
				loginError.set("");
				loginError.set("Username oder Passwort stimmen nicht überein.");
			}
		}
		eventBus.post(new LoginEvent("LoginEvent posted..."));
	}

	@HandlesAction(DoLogout.class)
	public void doLogout(Object obj) {
		isLoggedIn.set(new Boolean(false));
		System.out.println("LoginViewModel->doLogout()");
	}

}