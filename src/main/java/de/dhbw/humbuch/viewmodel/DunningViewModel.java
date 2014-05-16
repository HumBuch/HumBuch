package de.dhbw.humbuch.viewmodel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.google.inject.Inject;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.annotations.AfterVMBinding;
import de.davherrmann.mvvm.annotations.ProvidesState;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Dunning;
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.SchoolYear.Term;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

/**
 * Handles all actions for the {@link DunningView} and provides the necessary
 * states.
 * 
 * @author Martin Wentzel
 * @author Johannes Idelhauser
 * 
 */
public class DunningViewModel {

    public interface Dunnings extends State<Collection<Dunning>> {}

    @ProvidesState(Dunnings.class)
    public final State<Collection<Dunning>> dunnings = new BasicState<>(Collection.class);

    private DAO<Dunning> daoDunning;
    private DAO<SchoolYear> daoSchoolYear;
    private DAO<Student> daoStudent;
    
    private SchoolYear activeSchoolYear;
    private Properties properties;

    @Inject
    public DunningViewModel(DAO<Dunning> daoDunning, DAO<BorrowedMaterial> daoBorrowedMaterial, DAO<SchoolYear> daoSchoolYear, DAO<Student> daoStudent, Properties properties) {
        this.daoDunning = daoDunning;
        this.daoSchoolYear = daoSchoolYear;
        this.daoStudent = daoStudent;
        this.properties = properties;
    }

    @AfterVMBinding
    public void initialiseStates() {
    	dunnings.set(new ArrayList<Dunning>());
    }
    
    public void refresh() {
        updateSchoolYear();
        checkIfDunningCanBeClosed();
        createSecondDunnings();
        updateDunningsState();
    }
    
    /**
     * Checks for every sent dunning if all contained borrowed materials have been returned.
     * If all borrowed materials are returned, the dunning is closed.
     */
    private void checkIfDunningCanBeClosed() {
		List<Dunning> sentDunnings = daoDunning
				.findAllWithCriteria(Restrictions.or(
						Restrictions.eq("status", Dunning.Status.SENT),
						Restrictions.eq("status", Dunning.Status.OPENED)));
		//For every sent dunning
		for (Dunning dunning : sentDunnings) {
            Boolean toBeClosed = true;
            //Check if all materials of the dunning have been returned
            for(BorrowedMaterial material : dunning.getBorrowedMaterials()) {
                if(material.getReturnDate() == null) {
                    toBeClosed = false;
                    break;
                }
            }
            //If all materials of the dunning have been returned, close it
            if(toBeClosed) {
                dunning.setStatus(Dunning.Status.CLOSED);
                daoDunning.update(dunning);
            }
        }
    }
    
    /**
	 * Creates the second dunning for overdue first dunnings. A first dunning is
	 * overdue, when the dunning is not resolved within a specific time frame.
	 * This function gets all first dunnings which are in an SENT state. Then it
	 * checks for each dunning whether it is overdue or not. If so, a second
	 * dunning is created and the first dunning is closed
	 */
    private void createSecondDunnings() {
    	int deadline = getDeadlineSecondDunning();
        List<Dunning> sentFirstDunnings = daoDunning.findAllWithCriteria(Restrictions.and(
                Restrictions.eq("status", Dunning.Status.SENT),
                Restrictions.eq("type", Dunning.Type.TYPE1)));
        
        for (Dunning dunning : sentFirstDunnings) {
        
        	Date dateStatusSent = dunning.getStatusDate(Dunning.Status.SENT);
        	
        	//Check if the current date is after the specific sent-date of the dunning plus period
            if (currentDateAfterPeriod(dateStatusSent, deadline)) {
            	
            	//Create the new set of overdue materials
            	Set<BorrowedMaterial> overdueMaterials = new HashSet<BorrowedMaterial>();
            	for (BorrowedMaterial material : dunning.getBorrowedMaterials()) {
            		//If material of the dunning is still not returned
            		if (!material.isReturned()) {
            			overdueMaterials.add(material);
            		}
            	}
            	Dunning newDunning = new Dunning.Builder(
                            dunning.getStudent()).type(Dunning.Type.TYPE2)
                            .status(Dunning.Status.OPENED)
                            .borrowedMaterials(overdueMaterials)
                            .build();
                daoDunning.insert(newDunning);
                dunning.setStatus(Dunning.Status.CLOSED);
                daoDunning.update(dunning);
            }
        }
    }

