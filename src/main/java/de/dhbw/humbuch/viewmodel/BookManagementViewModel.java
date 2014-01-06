package de.dhbw.humbuch.viewmodel;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.AfterVMBinding;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.Category;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

/**
 * 
 * 
 * @author Martin Wentzel
 *
 */
public class BookManagementViewModel {

	/**
	 * The state and ActionHandler interfaces for exchanging data and actions between View and ViewModel
	 */
	public interface BookName extends State<String> {}
	public interface BookIdentifyer extends State<String> {}
	public interface BookProducer extends State<String> {}
	public interface BookFromGrade extends State<String> {}
	public interface BookToGrade extends State<String> {}
	public interface tableData extends State<TeachingMaterial[]> {}
	
	public interface DoGetBook extends ActionHandler{}
	public interface DoSaveBook extends ActionHandler{}
	
	/**
	 * State initialization
	 */
	@ProvidesState(BookName.class)
	public final BasicState<String> bookName = new BasicState<String>(String.class);
	@ProvidesState(BookIdentifyer.class)
	public final BasicState<String> bookIdentifyer = new BasicState<String>(String.class);
	@ProvidesState(BookProducer.class)
	public final BasicState<String> bookProducer = new BasicState<String>(String.class);
	@ProvidesState(BookFromGrade.class)
	public final BasicState<String> bookFromGrade = new BasicState<String>(String.class);
	@ProvidesState(BookToGrade.class)
	public final BasicState<String> bookToGrade= new BasicState<String>(String.class);
	@ProvidesState(tableData.class)
	public final State<Set<TeachingMaterial>> tableData = new BasicState<Set<TeachingMaterial>>(Set.class);
	
	/**
	 * DAO initialization
	 */
	@Inject
	private DAO<TeachingMaterial> daoTeachingMaterial;
	@Inject
	private DAO<Category> daoCategory;
	
	/**
	 * This function is called immediately after the ViewModel-binding. 
	 * The teaching material data is retrieved from the database and the tableData State is set. This leads to the display of the data in the frontend.
	 */
	@AfterVMBinding
	public void initiateState() {
		tableData.set(new HashSet<TeachingMaterial>(daoTeachingMaterial.findAll()));
	}
	
	/**
	 * Gets the requested teaching material from the database and sets the corresponding states. 
	 * @param id The unique database-id of a teaching material.
	 */
	@HandlesAction(DoGetBook.class)
	public void doGetBook(String id) {
		TeachingMaterial teachingMaterial = daoTeachingMaterial.find(Integer.parseInt(id));
		bookName.set(teachingMaterial.getName());
		bookIdentifyer.set(teachingMaterial.getIdentifyingNumber());
		bookProducer.set(teachingMaterial.getProducer());
		bookFromGrade.set(Integer.toString(teachingMaterial.getFromGrade()));
		bookToGrade.set(Integer.toString(teachingMaterial.getToGrade()));
	}
	
	/**
	 * Saves a teaching material to the database. It executes an insert or update, depending whether the teaching material is new or updated. 
	 * A new teaching material is given, when the id argument is 0.
	 * 
	 * @param id The id of the given teaching material data. 0 when a new material is added
	 * @param name The name of a teaching material
	 * @param identifyer The identifyer of a teaching material
	 * @param producer The producer of a teaching material
	 * @param fromGrade The grade from when this teaching material is used.
	 * @param toGrade The grade until this teaching material can be used.
	 */
	@HandlesAction(DoSaveBook.class)
	public void doSaveBook(String id,String name, String identifyer, String producer, String fromGrade, String toGrade) {
		if(id.length()==0) {
			Category category = daoCategory.find(1);
			TeachingMaterial teachingMaterial = new TeachingMaterial.Builder(category, name, identifyer, new Date(2013,12,01)).build();
			teachingMaterial.setProducer(producer);
			teachingMaterial.setFromGrade(Integer.parseInt(fromGrade));
			teachingMaterial.setToGrade(Integer.parseInt(toGrade));
			daoTeachingMaterial.insert(teachingMaterial);			
		}
		else {
			TeachingMaterial teachingMaterial = daoTeachingMaterial.find(Integer.parseInt(id));
			teachingMaterial.setName(name);
			teachingMaterial.setIdentifyingNumber(identifyer);
			teachingMaterial.setProducer(producer);
			teachingMaterial.setFromGrade(Integer.parseInt(fromGrade));
			teachingMaterial.setToGrade(Integer.parseInt(toGrade));
			daoTeachingMaterial.update(teachingMaterial);
		}
		tableData.set(new HashSet<TeachingMaterial>(daoTeachingMaterial.findAll()));		
	}
}