package de.dhbw.humbuch.view.components;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;


public class EnterDataComponent extends CustomComponent {

	private static final long serialVersionUID = 2174076688116026576L;


	public static enum Process {
		LENDING,
		RETURNING
	};

	private static final String SAVE = "Auswahl speichern";
	private static final String FIRST_NAME = "Vorname";
	private static final String LAST_NAME = "Nachname";
	private static final String CLASS = "Klasse";

	private static final String RECEIVED_BOOKS = "Erhaltene Bücher";
	private static final String ALL_BOOKS_RECEIVED = "Alle Bücher erhalten";
	private static final String RETURNED_BOOKS = "Züruckgegebene Bücher";
	private static final String ALL_BOOKS_RETURNED = "Alle Bücher zurückgegeben";

	private String enterDataHeader;
	private String enterDataLabel;

	private VerticalLayout verticalLayoutContent;
	private TreeTable treeTableStudents;
	private Button buttonSave;
	private FilterSearchBar filterSearchBar;

	public EnterDataComponent(Process process) {
		initProcess(process);
		init();
		buildLayout();
	}

	private void initProcess(Process process) {
		switch (process) {
		case LENDING:
			enterDataHeader = RECEIVED_BOOKS;
			enterDataLabel = ALL_BOOKS_RECEIVED;
			break;

		case RETURNING:
			enterDataHeader = RETURNED_BOOKS;
			enterDataLabel = ALL_BOOKS_RETURNED;
			break;
		}
	}

	private void init() {
		verticalLayoutContent = new VerticalLayout();
		buttonSave = new Button(SAVE);
		treeTableStudents = new TreeTable();
		filterSearchBar = new FilterSearchBar();

		verticalLayoutContent.setWidth("100%");
		verticalLayoutContent.setSpacing(true);
		verticalLayoutContent.setMargin(true);

		filterSearchBar.setWidth("100%");

		buttonSave.setIcon(new ThemeResource("images/icons/16/icon_save_red.png"));

		treeTableStudents.setWidth("100%");
		treeTableStudents.setPageLength(0);
		treeTableStudents.addContainerProperty(enterDataHeader, CheckBox.class, null);
		treeTableStudents.addContainerProperty(FIRST_NAME, String.class, null);
		treeTableStudents.addContainerProperty(LAST_NAME, String.class, null);
		treeTableStudents.addContainerProperty(CLASS, String.class, null);

		populateWithTestData();

	}

	private void buildLayout() {
		verticalLayoutContent.addComponent(filterSearchBar);
		verticalLayoutContent.addComponent(treeTableStudents);
		verticalLayoutContent.addComponent(buttonSave);
		verticalLayoutContent.setComponentAlignment(buttonSave, Alignment.BOTTOM_RIGHT);

		setCompositionRoot(verticalLayoutContent);
	}

	private void populateWithTestData() {
		// Create root elements
		treeTableStudents.addItem(new Object[] { new CheckBox(enterDataLabel), "Max", "Mustermann", "5a" }, 1);
		treeTableStudents.addItem(new Object[] { new CheckBox(enterDataLabel), "Clara", "Maier", "6b" }, 2);
		treeTableStudents.addItem(new Object[] { new CheckBox(enterDataLabel), "Hans", "Mustermann", "9c" }, 3);
		treeTableStudents.addItem(new Object[] { new CheckBox(enterDataLabel), "BLaa", "XYZ", "7a" }, 4);

		// Create child elements
		treeTableStudents.addItem(new Object[] { new CheckBox("Mathe für Anfänger"), null, null, null }, 5);
		treeTableStudents.addItem(new Object[] { new CheckBox("Deutsch für Anfänger"), null, null, null }, 6);
		treeTableStudents.addItem(new Object[] { new CheckBox("Englisch für Anfänger"), null, null, null }, 7);
		treeTableStudents.addItem(new Object[] { new CheckBox("Kochen für Anfänger"), null, null, null }, 8);
		treeTableStudents.addItem(new Object[] { new CheckBox("Mathe für Anfänger"), null, null, null }, 9);
		treeTableStudents.addItem(new Object[] { new CheckBox("Deutsch für Anfänger"), null, null, null }, 10);
		treeTableStudents.addItem(new Object[] { new CheckBox("Englisch für Anfänger"), null, null, null }, 11);
		treeTableStudents.addItem(new Object[] { new CheckBox("Kochen für Anfänger"), null, null, null }, 12);

		// Build the hierarchy
		treeTableStudents.setParent(5, 1);
		treeTableStudents.setParent(6, 1);
		treeTableStudents.setParent(7, 2);
		treeTableStudents.setParent(8, 2);
		treeTableStudents.setParent(9, 3);
		treeTableStudents.setParent(10, 3);
		treeTableStudents.setParent(11, 4);
		treeTableStudents.setParent(12, 4);
		// The childs (books) may not have additional childs
		for (int i = 5; i <= 12; i++) {
			treeTableStudents.setChildrenAllowed(i, false);
		}
	}
}
