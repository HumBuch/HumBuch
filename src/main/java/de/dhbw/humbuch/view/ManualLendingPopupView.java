package de.dhbw.humbuch.view;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;


public class ManualLendingPopupView extends VerticalLayout implements ViewInformation {

	private static final long serialVersionUID = -6517435259424504689L;

	private static final String TITLE = "Manuelle Ausleihe";
	private static final String SEARCH_MATERIALS = "Materialien durchsuchen";
	private static final String SAVE = "Ausgewählte Materialien ausleihen";
	private static final String CANCEL = "Manuelle Ausleihe abbrechen";
	private static final String TEACHING_MATERIAL_HEADER = "Verfügbare Lehrmittel";

	private HorizontalLayout horizontalLayoutButtonBar;
	private TextField textFieldSearchBar;
	private Table tableTeachingMaterials;
	private Button buttonSave;
	private Button buttonCancel;

	public ManualLendingPopupView() {
		init();
		buildLayout();
	}

	private void init() {
		horizontalLayoutButtonBar = new HorizontalLayout();
		textFieldSearchBar = new TextField(SEARCH_MATERIALS);
		tableTeachingMaterials = new Table();
		buttonSave = new Button(SAVE);
		buttonCancel = new Button(CANCEL);

		buttonSave.setIcon(new ThemeResource("images/icons/16/icon_save_red.png"));

		tableTeachingMaterials.addContainerProperty(TEACHING_MATERIAL_HEADER, String.class, null);

		fillTable();
	}

	private void buildLayout() {
		horizontalLayoutButtonBar.addComponent(buttonCancel);
		horizontalLayoutButtonBar.addComponent(buttonSave);

		addComponent(textFieldSearchBar);
		addComponent(tableTeachingMaterials);
		addComponent(horizontalLayoutButtonBar);
	}

	private void fillTable() {
		for (int i = 1; i <= 30; i++) {
			tableTeachingMaterials.addItem(new Object[] { "Material " + i }, i);
		}
	}

	@Override
	public String getTitle() {
		return TITLE;
	}
}
