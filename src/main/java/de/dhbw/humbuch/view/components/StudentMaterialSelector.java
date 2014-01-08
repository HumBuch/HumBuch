package de.dhbw.humbuch.view.components;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;


public class StudentMaterialSelector extends CustomComponent {

	private static final long serialVersionUID = -618911643102742679L;

	private static final String TREE_TABLE_HEADER = "Daten auswählen";
	private static final String SEARCH_STUDENTS = "Schüler durchsuchen";

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
		buildTreeTable();
	}

	private void buildLayout() {
		verticalLayoutContent.addComponent(textFieldSearchBar);
		verticalLayoutContent.addComponent(treeTableContent);

		setCompositionRoot(verticalLayoutContent);
	}

	private void buildTreeTable() {
		// root
		treeTableContent.addItem(new Object[] { new CheckBox("Alle Klassen") }, 1);
		// class levels
		treeTableContent.addItem(new Object[] { new CheckBox("Klassenstufe 5") }, 2);
		treeTableContent.addItem(new Object[] { new CheckBox("Klassenstufe 6") }, 3);
		treeTableContent.addItem(new Object[] { new CheckBox("Klassenstufe 7") }, 4);
		// classes
		treeTableContent.addItem(new Object[] { new CheckBox("5a") }, 5);
		treeTableContent.addItem(new Object[] { new CheckBox("5b") }, 6);
		treeTableContent.addItem(new Object[] { new CheckBox("6a") }, 7);
		treeTableContent.addItem(new Object[] { new CheckBox("6b") }, 8);
		treeTableContent.addItem(new Object[] { new CheckBox("7a") }, 9);
		treeTableContent.addItem(new Object[] { new CheckBox("7b") }, 10);
		// students
		treeTableContent.addItem(new Object[] { new CheckBox("Hans Wurst") }, 11);
		treeTableContent.addItem(new Object[] { new CheckBox("Max Mustermann") }, 12);
		treeTableContent.addItem(new Object[] { new CheckBox("Micky Mouse") }, 13);
		// borrowed materials
		treeTableContent.addItem(new Object[] { new CheckBox("Englisch Buch") }, 14);
		treeTableContent.setChildrenAllowed(14, false);
		treeTableContent.addItem(new Object[] { new CheckBox("Mathe Buch") }, 15);
		treeTableContent.setChildrenAllowed(15, false);
		treeTableContent.addItem(new Object[] { new CheckBox("Französisch Buch") }, 16);
		treeTableContent.setChildrenAllowed(16, false);
		// hierarchy
		treeTableContent.setParent(2, 1);
		treeTableContent.setParent(3, 1);
		treeTableContent.setParent(4, 1);
		treeTableContent.setParent(5, 2);
		treeTableContent.setParent(6, 2);
		treeTableContent.setParent(7, 3);
		treeTableContent.setParent(8, 3);
		treeTableContent.setParent(9, 4);
		treeTableContent.setParent(10, 4);
		treeTableContent.setParent(11, 5);
		treeTableContent.setParent(12, 7);
		treeTableContent.setParent(13, 9);
		treeTableContent.setParent(14, 11);
		treeTableContent.setParent(15, 12);
		treeTableContent.setParent(16, 13);
	}
}
