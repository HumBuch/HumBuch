package de.dhbw.humbuch.viewmodel;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.persistence.EntityManager;

import org.hibernate.criterion.Restrictions;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.dhbw.humbuch.guice.GuiceJUnitRunner;
import de.dhbw.humbuch.guice.TestModule;
import de.dhbw.humbuch.guice.GuiceJUnitRunner.GuiceModules;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;
import de.dhbw.humbuch.model.entity.User;
import de.dhbw.humbuch.util.PasswordHash;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModule.class })
public class LoginViewModelTest extends BaseTest {

	private LoginViewModel vm;
	private DAO<User> daoUser;
	
	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";

	@Inject
	public void setInjected(TestPersistenceInitialiser persistenceInitialiser,
			Provider<EntityManager> emProvider,
			LoginViewModel loginViewModel,
			DAO<User> daoUser) {
		this.daoUser = daoUser;
		super.setInjected(persistenceInitialiser, emProvider);
		
		this.vm = loginViewModel;
	}
	
	private void insertUser() {
		try {
			daoUser.insert(new User.Builder(USERNAME, PasswordHash.createHash(PASSWORD)).build());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testStateInitialisation() {
		assertNotNull(vm.isLoggedIn.get());
	}
	
	@Test
	public void testUserInsert() throws NoSuchAlgorithmException, InvalidKeySpecException {
		insertUser();
		User userInDB = daoUser.findSingleWithCriteria(Restrictions.eq("username", USERNAME));
		assertNotNull(userInDB);
		assertEquals(true, PasswordHash.validatePassword(PASSWORD, userInDB.getPassword()));
	}
	
	@Test
	public void testStateAfterVMInitialisation() {
		assertEquals(false, vm.isLoggedIn.get());
	}
	
	@Test
	public void testStateAfterSuccessfulLogin() {
		insertUser();
		vm.doLogin(USERNAME, PASSWORD);
		assertEquals(true, vm.isLoggedIn.get());
	}
	
	@Test
	public void testStateAfterUnsuccessfulLoginWithWrongPassword() {
		insertUser();
		vm.doLogin(USERNAME, "wrongPassword");
		assertEquals(false, vm.isLoggedIn.get());
	}
	
	@Test
	public void testStateAfterUnsuccessfulLoginWithWrongUsername() {
		insertUser();
		vm.doLogin("wrongUser", PASSWORD);
		assertEquals(false, vm.isLoggedIn.get());
	}
	
	@Test
	public void testStateAfterUnsuccessfulLoginWithAwfullyLongUsername()	{
		insertUser();
		vm.doLogin(new String(new char[1025]).replace('\0', 'X'), PASSWORD);
		assertEquals(false, vm.isLoggedIn.get());
	}
	
	@Test
	public void testStateAfterUnsuccessfulLoginWithAwfullyLongPassword()	{
		insertUser();
		vm.doLogin(USERNAME, new String(new char[1025]).replace('\0', 'X'));
		assertEquals(false, vm.isLoggedIn.get());
	}
	
	@Test
	public void testStateAfterSuccessfulLoginAndLogout() {
		insertUser();
		vm.doLogin(USERNAME, PASSWORD);
		vm.doLogout(null);
		assertEquals(false, vm.isLoggedIn.get());
	}
}
