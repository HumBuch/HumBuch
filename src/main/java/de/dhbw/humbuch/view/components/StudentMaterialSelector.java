package de.dhbw.humbuch.view.components;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TreeTable;

import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;

/**
 * This class represents the StudentMaterialSelector. It is displayed as a tree
 * table with grades on the top level and the teaching materials as leafes of
 * the tree.
 * 
 * @author Henning Muszynski
 * */
public class StudentMaterialSelector extends CustomComponent {

	private static final long serialVersionUID = -618911643102742679L;

	private static final int MAX_STUDENTS_BEFORE_COLLAPSE = 12;
	private static final String TREE_TABLE_HEADER_DATA = "Daten auswählen";
	private static final String TREE_TABLE_HEADER_DATE = "Ausgeliehen bis";
	private static final String GRADE = "Klasse ";
	private static final String EVERYTHING_SELECT = "Alle Klassen auswählen";
	private static final String EVERYTHING_DESELECT = "Auswahl aufheben";

	private TreeTable treeTableContent;
	private Map<Grade, Map<Student, List<BorrowedMaterial>>> gradeAndStudentsWithMaterials;
	private ArrayList<StudentMaterialSelectorObserver> registeredObservers;
	private CheckBox checkBoxEverything;
	private Object checkBoxEverythingId;
	private LinkedHashMap<CheckBox, Object> allGradeCheckBoxes;
	private LinkedHashMap<CheckBox, Object> allStudentCheckBoxes;
	private LinkedHashMap<CheckBox, Object> allMaterialCheckBoxes;
	private String filterString;
	private boolean collapseGrades;
	private boolean changedCollapseFlag;
	private ValueChangeListener gradeListener;
	private ValueChangeListener studentListener;
	private ValueChangeListener materialListener;

	/**
	 * Default constructor. Instantiiates a new StudentMaterialSelector
	 * instance.
	 * */
	public StudentMaterialSelector() {
		init();
		buildLayout();
	}

	/*
	 * Initialize and configure member variables
	 */
	private void init() {
		treeTableContent = new TreeTable();
		gradeAndStudentsWithMaterials = new LinkedHashMap<Grade, Map<Student, List<BorrowedMaterial>>>();
		checkBoxEverything = new CheckBox(EVERYTHING_SELECT);
		allGradeCheckBoxes = new LinkedHashMap<CheckBox, Object>();
		allStudentCheckBoxes = new LinkedHashMap<CheckBox, Object>();
		allMaterialCheckBoxes = new LinkedHashMap<CheckBox, Object>();
		collapseGrades = false;
		changedCollapseFlag = false;

		treeTableContent.setSizeFull();
		treeTableContent.setWidth("100%");
		treeTableContent.setImmediate(true);
		treeTableContent.addContainerProperty(TREE_TABLE_HEADER_DATA,
				CheckBox.class, null);
		treeTableContent.addContainerProperty(TREE_TABLE_HEADER_DATE,
				String.class, "");

		implementListeners();
	}

	private void buildLayout() {
		setCompositionRoot(treeTableContent);
	}

