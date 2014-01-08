package de.dhbw.humbuch.view.components;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;


public class StudentMaterialSelector extends CustomComponent {

	private static final long serialVersionUID = -618911643102742679L;


	public static enum Process {
		LENDING,
		RETURNING
	};

	private static final String TREE_TABLE_HEADER = "Daten auswählen";
	private static final String SEARCH_STUDENTS = "Schüler durchsuchen";
	private static final String SAVE_SELECTED_RETURNING = "Ausgewählte Bücher zurückgegeben";
	private static final String SAVE_SELECTED_LENDING = "Ausgewählte Bücher zurückgegeben";
	private static final String CLASS_LIST = "Klassenliste für Auswahl drucken";
	private static final String STUDENT_LIST = "Schülerliste für Auswahl drücken";

	private VerticalLayout verticalLayoutContent;
	private HorizontalLayout horizontalLayoutSearchBar;
	private TextField textFieldSearchBar;
	private TreeTable treeTableContent;
	private Button buttonSaveSelectedData;
	private Button buttonClassList;
	private Button buttonStudentList;
	private ThemeResource themeResourceIconPrint;
	private Process process;

	public StudentMaterialSelector(Process process) {
		this.process = process;
		init();
		buildLayout();
	}

	private void init() {
		verticalLayoutContent = new VerticalLayout();
		horizontalLayoutSearchBar = new HorizontalLayout();
		textFieldSearchBar = new TextField(SEARCH_STUDENTS);
		treeTableContent = new TreeTable();
		buttonClassList = new Button(CLASS_LIST);
		buttonStudentList = new Button(STUDENT_LIST);
		themeResourceIconPrint = new ThemeResource("images/icons/16/icon_print_red.png");

		// change button label depending on process
		if (process == Process.LENDING) {
			buttonSaveSelectedData = new Button(SAVE_SELECTED_RETURNING);
		}
		else if (process == Process.RETURNING) {
			buttonSaveSelectedData = new Button(SAVE_SELECTED_LENDING);
		}

		// set icons for buttons
		buttonSaveSelectedData.setIcon(themeResourceIconPrint);
		buttonClassList.setIcon(themeResourceIconPrint);
		buttonStudentList.setIcon(themeResourceIconPrint);

		horizontalLayoutSearchBar.setWidth("100%");
		textFieldSearchBar.setWidth("100%");

		treeTableContent.setWidth("100%");
		treeTableContent.setPageLength(0);
		treeTableContent.addContainerProperty(TREE_TABLE_HEADER, CheckBox.class, null);
		buildTreeTable();
	}

	private void buildLayout() {
		// Build button bar
		horizontalLayoutSearchBar.addComponent(buttonSaveSelectedData);
		if (process == Process.LENDING) {
			horizontalLayoutSearchBar.addComponent(buttonClassList);
		}
		horizontalLayoutSearchBar.addComponent(buttonStudentList);

		verticalLayoutContent.addComponent(textFieldSearchBar);
		verticalLayoutContent.addComponent(treeTableContent);
		verticalLayoutContent.addComponent(horizontalLayoutSearchBar);

		setCompositionRoot(verticalLayoutContent);
	}

	private void buildTreeTable() {
		// root
		treeTableContent.addItem(new Object[] {new CheckBox("Alle Klassen")}, 1);
		// class levels
		treeTableContent.addItem(new Object[] {new CheckBox("Klassenstufe 5")}, 2);
		treeTableContent.addItem(new Object[] {new CheckBox("Klassenstufe 6")}, 3);
		treeTableContent.addItem(new Object[] {new CheckBox("Klassenstufe 7")}, 4);
		// classes
		treeTableContent.addItem(new Object[] {new CheckBox("5a")}, 5);
		treeTableContent.addItem(new Object[] {new CheckBox("5b")}, 6);
		treeTableContent.addItem(new Object[] {new CheckBox("6a")}, 7);
		treeTableContent.addItem(new Object[] {new CheckBox("6b")}, 8);
		treeTableContent.addItem(new Object[] {new CheckBox("7a")}, 9);
		treeTableContent.addItem(new Object[] {new CheckBox("7b")}, 10);
		// students
		treeTableContent.addItem(new Object[] {new CheckBox("Hans Wurst")}, 11);
		treeTableContent.addItem(new Object[] {new CheckBox("Max Mustermann")}, 12);
		treeTableContent.addItem(new Object[] {new CheckBox("Micky Mouse")}, 13);
		// borrowed materials
		treeTableContent.addItem(new Object[] {new CheckBox("Englisch Buch")}, 14);
		treeTableContent.setChildrenAllowed(14, false);
		treeTableContent.addItem(new Object[] {new CheckBox("Mathe Buch")}, 15);
		treeTableContent.setChildrenAllowed(15, false);
		treeTableContent.addItem(new Object[] {new CheckBox("Französisch Buch")}, 16);
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
		treeTableContent.setParent(14, 5);
		treeTableContent.setParent(15, 7);
		treeTableContent.setParent(16, 9);
	}
}
