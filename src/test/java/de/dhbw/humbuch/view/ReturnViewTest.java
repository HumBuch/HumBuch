package de.dhbw.humbuch.view;

import org.junit.runner.RunWith;

import com.google.inject.Inject;

import de.dhbw.humbuch.guice.GuiceJUnitRunner;
import de.dhbw.humbuch.guice.TestModule;
import de.dhbw.humbuch.guice.GuiceJUnitRunner.GuiceModules;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;
import de.dhbw.humbuch.model.entity.User;
import de.dhbw.humbuch.viewmodel.Properties;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModule.class })
public class ReturnViewTest extends BaseTest {
	
	@Inject
	public void setInjected(MVVMConfig mvvmConfig,
			TestPersistenceInitialiser testPersistenceInitialiser, ReturnView view,
			Properties properties, DAO<User> daoUser) {
		super.setInjected(mvvmConfig, testPersistenceInitialiser, view, properties,
				daoUser);
	}
}