	private int getDeadlineSecondDunning() {
		int deadline = 0;
		String deadlineSecondDunning = properties.settings.get().get("dun_secondDunningDeadline");
    	if (deadlineSecondDunning != null) {
    		deadline = Integer.parseInt(deadlineSecondDunning);
    	}
		return deadline;
	}
	
	private int getDeadlineFirstDunning() {
		int deadline = 0;
		String deadlineFirstDunning = properties.settings.get().get("dun_firstDunningDeadline");
    	if (deadlineFirstDunning != null) {
    		deadline = Integer.parseInt(deadlineFirstDunning);
    	}
		return deadline;
	}
    
    /**
	 * Check if the current date is after the given date plus the given period
	 * of time
	 * 
	 * @param date
	 *            The original {@link Date} to check
	 * @param timePeriod
	 *            Number of days to add to the date
	 * @return Whether the current date is after the deadline or not
	 */
    private boolean currentDateAfterPeriod(Date date, int timePeriod) {
    	Calendar currentDate = Calendar.getInstance();
    	Calendar originDate = Calendar.getInstance();
    	originDate.setTime(date);
    	originDate.add(Calendar.DATE, timePeriod);
    	
    	if(currentDate.after(originDate)) {
    		return true;
    	} else {
    		return false;
    	}
    }
    
    /**
     * Updates the dunnings state
     */
    private void updateDunningsState() {
        Collection<Dunning> openDunnings = createFirstDunnings();
        Collection<Dunning> savedDunnings = daoDunning.findAll();
        openDunnings.addAll(savedDunnings);
        dunnings.set(openDunnings);
    }
    
	/**
	 * Creates temporary dunnings for overdue borrowed materials. The returned
	 * {@link Collection} of {@link Dunning}s is not saved to the database.
	 * 
	 * @return All newly generated dunnings that are not yet sent
	 */
    private Collection<Dunning> createFirstDunnings() {
    	
        List<Student> allStudents = daoStudent.findAll();
        Collection<Dunning> dunnings = new ArrayList<Dunning>();
        int deadline = getDeadlineFirstDunning();
        
		// Generate temporary key to be able to fill the vaadin table. If no
		// temporary id is used, the vaadin table will cause problems because of
		// the duplicate IDs.
		int id = generateTemporaryId();
		
        for (Student student : allStudents) {
        	List<BorrowedMaterial> borrowedMaterials = student.getBorrowedMaterials();
        	Set<BorrowedMaterial> overdueMaterials = new HashSet<BorrowedMaterial>();
        	
        	if (!borrowedMaterials.isEmpty()) {
        		
        		//For every borrowed material of the student
        		for (BorrowedMaterial material : borrowedMaterials) {
        			
        			//If the material is received and not yet returned
        			if (material.isReceived() && material.getReturnDate() == null) {
	        			Date dueDate = material.getBorrowUntil();
	        			
	        			//If material is manually lended and the current date is after due date + deadline
	        			if (dueDate != null && currentDateAfterPeriod(dueDate, deadline) && !dunningExistsWithMaterial(material)) {
	        				//Add to the dunning
	        				overdueMaterials.add(material);
	        			} else if (dueDate == null) {
	        				//If the materials isn't needed next term and there exists no dunning with the material
	        				if (!isNeededNextTerm(material) && !dunningExistsWithMaterial(material)) {
	        					
	        					//Is the deadline over?
	        					Term toTerm = material.getTeachingMaterial().getToTerm();
	                    		if (currentDateAfterPeriod(activeSchoolYear.getEndOf(toTerm), deadline)) {
	                    			//Add to the dunning
	                    			overdueMaterials.add(material);
	                    		}	
	        				}
	        			}
        			}
        			
        		}
        		
        	}
        	
        	//If there are overdue materials, create a dunning
        	if (!overdueMaterials.isEmpty()) {
				Dunning newDunning = new Dunning.Builder(student)
						.type(Dunning.Type.TYPE1)
						.status(Dunning.Status.OPENED)
						.borrowedMaterials(overdueMaterials)
						.id(id++)
						.build();
				dunnings.add(newDunning);
        	}
        	
        }
        return dunnings;
    }
    
