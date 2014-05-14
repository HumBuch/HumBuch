package de.dhbw.humbuch.viewmodel;


import static de.dhbw.humbuch.test.TestUtils.PASSWORD;
import static de.dhbw.humbuch.test.TestUtils.USERNAME;
import static de.dhbw.humbuch.test.TestUtils.standardUser;
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
import de.dhbw.humbuch.guice.GuiceJUnitRunner.GuiceModules;
import de.dhbw.humbuch.guice.TestModuleWithoutSingletons;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;
import de.dhbw.humbuch.model.entity.User;
import de.dhbw.humbuch.util.PasswordHash;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModuleWithoutSingletons.class })
public class LoginViewModelTest extends BaseTest {

	private LoginViewModel vm;
	private DAO<User> daoUser;

	@Inject
	public void setInjected(TestPersistenceInitialiser persistenceInitialiser,
			Provider<EntityManager> emProvider,
			LoginViewModel loginViewModel,
			DAO<User> daoUser) {
		this.daoUser = daoUser;
		super.setInjected(persistenceInitialiser, emProvider);
		
		this.vm = loginViewModel;
	}
	
	@Test
	public void testStateInitialisation() {
		assertNotNull(vm.isLoggedIn.get());
	}
	
	@Test
	public void testUserInsert() throws NoSuchAlgorithmException, InvalidKeySpecException {
		daoUser.insert(standardUser());
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
		daoUser.insert(standardUser());
		vm.doLogin(USERNAME, PASSWORD);
		assertEquals(true, vm.isLoggedIn.get());
	}
	
	@Test
	public void testStateAfterUnsuccessfulLoginWithWrongPassword() {
		daoUser.insert(standardUser());
		vm.doLogin(USERNAME, "wrongPassword");
		assertEquals(false, vm.isLoggedIn.get());
	}
	
	@Test
	public void testStateAfterUnsuccessfulLoginWithWrongUsername() {
		daoUser.insert(standardUser());
		vm.doLogin("wrongUser", PASSWORD);
		assertEquals(false, vm.isLoggedIn.get());
	}
	
	@Test
	public void testStateAfterUnsuccessfulLoginWithAwfullyLongUsername()	{
		daoUser.insert(standardUser());
		vm.doLogin(new String(new char[1025]).replace('\0', 'X'), PASSWORD);
		assertEquals(false, vm.isLoggedIn.get());
	}
	
	@Test
	public void testStateAfterUnsuccessfulLoginWithAwfullyLongPassword()	{
		daoUser.insert(standardUser());
		vm.doLogin(USERNAME, new String(new char[1025]).replace('\0', 'X'));
		assertEquals(false, vm.isLoggedIn.get());
	}
	
	@Test
	public void testStateAfterSuccessfulLoginAndLogout() {
		daoUser.insert(standardUser());
		vm.doLogin(USERNAME, PASSWORD);
		vm.doLogout(null);
		assertEquals(false, vm.isLoggedIn.get());
	}
}
