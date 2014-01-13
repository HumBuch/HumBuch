package de.dhbw.humbuch.viewmodel;

import java.util.Collection;

import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.AfterVMBinding;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.User;

public class SettingsViewModel {
	
	public interface DoUpdateSchoolYear extends ActionHandler {}
	public interface DoDeleteSchoolYear extends ActionHandler {}
	public interface DoInsertSchoolYear extends ActionHandler {}
	public interface DoUpdateUser extends ActionHandler {}
	public interface DoPasswordChange extends ActionHandler {}
	
	public interface SchoolYears extends State<Collection<SchoolYear>> {}
	public interface PasswordChangeStatus extends State<ChangeStatus> {}

	@ProvidesState(SchoolYears.class)
	private State<Collection<SchoolYear>> schoolYears = new BasicState<>(Collection.class);
	
	@ProvidesState(PasswordChangeStatus.class)
	private State<ChangeStatus> passwordChangeState = new BasicState<>(ChangeStatus.class);
	
	private DAO<SchoolYear> daoSchoolYear;
//	private DAO<User> daoUser;

	@Inject
	public SettingsViewModel(DAO<SchoolYear> daoSchoolYear, DAO<User> daoUser) {
		this.daoSchoolYear = daoSchoolYear;
//		this.daoUser = daoUser;
	}
	
	@AfterVMBinding
	private void afterAMBindung() {
		schoolYears.set(daoSchoolYear.findAll());
	}
	
	@HandlesAction(DoUpdateSchoolYear.class)
	public void doUpdateSchoolYear(SchoolYear schoolYear) {
		daoSchoolYear.update(schoolYear);
	}
	
	@HandlesAction(DoDeleteSchoolYear.class)
	public void DoDeleteSchoolYear(SchoolYear schoolYear) {
		daoSchoolYear.delete(schoolYear);
	}
	
	@HandlesAction(DoInsertSchoolYear.class)
	public void DoInsertSchoolYear(SchoolYear schoolYear) {
		daoSchoolYear.insert(schoolYear);
	}
	
	@HandlesAction(DoUpdateUser.class)
	public void doUpdateUser(String userName, String userEmail) {
		//TODO: aktuell eingeloggter User abfragen und Name/Mail ändern + persistieren
	}
	
	@HandlesAction(DoPasswordChange.class) 
	public void doPasswordChange(String currentPassword, String newPassword, String newPasswordVerified) {
		if(currentPassword.isEmpty() || newPassword.isEmpty() || newPasswordVerified.isEmpty()) {
			passwordChangeState.set(ChangeStatus.EMPTY_FIELDS);
		} else if(!newPassword.equals(newPasswordVerified)) {
			passwordChangeState.set(ChangeStatus.NEW_PASSWORD_NOT_EQUALS);
		} else {
			passwordChangeState.set(ChangeStatus.SUCCESSFULL);
		}
		
		//TODO: "currentPassword" prüfen
		//TODO: Änderung setzen + persistieren
	}
	
	public enum ChangeStatus {
		EMPTY_FIELDS,
		CURRENT_PASSWORD_WRONG,
		NEW_PASSWORD_NOT_EQUALS,
		SUCCESSFULL;
	}
 }
