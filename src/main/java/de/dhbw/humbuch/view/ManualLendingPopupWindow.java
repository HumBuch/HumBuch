package de.dhbw.humbuch.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;
import elemental.events.KeyboardEvent.KeyCode;


public class ManualLendingPopupWindow extends Window {

	private static final long serialVersionUID = -6517435259424504689L;

	private static final Logger LOG = LoggerFactory.getLogger(ManualLendingPopupWindow.class);

	private static final String SEARCH_MATERIALS = "Materialien durchsuchen";
	private static final String SAVE = "Materialien ausleihen";
	private static final String TEACHING_MATERIAL_HEADER = "Verfügbare Lehrmittel";
	private static final String BORROW_UNTIL_HEADER = "Ausleihen bis zum";
	private static final String NOTIFICATION_CAPTION_INVALID_DATE = "";
	private static final String NOTIFICATION_DESCR_INVALID_DATE = "Bitte geben Sie ein gültiges Datum ein.";
	private static final String NOTIFICATION_CAPTION_DATE_IN_PAST = "Datum liegt in der Vergangenheit.";
	private static final String NOTIFICATION_DESCR_DATE_IN_PAST = "Bitte geben Sie ein gültiges Datum in der Zukunft ein.";

	private VerticalLayout verticalLayoutContent;
	private HorizontalLayout horizontalLayoutHeaderBar;
	private TextField textFieldSearchBar;
	private Table tableTeachingMaterials;
	private Button buttonSave;
	private IndexedContainer containerTableTeachingMaterials;
	private ArrayList<TeachingMaterial> teachingMaterials;
	private HashMap<Object, HashMap<TeachingMaterial, PopupDateField>> idForMaterialsWithDates;
	private LendingView lendingView;
	private Student selectedStudent;

	public ManualLendingPopupWindow(LendingView lendingView, Student selectedStudent) {
		super("Manuelle Ausleihe für " + selectedStudent.getFirstname() + " " + selectedStudent.getLastname());
		this.lendingView = lendingView;
		this.selectedStudent = selectedStudent;
		init();
		buildLayout();
	}

	private void init() {
		verticalLayoutContent = new VerticalLayout();
		horizontalLayoutHeaderBar = new HorizontalLayout();
		textFieldSearchBar = new TextField(SEARCH_MATERIALS);
		tableTeachingMaterials = new Table();
		buttonSave = new Button(SAVE);
		idForMaterialsWithDates = new HashMap<Object, HashMap<TeachingMaterial, PopupDateField>>();
		containerTableTeachingMaterials = new IndexedContainer();

		textFieldSearchBar.focus();
		buttonSave.setEnabled(false);
		buttonSave.addStyleName("default");
		buttonSave.setClickShortcut(KeyCode.ENTER, null);

		containerTableTeachingMaterials.addContainerProperty(TEACHING_MATERIAL_HEADER, String.class, null);
		containerTableTeachingMaterials.addContainerProperty(BORROW_UNTIL_HEADER, PopupDateField.class, null);

		tableTeachingMaterials.setContainerDataSource(containerTableTeachingMaterials);
		tableTeachingMaterials.setWidth("100%");
		tableTeachingMaterials.setSelectable(true);
		tableTeachingMaterials.setMultiSelect(true);
		tableTeachingMaterials.setImmediate(true);
		setTableListener();
		updateTableContent();

		horizontalLayoutHeaderBar.setSpacing(true);
		verticalLayoutContent.setSpacing(true);
		verticalLayoutContent.setMargin(true);

		center();
		setCloseShortcut(KeyCode.ESC, null);
		setImmediate(true);
		setModal(true);
		setResizable(false);

		addListeners();
	}

	private void buildLayout() {
		horizontalLayoutHeaderBar.addComponent(textFieldSearchBar);
		horizontalLayoutHeaderBar.addComponent(buttonSave);
		horizontalLayoutHeaderBar.setComponentAlignment(buttonSave, Alignment.BOTTOM_CENTER);

		verticalLayoutContent.addComponent(horizontalLayoutHeaderBar);
		verticalLayoutContent.addComponent(tableTeachingMaterials);

		setContent(verticalLayoutContent);
	}

