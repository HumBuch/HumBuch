package de.dhbw.humbuch.view.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.view.LendingView;


public class StudentMaterialSelector extends CustomComponent {

	private static final long serialVersionUID = -618911643102742679L;

	private final static Logger LOG = LoggerFactory.getLogger(StudentMaterialSelector.class);

	public static final String TREE_TABLE_HEADER = "Daten auswählen";
	private static final String GRADE = "Klasse ";

	private VerticalLayout verticalLayoutContent;
	private TreeTable treeTableContent;
	private Map<Grade, Map<Student, List<BorrowedMaterial>>> gradeAndStudentsWithMaterials;
	private Map<Grade, Map<Student, List<BorrowedMaterial>>> gradeAndStudentsWithMaterialsFiltered;
	private LendingView registeredObserver;
	private HashMap<CheckBox, Object> allCheckBoxesWithId;
	private String filterString;

	public StudentMaterialSelector() {
		init();
		buildLayout();
	}

	public void setGradesAndStudentsWithMaterials(Map<Grade, Map<Student, List<BorrowedMaterial>>> gradeAndStudentsWithMaterials) {
		if (allCheckBoxesWithId.keySet().size() != 0) {
			updateTable(gradeAndStudentsWithMaterials);
			this.gradeAndStudentsWithMaterials = gradeAndStudentsWithMaterials;
			gradeAndStudentsWithMaterialsFiltered = gradeAndStudentsWithMaterials;
		}
		else {
			this.gradeAndStudentsWithMaterials = gradeAndStudentsWithMaterials;
			gradeAndStudentsWithMaterialsFiltered = gradeAndStudentsWithMaterials;
			buildTable();
		}
	}

	public HashSet<Grade> getCurrentlySelectedGrades() {
		HashSet<Grade> currentlySelectedGrades = new HashSet<Grade>();
		for (CheckBox checkBox : allCheckBoxesWithId.keySet()) {
			if (checkBox.getData() instanceof Grade) {
				if (checkBox.getValue() == true) {
					currentlySelectedGrades.add((Grade) checkBox.getData());
				}
			}
		}

		return currentlySelectedGrades;
	}

	public HashSet<Student> getCurrentlySelectedStudents() {
		HashSet<Student> currentlySelectedStudents = new HashSet<Student>();
		for (CheckBox checkBox : allCheckBoxesWithId.keySet()) {
			if (checkBox.getData() instanceof Student) {
				if (checkBox.getValue() == true) {
					currentlySelectedStudents.add((Student) checkBox.getData());
				}
			}
		}

		return currentlySelectedStudents;
	}

	public HashSet<BorrowedMaterial> getCurrentlySelectedBorrowedMaterials() {
		HashSet<BorrowedMaterial> currentlySelectedBorrowedMaterials = new HashSet<BorrowedMaterial>();
		for (CheckBox checkBox : allCheckBoxesWithId.keySet()) {
			if (checkBox.getData() instanceof BorrowedMaterial) {
				if (checkBox.getValue() == true) {
					currentlySelectedBorrowedMaterials.add((BorrowedMaterial) checkBox.getData());
				}
			}
		}
		return currentlySelectedBorrowedMaterials;
	}

	private void init() {
		verticalLayoutContent = new VerticalLayout();
		treeTableContent = new TreeTable();
		allCheckBoxesWithId = new HashMap<CheckBox, Object>();

		verticalLayoutContent.setSpacing(true);

		treeTableContent.setSizeFull();
		treeTableContent.setImmediate(true);
		treeTableContent.addContainerProperty(TREE_TABLE_HEADER, CheckBox.class, null);
	}

	private void buildLayout() {
		verticalLayoutContent.addComponent(treeTableContent);

		setCompositionRoot(verticalLayoutContent);
	}

