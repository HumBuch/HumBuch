package de.dhbw.humbuch.view;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

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
import de.dhbw.humbuch.util.PDFStudentList;
import de.dhbw.humbuch.view.components.PrintingComponent;
import de.dhbw.humbuch.view.components.StudentMaterialSelector;
import de.dhbw.humbuch.viewmodel.LendingViewModel;
import de.dhbw.humbuch.viewmodel.LendingViewModel.MaterialListGrades;
import de.dhbw.humbuch.viewmodel.LendingViewModel.StudentsWithUnreceivedBorrowedMaterials;


public class LendingView extends VerticalLayout implements View, ViewInformation {

	private static final long serialVersionUID = -6400075534193735694L;

	private final static Logger LOG = LoggerFactory.getLogger(LendingView.class);

	private static final String TITLE = "Ausleihe";
	private static final String SAVE_SELECTED_LENDING = "Ausgewählte Bücher erhalten";
	private static final String MANUAL_LENDING = "Manuell Material ausleihen";
	private static final String CLASS_LIST = "Klassenliste für Auswahl drucken";
	private static final String STUDENT_LIST = "Schülerliste für Auswahl drucken";
	private static final String CLASS_LIST_PDF = "KlassenListe.pdf";
	private static final String CLASS_LIST_WINDOW_TITLE = "Klassen Liste";
	private static final String STUDENT_LIST_PDF = "SchuelerAusleihListe.pdf";
	private static final String STUDENT_LIST_WINDOW_TITLE = "Schüler Ausleih Liste";

	private HorizontalLayout horizontalLayoutButtonBar;
	private StudentMaterialSelector studentMaterialSelector;
	private Button buttonSaveSelectedData;
	private Button buttonStudentList;
	private Button buttonClassList;
	private Button buttonManualLending;
	private ThemeResource themeResourceIconPrint;
	private LendingViewModel lendingViewModel;
	private ManualLendingPopupView manualLendingPopupView;
	private Window windowManualLending;

	@BindState(StudentsWithUnreceivedBorrowedMaterials.class)
	private State<Map<Grade, Map<Student, List<BorrowedMaterial>>>> gradeAndStudentsWithMaterials = new BasicState<Map<Grade, Map<Student, List<BorrowedMaterial>>>>(Map.class);

	@BindState(MaterialListGrades.class)
	public State<Map<Grade, Map<TeachingMaterial, Integer>>> materialListGrades = new BasicState<>(Map.class);

	@Inject
	public LendingView(ViewModelComposer viewModelComposer, LendingViewModel lendingViewModel) {
		this.lendingViewModel = lendingViewModel;
		bindViewModel(viewModelComposer, lendingViewModel);
		init();
		buildLayout();
	}

	private void init() {
		horizontalLayoutButtonBar = new HorizontalLayout();
		studentMaterialSelector = new StudentMaterialSelector();
		buttonSaveSelectedData = new Button(SAVE_SELECTED_LENDING);
		buttonClassList = new Button(CLASS_LIST);
		buttonStudentList = new Button(STUDENT_LIST);
		buttonManualLending = new Button(MANUAL_LENDING);
		themeResourceIconPrint = new ThemeResource("images/icons/16/icon_print_red.png");
		manualLendingPopupView = new ManualLendingPopupView();
		windowManualLending = new Window();

		buttonClassList.setIcon(themeResourceIconPrint);
		buttonStudentList.setIcon(themeResourceIconPrint);
		buttonSaveSelectedData.setIcon(new ThemeResource("images/icons/16/icon_save_red.png"));

		studentMaterialSelector.registerAsObserver(this);

		horizontalLayoutButtonBar.setSpacing(true);
		setSpacing(true);
		setMargin(true);

		windowManualLending.setCaption(manualLendingPopupView.getTitle());
		windowManualLending.center();
		windowManualLending.setContent(manualLendingPopupView);

		addListeners();
		updateStudentsWithUnreceivedBorrowedMaterials();
	}

