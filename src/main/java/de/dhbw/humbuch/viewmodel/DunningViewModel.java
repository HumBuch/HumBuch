package de.dhbw.humbuch.viewmodel;

import java.util.Collection;
import java.util.Set;

import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;

import de.davherrmann.mvvm.ActionHandler;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.AfterVMBinding;
import de.davherrmann.mvvm.annotations.HandlesAction;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.Dunning;

public class DunningViewModel {

	public interface StudentsDunned extends State<Collection<Dunning>> {
	}
	
	public interface StudentsToDun extends State<Collection<Dunning>>{
	}
	
	public interface doUpdateDunning extends ActionHandler{
	}
	
	@ProvidesState(StudentsDunned.class)
	public final State<Collection<Dunning>> studentsDunned = new BasicState<>(Collection.class);
	@ProvidesState(StudentsToDun.class)
	public final State<Collection<Dunning>> studentsToDun = new BasicState<>(Collection.class);
	
	private DAO<Dunning> daoDunning;
	
	@Inject
	public DunningViewModel(DAO<Dunning> daoDunning) {
		this.daoDunning = daoDunning;
	}
	
	@AfterVMBinding
	private void afterVMBinding() {
		updateStates();
	}
	
	private void updateStates() {
		Collection<Dunning> alreadyDunned = daoDunning.findAllWithCriteria(Restrictions.eq("stats", Dunning.Status.SENT));
		Collection<Dunning> toBeDunned = daoDunning.findAllWithCriteria(Restrictions.eq("stats", Dunning.Status.OPENED));
		studentsDunned.set(alreadyDunned);
		studentsToDun.set(toBeDunned);		
	}

	/**
	 * Updates a dunning
	 * @param dunning
	 * 			the dunning to be updated
	 */
	@HandlesAction(doUpdateDunning.class)
	public void doUpdateDunning(Dunning dunning) {
		daoDunning.update(dunning);
		updateStates();
	}
	
}
