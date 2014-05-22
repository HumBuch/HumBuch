package de.dhbw.humbuch.viewmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.hibernate.criterion.Restrictions;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.AfterVMBinding;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.event.MessageEvent;
import de.dhbw.humbuch.event.MessageEvent.Type;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Category;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

/**
 * Provides the {@link TeachingMaterialView} with data to display and manage teaching materials
 * 
 * @author David Vitt
 * @author Martin Wentzel
 *
 */
public class TeachingMaterialViewModel {

	public interface TeachingMaterials extends State<Collection<TeachingMaterial>> {}
	public interface Categories extends State<Collection<Category>> {}
	public interface StandardCategory extends State<Category> {}

	public interface DoUpdateTeachingMaterial extends ActionHandler {}
	public interface DoFetchTeachingMaterial extends ActionHandler {}
	public interface DoDeleteTeachingMaterial extends ActionHandler {}
	public interface DoUpdateCategory extends ActionHandler {}

	@ProvidesState(TeachingMaterials.class)
	public final State<Collection<TeachingMaterial>> teachingMaterials = new BasicState<>(Collection.class);

	@ProvidesState(Categories.class)
	public final State<Collection<Category>> categories = new BasicState<>(Collection.class);
	
	@ProvidesState(StandardCategory.class)
	public final State<Category> standardCategory = new BasicState<>(Category.class);

	private DAO<TeachingMaterial> daoTeachingMaterial;
	private DAO<Category> daoCategory;
	private DAO<BorrowedMaterial> daoBorrowedMaterial;
	private EventBus eventBus;

	/**
	 * Constructor
	 * 
	 * @param daoTeachingMaterial
	 *            DAO implementation to access TeachingMaterial entities
	 */
	@Inject
	public TeachingMaterialViewModel(DAO<TeachingMaterial> daoTeachingMaterial,
			DAO<Category> daoCategory,
			DAO<BorrowedMaterial> daoBorrowedMaterial, EventBus eventBus) {
		this.daoTeachingMaterial = daoTeachingMaterial;
		this.daoCategory = daoCategory;
		this.daoBorrowedMaterial = daoBorrowedMaterial;
		this.eventBus = eventBus;
	}

	@AfterVMBinding
	public void initialiseStates() {
		teachingMaterials.set(new ArrayList<TeachingMaterial>());
		categories.set(new ArrayList<Category>());
		standardCategory.set(null);
	}
	
	public void refresh() {
		updateTeachingMaterials();
		updateCategories();
	}

	private void updateTeachingMaterials() {
		teachingMaterials.set(daoTeachingMaterial.findAll());
	}

	private void updateCategories() {
		categories.set(daoCategory.findAll());
		standardCategory.set(daoCategory.findSingleWithCriteria(Restrictions.ilike("name", "B%ch%")));
	}

	/**
	 * Either persist a newly created TeachingMaterial or update an existing one
	 * 
	 * @param teachingMaterial
	 *            a TeachingMaterial to be persisted or updated
	 */
	@HandlesAction(DoUpdateTeachingMaterial.class)
	public void doUpdateTeachingMaterial(TeachingMaterial teachingMaterial) {
		if (teachingMaterial == null) {
			return;
		}
		
		daoTeachingMaterial.update(teachingMaterial);
		updateTeachingMaterials();
	}

	/**
	 * Deletes the teaching material or sets the validUntil date to the current
	 * Date. This decision depends on whether the teaching material is borrowed
	 * by a student.
	 * 
	 * @param teachingMaterial
	 *            the teaching material to be updated or deleted
	 */
	@HandlesAction(DoDeleteTeachingMaterial.class)
	public void doDeleteTeachingMaterial(TeachingMaterial teachingMaterial) {
		Collection<BorrowedMaterial> borrowedMaterial = daoBorrowedMaterial
				.findAllWithCriteria(
						Restrictions.eq("teachingMaterial", teachingMaterial),
						Restrictions.eq("received", true));
		if (borrowedMaterial.isEmpty()) {
			daoTeachingMaterial.delete(teachingMaterial);
			updateTeachingMaterials();
			eventBus.post(new MessageEvent("Löschen erfolgreich",
					"Das Lehrmittel wurde gelöscht.", Type.INFO));
		} else {
			teachingMaterial.setValidUntil(new Date());
			daoTeachingMaterial.update(teachingMaterial);
			updateTeachingMaterials();
			eventBus.post(new MessageEvent(
					"Löschen nicht möglich",
					"Das Lehrmittel ist noch ausgeliehen. \n Das Gültigkeitsdatum wurde jedoch auf das heutige Datum gesetzt.",
					Type.INFO));
		}
	}
}
