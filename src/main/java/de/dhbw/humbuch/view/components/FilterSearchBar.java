package de.dhbw.humbuch.view.components;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;


public class FilterSearchBar extends CustomComponent {

	private static final long serialVersionUID = 216578126848395373L;

	private static final String SEARCH = "Suchen";
	private static final String SEARCH_STUDENT = "Schüler suchen";
	private static final String CHOOSE_CLASS = "Nur Schüler aus gewählter Klasse zeigen";
	private static final String PARTIAL = "Nur Schüler mit unvollständigen Daten zeigen";

	private VerticalLayout verticalLayoutContent;
	private HorizontalLayout horizontalLayoutSearchBar;
	private HorizontalLayout horizontalLayoutFilter;
	private TextField textFieldSearchBar;
	private Button buttonSearch;
	private ComboBox comboBoxClass;
	private CheckBox checkBoxPartial;

	public FilterSearchBar() {
		init();
		buildLayout();
	}

	private void init() {
		verticalLayoutContent = new VerticalLayout();
		horizontalLayoutSearchBar = new HorizontalLayout();
		horizontalLayoutFilter = new HorizontalLayout();
		textFieldSearchBar = new TextField(SEARCH_STUDENT);
		buttonSearch = new Button(SEARCH);
		comboBoxClass = new ComboBox(CHOOSE_CLASS);
		checkBoxPartial = new CheckBox(PARTIAL);

		verticalLayoutContent.setWidth("100%");
		verticalLayoutContent.setSpacing(true);
		horizontalLayoutSearchBar.setWidth("100%");
		horizontalLayoutSearchBar.setSpacing(true);
		
		horizontalLayoutFilter.setWidth("100%");
		horizontalLayoutFilter.setSpacing(true);
		horizontalLayoutFilter.setStyleName("filter_background");
		

		textFieldSearchBar.setWidth("100%");
		buttonSearch.setWidth("100%");
		buttonSearch.setIcon(new ThemeResource("images/icons/16/icon_search_red.png"));

		comboBoxClass.setWidth("100%");
		comboBoxClass.setFilteringMode(FilteringMode.CONTAINS);
		populateWithTestData();
	}

	private void buildLayout() {
		horizontalLayoutSearchBar.addComponent(textFieldSearchBar);
		horizontalLayoutSearchBar.addComponent(buttonSearch);
		horizontalLayoutSearchBar.setComponentAlignment(buttonSearch, Alignment.BOTTOM_LEFT);

		horizontalLayoutFilter.addComponent(comboBoxClass);
		horizontalLayoutFilter.addComponent(checkBoxPartial);
		horizontalLayoutFilter.setComponentAlignment(checkBoxPartial, Alignment.BOTTOM_LEFT);

		verticalLayoutContent.addComponent(horizontalLayoutSearchBar);
		verticalLayoutContent.addComponent(horizontalLayoutFilter);

		setCompositionRoot(verticalLayoutContent);
	}

	private void populateWithTestData() {
		comboBoxClass.addItem("Klasse 5a");
		comboBoxClass.addItem("Klasse 5b");
		comboBoxClass.addItem("Klasse 5c");
		comboBoxClass.addItem("Klasse 6a");
		comboBoxClass.addItem("Klasse 6b");
		comboBoxClass.addItem("Klasse 7a");
		comboBoxClass.addItem("Klasse 7b");
		comboBoxClass.addItem("Klasse 8a");
		comboBoxClass.addItem("Klasse 8b");
		comboBoxClass.addItem("Klasse 8c");
	}
}
