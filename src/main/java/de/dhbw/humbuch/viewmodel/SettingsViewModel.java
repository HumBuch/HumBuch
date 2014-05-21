package de.dhbw.humbuch.viewmodel;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import de.dhbw.humbuch.model.entity.SettingsEntry;
import de.dhbw.humbuch.model.entity.User;
import de.dhbw.humbuch.util.PasswordHash;

/**
 * Provides {@link State}s and methods for inserting and updateing {@link SchoolYear}s and {@link Category}s.
 * 
 * @author David Vitt
 * 
 */
public class SettingsViewModel {

	private final static Logger LOG = LoggerFactory.getLogger(SettingsViewModel.class);

	public interface DoUpdateUser extends ActionHandler {}
	public interface DoPasswordChange extends ActionHandler {}

	public interface SchoolYears extends State<Collection<SchoolYear>> {}
	public interface Categories extends State<Collection<Category>> {}
	public interface SettingsEntries extends State<Collection<SettingsEntry>> {}
	public interface PasswordChangeStatus extends State<ChangeStatus> {}
	public interface UserName extends State<String> {}
	public interface UserEmail extends State<String> {}

	@ProvidesState(SchoolYears.class)
	public final State<Collection<SchoolYear>> schoolYears = new BasicState<>(Collection.class);

	@ProvidesState(Categories.class)
	public final State<Collection<Category>> categories = new BasicState<>(Collection.class);

	@ProvidesState(SettingsEntries.class)
	public final State<Collection<SettingsEntry>> settingsEntries = new BasicState<>(Collection.class);

	@ProvidesState(PasswordChangeStatus.class)
	public final State<ChangeStatus> passwordChangeStatus = new BasicState<>(ChangeStatus.class);

	@ProvidesState(UserName.class)
	public final State<String> userName = new BasicState<>(String.class);

	@ProvidesState(UserEmail.class)
	public final State<String> userEmail = new BasicState<>(String.class);

	private EventBus eventBus;
	private State<User> currentUser;
	private DAO<SchoolYear> daoSchoolYear;
	private DAO<User> daoUser;
	private DAO<Category> daoCategory;
	private DAO<SettingsEntry> daoSettingsEntry;

