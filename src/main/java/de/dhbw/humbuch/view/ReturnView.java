package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.view.components.MultiClassChooser;
import de.dhbw.humbuch.viewmodel.ReturnViewModel;


public class ReturnView extends Panel implements View {

	private static final long serialVersionUID = -525078997965992622L;

	private static final String TITLE = "Rückgabe";
	private static final String BUTTON_MATERIAL = "Materialliste";
	private static final String BUTTON_LENDING = "Ausleihliste";
	private static final String INPUT_PROMPT = "Schüler suchen";
	private static final String CHOOSE_LIST = "Rückgabe Listen drucken";
	private static final String POPULATE = "Listen ins System einpflegen";
	private static final String FIRST_NAME = "Vorname";
	private static final String LAST_NAME = "Nachname";
	private static final String CLASS = "Klasse";
	private static final String OKAY_HEADER = "Zurückgegebene Bücher";
	private static final String OKAY = "Alle Bücher zurückgegeben";
	private static final String SAVE = "Auswahl speichern";

	private Accordion accordionContent;
	private VerticalLayout verticalLayoutPopulate;
	private HorizontalLayout horizontalLayoutLists;
	private VerticalLayout verticalLayoutPopupFirstColumn;
	private VerticalLayout verticalLayoutPopupSecondColumn;
	private MultiClassChooser classChooser;
	private Button buttonMaterialList;
	private Button buttonLendingList;
	private Button buttonSave;
	private TextField searchbar;
	private Button buttonMaterialListStudent;
	private PopupView popupView;
	private TreeTable treeTableStudents;

	@Inject
	public ReturnView(ViewModelComposer viewModelComposer, ReturnViewModel returnViewModel) {
		init();
		buildLayout();
		bindViewModel(viewModelComposer, returnViewModel);
	}

	private void init() {
		accordionContent = new Accordion();
		
		buttonSave = new Button(SAVE);
		buttonSave.setIcon(new ThemeResource("images/icons/16/icon_save_red.png"));
		//buttonSave.setStyleName(BaseTheme.BUTTON_LINK);
		
		verticalLayoutPopulate = new VerticalLayout();
		verticalLayoutPopulate.setMargin(true);
		verticalLayoutPopulate.setSpacing(true);

		horizontalLayoutLists = new HorizontalLayout();
		horizontalLayoutLists.setWidth("100%");

		verticalLayoutPopupFirstColumn = new VerticalLayout();
		classChooser = new MultiClassChooser();

		buttonLendingList = new Button(BUTTON_LENDING);
		buttonMaterialList = new Button(BUTTON_MATERIAL);

		verticalLayoutPopupSecondColumn = new VerticalLayout();

		searchbar = new TextField("");
		searchbar.setInputPrompt(INPUT_PROMPT);

		buttonMaterialListStudent = new Button(BUTTON_LENDING);

		popupView = new PopupView(CHOOSE_LIST, horizontalLayoutLists);
		popupView.setHideOnMouseOut(false);

		treeTableStudents = new TreeTable();
		treeTableStudents.setSizeFull();
		treeTableStudents.setColumnReorderingAllowed(true);
		treeTableStudents.addContainerProperty(LAST_NAME, String.class, null);
		treeTableStudents.addContainerProperty(FIRST_NAME, String.class, null);
		treeTableStudents.addContainerProperty(CLASS, String.class, null);
		treeTableStudents.addContainerProperty(OKAY_HEADER, CheckBox.class, null);

		populateWithTestData();

		setSizeFull();
		setCaption(TITLE);
	}

	private void buildLayout() {
		verticalLayoutPopupFirstColumn.addComponent(classChooser);
		verticalLayoutPopupFirstColumn.addComponent(buttonMaterialList);
		verticalLayoutPopupFirstColumn.addComponent(buttonLendingList);

		horizontalLayoutLists.addComponent(verticalLayoutPopupFirstColumn);

		verticalLayoutPopupSecondColumn.addComponent(searchbar);
		verticalLayoutPopupSecondColumn.addComponent(buttonMaterialListStudent);

		horizontalLayoutLists.addComponent(verticalLayoutPopupSecondColumn);
		
//		verticalLayoutPopulate.addComponent(popupView);
		verticalLayoutPopulate.addComponent(treeTableStudents);
		verticalLayoutPopulate.addComponent(buttonSave);
		verticalLayoutPopulate.setComponentAlignment(buttonSave, Alignment.MIDDLE_RIGHT);

		accordionContent.addTab(horizontalLayoutLists, CHOOSE_LIST);
		accordionContent.addTab(verticalLayoutPopulate, POPULATE);
		accordionContent.setSelectedTab(verticalLayoutPopulate);
		
		setContent(accordionContent);
	}

	private void populateWithTestData() {
		// Create root elements
		treeTableStudents.addItem(new Object[] { "Mustermann", "Max", "5a", new CheckBox(OKAY) }, 1);
		treeTableStudents.addItem(new Object[] { "Maier", "Clara", "6b", new CheckBox(OKAY) }, 2);
		treeTableStudents.addItem(new Object[] { "Mustermann", "Hans", "9c", new CheckBox(OKAY) }, 3);
		treeTableStudents.addItem(new Object[] { "XYZ", "BLaa", "7a", new CheckBox(OKAY) }, 4);
		
		// Create child elements
		treeTableStudents.addItem(new Object[] { null, null, null, new CheckBox("Mathe für Anfänger") }, 5);
		treeTableStudents.addItem(new Object[] { null, null, null, new CheckBox("Deutsch für Anfänger") }, 6);
		treeTableStudents.addItem(new Object[] { null, null, null, new CheckBox("Englisch für Anfänger") }, 7);
		treeTableStudents.addItem(new Object[] { null, null, null, new CheckBox("Kochen für Anfänger") }, 8);
		treeTableStudents.addItem(new Object[] { null, null, null, new CheckBox("Mathe für Anfänger") }, 9);
		treeTableStudents.addItem(new Object[] { null, null, null, new CheckBox("Deutsch für Anfänger") }, 10);
		treeTableStudents.addItem(new Object[] { null, null, null, new CheckBox("Englisch für Anfänger") }, 11);
		treeTableStudents.addItem(new Object[] { null, null, null, new CheckBox("Kochen für Anfänger") }, 12);
		
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
		for(int i = 5; i <= 12; i++) {
			treeTableStudents.setChildrenAllowed(i, false);
		}
	}

	private void bindViewModel(ViewModelComposer viewModelComposer,
			Object... viewModels) {
		try {
			viewModelComposer.bind(this, viewModels);
		}
		catch (IllegalAccessException | NoSuchElementException
				| UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}
}
