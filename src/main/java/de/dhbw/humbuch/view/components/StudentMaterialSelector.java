package de.dhbw.humbuch.view.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TreeTable;

import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;


public class StudentMaterialSelector extends CustomComponent {

	private static final long serialVersionUID = -618911643102742679L;


	public enum MaterialType {
		ALL_MATERIALS("Alle Materialien"),
		ONLY_MANUAL("Manuelle Materialien"),
		ONLY_OFFICIAL("Offizielle Materialien");

		private final String title;

		MaterialType(String title) {
			this.title = title;
		}

		public String toString() {
			return title;
		}
	}

	private static final int MAX_STUDENTS_BEFORE_COLLAPSE = 12;
	private static final String TREE_TABLE_HEADER = "Daten auswählen";
	private static final String GRADE = "Klasse ";

	private TreeTable treeTableContent;
	private Map<Grade, Map<Student, List<BorrowedMaterial>>> gradeAndStudentsWithMaterials;
	private ArrayList<StudentMaterialSelectorObserver> registeredObservers;
	private LinkedHashMap<CheckBox, Object> allGradeCheckBoxes;
	private LinkedHashMap<CheckBox, Object> allStudentCheckBoxes;
	private LinkedHashMap<CheckBox, Object> allMaterialCheckBoxes;
	private String filterString;
	private MaterialType materialTypeFilter;
	private boolean collapseGrades;
	private boolean changedCollapseFlag;
	private ValueChangeListener gradeListener;
	private ValueChangeListener studentListener;
	private ValueChangeListener materialListener;

	public StudentMaterialSelector() {
		init();
		buildLayout();
	}

	/*
	 * Initialize and configure member variables
	 * */
	private void init() {
		treeTableContent = new TreeTable();
		gradeAndStudentsWithMaterials = new LinkedHashMap<Grade, Map<Student, List<BorrowedMaterial>>>();
		allGradeCheckBoxes = new LinkedHashMap<CheckBox, Object>();
		allStudentCheckBoxes = new LinkedHashMap<CheckBox, Object>();
		allMaterialCheckBoxes = new LinkedHashMap<CheckBox, Object>();
		collapseGrades = false;
		changedCollapseFlag = false;
		materialTypeFilter = MaterialType.ALL_MATERIALS;

		treeTableContent.setSizeFull();
		treeTableContent.setWidth("100%");
		treeTableContent.setImmediate(true);
		treeTableContent.addContainerProperty(TREE_TABLE_HEADER, CheckBox.class, null);

		implementListeners();
	}

	private void buildLayout() {
		setCompositionRoot(treeTableContent);
	}

