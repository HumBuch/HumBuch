package de.dhbw.humbuch.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;
import elemental.events.KeyboardEvent.KeyCode;

/**
 * Class representing the popup window of a manual lending or return process.
 * The constructor defines which process is displayed.
 * 
 * @author Henning Muszynski
 * */
public class ManualProcessPopupWindow extends Window {

	private static final long serialVersionUID = -6517435259424504689L;

	private static final Logger LOG = LoggerFactory
			.getLogger(ManualProcessPopupWindow.class);

	private static final String SEARCH_MATERIALS = "Materialien durchsuchen";
	private static final String SAVE_LEND = "Ausleihen";
	private static final String SAVE_RETURN = "Zurückgeben";
	private static final String TEACHING_MATERIAL_HEADER = "Lehrmittel";
	private static final String BORROW_UNTIL_HEADER = "Ausleihen bis zum";
	private static final String MULTI_CHOICE_EXPLANATION = "Mehrfachauswahl mittels Strg. oder Shift möglich";
	private static final String NOTIFICATION_CAPTION_INVALID_DATE = "Ungültiges Datum";
	private static final String NOTIFICATION_DESCR_INVALID_DATE = "Bitte geben Sie ein gültiges Datum ein.";
	private static final String NOTIFICATION_CAPTION_DATE_IN_PAST = "Datum liegt in der Vergangenheit";
	private static final String NOTIFICATION_DESCR_DATE_IN_PAST = "Bitte geben Sie ein gültiges Datum in der Zukunft ein.";

	private VerticalLayout verticalLayoutContent;
	private HorizontalLayout horizontalLayoutHeaderBar;
	private TextField textFieldSearchBar;
	private Table tableTeachingMaterials;
	private Button buttonSave;
	private Label labelMultiChoiceExplanation;
	private IndexedContainer containerTableTeachingMaterials;
	private ArrayList<TeachingMaterial> teachingMaterials;
	private HashMap<Object, HashMap<TeachingMaterial, PopupDateField>> idForMaterialsWithDates;
	private HashMap<Object, BorrowedMaterial> idForMaterials;
	private LendingView lendingView;
	private ReturnView returnView;
	private Student selectedStudent;

	/**
	 * Constructor taking a LendingView as parameter. When using this
	 * constructor a click on the save button triggers the manual lending
	 * process.
	 * 
	 * @param lendingView
	 *            lending view used for triggering the manual process
	 * @param selectedStudent
	 *            student for whom the process is triggered
	 * */
	public ManualProcessPopupWindow(LendingView lendingView,
			Student selectedStudent) {
		super("Manuelle Ausleihe für " + selectedStudent.getFirstname() + " "
				+ selectedStudent.getLastname() + " ("
				+ selectedStudent.getGrade() + ")");

		this.lendingView = lendingView;
		this.selectedStudent = selectedStudent;

		init();
		buildLayout();
	}

	/**
	 * Constructor taking a ReturnView as parameter. When using this constructor
	 * a click on the save button triggers the manual return process.
	 * 
	 * @param lendingView
	 *            lending view used for triggering the manual process
	 * @param selectedStudent
	 *            student for whom the process is triggered
	 * */
	public ManualProcessPopupWindow(ReturnView returnView,
			Student selectedStudent) {
		super("Manuelle Rückgabe für " + selectedStudent.getFirstname() + " "
				+ selectedStudent.getLastname() + " ("
				+ selectedStudent.getGrade() + ")");

		this.returnView = returnView;
		this.selectedStudent = selectedStudent;

		init();
		buildLayout();
	}

