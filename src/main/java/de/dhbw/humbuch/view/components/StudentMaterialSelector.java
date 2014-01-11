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
			LOG.warn("found " + gradeLevels.size() + " grade levels");
			
			// Collect all grade level checkboxes for selecting purposes
			ArrayList<CheckBox> gradeLevelCheckBoxes = new ArrayList<CheckBox>();
			// Add all the grade levels below the root
			for (Integer gradeLevel : gradeLevels) {
				CheckBox checkBoxGradeLevel = new CheckBox(GRADE_LEVEL + gradeLevel);
				gradeLevelCheckBoxes.add(checkBoxGradeLevel);
				Object gradeLevelItemId = treeTableContent.addItem(new Object[] { checkBoxGradeLevel }, null);
				treeTableContent.setParent(gradeLevelItemId, rootItemId);

				Set<Grade> grades = getAllGradesForGradeLevel(gradeLevel);
				LOG.warn("found " + grades.size() + " grades");
				
				// Collect all grade checkboxes for selecting purposes
				ArrayList<CheckBox> gradeCheckBoxes = new ArrayList<CheckBox>();
				// Add all grades below the grade level
				for (Grade grade : grades) {
					CheckBox checkBoxGrade = new CheckBox("" + grade.getGrade() + grade.getSuffix());
					gradeCheckBoxes.add(checkBoxGrade);
					Object gradeItemId = treeTableContent.addItem(new Object[] { checkBoxGrade }, null);
					treeTableContent.setParent(gradeItemId, gradeLevelItemId);

					List<Student> students = gradeAndStudents.get(grade);
					LOG.warn("found " + students.size() + " students");

					// Collect all student checkboxes for selecting purposes
					ArrayList<CheckBox> studentCheckBoxes = new ArrayList<CheckBox>();
					// Add all students below the grade level 
					for (Student student : students) {
						CheckBox checkBoxStudent = new CheckBox(student.getFirstname() + " " + student.getLastname());
						studentCheckBoxes.add(checkBoxStudent);
						Object studentItemId = treeTableContent.addItem(new Object[] { checkBoxStudent }, null);
						treeTableContent.setParent(studentItemId, gradeItemId);

						List<BorrowedMaterial> materials = student.getUnreceivedBorrowedList();
						LOG.warn("found " + materials.size() + " materials");
						
						// Collect all borrowed materials checkboxes for selecting purposes
						ArrayList<CheckBox> borrowedMaterialCheckBoxes = new ArrayList<CheckBox>();
						for (BorrowedMaterial material : materials) {
							CheckBox checkBoxMaterial = new CheckBox(material.getTeachingMaterial().getName());
							borrowedMaterialCheckBoxes.add(checkBoxMaterial);
							Object materialItemId = treeTableContent.addItem(new Object[] { checkBoxMaterial }, null);
							treeTableContent.setParent(materialItemId, studentItemId);
							treeTableContent.setChildrenAllowed(materialItemId, false);
						}

						// listener fur alle students damit sie BorrowedMaterial selecten
					}

					// listener fur alle grades damit sie die students selecten
				}

				// listener fur alle grade levels das sie die grades selecten
			}

			// Whenever the root changes change all grade levels
			checkBoxRoot.addValueChangeListener(new ValueChangeListener() {

				private static final long serialVersionUID = -7395293342050339912L;

				@Override
				public void valueChange(ValueChangeEvent event) {

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