	private void buildTable() {
		if (treeTableContent.removeAllItems()) {
			if (gradeAndStudentsWithMaterialsFiltered.isEmpty()) {
				return;
			}
			allCheckBoxesWithId.clear();
			Set<Grade> grades = gradeAndStudentsWithMaterialsFiltered.keySet();
			// Add all grades as roots
			for (Grade grade : grades) {
				final CheckBox checkBoxGrade = new CheckBox(GRADE + grade.getGrade() + grade.getSuffix());
				checkBoxGrade.setData(grade);
				Object gradeItemId = treeTableContent.addItem(new Object[] { checkBoxGrade }, null);

				allCheckBoxesWithId.put(checkBoxGrade, gradeItemId);
				List<Student> students = getAllStudentsForGrade(grade);

				// Collect all student checkboxes for selecting purposes
				final ArrayList<CheckBox> studentCheckBoxes = new ArrayList<CheckBox>();
				// Add all students below the grade level 
				for (final Student student : students) {
					final CheckBox checkBoxStudent = new CheckBox(student.getFirstname() + " " + student.getLastname());
					checkBoxStudent.setData(student);

					studentCheckBoxes.add(checkBoxStudent);

					Object studentItemId = treeTableContent.addItem(new Object[] { checkBoxStudent }, null);
					treeTableContent.setParent(studentItemId, gradeItemId);

					allCheckBoxesWithId.put(checkBoxStudent, studentItemId);
					List<BorrowedMaterial> materials = gradeAndStudentsWithMaterialsFiltered.get(grade).get(student);
					if (materials == null) {
						LOG.warn("Borrowed Materials for Student " + student.getFirstname() + " " + student.getLastname() + " are null");
						continue;
					}

					// Collect all borrowed materials checkboxes for selecting purposes
					final ArrayList<CheckBox> borrowedMaterialCheckBoxes = new ArrayList<CheckBox>();
					for (final BorrowedMaterial material : materials) {
						final CheckBox checkBoxMaterial = new CheckBox(material.getTeachingMaterial().getName());
						checkBoxMaterial.setData(material);

						borrowedMaterialCheckBoxes.add(checkBoxMaterial);

						Object materialItemId = treeTableContent.addItem(new Object[] { checkBoxMaterial }, null);
						treeTableContent.setParent(materialItemId, studentItemId);
						treeTableContent.setChildrenAllowed(materialItemId, false);

						allCheckBoxesWithId.put(checkBoxMaterial, materialItemId);

						// Whenever a borrowed material changes its value change the Set with all selected materials
						checkBoxMaterial.addValueChangeListener(new ValueChangeListener() {

							private static final long serialVersionUID = -7395293342050339912L;

							@Override
							public void valueChange(ValueChangeEvent event) {
								notifyObserver();
							}

						});
					}

					// Whenever the student changes its value it changes all materials
					checkBoxStudent.addValueChangeListener(new ValueChangeListener() {

						private static final long serialVersionUID = -7630914682634326352L;

						@Override
						public void valueChange(ValueChangeEvent event) {
							for (CheckBox checkBoxMaterial : borrowedMaterialCheckBoxes) {
								notifyObserver();

								checkBoxMaterial.setValue(checkBoxStudent.getValue());
							}
						}

					});
				}

				// Whenever the grade changes its value it changes all students
				checkBoxGrade.addValueChangeListener(new ValueChangeListener() {

					private static final long serialVersionUID = -1660506729015821014L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						boolean gradeSelected = checkBoxGrade.getValue();
						for (CheckBox checkBoxStudent : studentCheckBoxes) {
							notifyObserver();

							checkBoxStudent.setValue(gradeSelected);
						}
					}

				});
			}
		}
		else {
			LOG.warn("Could not remove all items from TreeTable in StudentMaterialSelector. "
					+ "Continuing without changing the displayed data.");
		}
	}

	private void updateTable(Map<Grade, Map<Student, List<BorrowedMaterial>>> newGradeAndStudentsWithMaterials) {
		ArrayList<Grade> newGrades = new ArrayList<Grade>(newGradeAndStudentsWithMaterials.keySet());
		ArrayList<Student> newStudents = getAllStudentsFromStructure(newGradeAndStudentsWithMaterials);
		ArrayList<BorrowedMaterial> newMaterials = getAllMaterialsFromStructure(newGradeAndStudentsWithMaterials);

		ArrayList<Grade> oldGrades = new ArrayList<Grade>(gradeAndStudentsWithMaterials.keySet());
		ArrayList<Student> oldStudents = getAllStudentsFromStructure(gradeAndStudentsWithMaterials);
		ArrayList<BorrowedMaterial> oldMaterials = getAllMaterialsFromStructure(gradeAndStudentsWithMaterials);

		oldGrades.removeAll(newGrades);
		oldStudents.removeAll(newStudents);
		oldMaterials.removeAll(newMaterials);

		for (CheckBox checkBox : allCheckBoxesWithId.keySet()) {
			if (checkBox.getData() instanceof Grade) {
				Grade grade = (Grade) checkBox.getData();
				if (oldGrades.contains(grade)) {
					treeTableContent.removeItem(allCheckBoxesWithId.get(checkBox));
				}
			}
			else if (checkBox.getData() instanceof Student) {
				Student student = (Student) checkBox.getData();
				if (oldStudents.contains(student)) {
					treeTableContent.removeItem(allCheckBoxesWithId.get(checkBox));
				}

			}
			else if (checkBox.getData() instanceof BorrowedMaterial) {
				BorrowedMaterial material = (BorrowedMaterial) checkBox.getData();
				if (oldMaterials.contains(material)) {
					treeTableContent.removeItem(allCheckBoxesWithId.get(checkBox));
				}
			}
		}
	}

	public void setFilterString(String filterString) {
		this.filterString = filterString;
		filterTableContent();
	}

	private void filterTableContent() {
		gradeAndStudentsWithMaterialsFiltered = new LinkedHashMap<Grade, Map<Student, List<BorrowedMaterial>>>();
		for (Grade grade : gradeAndStudentsWithMaterials.keySet()) {
			Map<Student, List<BorrowedMaterial>> entry = gradeAndStudentsWithMaterials.get(grade);
			Map<Student, List<BorrowedMaterial>> filteredEntries = new LinkedHashMap<Student, List<BorrowedMaterial>>();
			for (Student student : entry.keySet()) {
				boolean matchesFilter = false;

				// match firstname and lastname ignoring case
				String fullName = student.getFirstname() + " " + student.getLastname();
				fullName = fullName.toLowerCase();
				if (fullName.contains(filterString.toLowerCase())) {
					matchesFilter = true;
				}

				if (matchesFilter) {
					filteredEntries.put(student, entry.get(student));
				}
			}

			if (filteredEntries.size() != 0) {
				gradeAndStudentsWithMaterialsFiltered.put(grade, filteredEntries);
			}
		}
		buildTable();
	}

	private ArrayList<Student> getAllStudentsFromStructure(Map<Grade, Map<Student, List<BorrowedMaterial>>> structure) {
		ArrayList<Student> students = new ArrayList<Student>();
		for (Grade grade : structure.keySet()) {
			Map<Student, List<BorrowedMaterial>> studentsWithMaterials = structure.get(grade);
			students.addAll(studentsWithMaterials.keySet());
		}

		return students;
	}

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

	private List<Student> getAllStudentsForGrade(Grade grade) {
		Map<Student, List<BorrowedMaterial>> studentsWithMaterials = gradeAndStudentsWithMaterialsFiltered.get(grade);
		List<Student> studentList = new ArrayList<Student>(studentsWithMaterials.keySet());
		return studentList;
	}

	public void registerAsObserver(LendingView lendingView) {
		registeredObserver = lendingView;
	}

	public void notifyObserver() {
		if (registeredObserver == null) {
			return;
		}

		registeredObserver.update();
	}

	//	// Compare to the code of SimpleStringFilter. Just adapted one method to work with checkboxes
	//	public static class SimpleCheckBoxFilter implements Filter {
	//
	//		private static final long serialVersionUID = -562636677610443920L;
	//
	//		private final Object propertyId;
	//		private final String filterString;
	//		private final boolean ignoreCase;
	//		private final boolean onlyMatchPrefix;
	//
	//		public SimpleCheckBoxFilter(Object propertyId, String filterString, boolean ignoreCase, boolean onlyMatchPrefix) {
	//			this.propertyId = propertyId;
	//			this.filterString = filterString;
	//			this.ignoreCase = ignoreCase;
	//			this.onlyMatchPrefix = onlyMatchPrefix;
	//		}
	//
	//		@Override
	//		public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
	//			final Property<?> p = item.getItemProperty(propertyId);
	//			if (p == null) {
	//				return false;
	//			}
	//			Object propertyValue = p.getValue();
	//			if (propertyValue == null || !(propertyValue instanceof CheckBox)) {
	//				return false;
	//			}
	//			CheckBox checkBoxProperty = (CheckBox) propertyValue;
	//			final String value = ignoreCase ? checkBoxProperty.getCaption()
	//					.toLowerCase() : checkBoxProperty.getCaption();
	//			if (onlyMatchPrefix) {
	//				if (!value.startsWith(filterString)) {
	//					return false;
	//				}
	//			}
	//			else {
	//				if (!value.contains(filterString)) {
	//					return false;
	//				}
	//			}
	//			return true;
	//		}
	//
	//		@Override
	//		public boolean appliesToProperty(Object propertyId) {
	//			return this.propertyId.equals(propertyId);
	//		}
	//
	//		@Override
	//		public boolean equals(Object obj) {
	//
	//			// Only ones of the objects of the same class can be equal
	//			if (!(obj instanceof SimpleStringFilter)) {
	//				return false;
	//			}
	//			final SimpleStringFilter o = (SimpleStringFilter) obj;
	//
	//			// Checks the properties one by one
	//			if (propertyId != o.getPropertyId() && o.getPropertyId() != null
	//					&& !o.getPropertyId().equals(propertyId)) {
	//				return false;
	//			}
	//			if (filterString != o.getFilterString() && o.getFilterString() != null
	//					&& !o.getFilterString().equals(filterString)) {
	//				return false;
	//			}
	//			if (ignoreCase != o.isIgnoreCase()) {
	//				return false;
	//			}
	//			if (onlyMatchPrefix != o.isOnlyMatchPrefix()) {
	//				return false;
	//			}
	//
	//			return true;
	//		}
	//
	//		public Object getPropertyId() {
	//			return propertyId;
	//		}
	//
	//		public String getFilterString() {
	//			return filterString;
	//		}
	//
	//		public boolean isIgnoreCase() {
	//			return ignoreCase;
	//		}
	//
	//		public boolean isOnlyMatchPrefix() {
	//			return onlyMatchPrefix;
	//		}
	//
	//	}
}