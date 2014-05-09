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

public class DunningViewModel {

//	public interface StudentsDunned extends State<Collection<Dunning>> {}
//	public interface StudentsToDun extends State<Collection<Dunning>> {}
	public interface Dunnings extends State<Collection<Dunning>> {}

	public interface doUpdateDunning extends ActionHandler {}

//	@ProvidesState(StudentsDunned.class)
//	public final State<Collection<Dunning>> studentsDunned = new BasicState<>(Collection.class);
//
//	@ProvidesState(StudentsToDun.class)
//	public final State<Collection<Dunning>> studentsToDun = new BasicState<>(Collection.class);
	@ProvidesState(Dunnings.class)
	public final State<Collection<Dunning>> dunnings = new BasicState<>(Collection.class);

	private DAO<Dunning> daoDunning;
	private DAO<BorrowedMaterial> daoBorrowedMaterial;
	private DAO<SchoolYear> daoSchoolYear;

	private SchoolYear recentlyActiveSchoolYear;

	@Inject
	public DunningViewModel(DAO<Dunning> daoDunning, DAO<BorrowedMaterial> daoBorrowedMaterial, DAO<SchoolYear> daoSchoolYear) {
		this.daoDunning = daoDunning;
		this.daoBorrowedMaterial = daoBorrowedMaterial;
		this.daoSchoolYear = daoSchoolYear;
	}

	@AfterVMBinding
	public void refresh() {
		updateSchoolYear();
		checkIfDunningShouldBeClosed();
		createFirstDunnings();
		createSecondDunnings();
		updateStates();
	}

	/**
	 * Creates the first dunning for a student with borrowed materials which are
	 * overdue.
	 */
	private void createFirstDunnings() {
		List<BorrowedMaterial> listBorrowedMaterial = daoBorrowedMaterial.findAll();
		Map<Integer, List<BorrowedMaterial>> map = new HashMap<Integer, List<BorrowedMaterial>>();
		for (BorrowedMaterial borrowedMaterial : listBorrowedMaterial) {
			if (borrowedMaterial.getBorrowUntil() == null) {
				if (!isNeededNextTerm(borrowedMaterial)
						&& borrowedMaterial.isReceived()) {
					if (Calendar
							.getInstance()
							.after(addDeadlineToDate(recentlyActiveSchoolYear
									.getEndOf(borrowedMaterial.getTeachingMaterial().getToTerm())))
							|| !(borrowedMaterial.getTeachingMaterial()
									.getToGrade() == borrowedMaterial
									.getStudent().getGrade().getGrade())) {
						addBorrowedMaterialsFromStudentToMap(map,
								borrowedMaterial);
					}
				}
			} else if (Calendar.getInstance().after(addDeadlineToDate(borrowedMaterial.getBorrowUntil()))) { { // manually borrowed
					addBorrowedMaterialsFromStudentToMap(map, borrowedMaterial);
				}
			}
		}

		for (Integer key : map.keySet()) {
			List<BorrowedMaterial> entry = map.get(key);
			List<Dunning> existingFirstDunningsForStudent = daoDunning.findAllWithCriteria(
					Restrictions.or(
							Restrictions.eq("status", Dunning.Status.OPENED),
							Restrictions.eq("status", Dunning.Status.SENT),
							Restrictions.eq("status", Dunning.Status.CLOSED)),
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

	private void addBorrowedMaterialsFromStudentToMap(Map<Integer, List<BorrowedMaterial>> map,	BorrowedMaterial borrowedMaterial) {
		if (!map.containsKey(borrowedMaterial.getStudent().getId())) {
			map.put(borrowedMaterial.getStudent().getId(),
					new ArrayList<BorrowedMaterial>());
		}
		
		map.get(borrowedMaterial.getStudent().getId()).add(borrowedMaterial);
	}

	private Calendar addDeadlineToDate(Date date) {
		Calendar returnDate = Calendar.getInstance();
		returnDate.setTime(date);
		returnDate.add(Calendar.DATE, 15);
		return returnDate;
	}

	/**
	 * Creates the second dunning for overdue first dunnings. Retrieves all
	 * first dunnings which have been sent to the student. If the sent date is
	 * more than 15 days ago create the second dunning which is sent to the
	 * parents. First make sure that there is no second dunning created yet.
	 */
	private void createSecondDunnings() {
		List<Dunning> sentFirstDunnings = daoDunning.findAllWithCriteria(
				Restrictions.eq("status", Dunning.Status.SENT),
				Restrictions.eq("type", Dunning.Type.TYPE1));
		for (Dunning dunning : sentFirstDunnings) {
			if (Calendar.getInstance().after(
					addDeadlineToDate(dunning
							.getStatusDate(Dunning.Status.SENT)))) {
				if (daoDunning.findAllWithCriteria(
						Restrictions.or(
								Restrictions.eq("status", Dunning.Status.OPENED), 
								Restrictions.eq("status", Dunning.Status.SENT),
								Restrictions.eq("status", Dunning.Status.CLOSED)),
								Restrictions.eq("type", Dunning.Type.TYPE2),
								Restrictions.eq("student", dunning.getStudent()
						)).size() == 0) {
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
	}

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

	private void updateSchoolYear() {
		recentlyActiveSchoolYear = daoSchoolYear.findSingleWithCriteria(
				Order.desc("toDate"),
				Restrictions.le("fromDate", new Date()));
	}
	
	/**
	 * Checks for every open dunning whether the contained borrowedMaterials have been returned.
	 * If so, close the dunning.
	 */
	private void checkIfDunningShouldBeClosed() {
		List<Dunning> openDunnings = daoDunning.findAllWithCriteria(Restrictions.eq("status", Dunning.Status.SENT));
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

	private void updateStates() {
//		Collection<Dunning> alreadyDunned = daoDunning.findAllWithCriteria(
//				Restrictions.or(
//						Restrictions.eq("status", Dunning.Status.SENT),
//						Restrictions.eq("status", Dunning.Status.CLOSED)));
//		
//		Collection<Dunning> toBeDunned = daoDunning.findAllWithCriteria(
//				Restrictions.eq("status", Dunning.Status.OPENED));
//		
//		studentsDunned.set(alreadyDunned);
//		studentsToDun.set(toBeDunned);
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
		updateStates();
	}
}