	private void buildLayout() {
		horizontalLayoutButtonBar.addComponent(buttonSaveSelectedData);
		horizontalLayoutButtonBar.addComponent(buttonClassList);
		horizontalLayoutButtonBar.addComponent(buttonStudentList);
		horizontalLayoutButtonBar.setComponentAlignment(buttonSaveSelectedData, Alignment.MIDDLE_CENTER);
		horizontalLayoutButtonBar.setComponentAlignment(buttonClassList, Alignment.MIDDLE_CENTER);
		horizontalLayoutButtonBar.setComponentAlignment(buttonStudentList, Alignment.MIDDLE_CENTER);

		addComponent(studentMaterialSelector);
		addComponent(horizontalLayoutButtonBar);
	}

	private void addListeners() {
		addStateChangeListenersToStates();
		addButtonListeners();
	}

	private void addStateChangeListenersToStates() {
		gradeAndStudentsWithMaterials.addStateChangeListener(new StateChangeListener() {

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

	private void addButtonListeners() {
		buttonClassList.addClickListener(new ClickListener() {

			private static final long serialVersionUID = -5697082042876285467L;

			@Override
			public void buttonClick(ClickEvent event) {
				LendingView.this.lendingViewModel.generateMaterialListGrades(studentMaterialSelector.getCurrentlySelectedGrades());
			}
		});

		buttonStudentList.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 374606757101883863L;

			@Override
			public void buttonClick(ClickEvent event) {
				doStudentListPrinting();
			}

		});

		buttonSaveSelectedData.addClickListener(new ClickListener() {

			private static final long serialVersionUID = -7803362393771729291L;

			@Override
			public void buttonClick(ClickEvent event) {
				LendingView.this.lendingViewModel.setBorrowedMaterialsReceived(studentMaterialSelector.getCurrentlySelectedBorrowedMaterials());
			}
		});

		buttonManualLending.addClickListener(new ClickListener() {

			private static final long serialVersionUID = -526627937959389240L;

			@Override
			public void buttonClick(ClickEvent event) {
				if (buttonManualLending.isVisible()) {
					showManualLendingPopup();
				}
			}
		});
	}

	private void doClassListPrinting() {
		Map<Grade, Map<TeachingMaterial, Integer>> gradesAndTeachingMaterials = materialListGrades.get();
		if (gradesAndTeachingMaterials != null) {
			ByteArrayOutputStream baos = new PDFClassList(gradesAndTeachingMaterials).createByteArrayOutputStreamForPDF();
			StreamResource sr = new StreamResource(new PDFHandler.PDFStreamSource(baos), CLASS_LIST_PDF);

			new PrintingComponent(sr, CLASS_LIST_WINDOW_TITLE);
		}
		else {
			LOG.warn("Grades and Teaching materials are null. No list will be generated / shown.");
		}
	}

	private void doStudentListPrinting() {
//		Set<Student> selectedStudents = studentMaterialSelector.getCurrentlySelectedStudents();
//		if (selectedStudents != null) {
//			System.out.println("map size: " + selectedStudents.size());
//			ByteArrayOutputStream baos = new PDFStudentList.Builder(selectedStudents).build().createByteArrayOutputStreamForPDF();
//			StreamResource sr = new StreamResource(new PDFHandler.PDFStreamSource(baos), STUDENT_LIST_PDF);
//
//			new PrintingComponent(sr, STUDENT_LIST_WINDOW_TITLE);
//		}
//		else {
//			LOG.warn("No students selected. No list will be generated / shown.");
//		}
	}

	private void showManualLendingPopup() {
		getUI().addWindow(windowManualLending);
	}

	public void update(boolean singleStudentSelected) {
		if (singleStudentSelected) {
			horizontalLayoutButtonBar.addComponent(buttonManualLending);
		}
		else {
			horizontalLayoutButtonBar.removeComponent(buttonManualLending);
		}
	}

	private void updateStudentsWithUnreceivedBorrowedMaterials() {
		studentMaterialSelector.setGradesAndStudentsWithMaterials(gradeAndStudentsWithMaterials.get());
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

	@Override
	public String getTitle() {
		return TITLE;
	}
}