	/*
	 * Implement the code for the different listeners on the CheckBox components
	 * displayed in the TreeTable Whenever a CheckBox changes its value (gets
	 * selected / deselected) all registered observers get notified.
	 */
	private void implementListeners() {
		/*
		 * The everything listener is above the top level elements to select /
		 * deselect all elements at once
		 */
		checkBoxEverything.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -7395293342050339912L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean state = checkBoxEverything.getValue();

				if (state == true) {
					checkBoxEverything.setCaption(EVERYTHING_DESELECT);
				} else {
					checkBoxEverything.setCaption(EVERYTHING_SELECT);
				}

				for (CheckBox checkBoxGrade : allGradeCheckBoxes.keySet()) {
					checkBoxGrade.setValue(state);
				}
				notifyObserver();
			}
		});

		/*
		 * The grade listener is added to all top level elements (grades) in the
		 * tree. Whenever a grade is selected / deselected all Students
		 * belonging to that grade are selected / deselected, too.
		 */
		gradeListener = new ValueChangeListener() {

			private static final long serialVersionUID = -7395293342050339912L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				CheckBox checkBoxGrade = (CheckBox) event.getProperty();
				Grade grade = (Grade) checkBoxGrade.getData();
				boolean gradeSelected = checkBoxGrade.getValue();

				for (CheckBox checkBoxStudent : allStudentCheckBoxes.keySet()) {
					Student student = (Student) checkBoxStudent.getData();
					if (student.getGrade().equals(grade)) {
						checkBoxStudent.setValue(gradeSelected);
					}
				}
				notifyObserver();
			}
		};

		/*
		 * The student listener is added to all student elements in the tree.
		 * When a student is selected / deselected all of its materials are
		 * selected / deselected, too.
		 */
		studentListener = new ValueChangeListener() {

			private static final long serialVersionUID = 5610970965368269295L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				CheckBox checkBoxStudent = (CheckBox) event.getProperty();
				Student student = (Student) checkBoxStudent.getData();
				boolean studentSelected = checkBoxStudent.getValue();

				for (CheckBox checkBoxMaterial : allMaterialCheckBoxes.keySet()) {
					BorrowedMaterial material = (BorrowedMaterial) checkBoxMaterial
							.getData();
					if (material.getStudent().equals(student)) {
						checkBoxMaterial.setValue(studentSelected);
					}
				}
				notifyObserver();
			}
		};

		/*
		 * The material listener is added to all leafes (materials) in the tree.
		 * It only notifies all registered observers about the value change.
		 */
		materialListener = new ValueChangeListener() {

			private static final long serialVersionUID = 614006288313075687L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				notifyObserver();
			}
		};
	}

	/**
	 * Call this method when the data which you want to display within the
	 * StudentMaterialSelector changes. This methods not only saves the new data
	 * in a member variable but also updates the TreeTable which is responsible
	 * for showing the data.
	 * 
	 * @param newGradeAndStudentsWithMaterials
	 *            the new data to be displayed by the StudentMaterialSelector
	 * */
	public void setGradesAndStudentsWithMaterials(
			Map<Grade, Map<Student, List<BorrowedMaterial>>> newGradeAndStudentsWithMaterials) {
		this.gradeAndStudentsWithMaterials = newGradeAndStudentsWithMaterials;
		rebuildTable(newGradeAndStudentsWithMaterials);
	}

	/**
	 * Returns all currently selected grades of the StudentMaterialSelector or
	 * an empty HashSet. Will never return <code>null</code>.
	 * 
	 * @return the currently selected grades of the StudentMaterialSelector or
	 *         an empty HashSet if none are selected
	 * */
	public HashSet<Grade> getCurrentlySelectedGrades() {
		HashSet<Grade> currentlySelectedGrades = new HashSet<Grade>();
		for (CheckBox checkBox : allGradeCheckBoxes.keySet()) {
			if (checkBox.getValue() == true) {
				currentlySelectedGrades.add((Grade) checkBox.getData());
			}
		}

		return currentlySelectedGrades;
	}

	/**
	 * Returns all currently selected students of the StudentMaterialSelector or
	 * an empty HashSet. Will never return <code>null</code>.
	 * 
	 * @return the currently selected students of the StudentMaterialSelector or
	 *         an empty HashSet if none are selected
	 * */
	public HashSet<Student> getCurrentlySelectedStudents() {
		HashSet<Student> currentlySelectedStudents = new HashSet<Student>();
		for (CheckBox checkBox : allStudentCheckBoxes.keySet()) {
			if (checkBox.getValue() == true) {
				currentlySelectedStudents.add((Student) checkBox.getData());
			}
		}

		return currentlySelectedStudents;
	}

	/**
	 * Returns all currently selected borrowed materials of the
	 * StudentMaterialSelector or an empty HashSet. Will never return
	 * <code>null</code>.
	 * 
	 * @return the currently selected materials of the StudentMaterialSelector
	 *         or an empty HashSet if none are selected
	 * */
	public HashSet<BorrowedMaterial> getCurrentlySelectedBorrowedMaterials() {
		HashSet<BorrowedMaterial> currentlySelectedBorrowedMaterials = new HashSet<BorrowedMaterial>();
		for (CheckBox checkBox : allMaterialCheckBoxes.keySet()) {
			if (checkBox.getValue() == true) {
				currentlySelectedBorrowedMaterials
						.add((BorrowedMaterial) checkBox.getData());
			}
		}

		return currentlySelectedBorrowedMaterials;
	}

	/**
	 * Register any class implementing the StudentMaterialSelectorObserver
	 * interface to get notified when any selection changes.
	 * 
	 * @param observer
	 *            the class implementing the interface which should be notified
	 *            of all changes
	 * */
	public void registerAsObserver(StudentMaterialSelectorObserver observer) {
		if (registeredObservers == null) {
			registeredObservers = new ArrayList<StudentMaterialSelectorObserver>();
		} else if (registeredObservers.contains(observer)) {
			return;
		}
		registeredObservers.add(observer);
	}

	/**
	 * Call this method to filter for specific students. The filtering is
	 * case-insensitve and filters for the firstname and lastname of any student
	 * in the StudentMaterialSelector. It does not only saves the filter string
	 * in a member variable but updates the TreeTable to only show students
	 * matching the filter. When passing null it is treated as an empty string.
	 * 
	 * @param filterString
	 *            the string on which filtering should be based upon
	 * */
	public void setFilterString(String filterString) {
		if (filterString == null) {
			filterString = "";
		}
		this.filterString = filterString;
		filterTableContent();
	}

	/*
	 * Method used for filtering. It filters the currently used data structure
	 * inside the StudentMaterialSelector and filters every students first and
	 * lastname (ignoring case). After filtering is done this method rebuilds
	 * the TreeTable to visualize the result of the filter process.
	 */
	private void filterTableContent() {
		LinkedHashMap<Grade, Map<Student, List<BorrowedMaterial>>> gradeAndStudentsWithMaterialsFiltered = new LinkedHashMap<Grade, Map<Student, List<BorrowedMaterial>>>();
		for (Grade grade : gradeAndStudentsWithMaterials.keySet()) {
			Map<Student, List<BorrowedMaterial>> entry = gradeAndStudentsWithMaterials
					.get(grade);
			Map<Student, List<BorrowedMaterial>> filteredEntries = new LinkedHashMap<Student, List<BorrowedMaterial>>();
			for (Student student : entry.keySet()) {
				// match firstname and lastname ignoring case
				String fullName = student.getFirstname() + " "
						+ student.getLastname();
				fullName = fullName.toLowerCase();
				if (fullName.contains(filterString.toLowerCase())) {
					filteredEntries.put(student, entry.get(student));
				}
			}

			if (filteredEntries.size() != 0) {
				gradeAndStudentsWithMaterialsFiltered.put(grade,
						filteredEntries);
			}
		}
		rebuildTable(gradeAndStudentsWithMaterialsFiltered);
	}

	/*
	 * Helper method used for rebuilding the TreeTable. It removes all items
	 * currently showing and resets the member variables holding information
	 * about the current state. It adds all grades, students and materials
	 * within the provided structure to the newly build TreeTable. This method
	 * resets the expand states of the components in the TreeTable.
	 * 
	 * @param newGradeAndStudentsWithMaterials the new data structure which
	 * should be visualized by the StudentMaterialSelector
	 */
	private void rebuildTable(
			Map<Grade, Map<Student, List<BorrowedMaterial>>> newGradeAndStudentWithMaterials) {
		treeTableContent.removeAllItems();
		allGradeCheckBoxes.clear();
		allStudentCheckBoxes.clear();
		allMaterialCheckBoxes.clear();
		collapseGrades = false;
		changedCollapseFlag = false;

		for (Grade grade : newGradeAndStudentWithMaterials.keySet()) {
			addGradeToTree(grade);
			Map<Student, List<BorrowedMaterial>> entry = newGradeAndStudentWithMaterials
					.get(grade);
			for (Student student : entry.keySet()) {
				addStudentToTree(student);
				for (BorrowedMaterial material : entry.get(student)) {
					addMaterialToTree(material);
				}
			}
		}

		validateTableContent();
	}

	/*
	 * Helper method used to update or rebuild or the TreeTable. This method
	 * takes care of adding the grade to the tree and storing all information in
	 * the corresponding member variables.
	 * 
	 * @return the item id of the newly added grade or null if there has been an
	 * error
	 */
	private Object addGradeToTree(Grade grade) {
		if (grade == null) {
			return null;
		}
		if (checkBoxEverythingId == null
				|| !treeTableContent.containsId(checkBoxEverythingId)) {
			checkBoxEverythingId = treeTableContent.addItem(new Object[] {
					checkBoxEverything, "" }, null);
			treeTableContent.setCollapsed(checkBoxEverythingId, false);
		}

		CheckBox checkBoxGrade = new CheckBox(GRADE + grade.getGrade()
				+ grade.getSuffix());
		checkBoxGrade.setData(grade);
		checkBoxGrade.addValueChangeListener(gradeListener);
		Object gradeItemId = treeTableContent.addItem(new Object[] {
				checkBoxGrade, "" }, null);
		treeTableContent.setCollapsed(gradeItemId, collapseGrades);

		treeTableContent.setParent(gradeItemId, checkBoxEverythingId);

		allGradeCheckBoxes.put(checkBoxGrade, gradeItemId);

		return gradeItemId;
	}

	/*
	 * @see StudentMaterialSelector.addGradeToTree(Grade grade) This method
	 * takes care of putting the student under right grade to maintain a valid
	 * hierarchy.
	 * 
	 * @return the item id of the newly added student or null if there has been
	 * an error
	 */
	private Object addStudentToTree(Student student) {
		if (student == null) {
			return null;
		}
		CheckBox checkBoxStudent = new CheckBox(student.getFirstname() + " "
				+ student.getLastname());
		checkBoxStudent.setData(student);
		checkBoxStudent.addValueChangeListener(studentListener);
		Object studentItemId = treeTableContent.addItem(new Object[] {
				checkBoxStudent, "" }, null);

		Object parentGradeItemId = null;
		for (CheckBox checkBoxGrade : allGradeCheckBoxes.keySet()) {
			Grade grade = (Grade) checkBoxGrade.getData();
			if (grade.equals(student.getGrade())) {
				parentGradeItemId = allGradeCheckBoxes.get(checkBoxGrade);
				break;
			}
		}
		if (parentGradeItemId == null) {
			parentGradeItemId = addGradeToTree(student.getGrade());
		}
		treeTableContent.setParent(studentItemId, parentGradeItemId);

		allStudentCheckBoxes.put(checkBoxStudent, studentItemId);

		if (allStudentCheckBoxes.size() > MAX_STUDENTS_BEFORE_COLLAPSE
				&& changedCollapseFlag == false) {
			collapseGrades = true;
			changedCollapseFlag = true;
			for (CheckBox checkBoxGrade : allGradeCheckBoxes.keySet()) {
				treeTableContent.setCollapsed(
						allGradeCheckBoxes.get(checkBoxGrade), collapseGrades);
			}
		}

		return studentItemId;
	}

	/*
	 * @see StudentMaterialSelector.addGradeToTree(Grade grade) This method
	 * takes care of putting the material under right student to maintain a
	 * valid hierarchy.
	 * 
	 * @return the item id for the newly added material or null if there has
	 * been an error
	 */
	private Object addMaterialToTree(BorrowedMaterial material) {
		if (material == null) {
			return null;
		}
		CheckBox checkBoxMaterial = new CheckBox(material.getTeachingMaterial()
				.getName());
		checkBoxMaterial.setData(material);
		checkBoxMaterial.addValueChangeListener(materialListener);

		Date dateBorrowedUntil = material.getBorrowUntil();
		String borrowedUntil = "";
		if (dateBorrowedUntil != null) {
			borrowedUntil = new SimpleDateFormat("dd.MM.YYYY")
					.format(dateBorrowedUntil);
		} else {
			TeachingMaterial teachingMaterial = material.getTeachingMaterial();
			String to = GRADE + teachingMaterial.getToGrade() + ", "
					+ teachingMaterial.getToTerm();
			borrowedUntil = to;
		}

		Object materialItemId = treeTableContent.addItem(new Object[] {
				checkBoxMaterial, borrowedUntil }, null);
		treeTableContent.setChildrenAllowed(materialItemId, false);

		Object parentStudentItemId = null;
		for (CheckBox checkBoxStudent : allStudentCheckBoxes.keySet()) {
			Student student = (Student) checkBoxStudent.getData();
			if (student.equals(material.getStudent())) {
				parentStudentItemId = allStudentCheckBoxes.get(checkBoxStudent);
				break;
			}
		}
		if (parentStudentItemId == null) {
			parentStudentItemId = addStudentToTree(material.getStudent());
		}
		treeTableContent.setParent(materialItemId, parentStudentItemId);

		allMaterialCheckBoxes.put(checkBoxMaterial, materialItemId);

		return materialItemId;
	}

	/*
	 * Validates the tree table so that empty grades / students etc. are not
	 * displayed.
	 */
	private void validateTableContent() {
		if (allGradeCheckBoxes.isEmpty()) {
			treeTableContent.removeItem(checkBoxEverythingId);
		}

		removeEmptyGrades();
		removeEmptyStudents();
	}

	private void removeEmptyGrades() {
		for (CheckBox checkBoxGrade : allGradeCheckBoxes.keySet()) {
			Object gradeItemId = allGradeCheckBoxes.get(checkBoxGrade);
			if (treeTableContent.getChildren(gradeItemId) == null) {
				treeTableContent.removeItem(gradeItemId);
			}
		}
	}

	private void removeEmptyStudents() {
		for (CheckBox checkBoxStudent : allStudentCheckBoxes.keySet()) {
			Object studentItemId = allStudentCheckBoxes.get(checkBoxStudent);
			if (treeTableContent.getChildren(studentItemId) == null) {
				treeTableContent.removeItem(studentItemId);
			}
		}
	}

	/*
	 * Notifies all registered observers that the selection changed by calling
	 * their update method.
	 */
	private void notifyObserver() {
		if (registeredObservers == null) {
			return;
		}

		for (StudentMaterialSelectorObserver observer : registeredObservers) {
			observer.update();
		}
	}
}