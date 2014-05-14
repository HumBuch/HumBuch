package de.dhbw.humbuch.viewmodel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.Order;
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
import de.dhbw.humbuch.model.entity.Dunning;
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.SchoolYear.Term;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

/**
 * 
 * @author Martin Wentzel
 *
 */
public class DunningViewModel {

	public interface Dunnings extends State<Collection<Dunning>> {}
	public interface doUpdateDunning extends ActionHandler {}

	@ProvidesState(Dunnings.class)
	public final State<Collection<Dunning>> dunnings = new BasicState<>(Collection.class);

	private DAO<Dunning> daoDunning;
	private DAO<BorrowedMaterial> daoBorrowedMaterial;
	private DAO<SchoolYear> daoSchoolYear;
	
	private SchoolYear recentlyActiveSchoolYear;
	private Properties properties;

	@Inject
	public DunningViewModel(DAO<Dunning> daoDunning, DAO<BorrowedMaterial> daoBorrowedMaterial, DAO<SchoolYear> daoSchoolYear, Properties properties) {
		this.daoDunning = daoDunning;
		this.daoBorrowedMaterial = daoBorrowedMaterial;
		this.daoSchoolYear = daoSchoolYear;
		this.properties = properties;
	}

	@AfterVMBinding
	public void refresh() {
		updateSchoolYear();
		checkIfDunningShouldBeClosed();
		createFirstDunnings();
		createSecondDunnings();
		updateState();
	}

	/**
	 * Creates a dunning for an overdue borrowed material.
	 */
	private void createFirstDunnings() {
		List<BorrowedMaterial> listBorrowedMaterial = daoBorrowedMaterial.findAll();
		Map<Integer, List<BorrowedMaterial>> overdueMaterials = new HashMap<Integer, List<BorrowedMaterial>>();
		for (BorrowedMaterial borrowedMaterial : listBorrowedMaterial) {
			if (borrowedMaterial.getBorrowUntil() == null) {
				if (!isNeededNextTerm(borrowedMaterial)
						&& borrowedMaterial.isReceived()) {
					if (Calendar
							.getInstance()
							.after(addDeadlineToDateOfFirstDunning(recentlyActiveSchoolYear
									.getEndOf(borrowedMaterial.getTeachingMaterial().getToTerm())))
							|| !(borrowedMaterial.getTeachingMaterial()
									.getToGrade() == borrowedMaterial
									.getStudent().getGrade().getGrade())) {
						addBorrowedMaterialFromStudentToMap(overdueMaterials,
								borrowedMaterial);
					}
				}
			} else if (Calendar.getInstance().after(addDeadlineToDateOfFirstDunning(borrowedMaterial.getBorrowUntil()))) {// manually borrowed
					addBorrowedMaterialFromStudentToMap(overdueMaterials, borrowedMaterial);
			}
		}

		for (Integer key : overdueMaterials.keySet()) {
			List<BorrowedMaterial> entry = overdueMaterials.get(key);
			List<Dunning> existingFirstDunningsForStudent = daoDunning.findAllWithCriteria(
							Restrictions.eq("type", Dunning.Type.TYPE1),
							Restrictions.eq("student", entry.get(0).getStudent()
					));
			Set<BorrowedMaterial> allDunnedBorrowedMaterials = new HashSet<BorrowedMaterial>();
			for (Dunning existingDunning : existingFirstDunningsForStudent) {
				allDunnedBorrowedMaterials.addAll(existingDunning.getBorrowedMaterials());
			}
			entry.removeAll(allDunnedBorrowedMaterials);
			if (existingFirstDunningsForStudent.size() == 0 || entry.size() > 0) {
				Dunning newDunning = new Dunning.Builder(entry.get(0)
						.getStudent())
						.type(Dunning.Type.TYPE1)
						.status(Dunning.Status.OPENED)
						.borrowedMaterials(new HashSet<BorrowedMaterial>(entry))
						.build();
				daoDunning.insert(newDunning);
			}
		}
	}

	/**
	 * Adds a deadline to a date. The deadlines value is stored in the settings property.
	 * 
	 * @param endOf the date to be modified
	 * @return a calendar object with the modified endOf parameter
	 */
	private Calendar addDeadlineToDateOfFirstDunning(Date endOf) {
		String deadline = properties.settings.get().get("firstDunningDeadline");		
		return addDeadlineToDate(Integer.parseInt(deadline), endOf);
	}

	/**
	 * Adds a borrowed material to the map. The key of the map is the student id.
	 * When a key is not existent in the map, a new entry is created. 
	 * The borrowed material is added to the corresponding list of its student id.  
	 * 
	 * @param map a map of <code><Integer, List<BorrowedMaterial>></code> 
	 * @param borrowedMaterial a borrowed material to be added to the map
	 */
	private void addBorrowedMaterialFromStudentToMap(Map<Integer, List<BorrowedMaterial>> map,	BorrowedMaterial borrowedMaterial) {
		if (!map.containsKey(borrowedMaterial.getStudent().getId())) {
			map.put(borrowedMaterial.getStudent().getId(),
					new ArrayList<BorrowedMaterial>());
		}
		map.get(borrowedMaterial.getStudent().getId()).add(borrowedMaterial);
	}


