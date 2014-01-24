package de.dhbw.humbuch.view;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import de.dhbw.humbuch.util.PDFStudentList;
import de.dhbw.humbuch.view.components.PrintingComponent;
import de.dhbw.humbuch.view.components.StudentMaterialSelector;
import de.dhbw.humbuch.view.components.StudentMaterialSelectorObserver;
import de.dhbw.humbuch.viewmodel.ReturnViewModel;
import de.dhbw.humbuch.viewmodel.ReturnViewModel.ReturnListStudent;


public class ReturnView extends VerticalLayout implements View, ViewInformation, StudentMaterialSelectorObserver {

	private static final long serialVersionUID = -525078997965992622L;

	private static final String TITLE = "Rückgabe";
	private static final String SAVE_SELECTED_RETURNING = "Bücher zurückgegeben";
	private static final String STUDENT_LIST = "Schülerliste drucken";
	private static final String STUDENT_LIST_PDF = "SchuelerRueckgabeListe.pdf";
	private static final String STUDENT_LIST_WINDOW_TITLE = "Schüler Rückgabe Liste";
	private static final String FILTER_STUDENT = "Schüler filtern";

	private HorizontalLayout horizontalLayoutHeaderBar;
	private HorizontalLayout horizontalLayoutActions;
	private StudentMaterialSelector studentMaterialSelector;
	private TextField textFieldStudentFilter;
	private Button buttonSaveSelectedData;
	private Button buttonStudentList;
	private ReturnViewModel returnViewModel;

	@BindState(ReturnListStudent.class)
	private State<Map<Grade, Map<Student, List<BorrowedMaterial>>>> gradeAndStudentsWithMaterials = new BasicState<>(Map.class);

	@Inject
	public ReturnView(ViewModelComposer viewModelComposer, ReturnViewModel returnViewModel) {
		this.returnViewModel = returnViewModel;
		init();
		buildLayout();
		bindViewModel(viewModelComposer, returnViewModel);
	}

	private void init() {
		horizontalLayoutHeaderBar = new HorizontalLayout();
		horizontalLayoutActions = new HorizontalLayout();
		studentMaterialSelector = new StudentMaterialSelector();
		buttonSaveSelectedData = new Button(SAVE_SELECTED_RETURNING);
		buttonStudentList = new Button(STUDENT_LIST);
		textFieldStudentFilter = new TextField(FILTER_STUDENT);

		buttonSaveSelectedData.setEnabled(false);
		buttonStudentList.setEnabled(false);

		studentMaterialSelector.registerAsObserver(this);
		
		addListeners();
	}

	private void buildLayout() {
		horizontalLayoutHeaderBar.setWidth("100%");
		horizontalLayoutHeaderBar.setSpacing(true);
		horizontalLayoutActions.setSpacing(true);

		setSpacing(true);
		setMargin(true);
		
		horizontalLayoutActions.addComponent(buttonSaveSelectedData);
		horizontalLayoutActions.addComponent(buttonStudentList);
		horizontalLayoutActions.setComponentAlignment(buttonSaveSelectedData, Alignment.BOTTOM_CENTER);
		horizontalLayoutActions.setComponentAlignment(buttonStudentList, Alignment.BOTTOM_CENTER);

		horizontalLayoutHeaderBar.addComponent(textFieldStudentFilter);
		horizontalLayoutHeaderBar.addComponent(horizontalLayoutActions);
		horizontalLayoutHeaderBar.setComponentAlignment(horizontalLayoutActions, Alignment.BOTTOM_RIGHT);
		
		addComponent(horizontalLayoutHeaderBar);
		addComponent(studentMaterialSelector);
		
		setExpandRatio(studentMaterialSelector, 1);
	}

	private void addListeners() {
		gradeAndStudentsWithMaterials.addStateChangeListener(new StateChangeListener() {

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

	private void addButtonListeners() {
		buttonSaveSelectedData.addClickListener(new ClickListener() {

			private static final long serialVersionUID = -9208324317096088956L;

			@Override
			public void buttonClick(ClickEvent event) {
				ReturnView.this.returnViewModel.setBorrowedMaterialsReturned(studentMaterialSelector.getCurrentlySelectedBorrowedMaterials());

			}
		});

		buttonStudentList.addClickListener(new ClickListener() {

			private static final long serialVersionUID = -7743939402341845477L;

			@Override
			public void buttonClick(ClickEvent event) {
				doStudentListPrinting();
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

	private void doStudentListPrinting() {
		Map<Student, List<BorrowedMaterial>> informationForPdf = getPdfInformationFromStundentMaterialSelector();

		if (informationForPdf != null) {
			Set<PDFStudentList.Builder> builders = new LinkedHashSet<PDFStudentList.Builder>();
			for (Student student : informationForPdf.keySet()) {

				PDFStudentList.Builder builder = new PDFStudentList.Builder().returnList(informationForPdf.get(student));
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

	private Map<Student, List<BorrowedMaterial>> getPdfInformationFromStundentMaterialSelector() {
		Set<BorrowedMaterial> allSelectedMaterials = studentMaterialSelector.getCurrentlySelectedBorrowedMaterials();
		Map<Student, List<BorrowedMaterial>> informationForPdf = new HashMap<Student, List<BorrowedMaterial>>();

		for (BorrowedMaterial material : allSelectedMaterials) {
			Student student = material.getStudent();
			List<BorrowedMaterial> materials = new ArrayList<BorrowedMaterial>();

			if (informationForPdf.containsKey(student)) {
				materials = informationForPdf.get(student);
				materials.add(material);
				informationForPdf.put(student, materials);
			}
			else {
				materials.add(material);
				informationForPdf.put(student, materials);
			}
		}

		return informationForPdf;
	}

	private void updateReturnList() {
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

	@Override
	public void update() {
		HashSet<Student> students = studentMaterialSelector.getCurrentlySelectedStudents();
		HashSet<BorrowedMaterial> materials = studentMaterialSelector.getCurrentlySelectedBorrowedMaterials();

		if (materials.size() >= 1) {
			buttonSaveSelectedData.setEnabled(true);
		}
		else {
			buttonSaveSelectedData.setEnabled(false);
		}

		if (students.size() >= 1) {
			buttonStudentList.setEnabled(true);
		}
		else {
			buttonStudentList.setEnabled(false);
		}
	}
}