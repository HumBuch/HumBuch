package de.dhbw.humbuch.viewmodel;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static de.dhbw.humbuch.test.TestUtils.*;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.dhbw.humbuch.guice.GuiceJUnitRunner;
import de.dhbw.humbuch.guice.TestModule;
import de.dhbw.humbuch.guice.GuiceJUnitRunner.GuiceModules;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.Category;
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.SettingsEntry;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModule.class })
public class SettingsViewModelTest extends BaseTest {

	private SettingsViewModel vm;
	private Properties properties;
	private DAO<Category> daoCategory;
	private DAO<SchoolYear> daoSchoolYear;
	private DAO<SettingsEntry> daoSettingsEntry;

	@Inject
	public void setInjected(TestPersistenceInitialiser persistenceInitialiser,
			Provider<EntityManager> emProvider,
			SettingsViewModel settingsViewModel,
			Properties properties,
			DAO<Category> daoCategory,
			DAO<SchoolYear> daoSchoolYear,
			DAO<SettingsEntry> daoSettingsEntry) {
		this.properties = properties;
		this.daoCategory = daoCategory;
		this.daoSchoolYear = daoSchoolYear;
		this.daoSettingsEntry = daoSettingsEntry;
		super.setInjected(persistenceInitialiser, emProvider);
		
		this.vm = settingsViewModel;
	}
	
	@Before
	public void refreshViewModel() {
		properties.currentUser.set(user());
		vm.refresh();
	}
	
	@Test
	public void testStateInitialisation() {
		assertNotNull(vm.schoolYears.get());
		assertNotNull(vm.categories.get());
		assertNotNull(vm.settingsEntries.get());
		assertNotNull(vm.userName.get());
		assertNotNull(vm.userEmail.get());
	}
	
	@Test
	public void testStateCategoriesInsertOne() {
		daoCategory.insert(category());
		daoCategory.insert(category());
		vm.refresh();
		assertEquals(2, vm.categories.get().size());
	}
	
	@Test
	public void testStateCategoriesInsertThreeDeleteOne() {
		daoCategory.insert(category());
		daoCategory.insert(category());
		Category category = daoCategory.insert(category());
		vm.refresh();
		assertEquals(3, vm.categories.get().size());
		
		daoCategory.delete(category);
		vm.refresh();
		assertEquals(2, vm.categories.get().size());
	}
	
	@Test
	public void testStateSchoolYearsInsertTwo() {
		daoSchoolYear.insert(schoolYearSecondTermEnded());
		daoSchoolYear.insert(schoolYearFirstTermStarted());
		vm.refresh();
		assertEquals(2, vm.schoolYears.get().size());
	}
	
	@Test
	public void testStateSchoolYearsInsertThreeDeleteOne() {
		daoSchoolYear.insert(schoolYearSecondTermEnded());
		daoSchoolYear.insert(schoolYearFirstTermStarted());
		SchoolYear schoolYear = daoSchoolYear.insert(schoolYearSecondTermStarted());
		vm.refresh();
		assertEquals(3, vm.schoolYears.get().size());
		
		daoSchoolYear.delete(schoolYear);
		vm.refresh();
		assertEquals(2, vm.schoolYears.get().size());
	}
	
	@Test
	public void testStateSettingsEntriesInsertTwo() {
		daoSettingsEntry.insert(settingsEntry());
		daoSettingsEntry.insert(settingsEntry());
		vm.refresh();
		assertEquals(2, vm.settingsEntries.get().size());
	}
	//assertNotNull(vm.passwordChangeStatus.get());
}