	/**
	 * Creates the second dunning for overdue first dunnings. 
	 * A first dunning is overdue, when the dunning is not resolved within a specific time frame.
	 * This function gets all first dunnings which are in an SENT state. 
	 * Then it checks for each dunning whether it is overdue. If so, a second dunning is created and the first dunning is closed
	 * 
	 */
	private void createSecondDunnings() {
		List<Dunning> sentFirstDunnings = daoDunning.findAllWithCriteria(
				Restrictions.eq("status", Dunning.Status.SENT),
				Restrictions.eq("type", Dunning.Type.TYPE1));
		for (Dunning dunning : sentFirstDunnings) {
			if (Calendar.getInstance().after(
					addDeadlineToDateOfSecondDunning(dunning
							.getStatusDate(Dunning.Status.SENT)))) {
					Dunning newDunning = new Dunning.Builder(
							dunning.getStudent()).type(Dunning.Type.TYPE2)
							.status(Dunning.Status.OPENED)
							.borrowedMaterials(new HashSet<BorrowedMaterial>(dunning.getBorrowedMaterials()))
							.build();
					daoDunning.insert(newDunning);
					dunning.setStatus(Dunning.Status.CLOSED);
					daoDunning.update(dunning);
			}
		}
	}

	/**
	 * Adds a deadline to a date. The deadline's value is stored in the settings property.
	 * 
	 * @param endOf the date to be modified
	 * @return a calendar object with the modified endOf parameter
	 */
	private Calendar addDeadlineToDateOfSecondDunning(Date endOf) {
		String deadline = properties.settings.get().get("secondDunningDeadline");		
		return addDeadlineToDate(Integer.parseInt(deadline), endOf);
	}
	
	/**
	 * Adds a specific number of days to a date
	 * 
	 * @param deadline number of days
	 * @param date the date to be modified 
	 * @return a calendar object with the modified date 
	 */
	private Calendar addDeadlineToDate(int deadline, Date date) {
		Calendar returnDate = Calendar.getInstance();
		returnDate.setTime(date);
		returnDate.add(Calendar.DATE, deadline);
		return returnDate;
	}

	/**
	 * Checks whether a specific borrowed material is needed next term. 
	 * Thereto the according teaching material with its grade and term information is retrieved.
	 * 
	 * @param borrowedMaterial the borrowed material which is checked
	 * @return <code>true</code> if the borrowed materials is needed next term; <code>false</code> otherwise
	 */
	private boolean isNeededNextTerm(BorrowedMaterial borrowedMaterial) {
		TeachingMaterial teachingMaterial = borrowedMaterial.getTeachingMaterial();

		Integer toGrade = teachingMaterial.getToGrade();
		int currentGrade = borrowedMaterial.getStudent().getGrade().getGrade();
		Term toTerm = teachingMaterial.getToTerm();
		Term currentTerm = recentlyActiveSchoolYear.getRecentlyActiveTerm();

		if (toGrade == null)
			return false;

		return (toGrade > currentGrade || (toGrade == currentGrade && (toTerm.compareTo(currentTerm) > 0)));
	}
	
	/**
	 * Sets the value of the recentlyActiveSchoolYear variable.
	 */
	private void updateSchoolYear() {
		recentlyActiveSchoolYear = daoSchoolYear.findSingleWithCriteria(
				Order.desc("toDate"),
				Restrictions.le("fromDate", new Date()));
	}
	
	/**
	 * Checks for every open dunning whether all contained borrowed materials have been returned.
	 * When all borrowed materials are returned, close the dunning.
	 */
	private void checkIfDunningShouldBeClosed() {
		List<Dunning> openDunnings = daoDunning.findAllWithCriteria(
				Restrictions.or(
							Restrictions.eq("status", Dunning.Status.SENT),
							Restrictions.eq("status", Dunning.Status.OPENED)));
		for (Dunning dunning : openDunnings) {
			Set<BorrowedMaterial> borrowedMaterials = dunning.getBorrowedMaterials();
			Boolean toBeClosed = true;
			for(BorrowedMaterial material : borrowedMaterials) {
				if(material.getReturnDate() == null) {
					toBeClosed = false;
					break;
				}
			}
			if(toBeClosed) {
				dunning.setStatus(Dunning.Status.CLOSED);
				daoDunning.update(dunning);
			}
		}
	}

	/**
	 * Updates the dunnings state
	 */
	private void updateState() {
		Collection<Dunning> dunnings = daoDunning.findAll();
		this.dunnings.set(dunnings);
	}

	/**
	 * Updates a dunning
	 * 
	 * @param dunning
	 *            the dunning to be updated
	 */
	@HandlesAction(doUpdateDunning.class)
	public void doUpdateDunning(Dunning dunning) {
		daoDunning.update(dunning);
		updateState();
	}
}
