package de.dhbw.humbuch.viewmodel;

import java.util.Collection;
import java.util.Set;

import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.AfterVMBinding;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

public class BookManagementViewModel {

	public interface TeachingMaterials extends State<Set<TeachingMaterial>> {
	}

	public interface DoUpdateTeachingMaterial extends ActionHandler {
	}

	@ProvidesState(TeachingMaterials.class)
	public final State<Collection<TeachingMaterial>> teachingMaterials = new BasicState<>(
			Collection.class);

	private DAO<TeachingMaterial> daoTeachingMaterial;

	/**
	 * Constructor
	 * 
	 * @param daoTeachingMaterial
	 *            DAO implementation to access TeachingMaterial entities
	 */
	@Inject
	public BookManagementViewModel(DAO<TeachingMaterial> daoTeachingMaterial) {
		this.daoTeachingMaterial = daoTeachingMaterial;
	}

	@AfterVMBinding
	private void afterVMBinding() {
		updateTeachingMaterial();
	}
	
	private void updateTeachingMaterial() {
		teachingMaterials.set(daoTeachingMaterial.findAll());
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
		if (daoTeachingMaterial.find(teachingMaterial.getId()) != null) {
			daoTeachingMaterial.update(teachingMaterial);
		} else {
			daoTeachingMaterial.insert(teachingMaterial);
		}
		updateTeachingMaterial();
	}
}
