package de.dhbw.humbuch.view;

import static de.dhbw.humbuch.test.TestUtils.randomUser;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.vaadin.ui.Field;

import de.dhbw.humbuch.guice.GuiceJUnitRunner;
import de.dhbw.humbuch.guice.TestModule;
import de.dhbw.humbuch.guice.GuiceJUnitRunner.GuiceModules;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;
import de.dhbw.humbuch.model.entity.User;
import de.dhbw.humbuch.viewmodel.Properties;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModule.class })
public class SettingsViewTest {

	private SettingsView settingsView;
	private Properties properties;
	private DAO<User> daoUser;

	@Inject
	public void setInjected(MVVMConfig mvvmConfig,
			TestPersistenceInitialiser testPersistenceInitialiser,
			SettingsView settingsView,
			Properties properties,
			DAO<User> daoUser) {
		this.settingsView = settingsView;
		this.properties = properties;
		this.daoUser = daoUser;

	}
	
	@Before
	public void setLoggedInUser() {
		properties.currentUser.set(daoUser.insert(randomUser()));
	}

	@Test
	public void testInstantiation() {
		assertThat(settingsView, notNullValue());
	}
	
	@Test
	public void testEnter() {
		settingsView.enter(null);
	}
	
	@Test
	public void testTitleNotNull() {
		assertThat(settingsView.getTitle(), notNullValue());
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void testCommitFields() {
		settingsView.commitFields(new ArrayList<Field>());
	}
	
	@Test
	@SuppressWarnings("rawtypes")
	public void testDiscardFields() {
		settingsView.discardFields(new ArrayList<Field>());
	}
}