	/**
	 * Constructor
	 * 
	 * @param daoSchoolYear
	 * @param daoUser
	 * @param daoCategory
	 * @param daoSettingsEntry
	 * @param properties
	 * @param eventBus
	 */
	@Inject
	public SettingsViewModel(DAO<SchoolYear> daoSchoolYear, DAO<User> daoUser, DAO<Category> daoCategory, DAO<SettingsEntry> daoSettingsEntry,
			Properties properties, EventBus eventBus) {
		this.eventBus = eventBus;
		this.daoSchoolYear = daoSchoolYear;
		this.daoUser = daoUser;
		this.daoCategory = daoCategory;
		this.daoSettingsEntry = daoSettingsEntry;
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
	public void initialiseStates() {
		schoolYears.set(new ArrayList<SchoolYear>());
		categories.set(new ArrayList<Category>());
		settingsEntries.set(new ArrayList<SettingsEntry>());
		passwordChangeStatus.set(null);
		userName.set(null);
		userEmail.set(null);
	}
	
	public void refresh() {
		updateSchoolYears();
		updateCategories();
		updateSettingsEntries();
		updateUser();
	}

	private void updateSchoolYears() {
		schoolYears.set(daoSchoolYear.findAll());
	}

	private void updateCategories() {
		categories.set(daoCategory.findAll());
	}

	private void updateSettingsEntries() {
		settingsEntries.set(daoSettingsEntry.findAll());
	}

	private void updateUser() {
		userName.set(currentUser.get().getUsername());
		userEmail.set(currentUser.get().getEmail());
	}

	public void doUpdateSchoolYear(SchoolYear schoolYear) {
		daoSchoolYear.update(schoolYear);
		updateSchoolYears();
	}

	/**
	 * Delets {@link SchoolYear} if it isn't the current one.
	 * 
	 * @param schoolYear
	 */
	public void doDeleteSchoolYear(SchoolYear schoolYear) {
		if (!schoolYear.isActive()) {
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

	public void doUpdateSettingsEntry(SettingsEntry settingsEntry) {
		daoSettingsEntry.update(settingsEntry);
		updateSettingsEntries();
	}

	public void doDeleteCategory(Category category) {
		if (category.getTeachingMaterials().isEmpty()) {
			daoCategory.delete(category);
		} else {
			eventBus.post(new MessageEvent("Löschen nicht möglich!",
					"Kategorie wird noch verwendet.", Type.WARNING));
		}

		updateCategories();
	}

	/**
	 * Updates {@code userName} and {@code userEmail} of the current {@link User}.<br>
	 * Checks if username or email are already in use by another {@link User}.
	 * 
	 * @param userName
	 * @param userEmail
	 */
	@HandlesAction(DoUpdateUser.class)
	public void doUpdateUser(String userName, String userEmail) {
		if (userEmail.isEmpty())
			userEmail = null;
		
		Collection<User> userWithSameNameOrPassword = daoUser.findAllWithCriteria(
				Restrictions.or(
						Restrictions.eq("username", userName),
						Restrictions.eq("email", userEmail)));
		
		if ((userWithSameNameOrPassword.size() == 1	&& userWithSameNameOrPassword.contains(currentUser.get())) 
					|| userWithSameNameOrPassword.isEmpty()) {
			User user = currentUser.get();
			user.setUsername(userName);
			user.setEmail(userEmail);
			daoUser.update(user);
			this.userName.set(userName);
			this.userEmail.set(userEmail);
			currentUser.notifyAllListeners();
			eventBus.post(new MessageEvent("Daten wurden geändert"));
		} else {
			eventBus.post(new MessageEvent("Speichern fehlgeschlagen!",
					"Es existiert bereits ein Nutzer mit dem Nutzername oder der E-Mail-Adresse.", Type.WARNING));
		}
	}

	/**
	 * Tries to change the password of the currently logged in user. If one of
	 * the parameters is empty, a {@link MessageEvent} is posted to the {@link EventBus}.<br>
	 * If the new password and the new verified password do not match, a
	 * {@link MessageEvent} is posted to the {@link EventBus}.
	 * 
	 * After successfully checking the current password, the password is changed.
	 * 
	 * @param currentPassword
	 *            String with the current password
	 * @param newPassword
	 *            String with the new password
	 * @param newPasswordVerified
	 *            String with the verified new password
	 */
	@HandlesAction(DoPasswordChange.class)
	public void doPasswordChange(String currentPassword, String newPassword, String newPasswordVerified) {
		User user = currentUser.get();

		try {
			// Check if one of the fields is empty or the two new passwords do
			// not match
			if (currentPassword.isEmpty() || newPassword.isEmpty() || newPasswordVerified.isEmpty()) {
				eventBus.post(new MessageEvent("Leere Felder!", "Bitte alle Felder ausfüllen.", Type.WARNING));
			} else if (!newPassword.equals(newPasswordVerified)) {
				eventBus.post(new MessageEvent(
						"Passwörter stimmen nicht überein!", "Die beiden neuen Passwörter stimmen nicht überein.", Type.WARNING));
			} else if (!PasswordHash.validatePassword(currentPassword, user.getPassword())) {
				eventBus.post(new MessageEvent("Falsches Passwort!", "Das aktuelle Passwort ist nicht korrekt.", Type.WARNING));
			} else {
				// Change the password in the database and update the user object
				user.setPassword(PasswordHash.createHash(newPassword));

				daoUser.update(user);
				currentUser.notifyAllListeners();

				// TODO: passwordChangeStatus.notifyAllListeners(); does not work
				passwordChangeStatus.set(null);
				passwordChangeStatus.set(ChangeStatus.SUCCESSFULL);

				eventBus.post(new MessageEvent("Passwort geändert"));
			}

		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			LOG.warn(e.getMessage());
			eventBus.post(new MessageEvent("Fehler bei der Passwortänderung!", "Bitte kontaktieren Sie einen Entwickler.", Type.WARNING));
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