	/**
	 * Generates a temporary key for a dunning by extracting the maximum value
	 * of the id column from the dunning table in database.
	 * 
	 * @return Key, that is one greater than the biggest value in the id column
	 */
    private int generateTemporaryId() {
		Dunning a = daoDunning.findSingleWithCriteria(Order.desc("id"),
				Restrictions.or(Restrictions.eq("type", Dunning.Type.TYPE1),
						Restrictions.eq("type", Dunning.Type.TYPE2)));
		int id;
        if (a==null) {
        	id = 0;
        } else {
        	id = a.getId();
        }
        return ++id;
	}

    /**
     * Checks if there is a dunning in the database for the given {@link BorrowedMaterial}.
     * @param material {@link BorrowedMaterial} to check for
     * @return Whether there is a dunning for the material in the db or not
     */
	private boolean dunningExistsWithMaterial(BorrowedMaterial material) {
		List<Dunning> allDunnings = daoDunning.findAll();
		for (Dunning dunning : allDunnings) {
			if (dunning.getBorrowedMaterials().contains(material)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether a specific borrowed material is needed next term. Thereto
	 * the according teaching material with its grade and term information is
	 * retrieved.
	 * 
	 * @param borrowedMaterial
	 *            the borrowed material which is checked
	 * @return <code>true</code> if the borrowed materials is needed next term;
	 *         <code>false</code> otherwise
	 */
    private boolean isNeededNextTerm(BorrowedMaterial borrowedMaterial) {
        TeachingMaterial teachingMaterial = borrowedMaterial.getTeachingMaterial();

        Integer toGrade = teachingMaterial.getToGrade();
        int currentGrade = borrowedMaterial.getStudent().getGrade().getGrade();
        Term toTerm = teachingMaterial.getToTerm();
        Term currentTerm = activeSchoolYear.getRecentlyActiveTerm();

        if (toGrade == null)
            return false;

        return (toGrade > currentGrade || (toGrade == currentGrade && (toTerm.compareTo(currentTerm) > 0)));
    }
    
    /**
     * Updates the recently active {@link SchoolYear}
     */
    private void updateSchoolYear() {
        activeSchoolYear = daoSchoolYear.findSingleWithCriteria(
                Order.desc("toDate"),
                Restrictions.le("fromDate", new Date()));
		if (activeSchoolYear == null) {
			activeSchoolYear = new SchoolYear.Builder("now", getDate(
					Calendar.AUGUST, 1), getDate(Calendar.JUNE, 31))
					.endFirstTerm(getDate(Calendar.JANUARY, 31))
					.beginSecondTerm(getDate(Calendar.FEBRUARY, 1)).build();
		}
    }
    
	/**
	 * Returns {@link Date} object with the current year and the given month and
	 * day.
	 * 
	 * @param month
	 * @param day
	 * 
	 * @return {@link Date} object with given information
	 */
	private Date getDate(int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), month, day);
		return calendar.getTime();
	}
    
    /**
     * Updates a dunning by saving it to the database
     * 
     * @param dunning the dunning to be updated
     */
    public void doUpdateDunning(Dunning dunning) {
        daoDunning.update(dunning);
        updateDunningsState();
    }
}