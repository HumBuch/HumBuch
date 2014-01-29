package de.dhbw.humbuch.view;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

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
import de.dhbw.humbuch.util.PDFStudentList;
import de.dhbw.humbuch.view.components.PrintingComponent;
import de.dhbw.humbuch.view.components.StudentMaterialSelector;
import de.dhbw.humbuch.view.components.StudentMaterialSelectorObserver;
import de.dhbw.humbuch.viewmodel.LendingViewModel;
import de.dhbw.humbuch.viewmodel.LendingViewModel.MaterialListGrades;
import de.dhbw.humbuch.viewmodel.LendingViewModel.StudentsWithUnreceivedBorrowedMaterials;
import de.dhbw.humbuch.viewmodel.LendingViewModel.TeachingMaterials;
import de.dhbw.humbuch.viewmodel.StudentInformationViewModel;
import de.dhbw.humbuch.viewmodel.StudentInformationViewModel.Students;


public class LendingView extends VerticalLayout implements View, ViewInformation, StudentMaterialSelectorObserver {

	private static final long serialVersionUID = -6400075534193735694L;

	private final static Logger LOG = LoggerFactory.getLogger(LendingView.class);

	private static final String TITLE = "Ausleihe";
	private static final String MANUAL_LENDING_TITLE = "Manuelle Ausleihe";
	private static final String SAVE_SELECTED_LENDING = "Material Erhalten";
	private static final String MANUAL_LENDING = "Manuelle Ausleihe";
	private static final String MENU_PRINT = "Listen drucken";
	private static final String MENU_ITEM_STUDENT_LIST = "Schüler Liste";
	private static final String MENU_ITEM_CLASS_LIST = "Klassen Liste";
	private static final String CLASS_LIST_PDF = "KlassenListe.pdf";
	private static final String CLASS_LIST_WINDOW_TITLE = "Klassen Liste";
	private static final String STUDENT_LIST_PDF = "SchuelerAusleihListe.pdf";
	private static final String STUDENT_LIST_WINDOW_TITLE = "Schüler Ausleih Liste";
	private static final String FILTER_STUDENT = "Schüler filtern";

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

	@BindState(StudentsWithUnreceivedBorrowedMaterials.class)
	private State<Map<Grade, Map<Student, List<BorrowedMaterial>>>> gradeAndStudentsWithMaterials = new BasicState<Map<Grade, Map<Student, List<BorrowedMaterial>>>>(Map.class);

	@BindState(MaterialListGrades.class)
	public State<Map<Grade, Map<TeachingMaterial, Integer>>> materialListGrades = new BasicState<>(Map.class);

	@BindState(TeachingMaterials.class)
	private State<Collection<TeachingMaterial>> teachingMaterials = new BasicState<>(Collection.class);

	@BindState(Students.class)
	public State<Collection<Student>> students = new BasicState<>(Collection.class);

	@Inject
	public LendingView(ViewModelComposer viewModelComposer, LendingViewModel lendingViewModel, StudentInformationViewModel studentInformationViewModel) {
		this.lendingViewModel = lendingViewModel;
		init();
		buildLayout();
		bindViewModel(viewModelComposer, lendingViewModel, studentInformationViewModel);
	}

	private void init() {
		horizontalLayoutHeaderBar = new HorizontalLayout();
		horizontalLayoutActions = new HorizontalLayout();
		studentMaterialSelector = new StudentMaterialSelector();
		textFieldStudentFilter = new TextField();
		buttonSaveSelectedData = new Button(SAVE_SELECTED_LENDING);
		buttonManualLending = new Button(MANUAL_LENDING);
		menuBarPrinting = new MenuBar();

		buttonSaveSelectedData.addStyleName("default");
		buttonSaveSelectedData.setEnabled(false);
		
		textFieldStudentFilter.setInputPrompt(FILTER_STUDENT);
		textFieldStudentFilter.setWidth("50%");
		textFieldStudentFilter.setImmediate(true);
		
		defineMenuCommands();

		menuItemPrinting = menuBarPrinting.addItem(MENU_PRINT, null);
		subMenuItemClassList = menuItemPrinting.addItem(MENU_ITEM_CLASS_LIST, menuCommandClassList);
		subMenuItemClassList.setEnabled(false);
		subMenuItemStudentList = menuItemPrinting.addItem(MENU_ITEM_STUDENT_LIST, menuCommandStudentList);
		subMenuItemStudentList.setEnabled(false);

		studentMaterialSelector.registerAsObserver(this);
		studentMaterialSelector.setSizeFull();

		addListeners();
	}

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
		horizontalLayoutHeaderBar.setComponentAlignment(horizontalLayoutActions, Alignment.MIDDLE_RIGHT);
		horizontalLayoutHeaderBar.setComponentAlignment(textFieldStudentFilter, Alignment.MIDDLE_LEFT);
		horizontalLayoutHeaderBar.setExpandRatio(textFieldStudentFilter, 1);

