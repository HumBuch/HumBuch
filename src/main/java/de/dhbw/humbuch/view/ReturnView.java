package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.view.components.MultiClassChooser;
import de.dhbw.humbuch.viewmodel.ReturnViewModel;


public class ReturnView extends Panel implements View {

	private static final long serialVersionUID = -525078997965992622L;

	private static final String TITLE = "Rückgabe";
	private static final String BUTTON_MATERIAL = "Materialliste";
	private static final String BUTTON_LENDING = "Ausleihliste";
	private static final String INPUT_PROMPT = "Schüler suchen";
	private static final String CHOOSE_LIST = "Listenauswahl";
	private static final String FIRST_NAME = "Vorname";
	private static final String LAST_NAME = "Nachname";
	private static final String CLASS = "Klasse";
	private static final String OKAY_HEADER = "Daten in Ordnung";
	private static final String OKAY = "Alle Bücher zurückgegeben";

	private VerticalLayout verticalLayoutContent;
	private HorizontalLayout horizontalLayoutPopup;
	private VerticalLayout verticalLayoutPopupFirstColumn;
	private VerticalLayout verticalLayoutPopupSecondColumn;
	private MultiClassChooser classChooser;
	private Button buttonMaterialList;
	private Button buttonLendingList;
	private TextField searchbar;
	private Button buttonMaterialListStudent;
	private PopupView popupView;
//	private Table tableStudents;
	private TreeTable treeTableStudents;

	@Inject
	public ReturnView(ViewModelComposer viewModelComposer, ReturnViewModel returnViewModel) {
		init();
		buildLayout();
		bindViewModel(viewModelComposer, returnViewModel);
	}

	private void init() {
		verticalLayoutContent = new VerticalLayout();
		verticalLayoutContent.setMargin(true);
		verticalLayoutContent.setSpacing(true);

		horizontalLayoutPopup = new HorizontalLayout();
		horizontalLayoutPopup.setWidth("100%");

		verticalLayoutPopupFirstColumn = new VerticalLayout();
		classChooser = new MultiClassChooser();

		buttonLendingList = new Button(BUTTON_LENDING);
		buttonMaterialList = new Button(BUTTON_MATERIAL);

		verticalLayoutPopupSecondColumn = new VerticalLayout();

		searchbar = new TextField("");
		searchbar.setInputPrompt(INPUT_PROMPT);

		buttonMaterialListStudent = new Button(BUTTON_LENDING);

		popupView = new PopupView(CHOOSE_LIST, horizontalLayoutPopup);
		popupView.setHideOnMouseOut(false);

		treeTableStudents = new TreeTable();
		treeTableStudents.setSizeFull();
		treeTableStudents.addContainerProperty(LAST_NAME, CheckBox.class, null);
		treeTableStudents.addContainerProperty(FIRST_NAME, String.class, null);
		treeTableStudents.addContainerProperty(CLASS, String.class, null);
		treeTableStudents.addContainerProperty(OKAY_HEADER, Button.class, null);

		populateWithTestData();

		setSizeFull();
		setCaption(TITLE);
	}

	private void buildLayout() {
		verticalLayoutPopupFirstColumn.addComponent(classChooser);
		verticalLayoutPopupFirstColumn.addComponent(buttonMaterialList);
		verticalLayoutPopupFirstColumn.addComponent(buttonLendingList);

		horizontalLayoutPopup.addComponent(verticalLayoutPopupFirstColumn);

		verticalLayoutPopupSecondColumn.addComponent(searchbar);
		verticalLayoutPopupSecondColumn.addComponent(buttonMaterialListStudent);

		horizontalLayoutPopup.addComponent(verticalLayoutPopupSecondColumn);

		verticalLayoutContent.addComponent(popupView);

		verticalLayoutContent.addComponent(treeTableStudents);

		setContent(verticalLayoutContent);
	}

	private void populateWithTestData() {
		// Create root elements
		treeTableStudents.addItem(new Object[] { new CheckBox("Mustermann"), "Max", "5a", new Button(OKAY) }, 1);
		treeTableStudents.addItem(new Object[] { new CheckBox("Maier"), "Clara", "6b", new Button(OKAY) }, 2);
		treeTableStudents.addItem(new Object[] { new CheckBox("Mustermann"), "Hans", "9c", new Button(OKAY) }, 3);
		treeTableStudents.addItem(new Object[] { new CheckBox("XYZ"), "BLaa", "7a", new Button(OKAY) }, 4);
		
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
