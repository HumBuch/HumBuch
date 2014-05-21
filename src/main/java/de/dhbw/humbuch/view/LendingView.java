package de.dhbw.humbuch.view;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;
import de.dhbw.humbuch.util.PDFClassList;
import de.dhbw.humbuch.util.PDFHandler;
import de.dhbw.humbuch.util.PDFInformationProcessor;
import de.dhbw.humbuch.util.PDFStudentList;
import de.dhbw.humbuch.view.components.ConfirmDialog;
import de.dhbw.humbuch.view.components.PrintingComponent;
import de.dhbw.humbuch.view.components.StudentMaterialSelector;
import de.dhbw.humbuch.view.components.StudentMaterialSelectorObserver;
import de.dhbw.humbuch.viewmodel.LendingViewModel;
import de.dhbw.humbuch.viewmodel.LendingViewModel.MaterialListGrades;
import de.dhbw.humbuch.viewmodel.LendingViewModel.StudentsWithUnreceivedBorrowedMaterials;
import de.dhbw.humbuch.viewmodel.LendingViewModel.TeachingMaterials;
import de.dhbw.humbuch.viewmodel.StudentInformationViewModel;
import de.dhbw.humbuch.viewmodel.StudentInformationViewModel.Students;

/**
 * This view displays the Lendingscreen. It holds a horizontal headerbar
 * containing actions and a StudentMaterialSelector with all information about
 * the lent books of students. It is used to lent books and print student and
 * class lists.
 * 
 * @author Henning Muszynski
 * */
