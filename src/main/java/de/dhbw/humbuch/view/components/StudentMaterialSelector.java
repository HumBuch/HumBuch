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
	private Map<Grade, List<Student>> gradeAndStudents;
	private HashSet<Grade> currentlySelectedGrades;

	public StudentMaterialSelector() {
		init();
		buildLayout();
	}

	public void setStudentsWithUnreceivedBorrowedMaterials(Map<Grade, List<Student>> gradeAndStudents) {
		this.gradeAndStudents = gradeAndStudents;
		buildTable();
	}

	public Set<Grade> getCurrentlySelectedGrades() {
		return null;
	}

	private void init() {
		verticalLayoutContent = new VerticalLayout();
		textFieldSearchBar = new TextField(SEARCH_STUDENTS);
		treeTableContent = new TreeTable();

		verticalLayoutContent.setSpacing(true);
		textFieldSearchBar.setWidth("50%");

		treeTableContent.setWidth("100%");
		treeTableContent.setPageLength(0);
		treeTableContent.addContainerProperty(TREE_TABLE_HEADER, CheckBox.class, null);
	}

	private void buildLayout() {
		verticalLayoutContent.addComponent(textFieldSearchBar);
		verticalLayoutContent.addComponent(treeTableContent);

		setCompositionRoot(verticalLayoutContent);
	}

	private void buildTable() {
		if (treeTableContent.removeAllItems()) {
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
							for (CheckBox checkBoxStudent : studentCheckBoxes) {
								checkBoxStudent.setValue(checkBoxGrade.getValue());
							}
						}

					});
				}

				// Whenever the grade level changes its value it changes all grades
				checkBoxGradeLevel.addValueChangeListener(new ValueChangeListener() {

					private static final long serialVersionUID = -7395293342050339912L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						for (CheckBox checkBoxGrade : gradeCheckBoxes) {
							checkBoxGrade.setValue(checkBoxGradeLevel.getValue());
						}
					}

				});
			}

			// Whenever the root changes its value it changes all grade levels
			checkBoxRoot.addValueChangeListener(new ValueChangeListener() {

				private static final long serialVersionUID = -7395293342050339912L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					for (CheckBox checkBoxGradeLevel : gradeLevelCheckBoxes) {
						checkBoxGradeLevel.setValue(checkBoxRoot.getValue());
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
}
