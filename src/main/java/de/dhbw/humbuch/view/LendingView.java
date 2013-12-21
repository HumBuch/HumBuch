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
import de.dhbw.humbuch.viewmodel.LendingViewModel;

public class LendingView extends VerticalLayout implements View {

	private static final long serialVersionUID = -6400075534193735694L;

	private static final String BUTTON_MATERIAL = "Materialliste";
	private static final String BUTTON_LENDING = "Ausleihliste";
	private static final String INPUT_PROMPT = "Schüler suchen";
	private static final String CHOOSE_LIST = "Listenauswahl";
	private static final String FIRST_NAME = "Vorname";
	private static final String LAST_NAME = "Nachname";
	private static final String CLASS = "Klasse";
	
	private VerticalLayout verticalLayoutContent;
	private HorizontalLayout horizontalLayoutPopup;
	private VerticalLayout verticalLayoutPopupFirstColumn;
	private VerticalLayout verticalLayoutPopupSecondColumn;
	private MultiClassChooser  classChooser;
	private Button buttonMaterialList;
	private Button buttonLendingList;
	private TextField searchbar;
	private Button buttonMaterialListStudent;
	private PopupView popupView;
	private Table tableStudents;
	
	@Inject
	public LendingView(ViewModelComposer viewModelComposer, LendingViewModel lendingViewModel) {
		init();
		buildLayout();
		bindViewModel(viewModelComposer, lendingViewModel);
	}
	
	private void init() {
		verticalLayoutContent = new VerticalLayout();
		verticalLayoutContent.setMargin(true);
		verticalLayoutContent.setSpacing(true);
		
		horizontalLayoutPopup = new HorizontalLayout();
		horizontalLayoutPopup.setWidth("300px");
		
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
		tableStudents.addContainerProperty(LAST_NAME, String.class, null);
		tableStudents.addContainerProperty(FIRST_NAME, String.class, null);
		tableStudents.addContainerProperty(CLASS, String.class, null);
		tableStudents.addContainerProperty("", Button.class, null);
		tableStudents.addContainerProperty("", Button.class, null);
		
		populateWithTestData(tableStudents);
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
	

	private void populateWithTestData(Table tableStudents) {
		Button dataOk = new Button("Ok");
		Button dataInvalid = new Button("Editieren");
		tableStudents.addItem(new Object[] {"5a", "Mustermann", "Max", dataOk, dataInvalid}, 1);
		tableStudents.addItem(new Object[] {"8b", "Maier", "Clara", dataOk, dataInvalid}, 2);
		tableStudents.addItem(new Object[] {"9c", "Mustermann", "Hans", dataOk, dataInvalid}, 3);
		tableStudents.addItem(new Object[] {"7a", "XYZ", "BLaa", dataOk, dataInvalid}, 4);
	}
	
	private void bindViewModel(ViewModelComposer viewModelComposer,
			Object... viewModels) {
		try {
			viewModelComposer.bind(this, viewModels);
		} catch (IllegalAccessException | NoSuchElementException
				| UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

}
