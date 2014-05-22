package de.dhbw.humbuch.view;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

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
import de.dhbw.humbuch.util.PDFHandler;
import de.dhbw.humbuch.util.PDFInformationProcessor;
import de.dhbw.humbuch.util.PDFStudentList;
import de.dhbw.humbuch.view.components.ConfirmDialog;
import de.dhbw.humbuch.view.components.PrintingComponent;
import de.dhbw.humbuch.view.components.StudentMaterialSelector;
import de.dhbw.humbuch.view.components.StudentMaterialSelectorObserver;
import de.dhbw.humbuch.viewmodel.ReturnViewModel;
import de.dhbw.humbuch.viewmodel.ReturnViewModel.ReturnListStudent;
import de.dhbw.humbuch.viewmodel.StudentInformationViewModel;
import de.dhbw.humbuch.viewmodel.StudentInformationViewModel.Students;

/**
 * This view displays the Returnscreen. It holds a horizontal headerbar
 * containing actions and a StudentMaterialSelector with all information about
 * the lent books of students. It is used to return the books and create student
 * lists.
 * 
 * @author Henning Muszynski
 * */
public class ReturnView extends VerticalLayout implements View,
		ViewInformation, StudentMaterialSelectorObserver {

	private static final long serialVersionUID = -525078997965992622L;

	private static final String TITLE = "Rückgabe";
	private static final String SAVE_SELECTED_RETURNING = "Material zurückgegeben";
	private static final String MANUAL_RETURN = "Manuelle Rückgabe";
	private static final String MANUAL_RETURN_TITLE = "Manuelle Rückgabe";
	private static final String STUDENT_LIST = "Schülerliste drucken";
	private static final String STUDENT_LIST_PDF = "SchuelerRueckgabeListe.pdf";
	private static final String STUDENT_LIST_WINDOW_TITLE = "Schüler Rückgabe Liste";
	private static final String FILTER_STUDENT = "Schüler filtern";
	private static final String MSG_CONFIRM_RETURN = "Sind alle Listen für die ausgewählten Lehrmaterialien unterschrieben vorhanden?";

	private HorizontalLayout horizontalLayoutHeaderBar;
	private HorizontalLayout horizontalLayoutActions;
	private StudentMaterialSelector studentMaterialSelector;
	private TextField textFieldStudentFilter;
	private Button buttonManualReturn;
	private Button buttonSaveSelectedData;
	private Button buttonStudentList;
	private ReturnViewModel returnViewModel;
	private StudentInformationViewModel studentInformationViewModel;
	private ConfirmDialog.Listener confirmListener;

	@BindState(ReturnListStudent.class)
	private State<Map<Grade, Map<Student, List<BorrowedMaterial>>>> gradeAndStudentsWithMaterials = new BasicState<>(
			Map.class);

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
	 * @param returnViewModel
	 *            the return viewmodel
	 * @param studentInformationViewModel
	 *            the student information viewmodel
	 * */
	@Inject
	public ReturnView(ViewModelComposer viewModelComposer,
			ReturnViewModel returnViewModel,
			StudentInformationViewModel studentInformationViewModel) {
		this.returnViewModel = returnViewModel;
		this.studentInformationViewModel = studentInformationViewModel;
		init();
		buildLayout();
		bindViewModel(viewModelComposer, returnViewModel,
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
		buttonSaveSelectedData = new Button(SAVE_SELECTED_RETURNING);
		buttonStudentList = new Button(STUDENT_LIST);
		buttonManualReturn = new Button(MANUAL_RETURN);
		textFieldStudentFilter = new TextField();

		buttonSaveSelectedData.setEnabled(false);
		buttonStudentList.setEnabled(false);

		textFieldStudentFilter.setInputPrompt(FILTER_STUDENT);
		textFieldStudentFilter.setWidth("50%");
		textFieldStudentFilter.setImmediate(true);

		studentMaterialSelector.registerAsObserver(this);
		studentMaterialSelector.setSizeFull();

		addListeners();
	}

	/*
	 * Builds the layout.
	 */
	private void buildLayout() {
		horizontalLayoutHeaderBar.setWidth("100%");
		horizontalLayoutHeaderBar.setSpacing(true);
		horizontalLayoutActions.setSpacing(true);

		setSizeFull();
		setSpacing(true);
		setMargin(true);

		horizontalLayoutActions.addComponent(buttonSaveSelectedData);
		horizontalLayoutActions.addComponent(buttonManualReturn);
		horizontalLayoutActions.addComponent(buttonStudentList);

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
	 * General listener method. It adds a listener to the confirm dialog as well
	 * as to the state for the students and grade and calls a sub method which
	 * add listeners as well.
	 */
	private void addListeners() {
		confirmListener = new ConfirmDialog.Listener() {

			private static final long serialVersionUID = -2819494096932449586L;

			@Override
			public void onClose(ConfirmDialog dialog) {
				if (dialog.isConfirmed()) {
					HashSet<BorrowedMaterial> materials = studentMaterialSelector
							.getCurrentlySelectedBorrowedMaterials();
					returnTeachingMaterials(materials);
				}
			}
		};

		gradeAndStudentsWithMaterials
				.addStateChangeListener(new StateChangeListener() {

					@Override
					public void stateChange(Object value) {
						if (value == null) {
							return;
						}

						updateReturnList();
					}
				});

		addButtonListeners();
	}

	/*
	 * Adds listeners to the buttons. ClickListener are added to the save,
	 * manual return and student list printing button.
	 */
	private void addButtonListeners() {
		buttonSaveSelectedData.addClickListener(new ClickListener() {

			private static final long serialVersionUID = -9208324317096088956L;

			@Override
			public void buttonClick(ClickEvent event) {
				ConfirmDialog.show(MSG_CONFIRM_RETURN, confirmListener);
			}
		});

		buttonStudentList.addClickListener(new ClickListener() {

			private static final long serialVersionUID = -7743939402341845477L;

			@Override
			public void buttonClick(ClickEvent event) {
				doStudentListPrinting();
			}

		});

		buttonManualReturn.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 6196708024508507923L;

			@Override
			public void buttonClick(ClickEvent event) {
				HashSet<Student> selectedStudents = (HashSet<Student>) studentMaterialSelector
						.getCurrentlySelectedStudents();
				studentInformationViewModel.refresh();
				if (selectedStudents.size() == 0) {
					SelectStudentPopupWindow sspw = new SelectStudentPopupWindow(
							MANUAL_RETURN_TITLE, ReturnView.this, students
									.get());
					getUI().addWindow(sspw);
				} else if (selectedStudents.size() == 1) {
					// This loop runs only once
					for (Student student : selectedStudents) {
						ManualProcessPopupWindow mlpw = new ManualProcessPopupWindow(
								ReturnView.this, student);
						getUI().addWindow(mlpw);
					}
				}
			}
		});

		textFieldStudentFilter.addTextChangeListener(new TextChangeListener() {

			private static final long serialVersionUID = -8656489769177447342L;

			@Override
			public void textChange(TextChangeEvent event) {
				studentMaterialSelector.setFilterString(event.getText());
			}
		});
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
						.returnList(informationForPdf.get(student));
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

	/**
	 * All given materials get returned. They can belong to different students
	 * in different classes.
	 * 
	 * @param materials
	 *            the materials which shall be returned
	 * */
	public void returnTeachingMaterials(Set<BorrowedMaterial> materials) {
		returnViewModel.setBorrowedMaterialsReturned(materials);
	}

	/*
	 * This method is called from the state change listener and is responsible
	 * for updating the StudentMaterialSelector accordingly.
	 */
	private void updateReturnList() {
		returnViewModel.refreshStudents();
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
		returnViewModel.refresh();
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

	/**
	 * Update procedure from the view model in order to get new information
	 * without the need to manually refresh.
	 */
	@Override
	public void update() {
		HashSet<Student> students = studentMaterialSelector
				.getCurrentlySelectedStudents();
		HashSet<BorrowedMaterial> materials = studentMaterialSelector
				.getCurrentlySelectedBorrowedMaterials();

		// Adapt save button
		if (materials.size() >= 1) {
			buttonSaveSelectedData.setEnabled(true);
		} else {
			buttonSaveSelectedData.setEnabled(false);
		}

		// Adapt manual return button
		if (students.size() <= 1) {
			buttonManualReturn.setEnabled(true);
		} else {
			buttonManualReturn.setEnabled(false);
		}

		// Adapt student list button
		if (students.size() >= 1) {
			buttonStudentList.setEnabled(true);
		} else {
			buttonStudentList.setEnabled(false);
		}
	}
}