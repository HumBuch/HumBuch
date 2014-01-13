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


public class StudentMaterialSelector extends CustomComponent {

	private static final long serialVersionUID = -618911643102742679L;

	private final static Logger LOG = LoggerFactory.getLogger(StudentMaterialSelector.class);

	private static final String TREE_TABLE_HEADER = "Daten auswählen";
	private static final String SEARCH_STUDENTS = "Schüler durchsuchen";
	private static final String ALL_GRADES = "Alle Klassen";
	private static final String GRADE_LEVEL = "Klassenstufe ";

	private VerticalLayout verticalLayoutContent;
	private TextField textFieldSearchBar;
	private TreeTable treeTableContent;
	//	private IndexedContainer indexedContainerForTreeTable;
	private Map<Grade, List<Student>> gradeAndStudents;
	private HashSet<Grade> currentlySelectedGrades;

	public StudentMaterialSelector() {
		init();
		buildLayout();
	}

	public void setGradesAndStudents(Map<Grade, List<Student>> gradeAndStudents) {
		this.gradeAndStudents = gradeAndStudents;
		buildTable();
	}

	public Set<Grade> getCurrentlySelectedGrades() {
		return currentlySelectedGrades;
	}

	private void init() {
		verticalLayoutContent = new VerticalLayout();
		textFieldSearchBar = new TextField(SEARCH_STUDENTS);
		treeTableContent = new TreeTable();
		currentlySelectedGrades = new HashSet<Grade>();
		//		indexedContainerForTreeTable = new IndexedContainer();

		verticalLayoutContent.setSpacing(true);
		textFieldSearchBar.setWidth("50%");

		treeTableContent.setWidth("100%");
		treeTableContent.setPageLength(0);
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
			if(gradeAndStudents.isEmpty()) {
				return;
			}
			
			// Build root of tree table
			final CheckBox checkBoxRoot = new CheckBox(ALL_GRADES);
			Object rootItemId = treeTableContent.addItem(new Object[] { checkBoxRoot }, null);

			Set<Integer> gradeLevels = getAllGradeLevels();

			// Collect all grade level checkboxes for selecting purposes
			final ArrayList<CheckBox> gradeLevelCheckBoxes = new ArrayList<CheckBox>();
			// Add all the grade levels below the root
			for (Integer gradeLevel : gradeLevels) {
				final CheckBox checkBoxGradeLevel = new CheckBox(GRADE_LEVEL + gradeLevel);
				gradeLevelCheckBoxes.add(checkBoxGradeLevel);
				Object gradeLevelItemId = treeTableContent.addItem(new Object[] { checkBoxGradeLevel }, null);
				treeTableContent.setParent(gradeLevelItemId, rootItemId);

				Set<Grade> grades = getAllGradesForGradeLevel(gradeLevel);

				// Collect all grade checkboxes for selecting purposes
				final ArrayList<CheckBox> gradeCheckBoxes = new ArrayList<CheckBox>();
				// Add all grades below the grade level
				for (Grade grade : grades) {
					final CheckBox checkBoxGrade = new CheckBox("" + grade.getGrade() + grade.getSuffix());
					checkBoxGrade.setData(grade);
					gradeCheckBoxes.add(checkBoxGrade);
					Object gradeItemId = treeTableContent.addItem(new Object[] { checkBoxGrade }, null);
					treeTableContent.setParent(gradeItemId, gradeLevelItemId);

					List<Student> students = gradeAndStudents.get(grade);

					// Collect all student checkboxes for selecting purposes
					final ArrayList<CheckBox> studentCheckBoxes = new ArrayList<CheckBox>();
					// Add all students below the grade level 
					for (Student student : students) {
						final CheckBox checkBoxStudent = new CheckBox(student.getFirstname() + " " + student.getLastname());
						studentCheckBoxes.add(checkBoxStudent);
						Object studentItemId = treeTableContent.addItem(new Object[] { checkBoxStudent }, null);
						treeTableContent.setParent(studentItemId, gradeItemId);

						List<BorrowedMaterial> materials = student.getUnreceivedBorrowedList();

						// Collect all borrowed materials checkboxes for selecting purposes
						final ArrayList<CheckBox> borrowedMaterialCheckBoxes = new ArrayList<CheckBox>();
						for (BorrowedMaterial material : materials) {
							CheckBox checkBoxMaterial = new CheckBox(material.getTeachingMaterial().getName());
							borrowedMaterialCheckBoxes.add(checkBoxMaterial);
							Object materialItemId = treeTableContent.addItem(new Object[] { checkBoxMaterial }, null);
							treeTableContent.setParent(materialItemId, studentItemId);
							treeTableContent.setChildrenAllowed(materialItemId, false);
						}

						// Whenever the student changes its value it changes all materials
						checkBoxStudent.addValueChangeListener(new ValueChangeListener() {

							private static final long serialVersionUID = -7630914682634326352L;

							@Override
							public void valueChange(ValueChangeEvent event) {
								for (CheckBox checkBoxMaterial : borrowedMaterialCheckBoxes) {
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
								if (gradeSelected) {
									currentlySelectedGrades.add((Grade) checkBoxGrade.getData());
								}
								else {
									currentlySelectedGrades.remove(checkBoxGrade.getData());
								}
								checkBoxStudent.setValue(gradeSelected);
							}
						}

					});
				}

				// Whenever the grade level changes its value it changes all grades
				checkBoxGradeLevel.addValueChangeListener(new ValueChangeListener() {

					private static final long serialVersionUID = -7395293342050339912L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						boolean gradeLevelSelected = checkBoxGradeLevel.getValue();
						for (CheckBox checkBoxGrade : gradeCheckBoxes) {
							if (gradeLevelSelected) {
								currentlySelectedGrades.add((Grade) checkBoxGrade.getData());
							}
							else {
								currentlySelectedGrades.remove(checkBoxGrade.getData());
							}

							checkBoxGrade.setValue(gradeLevelSelected);
						}
					}

				});
			}

			// Whenever the root changes its value it changes all grade levels
			checkBoxRoot.addValueChangeListener(new ValueChangeListener() {

				private static final long serialVersionUID = -7395293342050339912L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					boolean rootSelected = checkBoxRoot.getValue();
					for (CheckBox checkBoxGradeLevel : gradeLevelCheckBoxes) {
						if (rootSelected) {
							currentlySelectedGrades.addAll(gradeAndStudents.keySet());
						}
						else {
							currentlySelectedGrades.removeAll(gradeAndStudents.keySet());
						}

						checkBoxGradeLevel.setValue(rootSelected);
					}
				}

			});
		}
		else {
			LOG.warn("Could not remove all items from TreeTable in StudentMaterialSelector. "
					+ "Continuing without changing the displayed data.");
		}
	}

	private Set<Integer> getAllGradeLevels() {
		Set<Grade> allGrades = gradeAndStudents.keySet();
		HashSet<Integer> allGradeLevels = new HashSet<Integer>();

		// collect all unique grade levels
		for (Grade grade : allGrades) {
			allGradeLevels.add(grade.getGrade());
		}

		return allGradeLevels;
	}

	private Set<Grade> getAllGradesForGradeLevel(Integer gradeLevel) {
		Set<Grade> allGrades = gradeAndStudents.keySet();
		HashSet<Grade> allGradesForGradeLevel = new HashSet<Grade>();

		for (Grade grade : allGrades) {
			if (grade.getGrade() == gradeLevel) {
				allGradesForGradeLevel.add(grade);
			}
		}

		return allGradesForGradeLevel;
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