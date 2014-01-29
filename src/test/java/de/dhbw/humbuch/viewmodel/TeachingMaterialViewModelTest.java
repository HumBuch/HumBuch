package de.dhbw.humbuch.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

import de.dhbw.humbuch.guice.GuiceJUnitRunner;
import de.dhbw.humbuch.guice.GuiceJUnitRunner.GuiceModules;
import de.dhbw.humbuch.guice.TestModule;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.TeachingMaterial;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModule.class })
public class TeachingMaterialViewModelTest {
	private TeachingMaterialViewModel bookManagementViewModel;
	private DAO<TeachingMaterial> daoTeachingMaterial;

	@Inject
	public void setPersistenceInitialiser(
			TestPersistenceInitialiser persistenceInitialiser) {
	}

	@Inject
	public void setViewModel(TeachingMaterialViewModel bookManagementViewModel) {
		this.bookManagementViewModel = bookManagementViewModel;
	}

	@Inject
	public void setDAOs(DAO<TeachingMaterial> daoTeachingMaterial) {
		this.daoTeachingMaterial = daoTeachingMaterial;
	}

	@Test
	public void testUpdateExistingTeachingMaterial() {
		daoTeachingMaterial.insert(new TeachingMaterial());
		TeachingMaterial teachingMaterial = daoTeachingMaterial.find(1);
		assertNotNull(teachingMaterial);
		String dateString = new Date().toString();
		teachingMaterial.setComment(dateString);
		bookManagementViewModel.doUpdateTeachingMaterial(teachingMaterial);
		teachingMaterial = daoTeachingMaterial.find(1);
		assertEquals(dateString, teachingMaterial.getComment());
	}
}
