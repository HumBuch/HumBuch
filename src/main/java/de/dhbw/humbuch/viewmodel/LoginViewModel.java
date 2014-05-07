package de.dhbw.humbuch.viewmodel;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.event.LoginEvent;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.User;
import de.dhbw.humbuch.util.PasswordHash;
import de.dhbw.humbuch.view.MainUI;

public class LoginViewModel {

	private final static Logger LOG = LoggerFactory.getLogger(MainUI.class);
	
	public interface IsLoggedIn extends State<Boolean> {}

	public interface DoLogout extends ActionHandler {}
	public interface DoLogin extends ActionHandler {}
	
	private DAO<User> daoUser;
	private EventBus eventBus;
	private Properties properties;

	@ProvidesState(IsLoggedIn.class)
	public final BasicState<Boolean> isLoggedIn = new BasicState<Boolean>(Boolean.class);
	
	@Inject
	public LoginViewModel(DAO<User> daoUser, Properties properties, EventBus eventBus) {
		this.properties = properties;
		this.eventBus = eventBus;
		this.daoUser = daoUser;
		updateLoginStatus();
	}
	
	private void updateLoginStatus() {
		isLoggedIn.set(properties.currentUser.get() != null);
	}

	@HandlesAction(DoLogin.class)
	public void doLogin(String username, String password) {
		try {
			if (username.equals("") || password.equals("")) {
				eventBus.post(new LoginEvent("Bitte geben Sie einen Nutzernamen und Passwort an."));
				return;
			} else {
				List<User> user = (List<User>) daoUser.findAllWithCriteria(Restrictions.eq("username", username));
				if(!user.isEmpty()) {
						if(PasswordHash.validatePassword(password, user.get(0).getPassword())) {
							properties.currentUser.set(user.get(0));
							updateLoginStatus();
							return;
						}
				}
				eventBus.post(new LoginEvent("Username oder Passwort stimmen nicht überein."));
			}
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			eventBus.post(new LoginEvent("Fehler bei Login. Bitte kontaktieren Sie einen Entwickler."));
			LOG.warn(e.getMessage());
			return;
		}
	}

	@HandlesAction(DoLogout.class)
	public void doLogout(Object obj) {
		properties.currentUser.set(null);
		updateLoginStatus();
	}

}