		addComponent(horizontalLayoutHeaderBar);
		addComponent(studentMaterialSelector);
		setExpandRatio(studentMaterialSelector, 1);
	}

	private void addListeners() {
		addStateChangeListenersToStates();
		addFilterListeners();
		addButtonListeners();
	}

	private void defineMenuCommands() {
		menuCommandClassList = new Command() {

			private static final long serialVersionUID = 7304218414715312144L;

			@Override
			public void menuSelected(MenuItem selectedItem) {
				LendingView.this.lendingViewModel.generateMaterialListGrades(studentMaterialSelector.getCurrentlySelectedGrades());
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
				HashSet<Student> selectedStudents = (HashSet<Student>) studentMaterialSelector.getCurrentlySelectedStudents();
				if (selectedStudents.size() == 0) {
					SelectStudentPopupWindow sspw = new SelectStudentPopupWindow(MANUAL_LENDING_TITLE, LendingView.this, students.get());
					getUI().addWindow(sspw);
				}
				else if (selectedStudents.size() == 1) {
					// This loop runs only once
					for (Student student : selectedStudents) {
						ManualProcessPopupWindow mlpw = new ManualProcessPopupWindow(LendingView.this, student);
						getUI().addWindow(mlpw);
					}
				}
			}
		});
	}

	private void addFilterListeners() {
		textFieldStudentFilter.addTextChangeListener(new TextChangeListener() {

			private static final long serialVersionUID = -2524687738109998947L;

			@Override
			public void textChange(TextChangeEvent event) {
				studentMaterialSelector.setFilterString(event.getText());
			}
		});
	}

	private void doClassListPrinting() {
		Map<Grade, Map<TeachingMaterial, Integer>> gradesAndTeachingMaterials = materialListGrades.get();
		if (gradesAndTeachingMaterials != null) {
			ByteArrayOutputStream baos = new PDFClassList(gradesAndTeachingMaterials).createByteArrayOutputStreamForPDF();
			if (baos != null) {
				String fileNameIncludingHash = "" + new Date().hashCode() + "_" + CLASS_LIST_PDF;
				StreamResource sr = new StreamResource(new PDFHandler.PDFStreamSource(baos), fileNameIncludingHash);

				new PrintingComponent(sr, CLASS_LIST_WINDOW_TITLE);
			}
		}
		else {
			LOG.warn("Grades and Teaching materials are null. No list will be generated / shown.");
		}
	}

	private void doStudentListPrinting() {

		LinkedHashMap<Student, List<BorrowedMaterial>> informationForPdf = getPdfInformationFromStundentMaterialSelector();

		if (informationForPdf != null) {
			Set<PDFStudentList.Builder> builders = new LinkedHashSet<PDFStudentList.Builder>();
			for (Student student : informationForPdf.keySet()) {

				PDFStudentList.Builder builder = new PDFStudentList.Builder().lendingList(informationForPdf.get(student));
				builders.add(builder);
			}
			ByteArrayOutputStream baos = new PDFStudentList(builders).createByteArrayOutputStreamForPDF();
			if (baos != null) {
				String fileNameIncludingHash = "" + new Date().hashCode() + "_" + STUDENT_LIST_PDF;
				StreamResource sr = new StreamResource(new PDFHandler.PDFStreamSource(baos), fileNameIncludingHash);

				new PrintingComponent(sr, STUDENT_LIST_WINDOW_TITLE);
			}
		}
	}

	private LinkedHashMap<Student, List<BorrowedMaterial>> getPdfInformationFromStundentMaterialSelector() {
		HashSet<BorrowedMaterial> allSelectedMaterials = studentMaterialSelector.getCurrentlySelectedBorrowedMaterials();
		HashSet<Student> allSelectedStudents = studentMaterialSelector.getCurrentlySelectedStudents();
		LinkedHashMap<Student, List<BorrowedMaterial>> studentsWithMaterials = new LinkedHashMap<Student, List<BorrowedMaterial>>();

		// Sort for grades and students
		TreeMap<Grade, List<Student>> treeToSortForGrades = new TreeMap<Grade, List<Student>>();
		for (Student student : allSelectedStudents) {
			if (treeToSortForGrades.containsKey(student.getGrade())) {
				List<Student> studentsInGrade = treeToSortForGrades.get(student.getGrade());
				if (studentsInGrade.contains(student)) {
					continue;
				}
				studentsInGrade.add(student);
				Collections.sort(studentsInGrade);
				treeToSortForGrades.put(student.getGrade(), studentsInGrade);
			}
			else {
				List<Student> studentList = new ArrayList<Student>();
				studentList.add(student);
				treeToSortForGrades.put(student.getGrade(), studentList);
			}
		}

		// Extract all the informationen needed to create the pdf
		for (Grade grade : treeToSortForGrades.keySet()) {
			List<Student> studentsInGrade = treeToSortForGrades.get(grade);
			for (Student student : studentsInGrade) {
				for (BorrowedMaterial material : allSelectedMaterials) {
					if (student.equals(material.getStudent())) {
						if (studentsWithMaterials.containsKey(student)) {
							List<BorrowedMaterial> currentlyAddedMaterials = studentsWithMaterials.get(student);
							currentlyAddedMaterials.add(material);
							Collections.sort(currentlyAddedMaterials);
							studentsWithMaterials.put(student, currentlyAddedMaterials);
						}
						else {
							List<BorrowedMaterial> materialList = new ArrayList<BorrowedMaterial>();
							materialList.add(material);
							studentsWithMaterials.put(student, materialList);
						}
					}
				}
			}
		}

		return studentsWithMaterials;
	}

	@Override
	public void update() {
		// Get information about current selection of student material selector
		HashSet<Student> students = studentMaterialSelector.getCurrentlySelectedStudents();
		HashSet<BorrowedMaterial> materials = studentMaterialSelector.getCurrentlySelectedBorrowedMaterials();
		HashSet<Grade> grades = studentMaterialSelector.getCurrentlySelectedGrades();

		// Adapt manual lending button
		if (students.size() <= 1) {
			buttonManualLending.setEnabled(true);
		}
		else {
			buttonManualLending.setEnabled(false);
		}

		// Adapt student list button
		if (students.size() >= 1) {
			subMenuItemStudentList.setEnabled(true);
		}
		else {
			subMenuItemStudentList.setEnabled(false);
		}

		// Adapt class list button
		if (grades.size() >= 1) {
			subMenuItemClassList.setEnabled(true);
		}
		else {
			subMenuItemClassList.setEnabled(false);
		}

		// Adapt save button
		if (materials.size() >= 1) {
			buttonSaveSelectedData.setEnabled(true);
		}
		else {
			buttonSaveSelectedData.setEnabled(false);
		}
	}

	private void updateStudentsWithUnreceivedBorrowedMaterials() {
		studentMaterialSelector.setGradesAndStudentsWithMaterials(gradeAndStudentsWithMaterials.get());
	}

	public ArrayList<TeachingMaterial> getTeachingMaterials() {
		return new ArrayList<TeachingMaterial>(teachingMaterials.get());
	}

	public void saveTeachingMaterialsForStudents(HashMap<Student, HashMap<TeachingMaterial, Date>> saveStructure) {
		// the outer loop runs only once
		for (Student student : saveStructure.keySet()) {
			HashMap<TeachingMaterial, Date> materialsWithDates = saveStructure.get(student);
			for (TeachingMaterial material : materialsWithDates.keySet()) {
				lendingViewModel.doManualLending(student, material, materialsWithDates.get(material));
			}
		}
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
		// TODO Q&D: have to be changed after "data has changed"-system is implemented
		lendingViewModel.updateTeachingMaterials();
	}

	@Override
	public String getTitle() {
		return TITLE;
	}
}