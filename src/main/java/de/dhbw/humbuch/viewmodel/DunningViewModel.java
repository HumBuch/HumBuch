package de.dhbw.humbuch.viewmodel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import de.dhbw.humbuch.model.entity.Dunning;
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.SchoolYear.Term;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

public class DunningViewModel {

	public interface StudentsDunned extends State<Collection<Dunning>> {
	}

	public interface StudentsToDun extends State<Collection<Dunning>> {
	}

	public interface doUpdateDunning extends ActionHandler {
	}

	@ProvidesState(StudentsDunned.class)
	public final State<Collection<Dunning>> studentsDunned = new BasicState<>(
			Collection.class);
	@ProvidesState(StudentsToDun.class)
	public final State<Collection<Dunning>> studentsToDun = new BasicState<>(
			Collection.class);

	private DAO<Dunning> daoDunning;
	private DAO<BorrowedMaterial> daoBorrowedMaterial;
	private DAO<SchoolYear> daoSchoolYear;

	private SchoolYear currentSchoolYear;

	@Inject
	public DunningViewModel(DAO<Dunning> daoDunning,
			DAO<BorrowedMaterial> daoBorrowedMaterial,
			DAO<SchoolYear> daoSchoolYear) {
		this.daoDunning = daoDunning;
		this.daoBorrowedMaterial = daoBorrowedMaterial;
		this.daoSchoolYear = daoSchoolYear;
	}

	@AfterVMBinding
	private void afterVMBinding() {
		updateSchoolYear();
		createFirstDunnings();
		createSecondDunnings();
		updateStates();
	}

	private void createFirstDunnings() {
		List<BorrowedMaterial> listBorrowedMaterial = daoBorrowedMaterial
				.findAll();
		Map<Integer, List<BorrowedMaterial>> map = new HashMap<Integer, List<BorrowedMaterial>>();
		System.out.println("Anzahl borrowed:" + listBorrowedMaterial.size());
		for (BorrowedMaterial borrowedMaterial : listBorrowedMaterial) {
			// check, if a student forgot to return borrowed materials
			// is the borrowedMaterial needed for the next term?
			if (isNeededNextTerm(borrowedMaterial)) {
				System.out.println("not needed");
				// material is not received
				if (!borrowedMaterial.isReceived()) {
					System.out.println("not received");
					// not manually borrowed
					System.out.println(borrowedMaterial.getBorrowUntil());
					if (borrowedMaterial.getBorrowUntil() == null) {
						System.out.println("not manually borrowed");
						// check if today is later than end of term
						Calendar termEndDate = new GregorianCalendar();
						termEndDate.setTime(currentSchoolYear
								.getEndDateOfTerm(currentSchoolYear
										.getCurrentTerm()));
						termEndDate.add(Calendar.DATE, 15);
						// collect all borrowed materials for a student
						if (new GregorianCalendar().after(termEndDate)) {
							System.out.println("overdue!");
							if (!map.containsKey(borrowedMaterial.getStudent()
									.getId())) {
								map.put(borrowedMaterial.getStudent().getId(),
										new ArrayList<BorrowedMaterial>());
							}
							List<BorrowedMaterial> temp = map
									.get(borrowedMaterial.getStudent().getId());
							temp.add(borrowedMaterial);
							map.put(borrowedMaterial.getStudent().getId(), temp);
							System.out.println("student1"
									+ borrowedMaterial.getStudent().getId());
						}
					}
					// manually borrowed
					else {
						System.out.println("manually borrowed");
						// check if the manually borrowed teaching material
						// should already has been returned
						Calendar borrowUntil = new GregorianCalendar();
						borrowUntil.setTime(borrowedMaterial.getBorrowUntil());
						if (new GregorianCalendar().after(borrowUntil)) {
							if (!map.containsKey(borrowedMaterial.getStudent()
									.getId())) {
								map.put(borrowedMaterial.getStudent().getId(),
										new ArrayList<BorrowedMaterial>());
							}
							List<BorrowedMaterial> temp = map
									.get(borrowedMaterial.getStudent().getId());
							temp.add(borrowedMaterial);
							map.put(borrowedMaterial.getStudent().getId(), temp);
							System.out.println("student2"
									+ borrowedMaterial.getStudent().getId());
						}
					}
				}
			}
		}
		System.out.println("create dunnings for every student" + map.size());
		// create one dunning for every student
		for (Integer inoifehoiet : map.keySet()) {
			List<BorrowedMaterial> entry = map.get(inoifehoiet);
			System.out.println(entry.size());
			System.out.println(entry.get(0));
			Dunning newDunning = new Dunning.Builder(entry.get(0).getStudent())
					.type(Dunning.Type.TYPE1).status(Dunning.Status.OPENED).borrowedMaterials(new HashSet<BorrowedMaterial>())
					.build();
			for (BorrowedMaterial value : entry) {
				System.out.println("add");
				System.out.println(value);
				newDunning.addBorrowedMaterials(value);
			}
			
			if (daoDunning.findAllWithCriteria(
					Restrictions.eq("status", Dunning.Status.OPENED),
					Restrictions.eq("type", Dunning.Type.TYPE1),
					Restrictions.eq("studentId", newDunning.getStudent()
							.getId())).size() == 0) {
				System.out.println("insert");
				daoDunning.insert(newDunning);
			}
		}
	}

