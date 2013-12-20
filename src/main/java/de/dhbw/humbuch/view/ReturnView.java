package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.view.components.MultiClassChooser;
import de.dhbw.humbuch.viewmodel.ReturnViewModel;


public class ReturnView extends VerticalLayout implements View {

	private static final long serialVersionUID = -525078997965992622L;

	private static final String BUTTON_MATERIAL = "Materialliste";
	private static final String BUTTON_LENDING = "Ausleihliste";
	private static final String INPUT_PROMPT = "Schüler suchen";
	private static final String CHOOSE_LIST = "Listenauswahl";
	private static final String FIRST_NAME = "Vorname";
	private static final String LAST_NAME = "Nachname";
	private static final String CLASS = "Klasse";
	private static final String OKAY_HEADER = "Daten okay";
	private static final String OKAY = "Alle Bücher zurückgegeben";
	private static final String EDIT = "Bücher unvollständig";
	private static final String EDIT_HEADER = "Daten anpassen";

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
	private Table tableStudents;

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

		tableStudents = new Table();
		tableStudents.setWidth("100%");
		tableStudents.setSelectable(true);
		tableStudents.addContainerProperty(FIRST_NAME, String.class, null);
		tableStudents.addContainerProperty(LAST_NAME, String.class, null);
		tableStudents.addContainerProperty(CLASS, String.class, null);
		tableStudents.addContainerProperty(OKAY_HEADER, Button.class, null);
		tableStudents.addContainerProperty(EDIT_HEADER, Button.class, null);

		populateWithTestData();
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

		verticalLayoutContent.addComponent(tableStudents);

		addComponent(verticalLayoutContent);
	}

	private Button okButton;
	private Button editButton;
	
	private void populateWithTestData() {
		okButton = new Button(OKAY);
		editButton = new Button(EDIT);
		
		tableStudents.addItem(new Object[] { "Max", "Mustermann", "5a", okButton, editButton }, 1);
		tableStudents.addItem(new Object[] { "Clara", "Maier", "8b", okButton, editButton }, 2);
		tableStudents.addItem(new Object[] { "Hans", "Mustermann", "9c", okButton, editButton }, 3);
		tableStudents.addItem(new Object[] { "Cat", "Dog", "7b", okButton, editButton }, 4);
		tableStudents.addItem(new Object[] { "Spongebob", "Schwammkopf", "6a", okButton, editButton }, 5);

		tableStudents.addItem(new Object[] { "Max", "Mustermann", "5a", okButton, editButton }, 6);
		tableStudents.addItem(new Object[] { "Clara", "Maier", "8b", okButton, editButton }, 7);
		tableStudents.addItem(new Object[] { "Hans", "Mustermann", "9c", okButton, editButton }, 8);
		tableStudents.addItem(new Object[] { "Cat", "Dog", "7b", okButton, editButton }, 9);
		tableStudents.addItem(new Object[] { "Spongebob", "Schwammkopf", "6a", okButton, editButton }, 10);
		
//		tableStudents.addItem(new Object[] { "Max", "Mustermann", "5a", new Button(OKAY), new Button(EDIT) }, 1);
//		tableStudents.addItem(new Object[] { "Clara", "Maier", "8b", new Button(OKAY), new Button(EDIT) }, 2);
//		tableStudents.addItem(new Object[] { "Hans", "Mustermann", "9c", new Button(OKAY), new Button(EDIT) }, 3);
//		tableStudents.addItem(new Object[] { "Cat", "Dog", "7b", new Button(OKAY), new Button(EDIT) }, 4);
//		tableStudents.addItem(new Object[] { "Spongebob", "Schwammkopf", "6a", new Button(OKAY), new Button(EDIT) }, 5);
//
//		tableStudents.addItem(new Object[] { "Max", "Mustermann", "5a", new Button(OKAY), new Button(EDIT) }, 6);
//		tableStudents.addItem(new Object[] { "Clara", "Maier", "8b", new Button(OKAY), new Button(EDIT) }, 7);
//		tableStudents.addItem(new Object[] { "Hans", "Mustermann", "9c", new Button(OKAY), new Button(EDIT) }, 8);
//		tableStudents.addItem(new Object[] { "Cat", "Dog", "7b", new Button(OKAY), new Button(EDIT) }, 9);
//		tableStudents.addItem(new Object[] { "Spongebob", "Schwammkopf", "6a", new Button(OKAY), new Button(EDIT) }, 10);
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