	/*
	 * Initializes all member variables and configures them.
	 */
	private void init() {
		verticalLayoutContent = new VerticalLayout();
		horizontalLayoutHeaderBar = new HorizontalLayout();
		textFieldSearchBar = new TextField(SEARCH_MATERIALS);
		tableTeachingMaterials = new Table();
		idForMaterialsWithDates = new HashMap<Object, HashMap<TeachingMaterial, PopupDateField>>();
		idForMaterials = new HashMap<Object, BorrowedMaterial>();
		containerTableTeachingMaterials = new IndexedContainer();
		labelMultiChoiceExplanation = new Label(MULTI_CHOICE_EXPLANATION);

		labelMultiChoiceExplanation.addStyleName("caption");
		textFieldSearchBar.focus();
		adaptButton();

		containerTableTeachingMaterials.addContainerProperty(
				TEACHING_MATERIAL_HEADER, String.class, null);
		if (lendingView != null) {
			containerTableTeachingMaterials.addContainerProperty(
					BORROW_UNTIL_HEADER, PopupDateField.class, null);
		}

		tableTeachingMaterials
				.setContainerDataSource(containerTableTeachingMaterials);
		tableTeachingMaterials.setWidth("100%");
		tableTeachingMaterials.setSelectable(true);
		tableTeachingMaterials.setMultiSelect(true);
		tableTeachingMaterials.setImmediate(true);
		setTableListener();
		updateTableContent();

		horizontalLayoutHeaderBar.setSpacing(true);
		verticalLayoutContent.setSpacing(true);
		verticalLayoutContent.setMargin(true);

		setWidth("50%");
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
		horizontalLayoutHeaderBar.setComponentAlignment(buttonSave,
				Alignment.BOTTOM_CENTER);

		verticalLayoutContent.addComponent(horizontalLayoutHeaderBar);
		verticalLayoutContent.addComponent(tableTeachingMaterials);
		verticalLayoutContent.addComponent(labelMultiChoiceExplanation);

		setContent(verticalLayoutContent);
	}

	/*
	 * Adapts the button label to the given process (return / lend)
	 */
	private void adaptButton() {
		if (lendingView != null) {
			buttonSave = new Button(SAVE_LEND);
		} else {
			buttonSave = new Button(SAVE_RETURN);
		}

		buttonSave.setEnabled(false);
		buttonSave.addStyleName("default");
		buttonSave.setClickShortcut(KeyCode.ENTER, null);
	}

	/*
	 * Updates the content of the table depending on the process.
	 */
	private void updateTableContent() {
		if (lendingView != null) {
			updateTableManualLending();
		} else if (returnView != null) {
			updateTableManualReturn();
		}
	}