	private void createSecondDunnings() {
		List<Dunning> listDunning = daoDunning.findAllWithCriteria(
				Restrictions.eq("status", Dunning.Status.SENT),
				Restrictions.eq("type", Dunning.Type.TYPE1));
		for (Dunning dunning : listDunning) {
			Calendar statusDate = new GregorianCalendar();
			statusDate.setTime(dunning.getStatusDate(Dunning.Status.SENT));
			statusDate.add(Calendar.DATE, 15);
			// Create a second dunning
			if (new GregorianCalendar().after(statusDate)) {
				Dunning newDunning = new Dunning.Builder(dunning.getStudent())
						.type(Dunning.Type.TYPE2).status(Dunning.Status.OPENED)
						.build();
				if (daoDunning.findAllWithCriteria(
						Restrictions.eq("status", Dunning.Status.OPENED),
						Restrictions.eq("type", Dunning.Type.TYPE2),
						Restrictions.eq("studentId", dunning.getStudent()
								.getId())).size() == 0) {
					System.out.println("insert 2");
					daoDunning.insert(newDunning);
				}
			}
		}
	}

	private boolean isNeededNextTerm(BorrowedMaterial borrowedMaterial) {
		TeachingMaterial teachingMaterial = borrowedMaterial
				.getTeachingMaterial();

		Integer toGrade = teachingMaterial.getToGrade();
		int currentGrade = borrowedMaterial.getStudent().getGrade().getGrade();
		Term toTerm = teachingMaterial.getToTerm();
		Term currentTerm = currentSchoolYear.getCurrentTerm();

		if (toGrade == null)
			return false;

		return (toGrade > currentGrade || (toGrade == currentGrade && (toTerm
				.compareTo(currentTerm) > 0)));
	}

	private void updateSchoolYear() {
		currentSchoolYear = daoSchoolYear.findSingleWithCriteria(
				Restrictions.le("fromDate", new Date()),
				Restrictions.ge("toDate", new Date()));
	}

	private void updateStates() {
		Collection<Dunning> alreadyDunned = daoDunning
				.findAllWithCriteria(Restrictions.eq("status",
						Dunning.Status.SENT));
		Collection<Dunning> toBeDunned = daoDunning
				.findAllWithCriteria(Restrictions.eq("status",
						Dunning.Status.OPENED));
		studentsDunned.set(alreadyDunned);
		studentsToDun.set(toBeDunned);
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
