package de.dhbw.humbuch.viewmodel;

import java.util.Collection;

import org.hibernate.criterion.Restrictions;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.annotations.AfterVMBinding;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.event.MessageEvent;
import de.dhbw.humbuch.event.MessageEvent.Type;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.Category;
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.User;

public class SettingsViewModel {

	public interface DoUpdateUser extends ActionHandler {}
	public interface DoPasswordChange extends ActionHandler {}

	public interface SchoolYears extends State<Collection<SchoolYear>> {}
	public interface Categories extends State<Collection<Category>> {}
	public interface PasswordChangeStatus extends State<ChangeStatus> {}
	public interface UserName extends State<String> {}
	public interface UserEmail extends State<String> {}

	@ProvidesState(SchoolYears.class)
	private State<Collection<SchoolYear>> schoolYears = new BasicState<>(Collection.class);

	@ProvidesState(Categories.class)
	private State<Collection<Category>> categories = new BasicState<>(Collection.class);

	@ProvidesState(PasswordChangeStatus.class)
	private State<ChangeStatus> passwordChangeStatus = new BasicState<>(ChangeStatus.class);

	@ProvidesState(UserName.class)
	private State<String> userName = new BasicState<>(String.class);

	@ProvidesState(UserEmail.class)
	private State<String> userEmail = new BasicState<>(String.class);

	private EventBus eventBus;
	private State<User> currentUser;
	private DAO<SchoolYear> daoSchoolYear;
	private DAO<User> daoUser;
	private DAO<Category> daoCategory;

	@Inject
	public SettingsViewModel(DAO<SchoolYear> daoSchoolYear, DAO<User> daoUser, DAO<Category> daoCategory, 
			Properties properties, EventBus eventBus) {
		this.eventBus = eventBus;
		this.daoSchoolYear = daoSchoolYear;
		this.daoUser = daoUser;
		this.daoCategory = daoCategory;
		this.currentUser = properties.currentUser;
		this.currentUser.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object value) {
				if (value != null)
					updateUser();
			}
		});
	}

	@AfterVMBinding
	private void afterAMBindung() {
		updateSchoolYears();
		updateCategories();
		updateUser();
	}

	private void updateSchoolYears() {
		schoolYears.set(daoSchoolYear.findAll());
	}

	private void updateCategories() {
		categories.set(daoCategory.findAll());
	}

	private void updateUser() {
		userName.set(currentUser.get().getUsername());
		userEmail.set(currentUser.get().getEmail());
	}

	public void doUpdateSchoolYear(SchoolYear schoolYear) {
		if (schoolYear.getYear().isEmpty()) {
			eventBus.post(new MessageEvent("Speichern nicht möglich!",
					"Das Feld 'Schuljahr' darf nicht leer sein.", Type.WARNING));
		} else {
			daoSchoolYear.update(schoolYear);
		}
		
		updateSchoolYears();
	}

	public void doDeleteSchoolYear(SchoolYear schoolYear) {
		if(!schoolYear.isCurrentYear()) {
			daoSchoolYear.delete(schoolYear);
		} else {
			eventBus.post(new MessageEvent("Löschen nicht möglich!",
					"Das aktuelle Schuljahr kann nicht gelöscht werden.", Type.WARNING));
		}
		
		updateSchoolYears();
	}

	public void doUpdateCategory(Category category) {
		if (category.getName().isEmpty()) {
			eventBus.post(new MessageEvent("Speichern nicht möglich!",
					"Das Feld 'Kategorie' darf nicht leer sein.", Type.WARNING));
		} else {
			daoCategory.update(category);
		}
		
		updateCategories();
	}

	public void doDeleteCategory(Category category) {
		if(category.getTeachingMaterials().isEmpty()) {
			daoCategory.delete(category);
		} else {
			eventBus.post(new MessageEvent("Löschen nicht möglich!",
					"Kategorie wird noch verwendet.", Type.WARNING));
		}
		
		updateCategories();
	}

	@HandlesAction(DoUpdateUser.class)
	public void doUpdateUser(String userName, String userEmail) {
		if (userEmail.isEmpty())
			userEmail = null;
		
		Collection<User> userWithSameNameOrPassword = daoUser
				.findAllWithCriteria(Restrictions.or(
						Restrictions.eq("username", userName),
						Restrictions.eq("email", userEmail)));
		
		if (!userWithSameNameOrPassword.isEmpty()) {
			User user = currentUser.get();
			user.setUsername(userName);
			user.setEmail(userEmail);
			daoUser.update(user);
			this.userName.set(userName);
			this.userEmail.set(userEmail);
			currentUser.notifyAllListeners();
			eventBus.post(new MessageEvent("Daten wurden geändert"));
		} else {
			eventBus.post(new MessageEvent(
					"Speichern fehlgeschlagen!",
					"Es existiert bereits ein Nutzer mit dem Nutzername oder der E-Mail-Adresse.",
					Type.WARNING));
		}
	}

	@HandlesAction(DoPasswordChange.class)
	public void doPasswordChange(String currentPassword, String newPassword, String newPasswordVerified) {
		User user = currentUser.get();
		if (currentPassword.isEmpty() || newPassword.isEmpty()
				|| newPasswordVerified.isEmpty()) {
			eventBus.post(new MessageEvent("Leere Felder!",
					"Bitte alle Felder ausfüllen.", Type.WARNING));
		} else if (!user.getPassword().equals(currentPassword)) {
			eventBus.post(new MessageEvent("Falsches Passwort!",
					"Das aktuelle Passwort ist nicht korrekt.", Type.WARNING));
		} else if (!newPassword.equals(newPasswordVerified)) {
			eventBus.post(new MessageEvent("Passwörter stimmen nicht überein!",
					"Die beiden neuen Passwörter stimmen nicht überein.",
					Type.WARNING));
		} else {
			user.setPassword(newPassword);
			daoUser.update(user);
			currentUser.notifyAllListeners();
			passwordChangeStatus.set(null);
			passwordChangeStatus.set(ChangeStatus.SUCCESSFULL);
			// TODO: passwordChangeStatus.notifyAllListeners(); does not work
			eventBus.post(new MessageEvent("Passwort geändert"));
		}
	}

	public enum ChangeStatus {
		EMPTY_FIELDS, 
		CURRENT_PASSWORD_WRONG, 
		NEW_PASSWORD_NOT_EQUALS, 
		NAME_OR_MAIL_ALREADY_EXISTS, 
		FAILED, 
		SUCCESSFULL;
	}
}