	private void updateTableContent() {
		teachingMaterials = lendingView.getTeachingMaterials();

		if (teachingMaterials == null) {
			return;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 1);

		final Date comingMonth = calendar.getTime();

		ValueChangeListener dateValidator = new ValueChangeListener() {

			private static final long serialVersionUID = 5165858023604029960L;

			@SuppressWarnings("unchecked")
			@Override
			public void valueChange(ValueChangeEvent event) {
				boolean isValid = validateDate((Date) event.getProperty().getValue());
				if (!isValid) {
					event.getProperty().setValue(comingMonth);
				}
			}
		};

		for (TeachingMaterial teachingMaterial : teachingMaterials) {
			PopupDateField dateField = new PopupDateField();
			dateField.setValue(comingMonth);
			dateField.addValueChangeListener(dateValidator);
			dateField.setImmediate(true);

			HashMap<TeachingMaterial, PopupDateField> materialWithDate = new HashMap<TeachingMaterial, PopupDateField>();
			materialWithDate.put(teachingMaterial, dateField);
			Object itemId = tableTeachingMaterials.addItem(new Object[] { teachingMaterial.getName(), dateField }, null);

			idForMaterialsWithDates.put(itemId, materialWithDate);
		}
	}

	private void setTableListener() {
		tableTeachingMaterials.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8774191239600142741L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Object selectedIds = tableTeachingMaterials.getValue();
				if (selectedIds instanceof Set<?>) {
					Set<?> ids = (Set<?>) selectedIds;
					if (ids.size() == 0) {
						buttonSave.setEnabled(false);
						return;
					}

					buttonSave.setEnabled(true);
				}
				else {
					LOG.warn("Table selection is not an instance of Set<?>");
				}
			}
		});
	}

	private void addListeners() {
		buttonSave.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 4375804067002022079L;

			@Override
			public void buttonClick(ClickEvent event) {
				saveTeachingMaterialsForStudent();
			}
		});

		textFieldSearchBar.addTextChangeListener(new TextChangeListener() {

			private static final long serialVersionUID = -6281243106168356850L;

			@Override
			public void textChange(TextChangeEvent event) {
				Filter filter = new SimpleStringFilter(TEACHING_MATERIAL_HEADER, event.getText(), true, false);
				containerTableTeachingMaterials.removeAllContainerFilters();
				containerTableTeachingMaterials.addContainerFilter(filter);
			}
		});
	}

	public void setTeachingMaterials(Collection<TeachingMaterial> teachingMaterials) {
		this.teachingMaterials = new ArrayList<TeachingMaterial>(teachingMaterials);
		updateTableContent();
	}

	@SuppressWarnings("unchecked")
	private void saveTeachingMaterialsForStudent() {
		HashMap<TeachingMaterial, Date> teachingMaterialsWithDates = new HashMap<TeachingMaterial, Date>();

		Object selectedIds = tableTeachingMaterials.getValue();
		if (selectedIds instanceof Set<?>) {
			Set<Object> ids = (Set<Object>) selectedIds;
			if (ids.size() == 0) {
				return;
			}

			for (Object selectedId : ids) {
				HashMap<TeachingMaterial, PopupDateField> tableRow = idForMaterialsWithDates.get(selectedId);
				// this loop runs only once
				for (TeachingMaterial material : tableRow.keySet()) {
					PopupDateField dateField = tableRow.get(material);
					teachingMaterialsWithDates.put(material, dateField.getValue());
				}
			}
		}
		else {
			LOG.warn("Table selection is not an instance of Set<?>");
		}

		HashMap<Student, HashMap<TeachingMaterial, Date>> saveStructure = new HashMap<Student, HashMap<TeachingMaterial, Date>>();
		saveStructure.put(selectedStudent, teachingMaterialsWithDates);

		lendingView.saveTeachingMaterialsForStudents(saveStructure);

		closeMe();
	}

	private void closeMe() {
		UI.getCurrent().removeWindow(this);
		close();
	}

	private boolean validateDate(Date date) {
		if (date == null) {
			Notification.show(NOTIFICATION_CAPTION_INVALID_DATE, NOTIFICATION_DESCR_INVALID_DATE, Type.WARNING_MESSAGE);
			return false;
		}
		else if (date.before(new Date())) {
			Notification.show(NOTIFICATION_CAPTION_DATE_IN_PAST, NOTIFICATION_DESCR_DATE_IN_PAST, Type.WARNING_MESSAGE);
			return false;
		}
		else {
			return true;
		}
	}
}