public class LendingView extends VerticalLayout implements View,
		ViewInformation, StudentMaterialSelectorObserver {

	private static final long serialVersionUID = -6400075534193735694L;

	private final static Logger LOG = LoggerFactory
			.getLogger(LendingView.class);

	private static final String TITLE = "Ausleihe";
	private static final String MANUAL_LENDING_TITLE = "Manuelle Ausleihe";
	private static final String SAVE_SELECTED_LENDING = "Material erhalten";
	private static final String MANUAL_LENDING = "Manuelle Ausleihe";
	private static final String MENU_PRINT = "Listen drucken";
	private static final String MENU_ITEM_STUDENT_LIST = "Schüler Liste";
	private static final String MENU_ITEM_CLASS_LIST = "Klassen Liste";
	private static final String CLASS_LIST_PDF = "KlassenListe.pdf";
	private static final String CLASS_LIST_WINDOW_TITLE = "Klassen Liste";
	private static final String STUDENT_LIST_PDF = "SchuelerAusleihListe.pdf";
	private static final String STUDENT_LIST_WINDOW_TITLE = "Schüler Ausleih Liste";
	private static final String FILTER_STUDENT = "Schüler filtern";
	private static final String MSG_CONFIRM_RECEIVE = "Sind alle Listen für die ausgewählten Lehrmaterialien unterschrieben vorhanden?";

	private HorizontalLayout horizontalLayoutHeaderBar;
	private HorizontalLayout horizontalLayoutActions;
	private StudentMaterialSelector studentMaterialSelector;
	private TextField textFieldStudentFilter;
	private Button buttonSaveSelectedData;
	private Button buttonManualLending;
	private MenuBar menuBarPrinting;
	private MenuItem menuItemPrinting;
	private MenuItem subMenuItemClassList;
	private MenuItem subMenuItemStudentList;
	private Command menuCommandClassList;
	private Command menuCommandStudentList;
	private LendingViewModel lendingViewModel;
	private StudentInformationViewModel studentInformationViewModel;
	private ConfirmDialog.Listener confirmListener;

	@BindState(StudentsWithUnreceivedBorrowedMaterials.class)
	private State<Map<Grade, Map<Student, List<BorrowedMaterial>>>> gradeAndStudentsWithMaterials = new BasicState<Map<Grade, Map<Student, List<BorrowedMaterial>>>>(
			Map.class);

	@BindState(MaterialListGrades.class)
	public State<Map<Grade, Map<TeachingMaterial, Integer>>> materialListGrades = new BasicState<>(
			Map.class);

	@BindState(TeachingMaterials.class)
	private State<Collection<TeachingMaterial>> teachingMaterials = new BasicState<>(
			Collection.class);

	@BindState(Students.class)
	public State<Collection<Student>> students = new BasicState<>(
			Collection.class);

	/**
	 * Default constructor gets injected. It initializes all views and builds
	 * the layout. It connects the viewmodel automatically. All parameter get
	 * injected.
	 * 
	 * @param viewModelComposer
	 *            the viewmodel composer
	 * @param lendingViewModel
	 *            the lending viewmodel
	 * @param studentInformationViewModel
	 *            the student information viewmodel
	 * */
	@Inject
	public LendingView(ViewModelComposer viewModelComposer,
			LendingViewModel lendingViewModel,
			StudentInformationViewModel studentInformationViewModel) {
		this.lendingViewModel = lendingViewModel;
		this.studentInformationViewModel = studentInformationViewModel;
		init();
		buildLayout();
		bindViewModel(viewModelComposer, lendingViewModel,
				studentInformationViewModel);
	}

	/*
	 * The init method is responsible for initializing all member variables and
	 * view components. It configures the components and finally builds the
	 * layout.
	 */
	private void init() {
		horizontalLayoutHeaderBar = new HorizontalLayout();
		horizontalLayoutActions = new HorizontalLayout();
		studentMaterialSelector = new StudentMaterialSelector();
		textFieldStudentFilter = new TextField();
		buttonSaveSelectedData = new Button(SAVE_SELECTED_LENDING);
		buttonManualLending = new Button(MANUAL_LENDING);
		menuBarPrinting = new MenuBar();

		buttonSaveSelectedData.setEnabled(false);

		textFieldStudentFilter.setInputPrompt(FILTER_STUDENT);
		textFieldStudentFilter.setWidth("50%");
		textFieldStudentFilter.setImmediate(true);

		defineMenuCommands();

		menuItemPrinting = menuBarPrinting.addItem(MENU_PRINT, null);
		subMenuItemClassList = menuItemPrinting.addItem(MENU_ITEM_CLASS_LIST,
				menuCommandClassList);
		subMenuItemClassList.setEnabled(false);
		subMenuItemStudentList = menuItemPrinting.addItem(
				MENU_ITEM_STUDENT_LIST, menuCommandStudentList);
		subMenuItemStudentList.setEnabled(false);

		studentMaterialSelector.registerAsObserver(this);
		studentMaterialSelector.setSizeFull();

		addListeners();
	}

	/*
	 * Builds the layout.
	 */
	private void buildLayout() {
		horizontalLayoutHeaderBar.setWidth("100%");
		horizontalLayoutActions.setSpacing(true);
		setSpacing(true);
		setMargin(true);
		setSizeFull();

		horizontalLayoutActions.addComponent(buttonSaveSelectedData);
		horizontalLayoutActions.addComponent(buttonManualLending);
		horizontalLayoutActions.addComponent(menuBarPrinting);

		horizontalLayoutHeaderBar.addComponent(textFieldStudentFilter);
		horizontalLayoutHeaderBar.addComponent(horizontalLayoutActions);
		horizontalLayoutHeaderBar.setComponentAlignment(
				horizontalLayoutActions, Alignment.MIDDLE_RIGHT);
		horizontalLayoutHeaderBar.setComponentAlignment(textFieldStudentFilter,
				Alignment.MIDDLE_LEFT);
		horizontalLayoutHeaderBar.setExpandRatio(textFieldStudentFilter, 1);

		addComponent(horizontalLayoutHeaderBar);
		addComponent(studentMaterialSelector);
		setExpandRatio(studentMaterialSelector, 1);
	}

	/*
	 * General listener method. It adds a listener to the confirm dialog and
	 * calls all sub methods which add listeners as well.
	 */
	private void addListeners() {
		confirmListener = new ConfirmDialog.Listener() {

			private static final long serialVersionUID = 3854273511956714408L;

			@Override
			public void onClose(ConfirmDialog dialog) {
				if (dialog.isConfirmed()) {
					setMaterialsReceived();
				}
			}
		};

		addStateChangeListenersToStates();
		addFilterListeners();
		addButtonListeners();
	}

	/*
	 * Defines the menu command. The menubar is styled as button which when
	 * clicked two menu commands appear. They allow to choose between printing a
	 * student or class list.
	 */
	private void defineMenuCommands() {
		menuCommandClassList = new Command() {

			private static final long serialVersionUID = 7304218414715312144L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				LendingView.this.lendingViewModel
						.generateMaterialListGrades(studentMaterialSelector
								.getCurrentlySelectedGrades());
			}
		};

		menuCommandStudentList = new Command() {

			private static final long serialVersionUID = -5544295528932232629L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				doStudentListPrinting();
			}
		};
	}

	/*
	 * Adds the listeners to the states in order to get notified whenever a
	 * state changes. This view is listening to changes of the grade and
	 * students state as well as the material list state.
	 */
	private void addStateChangeListenersToStates() {
		gradeAndStudentsWithMaterials
				.addStateChangeListener(new StateChangeListener() {

					@Override
					public void stateChange(Object value) {
						if (value == null) {
							return;
						}
						updateStudentsWithUnreceivedBorrowedMaterials();
					}
				});

		materialListGrades.addStateChangeListener(new StateChangeListener() {

			@Override
			public void stateChange(Object value) {
				if (value == null) {
					return;
				}
				doClassListPrinting();
			}
		});
	}

	/*
	 * Adds listeners to the buttons. ClickListener are added to the save and
	 * manual lending button.
	 */
	private void addButtonListeners() {
		buttonSaveSelectedData.addClickListener(new ClickListener() {

			private static final long serialVersionUID = -7803362393771729291L;

			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(MSG_CONFIRM_RECEIVE, confirmListener);
			}
		});

		buttonManualLending.addClickListener(new ClickListener() {

			private static final long serialVersionUID = -526627937959389240L;

			@Override
			public void buttonClick(ClickEvent event) {
				doManualLending();
			}
		});
	}

	/*
	 * Adds the listener to the filter above the StudentMaterialSelector. This
	 * allows a live search for the students names.
	 */
	private void addFilterListeners() {
		textFieldStudentFilter.addTextChangeListener(new TextChangeListener() {

			private static final long serialVersionUID = -2524687738109998947L;

			@Override
			public void textChange(TextChangeEvent event) {
				studentMaterialSelector.setFilterString(event.getText());
			}
		});
	}

	/*
	 * Starts the manual lending process. When no student is selected a
	 * SelectStudentPopupWindow is shown otherwise the ManualProcessPopupWindow
	 * is shown. When multiple students are selected nothing happens (this
	 * should never happen since the button get is disabled when multiple
	 * students are selected).
	 */
	private void doManualLending() {
		HashSet<Student> selectedStudents = (HashSet<Student>) studentMaterialSelector
				.getCurrentlySelectedStudents();
		if (selectedStudents.size() == 0) {
			SelectStudentPopupWindow sspw = new SelectStudentPopupWindow(
					MANUAL_LENDING_TITLE, LendingView.this, students.get());
			getUI().addWindow(sspw);
		} else if (selectedStudents.size() == 1) {
			// This loop runs only once
			for (Student student : selectedStudents) {
				ManualProcessPopupWindow mlpw = new ManualProcessPopupWindow(
						LendingView.this, student);
				getUI().addWindow(mlpw);
			}
		}
	}

	/*
	 * This method triggers the pdf creation of a class list. The pdf is created
	 * for the selected class in the StudentMaterialSelector. It is possible to
	 * create multiple pdfs (meaning the pdf having multiple pages) when
	 * multiple classes are selected.
	 */
	private void doClassListPrinting() {
		Map<Grade, Map<TeachingMaterial, Integer>> gradesAndTeachingMaterials = materialListGrades
				.get();
		if (gradesAndTeachingMaterials != null) {
			ByteArrayOutputStream baos = new PDFClassList(
					gradesAndTeachingMaterials)
					.createByteArrayOutputStreamForPDF();
			if (baos != null) {
				String fileNameIncludingHash = "" + new Date().hashCode() + "_"
						+ CLASS_LIST_PDF;
				StreamResource sr = new StreamResource(
						new PDFHandler.PDFStreamSource(baos),
						fileNameIncludingHash);

				new PrintingComponent(sr, CLASS_LIST_WINDOW_TITLE);
			}
		} else {
			LOG.warn("Grades and Teaching materials are null. No list will be generated / shown.");
		}
	}

	/*
	 * This method triggers the pdf creation of a student list. The pdf is
	 * created for the selected students in the StudentMaterialSelector. It is
	 * possible to create multiple pdfs (meaning the pdf having multiple pages)
	 * when multiple students or classes are selected.
	 */
	private void doStudentListPrinting() {

		LinkedHashMap<Student, List<BorrowedMaterial>> informationForPdf = getPdfInformationFromStundentMaterialSelector();

		if (informationForPdf != null) {
			Set<PDFStudentList.Builder> builders = new LinkedHashSet<PDFStudentList.Builder>();
			for (Student student : informationForPdf.keySet()) {

				PDFStudentList.Builder builder = new PDFStudentList.Builder()
						.lendingList(informationForPdf.get(student));
				builders.add(builder);
			}
			ByteArrayOutputStream baos = new PDFStudentList(builders)
					.createByteArrayOutputStreamForPDF();
			if (baos != null) {
				String fileNameIncludingHash = "" + new Date().hashCode() + "_"
						+ STUDENT_LIST_PDF;
				StreamResource sr = new StreamResource(
						new PDFHandler.PDFStreamSource(baos),
						fileNameIncludingHash);

				new PrintingComponent(sr, STUDENT_LIST_WINDOW_TITLE);
			}
		}
	}

	/*
	 * Collects all information needed for the pdf generation from the
	 * StudentMaterialSelector. The information is collected and processed. It
	 * gets sorted and applied to the needed data structure.
	 * 
	 * @return all information needed for the pdf generation from the
	 * StudentMaterialSelector
	 */
	private LinkedHashMap<Student, List<BorrowedMaterial>> getPdfInformationFromStundentMaterialSelector() {
		HashSet<BorrowedMaterial> allSelectedMaterials = studentMaterialSelector
				.getCurrentlySelectedBorrowedMaterials();
		HashSet<Student> allSelectedStudents = studentMaterialSelector
				.getCurrentlySelectedStudents();

		return PDFInformationProcessor.linkStudentsAndMaterials(
				allSelectedMaterials, allSelectedStudents);
	}

	/*
	 * This method is called after the save button is pressed and the appearing
	 * confirm dialog is accepted. It then communicates with the view model in
	 * order to save the selected borrowed materials.
	 */
	private void setMaterialsReceived() {
		LendingView.this.lendingViewModel
				.setBorrowedMaterialsReceived(studentMaterialSelector
						.getCurrentlySelectedBorrowedMaterials());
	}

	/*
	 * This method is called whenever the the students or their material get
	 * updated.
	 */
	private void updateStudentsWithUnreceivedBorrowedMaterials() {
		studentMaterialSelector
				.setGradesAndStudentsWithMaterials(gradeAndStudentsWithMaterials
						.get());
	}

	/*
	 * Binds the view model.
	 */
	private void bindViewModel(ViewModelComposer viewModelComposer,
			Object... viewModels) {
		try {
			viewModelComposer.bind(this, viewModels);
		} catch (IllegalAccessException | NoSuchElementException
				| UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update procedure from the view model in order to get new information
	 * without the need to manually refresh.
	 */
	@Override
	public void update() {
		// Get information about current selection of student material selector
		HashSet<Student> students = studentMaterialSelector
				.getCurrentlySelectedStudents();
		HashSet<BorrowedMaterial> materials = studentMaterialSelector
				.getCurrentlySelectedBorrowedMaterials();
		HashSet<Grade> grades = studentMaterialSelector
				.getCurrentlySelectedGrades();

		// Adapt manual lending button
		if (students.size() <= 1) {
			buttonManualLending.setEnabled(true);
		} else {
			buttonManualLending.setEnabled(false);
		}

		// Adapt student list button
		if (students.size() >= 1) {
			subMenuItemStudentList.setEnabled(true);
		} else {
			subMenuItemStudentList.setEnabled(false);
		}

		// Adapt class list button
		if (grades.size() >= 1) {
			subMenuItemClassList.setEnabled(true);
		} else {
			subMenuItemClassList.setEnabled(false);
		}

		// Adapt save button
		if (materials.size() >= 1) {
			buttonSaveSelectedData.setEnabled(true);
		} else {
			buttonSaveSelectedData.setEnabled(false);
		}
	}

	/**
	 * Returns a list of all teaching materials. This is used to create the list
	 * for the manual lending process for example.
	 * 
	 * @return a list of all teaching materials
	 * */
	public ArrayList<TeachingMaterial> getTeachingMaterials() {
		return new ArrayList<TeachingMaterial>(teachingMaterials.get());
	}

	/**
	 * Saves all Teaching Materials with the specified return date for all
	 * students. This method is used for the manual lending process and is
	 * normally used to update the teaching materials of one student. The passed
	 * structure has to be valid since no further validation is executed.
	 * 
	 * @param saveStructure
	 *            a map containing all students and their teaching materials
	 *            including a return date.
	 * */
	public void saveTeachingMaterialsForStudents(
			HashMap<Student, HashMap<TeachingMaterial, Date>> saveStructure) {
		// the outer loop runs only once
		for (Student student : saveStructure.keySet()) {
			HashMap<TeachingMaterial, Date> materialsWithDates = saveStructure
					.get(student);
			for (TeachingMaterial material : materialsWithDates.keySet()) {
				lendingViewModel.doManualLending(student, material,
						materialsWithDates.get(material));
			}
		}
	}

	/**
	 * This method is alway called when the view is entered (navigated to). It
	 * refreshes the viewmodel and thus updates the StudentMaterialSelector and
	 * other view components.
	 * 
	 * @param event
	 *            the event is not used.
	 * */
	@Override
	public void enter(ViewChangeEvent event) {
		studentInformationViewModel.refresh();
		lendingViewModel.refresh();
	}

	/**
	 * Returns the title of this view.
	 * 
	 * @return the title of this view
	 * */
	@Override
	public String getTitle() {
		return TITLE;
	}
}
