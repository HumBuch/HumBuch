package de.dhbw.humbuch.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.dhbw.humbuch.model.entity.TeachingMaterial;


public class ManualLendingPopupView extends VerticalLayout implements ViewInformation {

	private static final long serialVersionUID = -6517435259424504689L;

	private static final Logger LOG = LoggerFactory.getLogger(ManualLendingPopupView.class);

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
	private ArrayList<TeachingMaterial> teachingMaterials;
	private ArrayList<TeachingMaterial> currentlySelectedMaterials;
	private LendingView lendingView;

	public ManualLendingPopupView(LendingView lendingView) {
		this.lendingView = lendingView;
		init();
		buildLayout();
	}

	private void init() {
		horizontalLayoutButtonBar = new HorizontalLayout();
		textFieldSearchBar = new TextField(SEARCH_MATERIALS);
		tableTeachingMaterials = new Table();
		buttonSave = new Button(SAVE);
		buttonCancel = new Button(CANCEL);
		currentlySelectedMaterials = new ArrayList<TeachingMaterial>();

		buttonSave.setIcon(new ThemeResource("images/icons/16/icon_save_red.png"));

		textFieldSearchBar.setWidth("66%");

		tableTeachingMaterials.addContainerProperty(TEACHING_MATERIAL_HEADER, String.class, null);
		tableTeachingMaterials.setWidth("100%");
		tableTeachingMaterials.setSelectable(true);
		tableTeachingMaterials.setMultiSelect(true);
		tableTeachingMaterials.setImmediate(true);
		setTableListener();
		updateTableContent();

		setSpacing(true);
		setMargin(true);

		addButtonListeners();
	}

	private void buildLayout() {
		horizontalLayoutButtonBar.addComponent(buttonCancel);
		horizontalLayoutButtonBar.addComponent(buttonSave);

		addComponent(textFieldSearchBar);
		addComponent(tableTeachingMaterials);
		addComponent(horizontalLayoutButtonBar);
	}

	private void updateTableContent() {
		if (teachingMaterials == null) {
			return;
		}

		int i = 1;
		for (TeachingMaterial teachingMaterial : teachingMaterials) {
			tableTeachingMaterials.addItem(new Object[] { teachingMaterial.getName() }, i);
			i++;
		}
	}

	private void setTableListener() {
		tableTeachingMaterials.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8774191239600142741L;

			@SuppressWarnings("unchecked")
			@Override
			public void valueChange(ValueChangeEvent event) {
				currentlySelectedMaterials.clear();
				Object selectedIds = tableTeachingMaterials.getValue();
				if (selectedIds instanceof Set<?>) {
					Set<Integer> ids = (Set<Integer>) selectedIds;
					for (Integer id : ids) {
						currentlySelectedMaterials.add(teachingMaterials.get(id - 1));
					}
				}
				else {
					LOG.warn("Table selection is not an instance of Set<?>");
				}
			}
		});
	}

	private void addButtonListeners() {
		buttonCancel.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 3353625484974813579L;

			@Override
			public void buttonClick(ClickEvent event) {
				lendingView.closePopup(false);
			}
		});

		buttonSave.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 4375804067002022079L;

			@Override
			public void buttonClick(ClickEvent event) {
				lendingView.closePopup(true);
			}
		});
	}

	public ArrayList<TeachingMaterial> getCurrentlySelectedTeachingMaterials() {
		return currentlySelectedMaterials;
	}

	public void updateTeachingMaterials(Collection<TeachingMaterial> teachingMaterials) {
		this.teachingMaterials = new ArrayList<TeachingMaterial>(teachingMaterials);
		updateTableContent();
	}

	@Override
	public String getTitle() {
		return TITLE;
	}
}
