package de.dhbw.humbuch.viewmodel;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.dhbw.humbuch.guice.GuiceJUnitRunner;
import de.dhbw.humbuch.guice.TestModule;
import de.dhbw.humbuch.guice.GuiceJUnitRunner.GuiceModules;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModule.class })
public class StudentInformationViewModelTest extends BaseTest {

	private StudentInformationViewModel vm;

	@Inject
	public void setInjected(TestPersistenceInitialiser persistenceInitialiser,
			Provider<EntityManager> emProvider,
			StudentInformationViewModel studentInformationViewModel) {
		super.setInjected(persistenceInitialiser, emProvider);
		
		this.vm = studentInformationViewModel;
	}
	
	@Before
	public void refreshViewModel() {
		vm.refresh();
	}
	
	@Test
	public void testStateInitialisation() {
		assertNotNull(vm.students.get());
	}
}
