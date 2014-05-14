package de.dhbw.humbuch.view;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.vaadin.ui.Field;

import de.dhbw.humbuch.guice.GuiceJUnitRunner;
import de.dhbw.humbuch.guice.GuiceJUnitRunner.GuiceModules;
import de.dhbw.humbuch.guice.TestModule;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;
import de.dhbw.humbuch.model.entity.User;
import de.dhbw.humbuch.viewmodel.Properties;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModule.class })
public class SettingsViewTest extends BaseTest {

	private SettingsView settingsView;

	@Inject
	public void setInjected(MVVMConfig mvvmConfig,
			TestPersistenceInitialiser testPersistenceInitialiser,
			SettingsView view,
			Properties properties,
			DAO<User> daoUser) {
		this.settingsView = view;
		super.setInjected(mvvmConfig, testPersistenceInitialiser, view, properties, daoUser);
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