	/*
	 * Updates the table for manual lending. Adding all teaching materials
	 * provided from the LendingView and Datefields. When adding the date fields
	 * a value change listener for validation is added. The default date in the
	 * table is one month in the future.
	 */
	private void updateTableManualLending() {
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
				boolean isValid = validateDate((Date) event.getProperty()
						.getValue());
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
			Object itemId = tableTeachingMaterials.addItem(new Object[] {
					teachingMaterial.getName(), dateField }, null);

			idForMaterialsWithDates.put(itemId, materialWithDate);
		}
	}

	/*
	 * Updates the table for manual return. It adds just the borrowed materials
	 * for the student in the table.
	 */
	private void updateTableManualReturn() {
		List<BorrowedMaterial> materials = selectedStudent
				.getUnreturnedBorrowedMaterials();
		for (BorrowedMaterial material : materials) {
			Object itemId = tableTeachingMaterials.addItem(
					new Object[] { material.getTeachingMaterial().getName() },
					null);
			idForMaterials.put(itemId, material);
		}
	}

	/*
	 * When an item in the table gets selected the save button is enabled. When
	 * no item is selected the save button is disabled.
	 */
	private void setTableListener() {
		tableTeachingMaterials
				.addValueChangeListener(new ValueChangeListener() {

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
						} else {
							LOG.warn("Table selection is not an instance of Set<?>");
						}
					}
				});
	}

	/*
	 * Adds a listener to the save button and one responsible for filtering the
	 * table.
	 */
	private void addListeners() {
		/*
		 * Decides whether the manual lending or the manual return is saved.
		 */
		buttonSave.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 4375804067002022079L;

			@Override
			public void buttonClick(ClickEvent event) {
				if (lendingView != null) {
					finishManualLendingProcess();
				} else if (returnView != null) {
					finishManualReturnProcess();
				} else {
					LOG.warn("Could not determine which process should be finished. Both views are null.");
				}
				closeMe();
			}
		});

		/*
		 * Puts a string filter on the table content. The filter is
		 * case-insensitive and matches anywhere in the teaching material (not
		 * only prefix matching).
		 */
		textFieldSearchBar.addTextChangeListener(new TextChangeListener() {

			private static final long serialVersionUID = -6281243106168356850L;

			@Override
			public void textChange(TextChangeEvent event) {
				Filter filter = new SimpleStringFilter(
						TEACHING_MATERIAL_HEADER, event.getText(), true, false);
				containerTableTeachingMaterials.removeAllContainerFilters();
				containerTableTeachingMaterials.addContainerFilter(filter);
				tableTeachingMaterials.setValue(null);
			}
		});
	}

	/*
	 * Save all selected materials for the student object using
	 * LendingView.saveTeachingMaterialsForStudents. Afterwards the window is
	 * closed.
	 */
	@SuppressWarnings("unchecked")
	private void finishManualLendingProcess() {

		HashMap<TeachingMaterial, Date> teachingMaterialsWithDates = new HashMap<TeachingMaterial, Date>();

		Object selectedIds = tableTeachingMaterials.getValue();
		if (selectedIds instanceof Set<?>) {
			Set<Object> ids = (Set<Object>) selectedIds;
			if (ids.size() == 0) {
				return;
			}

			for (Object selectedId : ids) {
				HashMap<TeachingMaterial, PopupDateField> tableRow = idForMaterialsWithDates
						.get(selectedId);
				// this loop runs only once
				for (TeachingMaterial material : tableRow.keySet()) {
					PopupDateField dateField = tableRow.get(material);
					teachingMaterialsWithDates.put(material,
							dateField.getValue());
				}
			}
		} else {
			LOG.warn("Table selection is not an instance of Set<?>");
		}

		HashMap<Student, HashMap<TeachingMaterial, Date>> saveStructure = new HashMap<Student, HashMap<TeachingMaterial, Date>>();
		saveStructure.put(selectedStudent, teachingMaterialsWithDates);

		lendingView.saveTeachingMaterialsForStudents(saveStructure);

		closeMe();
	}

	/*
	 * Return all selected materials for the student object using
	 * ReturnView.returnTeachingMaterialsForStudents. Afterwards the window is
	 * closed.
	 */
	@SuppressWarnings("unchecked")
	private void finishManualReturnProcess() {
		Set<BorrowedMaterial> selectedMaterials = new HashSet<BorrowedMaterial>();

		Object selectedIds = tableTeachingMaterials.getValue();
		if (selectedIds instanceof Set<?>) {
			Set<Object> ids = (Set<Object>) selectedIds;
			if (ids.size() == 0) {
				return;
			}

			for (Object selectedId : ids) {
				selectedMaterials.add(idForMaterials.get(selectedId));
			}
		} else {
			LOG.warn("Table selection is not an instance of Set<?>");
		}

		returnView.returnTeachingMaterials(selectedMaterials);
	}

	/*
	 * Method for validating a date object.
	 * 
	 * @return returns false when passing null or a date in the past returns
	 * true otherwise
	 */
	private boolean validateDate(Date date) {

		if (date == null) {
			Notification.show(NOTIFICATION_CAPTION_INVALID_DATE,
					NOTIFICATION_DESCR_INVALID_DATE, Type.WARNING_MESSAGE);
			return false;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		if (isToday(calendar)) {
			return true;
		} else if (date.before(new Date())) {
			Notification.show(NOTIFICATION_CAPTION_DATE_IN_PAST,
					NOTIFICATION_DESCR_DATE_IN_PAST, Type.WARNING_MESSAGE);
			return false;
		} else {
			return true;
		}
	}

	/*
	 * Checks whether a given calendar object represents today or not.
	 * 
	 * @return true when then is today
	 */
	private boolean isToday(Calendar then) {
		Calendar when = Calendar.getInstance();
		when.setTime(new Date());

		if (when.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR)) {
			return true;
		}

		return false;
	}

	/*
	 * Closes this window. Removing it from the UI and calling the Window.close
	 * method
	 */
	private void closeMe() {
		UI.getCurrent().removeWindow(this);
		close();
	}
}
