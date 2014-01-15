package de.dhbw.humbuch.viewmodel;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.AfterVMBinding;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Category;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

public class BookManagementViewModel {

	public interface TeachingMaterials extends State<Collection<TeachingMaterial>> {
	}
	public interface TeachingMaterialInfo extends State<TeachingMaterial>{
	}
	public interface Categories extends State<Collection<Category>> {
	}

	public interface DoUpdateTeachingMaterial extends ActionHandler {
	}
	public interface DoFetchTeachingMaterial extends ActionHandler{
	}
	public interface DoDeleteTeachingMaterial extends ActionHandler{
	}
	public interface DoUpdateCategory extends ActionHandler{
	}


	@ProvidesState(TeachingMaterials.class)
	public final State<Collection<TeachingMaterial>> teachingMaterials = new BasicState<>(
			Collection.class);
	@ProvidesState(TeachingMaterialInfo.class)
	public final State<TeachingMaterial> teachingMaterialInfo = new BasicState<>(TeachingMaterial.class);
	@ProvidesState(Categories.class)
	public final State<Map<Integer,Category>> categories = new BasicState<>(Map.class);

	private DAO<TeachingMaterial> daoTeachingMaterial;
	private DAO<Category> daoCategory;
	private DAO<BorrowedMaterial> daoBorrowedMaterial;
	/**
	 * Constructor
	 * 
	 * @param daoTeachingMaterial
	 *            DAO implementation to access TeachingMaterial entities
	 */
	@Inject
	public BookManagementViewModel(DAO<TeachingMaterial> daoTeachingMaterial, DAO<Category> daoCategory) {
		this.daoTeachingMaterial = daoTeachingMaterial;
		this.daoCategory = daoCategory;
	}

	@AfterVMBinding
	private void afterVMBinding() {
		updateTeachingMaterial();
		updateCategory();
	}
	
	private void updateTeachingMaterial() {
		teachingMaterials.set(daoTeachingMaterial.findAll());	
	}
	
	private void updateCategory() {
		Collection<Category> categories = daoCategory.findAll();
		Map<Integer,Category> tempMap = new HashMap<Integer,Category>();
		for(Category category : categories) {
			tempMap.put(category.getId(), category);
		}
		this.categories.set(tempMap);
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
	/**
	 * Fetches a teaching material and sets the teachingMaterialInfoState
	 * @param id
	 * 		the id of the teaching material to be fetched
	 */
	@HandlesAction(DoFetchTeachingMaterial.class)
	public void doFetchTeachingMaterial(int id) {
		teachingMaterialInfo.set(daoTeachingMaterial.find(id));
	}

	/**
	 * Deletes the teaching material or sets the validUntil date to the current Date. 
	 * This decision depends on whether the teaching material is borrowed by a student.
	 * @param teachingMaterial
	 * 			the teacing material to be updated or deleted
	 */
	@HandlesAction(DoDeleteTeachingMaterial.class)
	public void doDeleteTeachingMaterial(TeachingMaterial teachingMaterial) {
		Collection<BorrowedMaterial> borrowedMaterial = daoBorrowedMaterial.findAllWithCriteria(
						Restrictions.eq("teachingMaterialId", teachingMaterial.getId()));
		if(borrowedMaterial.size()==0) {
			daoTeachingMaterial.delete(teachingMaterial);
		}
		else {
			teachingMaterial.setValidUntil(new Date());
			daoTeachingMaterial.update(teachingMaterial);
		}
	}
	
	/**
	 * Either persist a newly created category or update an existing one
	 * 
	 * @param category
	 * 			a category to be persisted or updated
	 */
	@HandlesAction(DoUpdateCategory.class)
	public void doUpdateCategory(Category category) {
		if (category == null) {
			return;
		}
		if (daoCategory.find(category.getId()) != null) {
			daoCategory.update(category);
		} else {
			daoCategory.insert(category);
		}
		updateCategory();
	}
}
