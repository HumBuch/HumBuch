package de.dhbw.humbuch.viewmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;

import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Provider;

import de.dhbw.humbuch.guice.GuiceJUnitRunner;
import de.dhbw.humbuch.guice.GuiceJUnitRunner.GuiceModules;
import de.dhbw.humbuch.guice.TestModuleWithoutSingletons;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.TeachingMaterial;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;


@RunWith(GuiceJUnitRunner.class)
@GuiceModules({ TestModuleWithoutSingletons.class })
/**
 * @author Martin Wentzel
 */
public class TeachingMaterialViewModelTest extends BaseTest {
	private TeachingMaterialViewModel teachingMaterialViewModel;
	private DAO<TeachingMaterial> daoTeachingMaterial;
	private DAO<BorrowedMaterial> daoBorrowedMaterial;

	@Inject
	public void setInjected(TestPersistenceInitialiser persistenceInitialiser,
			Provider<EntityManager> emProvider,
			TeachingMaterialViewModel dunningViewModel, DAO<TeachingMaterial> daoTeachingMaterial,
			DAO<BorrowedMaterial> daoBorrowedMaterial) {
		super.setInjected(persistenceInitialiser, emProvider);
		
		this.teachingMaterialViewModel = dunningViewModel;
		this.daoTeachingMaterial = daoTeachingMaterial;
		this.daoBorrowedMaterial = daoBorrowedMaterial;
	}
	
	private Date today() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		return calendar.getTime();
	}
	
	private void addNullTeachingMaterial() {
		TeachingMaterial teachingMaterial = null;
		teachingMaterialViewModel.doUpdateTeachingMaterial(teachingMaterial);
	}
	
	private void addTeachingMaterial(){
		TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(null, "Book1", null, today()).build();
		teachingMaterialViewModel.doUpdateTeachingMaterial(teachingMaterial);
	}
	
	private void updateTeachingMaterial() {
		TeachingMaterial teachingMaterial = daoTeachingMaterial.findSingleWithCriteria(Restrictions.eq("name", "Book1"));
		teachingMaterial.setName("Book2");
		teachingMaterialViewModel.doUpdateTeachingMaterial(teachingMaterial);
	}
	
	private void deleteTeachingMaterial() {
		TeachingMaterial teachingMaterial = daoTeachingMaterial.findSingleWithCriteria(Restrictions.eq("name", "Book1"));
		teachingMaterialViewModel.doDeleteTeachingMaterial(teachingMaterial);
	}
	
	private void borrowTeachingMaterial() {
		TeachingMaterial teachingMaterial = daoTeachingMaterial.findSingleWithCriteria(Restrictions.eq("name", "Book1"));
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(null, teachingMaterial, today()).build();
		borrowedMaterial.setReceived(true);
		daoBorrowedMaterial.insert(borrowedMaterial);
	}
	
	@Before
	public void refreshDunningViewModel() {
		teachingMaterialViewModel.refresh();
	}
	
	@Test
	public void testStateInitialisation() {
		assertNotNull(teachingMaterialViewModel.teachingMaterials.get());
	}
	
	@Test
	public void testInitialTeachingMaterials() {
		assertEquals(0, teachingMaterialViewModel.teachingMaterials.get().size());
	}
	
	@Test
	public void testAddNullTeachingMaterial() {
		addNullTeachingMaterial();
		assertEquals(0, teachingMaterialViewModel.teachingMaterials.get().size());
	}
	
	@Test
	public void testAddTeachingMaterial() {
		addTeachingMaterial();
		assertEquals(1, teachingMaterialViewModel.teachingMaterials.get().size());
	}
	
	@Test
	public void testUpdateTeachingMaterial() {
		addTeachingMaterial();
		updateTeachingMaterial();
		assertEquals(1, teachingMaterialViewModel.teachingMaterials.get().size());
	}
	
	@Test
	public void testDeleteUnborrowedTeachingMaterial() {
		addTeachingMaterial();
		deleteTeachingMaterial();
		assertEquals(0, teachingMaterialViewModel.teachingMaterials.get().size());
	}
	
	@Test
	public void testDeleteBorrowedTeachingMaterial() {
		addTeachingMaterial();
		borrowTeachingMaterial();
		deleteTeachingMaterial();
		assertEquals(1, teachingMaterialViewModel.teachingMaterials.get().size());
	}
}
