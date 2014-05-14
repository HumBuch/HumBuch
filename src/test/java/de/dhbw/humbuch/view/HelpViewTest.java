package de.dhbw.humbuch.view;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

import de.dhbw.humbuch.guice.GuiceJUnitRunner;
import de.dhbw.humbuch.guice.GuiceJUnitRunner.GuiceModules;
import de.dhbw.humbuch.guice.TestModule;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;
import de.dhbw.humbuch.model.entity.User;
import de.dhbw.humbuch.viewmodel.Properties;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModule.class })
public class HelpViewTest extends BaseTest {

	private HelpView helpView;

	@Inject
	public void setInjected(MVVMConfig mvvmConfig,
			TestPersistenceInitialiser testPersistenceInitialiser,
			HelpView view, Properties properties, DAO<User> daoUser) {
		this.helpView = view;
		super.setInjected(mvvmConfig, testPersistenceInitialiser, view,
				properties, daoUser);
	}

	@Test
	public void testSetHelpText() {
		helpView.setHelpText("Test");
	}
}