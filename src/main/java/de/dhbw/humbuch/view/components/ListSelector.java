package de.dhbw.humbuch.view.components;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;


public class ListSelector extends CustomComponent {

	private static final long serialVersionUID = 5363626372866373934L;

	public static enum Process {
		LENDING,
		RETURNING
	};
	
	private static final String SEARCH_STUDENT = "Schüler suchen";
	private static final String SEARCH = "Suchen";
	private static final String MATERIAL_LIST = "Materiallisten für ausgewählte Klassen drucken";
	private static final String CLASS_LIST = "Schülerlisten für ausgewählte Klassen drucken";
	private static final String STUDENT_LIST = "Schülerliste drucken";

	private static final String FIRST_NAME = "Vorname";
	private static final String LAST_NAME = "Nachname";
	private static final String CLASS = "Klasse";

	private HorizontalLayout horizontalLayoutContent;
	private VerticalLayout verticalLayoutClass;
	private VerticalLayout verticalLayoutStudent;
	private HorizontalLayout horizontalLayoutSearch;
	private MultiClassChooser multiClassChooser;
	private TextField textFieldSearchBar;
	private Button buttonSearch;
	private Button buttonMaterialList;
	private Button buttonClassList;
	private Button buttonStudentList;
	private Table tableSearchResults;
	private ThemeResource resourceIconPrint;

	public ListSelector(Process process) {
		init();
		buildLayout();
		// TODO: dirty, since you should check process before
		adaptToProcess(process);
	}

	private void init() {
		horizontalLayoutContent = new HorizontalLayout();
		verticalLayoutClass = new VerticalLayout();
		verticalLayoutStudent = new VerticalLayout();
		horizontalLayoutSearch = new HorizontalLayout();
		multiClassChooser = new MultiClassChooser();
		textFieldSearchBar = new TextField(SEARCH_STUDENT);
		buttonSearch = new Button(SEARCH);
		buttonMaterialList = new Button(MATERIAL_LIST);
		buttonClassList = new Button(CLASS_LIST);
		buttonStudentList = new Button(STUDENT_LIST);
		tableSearchResults = new Table();
		resourceIconPrint = new ThemeResource("images/icons/16/icon_print_red.png");

		horizontalLayoutContent.setWidth("100%");
		horizontalLayoutContent.setSpacing(true);
		verticalLayoutClass.setWidth("100%");
		verticalLayoutClass.setSpacing(true);
		verticalLayoutClass.setMargin(true);
		verticalLayoutStudent.setWidth("100%");
		verticalLayoutStudent.setSpacing(true);
		verticalLayoutStudent.setMargin(true);
		horizontalLayoutSearch.setWidth("100%");
		horizontalLayoutSearch.setSpacing(true);

		textFieldSearchBar.setWidth("100%");

		buttonSearch.setIcon(new ThemeResource("images/icons/16/icon_search_red.png"));
		buttonSearch.setWidth("100%");
		buttonMaterialList.setIcon(resourceIconPrint);
		buttonMaterialList.setWidth("100%");
		buttonClassList.setIcon(resourceIconPrint);
		buttonClassList.setWidth("100%");
		buttonStudentList.setIcon(resourceIconPrint);
		buttonStudentList.setWidth("100%");

		tableSearchResults.setWidth("100%");
		tableSearchResults.setPageLength(0);
		tableSearchResults.addContainerProperty(FIRST_NAME, String.class, null);
		tableSearchResults.addContainerProperty(LAST_NAME, String.class, null);
		tableSearchResults.addContainerProperty(CLASS, String.class, null);
	}

	private void buildLayout() {
		verticalLayoutClass.addComponent(multiClassChooser);
		verticalLayoutClass.addComponent(buttonMaterialList);
		verticalLayoutClass.setComponentAlignment(buttonMaterialList, Alignment.BOTTOM_CENTER);
		verticalLayoutClass.addComponent(buttonClassList);
		verticalLayoutClass.setComponentAlignment(buttonClassList, Alignment.BOTTOM_CENTER);

		horizontalLayoutSearch.addComponent(textFieldSearchBar);
		horizontalLayoutSearch.addComponent(buttonSearch);
		horizontalLayoutSearch.setComponentAlignment(buttonSearch, Alignment.BOTTOM_RIGHT);

		verticalLayoutStudent.addComponent(horizontalLayoutSearch);
		verticalLayoutStudent.addComponent(tableSearchResults);
		verticalLayoutStudent.addComponent(buttonStudentList);
		verticalLayoutStudent.setComponentAlignment(buttonStudentList, Alignment.BOTTOM_CENTER);

		horizontalLayoutContent.addComponent(verticalLayoutClass);
		horizontalLayoutContent.addComponent(verticalLayoutStudent);
		
		setCompositionRoot(horizontalLayoutContent);
	}
	
	private void adaptToProcess(Process process) {
		switch(process) {
		case LENDING:
			// nothing to do
			break;
		case RETURNING:
			verticalLayoutClass.removeComponent(buttonMaterialList);
			break;
		}
	}
}