	/*
	 * Implement the code for the different listeners on the CheckBox components displayed in the TreeTable
	 * Whenever a CheckBox changes its value (gets selected / deselected) all registered observers get notified.
	 * */
	private void implementListeners() {
		/*
		 * The grade listener is added to all top level elements (grades) in the tree.
		 * Whenever a grade is selected / deselected all Students belonging to that grade are selected / deselected, too.
		 * */
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
		 * The student listener is added to all student elements in the tree. When a student is selected / deselected 
		 * all of its materials are selected / deselected, too.
		 * */
		studentListener = new ValueChangeListener() {

			private static final long serialVersionUID = 5610970965368269295L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				CheckBox checkBoxStudent = (CheckBox) event.getProperty();
				Student student = (Student) checkBoxStudent.getData();
				boolean studentSelected = checkBoxStudent.getValue();

				for (CheckBox checkBoxMaterial : allMaterialCheckBoxes.keySet()) {
					BorrowedMaterial material = (BorrowedMaterial) checkBoxMaterial.getData();
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
		 * */
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
	public void setGradesAndStudentsWithMaterials(Map<Grade, Map<Student, List<BorrowedMaterial>>> newGradeAndStudentsWithMaterials) {
		updateTable(newGradeAndStudentsWithMaterials);
		this.gradeAndStudentsWithMaterials = newGradeAndStudentsWithMaterials;
	}

	/**
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
	 * @return the currently selected materials of the StudentMaterialSelector
	 *         or an empty HashSet if none are selected
	 * */
	public HashSet<BorrowedMaterial> getCurrentlySelectedBorrowedMaterials() {
		HashSet<BorrowedMaterial> currentlySelectedBorrowedMaterials = new HashSet<BorrowedMaterial>();
		for (CheckBox checkBox : allMaterialCheckBoxes.keySet()) {
			if (checkBox.getValue() == true) {
				currentlySelectedBorrowedMaterials.add((BorrowedMaterial) checkBox.getData());
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
		}
		else if (registeredObservers.contains(observer)) {
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
		filterTableContentForString();
	}

	/**
	 * Call this method to filter for specific material types. This method not
	 * only saves the material type to filter for in a member variable but also
	 * updates TreeTable to show only the borrowed materials matching the
	 * filter. When passing null all borrowed materials are shown.
	 * 
	 * @param materialTypeFilter
	 *            the material type on which filtering should be based upon
	 * */
	public void setFilterMaterialType(MaterialType materialTypeFilter) {
		if (materialTypeFilter == null) {
			materialTypeFilter = MaterialType.ALL_MATERIALS;
		}
		this.materialTypeFilter = materialTypeFilter;
		filterTableContentForMaterialType();
	}

	/*
	 * Method used for filtering. It filters the currently used data structure inside the StudentMaterialSelector
	 * and filters every students first and lastname (ignoring case). After filtering is done this method
	 * rebuilds the TreeTable to visualize the result of the filter process.
	 * */
	private void filterTableContentForString() {
		LinkedHashMap<Grade, Map<Student, List<BorrowedMaterial>>> gradeAndStudentsWithMaterialsFiltered = new LinkedHashMap<Grade, Map<Student, List<BorrowedMaterial>>>();
		for (Grade grade : gradeAndStudentsWithMaterials.keySet()) {
			Map<Student, List<BorrowedMaterial>> entry = gradeAndStudentsWithMaterials.get(grade);
			Map<Student, List<BorrowedMaterial>> filteredEntries = new LinkedHashMap<Student, List<BorrowedMaterial>>();
			for (Student student : entry.keySet()) {
				// match firstname and lastname ignoring case
				String fullName = student.getFirstname() + " " + student.getLastname();
				fullName = fullName.toLowerCase();
				if (fullName.contains(filterString.toLowerCase())) {
					filteredEntries.put(student, entry.get(student));
				}
			}

			if (filteredEntries.size() != 0) {
				gradeAndStudentsWithMaterialsFiltered.put(grade, filteredEntries);
			}
		}
		rebuildTable(gradeAndStudentsWithMaterialsFiltered);
	}

	/*
	 * 
	 * */
	private void filterTableContentForMaterialType() {
		LinkedHashMap<Grade, Map<Student, List<BorrowedMaterial>>> gradeAndStudentsWithMaterialsFiltered = new LinkedHashMap<Grade, Map<Student, List<BorrowedMaterial>>>();
		for (Grade grade : gradeAndStudentsWithMaterials.keySet()) {
			Map<Student, List<BorrowedMaterial>> entry = gradeAndStudentsWithMaterials.get(grade);
			Map<Student, List<BorrowedMaterial>> filteredEntries = new LinkedHashMap<Student, List<BorrowedMaterial>>();

			for (Student student : entry.keySet()) {
				List<BorrowedMaterial> materials = entry.get(student);
				List<BorrowedMaterial> filteredMaterials = new ArrayList<BorrowedMaterial>();

				if (materialTypeFilter.equals(MaterialType.ONLY_MANUAL)) {
					for (BorrowedMaterial material : materials) {
						if (material.getBorrowUntil() != null) {
							filteredMaterials.add(material);
						}
					}
				}
				else if (materialTypeFilter.equals(MaterialType.ONLY_OFFICIAL)) {
					for (BorrowedMaterial material : materials) {
						if (material.getBorrowUntil() == null) {
							filteredMaterials.add(material);
						}
					}
				}
				else {
					filteredMaterials.addAll(materials);
				}
				
				if(filteredMaterials.size() > 0) {
					filteredEntries.put(student, filteredMaterials);
				}
			}

			if (filteredEntries.size() != 0) {
				gradeAndStudentsWithMaterialsFiltered.put(grade, filteredEntries);
			}
		}
		rebuildTable(gradeAndStudentsWithMaterialsFiltered);
	}

	/*
	 * Helper method used for rebuilding the TreeTable. It removes all items currently showing and resets the member variables
	 * holding information about the current state. It adds all grades, students and materials within the provided structure to the
	 * newly build TreeTable. This method resets the expand states of the components in the TreeTable.
	 *
	 * @param newGradeAndStudentsWithMaterials
	 * 		the new data structure which should be visualized by the StudentMaterialSelector
	 * */
	private void rebuildTable(LinkedHashMap<Grade, Map<Student, List<BorrowedMaterial>>> newGradeAndStudentWithMaterials) {
		treeTableContent.removeAllItems();
		allGradeCheckBoxes.clear();
		allStudentCheckBoxes.clear();
		allMaterialCheckBoxes.clear();
		collapseGrades = false;
		changedCollapseFlag = false;

		for (Grade grade : newGradeAndStudentWithMaterials.keySet()) {
			Set<Student> students = newGradeAndStudentWithMaterials.get(grade).keySet();
			if (students.size() > 0) {
				addGradeToTree(grade);
				Map<Student, List<BorrowedMaterial>> entry = newGradeAndStudentWithMaterials.get(grade);
				for (Student student : entry.keySet()) {
					if (entry.get(student).size() > 0) {
						addStudentToTree(student);
						for (BorrowedMaterial material : entry.get(student)) {
							addMaterialToTree(material);
						}
					}
				}
			}
		}
	}

	/*
	 * Updates the table using the provided new data structure and adds or removes all differences between
	 * the (passed) new structure and currently displayed structure.
	 * It does not set new structure as member variable of the StudentMaterialSelector!
	 * 
	 * @param newGradeAndStudentsWithMaterials
	 * 		the new structure which should be the basis for the displayed content in the StudentMaterialSelector
	 * */
	private void updateTable(Map<Grade, Map<Student, List<BorrowedMaterial>>> newGradeAndStudentsWithMaterials) {
		if (newGradeAndStudentsWithMaterials == null ||
				gradeAndStudentsWithMaterials == null) {
			return;
		}

		ArrayList<Grade> newGrades = new ArrayList<Grade>(newGradeAndStudentsWithMaterials.keySet());
		ArrayList<Student> newStudents = getAllStudentsFromStructure(newGradeAndStudentsWithMaterials);
		ArrayList<BorrowedMaterial> newMaterials = getAllMaterialsFromStructure(newGradeAndStudentsWithMaterials);

		ArrayList<Grade> oldGrades = new ArrayList<Grade>(gradeAndStudentsWithMaterials.keySet());
		ArrayList<Student> oldStudents = getAllStudentsFromStructure(gradeAndStudentsWithMaterials);
		ArrayList<BorrowedMaterial> oldMaterials = getAllMaterialsFromStructure(gradeAndStudentsWithMaterials);

		updateGradeNodes(oldGrades, newGrades);
		updateStudentNodes(oldStudents, newStudents);
		updateMaterialNodes(oldMaterials, newMaterials);

		notifyObserver();
	}

	/*
	 * Helper method used for updating the TreeTable. Updates all grades to be displayed by the StudentMaterialSelector.
	 * When newGrades contains more grade objects than oldGrades grades are added to the TreeTable. When
	 * oldGrades contains more grade objects than newGrades grades are removed from the TreeTable. When
	 * newGrades is equals to oldGrades nothing happens.
	 * This method takes care of setting the corresponding listeners on the added elements and manages
	 * all internally relevant member variables.
	 * 
	 * @param oldGrades
	 * 		an arraylist containing all currently showing grades
	 * @param newGrades
	 * 		an arraylist containing all grades to be displayed after calling this method
	 * */
	private void updateGradeNodes(ArrayList<Grade> oldGrades, ArrayList<Grade> newGrades) {
		if (newGrades.size() > oldGrades.size()) {
			newGrades.removeAll(oldGrades);
			for (Grade grade : newGrades) {
				addGradeToTree(grade);
			}
		}
		else if (newGrades.size() < oldGrades.size()) {
			oldGrades.removeAll(newGrades);
			for (Grade grade : oldGrades) {
				for (CheckBox checkBoxGrade : allGradeCheckBoxes.keySet()) {
					if (checkBoxGrade.getData().equals(grade)) {
						treeTableContent.removeItem(allGradeCheckBoxes.get(checkBoxGrade));

						allGradeCheckBoxes.remove(checkBoxGrade);
						break;
					}
				}
			}
		}
	}

	/*
	 * Helper method used by update table. @see StudentMaterialSelector.updateGradeNodes
	 * 
	 * @param oldStudents
	 * 		an arraylist containing all currently showing students
	 * @param newStudents
	 * 		an arraylist containing all students to be displayed after calling this method
	 * */
	private void updateStudentNodes(ArrayList<Student> oldStudents, ArrayList<Student> newStudents) {
		if (newStudents.size() > oldStudents.size()) {
			newStudents.removeAll(oldStudents);
			for (Student student : newStudents) {
				addStudentToTree(student);
			}
		}
		else if (newStudents.size() < oldStudents.size()) {
			oldStudents.removeAll(newStudents);
			for (Student student : oldStudents) {
				for (CheckBox checkBoxStudent : allStudentCheckBoxes.keySet()) {
					if (checkBoxStudent.getData().equals(student)) {
						treeTableContent.removeItem(allStudentCheckBoxes.get(checkBoxStudent));
						allGradeCheckBoxes.remove(checkBoxStudent);
						break;
					}
				}
			}
		}
	}

	/*
	 * Helper method used by update table. @see StudentMaterialSelector.updateGradeNodes
	 * 
	 * @param oldMaterials
	 * 		an arraylist containing all currently showing materials
	 * @param newMaterials
	 * 		an arraylist containing all materials to be displayed after calling this method
	 * */
	private void updateMaterialNodes(ArrayList<BorrowedMaterial> oldMaterials, ArrayList<BorrowedMaterial> newMaterials) {
		if (newMaterials.size() > oldMaterials.size()) {
			newMaterials.removeAll(oldMaterials);
			for (BorrowedMaterial material : newMaterials) {
				addMaterialToTree(material);
			}
		}
		else if (newMaterials.size() < oldMaterials.size()) {
			oldMaterials.removeAll(newMaterials);
			for (BorrowedMaterial material : oldMaterials) {
				for (CheckBox checkBoxMaterial : allMaterialCheckBoxes.keySet()) {
					if (checkBoxMaterial.getData().equals(material)) {
						treeTableContent.removeItem(allMaterialCheckBoxes.get(checkBoxMaterial));
						allGradeCheckBoxes.remove(checkBoxMaterial);
						break;
					}
				}
			}
		}
	}

	/*
	 * Helper method used to update or rebuild or the TreeTable.
	 * This method takes care of adding the grade to the tree and storing 
	 * all information in the corresponding member variables.
	 * 
	 * @return
	 * 		the item id of the newly added grade or null if there has been an error
	 * */
	private Object addGradeToTree(Grade grade) {
		if (grade == null) {
			return null;
		}
		CheckBox checkBoxGrade = new CheckBox(GRADE + grade.getGrade() + grade.getSuffix());
		checkBoxGrade.setData(grade);
		checkBoxGrade.addValueChangeListener(gradeListener);
		Object gradeItemId = treeTableContent.addItem(new Object[] { checkBoxGrade }, null);
		treeTableContent.setCollapsed(gradeItemId, collapseGrades);

		allGradeCheckBoxes.put(checkBoxGrade, gradeItemId);

		return gradeItemId;
	}

	/*
	 * @see StudentMaterialSelector.addGradeToTree(Grade grade)
	 * This method takes care of putting the student under right grade to maintain a valid hierarchy.
	 * 
	 * @return
	 * 		the item id of the newly added student or null if there has been an error
	 * */
	private Object addStudentToTree(Student student) {
		if (student == null) {
			return null;
		}
		CheckBox checkBoxStudent = new CheckBox(student.getFirstname() + " " + student.getLastname());
		checkBoxStudent.setData(student);
		checkBoxStudent.addValueChangeListener(studentListener);
		Object studentItemId = treeTableContent.addItem(new Object[] { checkBoxStudent }, null);

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

		if (allStudentCheckBoxes.size() > MAX_STUDENTS_BEFORE_COLLAPSE &&
				changedCollapseFlag == false) {
			collapseGrades = true;
			changedCollapseFlag = true;
			for (CheckBox checkBoxGrade : allGradeCheckBoxes.keySet()) {
				treeTableContent.setCollapsed(allGradeCheckBoxes.get(checkBoxGrade), collapseGrades);
			}
		}

		return studentItemId;
	}

	/*
	 * @see StudentMaterialSelector.addGradeToTree(Grade grade)
	 * This method takes care of putting the material under right student to maintain a valid hierarchy.
	 * 
	 * @return
	 * 		the item id for the newly added material or null if there has been an error
	 * */
	private Object addMaterialToTree(BorrowedMaterial material) {
		if (material == null) {
			return null;
		}
		CheckBox checkBoxMaterial = new CheckBox(material.getTeachingMaterial().getName());
		checkBoxMaterial.setData(material);
		checkBoxMaterial.addValueChangeListener(materialListener);
		Object materialItemId = treeTableContent.addItem(new Object[] { checkBoxMaterial }, null);
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
	 * Helper method which extracts all student objects from a data structure
	 * @param structure
	 * 		the structure which all students object should be extracted from
	 * @return
	 * 		all students from the structure in an arraylist
	 * */
	private ArrayList<Student> getAllStudentsFromStructure(Map<Grade, Map<Student, List<BorrowedMaterial>>> structure) {
		ArrayList<Student> students = new ArrayList<Student>();
		for (Grade grade : structure.keySet()) {
			Map<Student, List<BorrowedMaterial>> studentsWithMaterials = structure.get(grade);
			students.addAll(studentsWithMaterials.keySet());
		}

		return students;
	}

	/*
	 * Helper method which extracts all borrowedmaterial objects from a data structure
	 * @param structure
	 * 		the structure which all borrowedmaterial objects should be extracted from
	 * @return
	 * 		all borrowedmaterials from the structure in an arraylist
	 * */
	private ArrayList<BorrowedMaterial> getAllMaterialsFromStructure(Map<Grade, Map<Student, List<BorrowedMaterial>>> structure) {
		ArrayList<BorrowedMaterial> materials = new ArrayList<BorrowedMaterial>();
		for (Grade grade : structure.keySet()) {
			Map<Student, List<BorrowedMaterial>> studentsWithMaterials = structure.get(grade);
			for (Student student : studentsWithMaterials.keySet()) {
				materials.addAll(studentsWithMaterials.get(student));
			}
		}

		return materials;
	}

	/*
	 * Notifies all registered observers that the selection changed by calling their update method.
	 * */
	private void notifyObserver() {
		if (registeredObservers == null) {
			return;
		}

		for (StudentMaterialSelectorObserver observer : registeredObservers) {
			observer.update();
		}
	}
}