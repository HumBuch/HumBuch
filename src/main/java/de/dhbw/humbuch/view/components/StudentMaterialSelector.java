package de.dhbw.humbuch.view.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.view.LendingView;


public class StudentMaterialSelector extends CustomComponent {

	private static final long serialVersionUID = -618911643102742679L;

	private final static Logger LOG = LoggerFactory.getLogger(StudentMaterialSelector.class);

	private static final String TREE_TABLE_HEADER = "Daten auswählen";
	private static final String SEARCH_STUDENTS = "Schüler durchsuchen";
	private static final String GRADE = "Klasse ";

	private VerticalLayout verticalLayoutContent;
	private TextField textFieldSearchBar;
	private TreeTable treeTableContent;
	//	private IndexedContainer indexedContainerForTreeTable;
	private Map<Grade, Map<Student, List<BorrowedMaterial>>> gradeAndStudentsWithMaterials;
	private LendingView registeredObserver;
	private ArrayList<CheckBox> allCheckBoxes;

	public StudentMaterialSelector() {
		init();
		buildLayout();
	}

	public void setGradesAndStudentsWithMaterials(Map<Grade, Map<Student, List<BorrowedMaterial>>> gradeAndStudentsWithMaterials) {
		this.gradeAndStudentsWithMaterials = gradeAndStudentsWithMaterials;
		buildTable();
	}

	public HashSet<Grade> getCurrentlySelectedGrades() {
		HashSet<Grade> currentlySelectedGrades = new HashSet<Grade>();
		for (CheckBox checkBox : allCheckBoxes) {
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
		for (CheckBox checkBox : allCheckBoxes) {
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
		for (CheckBox checkBox : allCheckBoxes) {
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
		textFieldSearchBar = new TextField(SEARCH_STUDENTS);
		treeTableContent = new TreeTable();
		allCheckBoxes = new ArrayList<CheckBox>();

		//		indexedContainerForTreeTable = new IndexedContainer();

		verticalLayoutContent.setSpacing(true);
		textFieldSearchBar.setWidth("50%");

		treeTableContent.setSizeFull();
		treeTableContent.addContainerProperty(TREE_TABLE_HEADER, CheckBox.class, null);

		//		indexedContainerForTreeTable.addContainerProperty(TREE_TABLE_HEADER, CheckBox.class, null);
		//		treeTableContent.setContainerDataSource(indexedContainerForTreeTable);
		//		addFilterToContainer();
	}

	private void buildLayout() {
		verticalLayoutContent.addComponent(textFieldSearchBar);
		verticalLayoutContent.addComponent(treeTableContent);

		setCompositionRoot(verticalLayoutContent);
	}

	//	private void addFilterToContainer() {
	//		textFieldSearchBar.addTextChangeListener(new TextChangeListener() {
	//
	//			private static final long serialVersionUID = 1909461513444694234L;
	//
	//			@Override
	//			public void textChange(TextChangeEvent event) {
	//				SimpleCheckBoxFilter filter = new SimpleCheckBoxFilter(TREE_TABLE_HEADER, event.getText(), true, false);
	//				indexedContainerForTreeTable.removeAllContainerFilters();
	//				indexedContainerForTreeTable.addContainerFilter(filter);
	//			}
	//		});
	//	}

	private void buildTable() {
		if (treeTableContent.removeAllItems()) {
			if (gradeAndStudentsWithMaterials.isEmpty()) {
				return;
			}

			Set<Grade> grades = gradeAndStudentsWithMaterials.keySet();
			// Add all grades below the grade level
			for (Grade grade : grades) {
				final CheckBox checkBoxGrade = new CheckBox(GRADE + grade.getGrade() + grade.getSuffix());
				checkBoxGrade.setData(grade);

				allCheckBoxes.add(checkBoxGrade);

				Object gradeItemId = treeTableContent.addItem(new Object[] { checkBoxGrade }, null);

				List<Student> students = getAllStudentsForGrade(grade);

				// Collect all student checkboxes for selecting purposes
				final ArrayList<CheckBox> studentCheckBoxes = new ArrayList<CheckBox>();
				// Add all students below the grade level 
				for (final Student student : students) {
					final CheckBox checkBoxStudent = new CheckBox(student.getFirstname() + " " + student.getLastname());
					checkBoxStudent.setData(student);

					studentCheckBoxes.add(checkBoxStudent);
					allCheckBoxes.add(checkBoxStudent);

					Object studentItemId = treeTableContent.addItem(new Object[] { checkBoxStudent }, null);
					treeTableContent.setParent(studentItemId, gradeItemId);

					List<BorrowedMaterial> materials = gradeAndStudentsWithMaterials.get(grade).get(student);

					// Collect all borrowed materials checkboxes for selecting purposes
					final ArrayList<CheckBox> borrowedMaterialCheckBoxes = new ArrayList<CheckBox>();
					for (final BorrowedMaterial material : materials) {
						final CheckBox checkBoxMaterial = new CheckBox(material.getTeachingMaterial().getName());
						checkBoxMaterial.setData(material);

						borrowedMaterialCheckBoxes.add(checkBoxMaterial);
						allCheckBoxes.add(checkBoxMaterial);

						Object materialItemId = treeTableContent.addItem(new Object[] { checkBoxMaterial }, null);
						treeTableContent.setParent(materialItemId, studentItemId);
						treeTableContent.setChildrenAllowed(materialItemId, false);

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

	private List<Student> getAllStudentsForGrade(Grade grade) {
		Map<Student, List<BorrowedMaterial>> studentsWithMaterials = gradeAndStudentsWithMaterials.get(grade);
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
	//	private class SimpleCheckBoxFilter implements Filter {
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