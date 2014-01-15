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
import com.vaadin.ui.Embedded;
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
import de.dhbw.humbuch.util.PDFHandler;
import de.dhbw.humbuch.util.PDFStudentList;
import de.dhbw.humbuch.view.components.StudentMaterialSelector;
import de.dhbw.humbuch.viewmodel.ReturnViewModel;
import de.dhbw.humbuch.viewmodel.ReturnViewModel.ReturnListStudent;


public class ReturnView extends VerticalLayout implements View, ViewInformation {

	private static final long serialVersionUID = -525078997965992622L;

	private final static Logger LOG = LoggerFactory.getLogger(ReturnView.class);

	private static final String TITLE = "Rückgabe";
	private static final String SAVE_SELECTED_RETURNING = "Ausgewählte Bücher zurückgegeben";
	private static final String STUDENT_LIST = "Schülerliste für Auswahl drucken";
	private static final String STUDENT_LIST_PDF = "SchuelerRueckgabeListe.pdf";
	private static final String STUDENT_LIST_WINDOW_TITLE = "Schüler Rückgabe Liste";

	private HorizontalLayout horizontalLayoutButtonBar;
	private StudentMaterialSelector studentMaterialSelector;
	private Button buttonSaveSelectedData;
	private Button buttonStudentList;
	private ReturnViewModel returnViewModel;

	@BindState(ReturnListStudent.class)
	private State<Map<Grade, Map<Student, List<BorrowedMaterial>>>> gradeAndStudentsWithMaterials = new BasicState<>(Map.class);

	@Inject
	public ReturnView(ViewModelComposer viewModelComposer, ReturnViewModel returnViewModel) {
		this.returnViewModel = returnViewModel;
		bindViewModel(viewModelComposer, returnViewModel);
		init();
		buildLayout();
	}

	private void init() {
		horizontalLayoutButtonBar = new HorizontalLayout();
		studentMaterialSelector = new StudentMaterialSelector();
		buttonSaveSelectedData = new Button(SAVE_SELECTED_RETURNING);
		buttonStudentList = new Button(STUDENT_LIST);

		buttonSaveSelectedData.setIcon(new ThemeResource("images/icons/16/icon_save_red.png"));
		buttonStudentList.setIcon(new ThemeResource("images/icons/16/icon_print_red.png"));

		horizontalLayoutButtonBar.setSpacing(true);

		setSpacing(true);
		setMargin(true);

		addListeners();
		updateReturnList();
	}

	private void buildLayout() {
		horizontalLayoutButtonBar.addComponent(buttonSaveSelectedData);
		horizontalLayoutButtonBar.addComponent(buttonStudentList);
		horizontalLayoutButtonBar.setComponentAlignment(buttonSaveSelectedData, Alignment.MIDDLE_CENTER);
		horizontalLayoutButtonBar.setComponentAlignment(buttonStudentList, Alignment.MIDDLE_CENTER);

		addComponent(studentMaterialSelector);
		addComponent(horizontalLayoutButtonBar);
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
	}

	private void doStudentListPrinting() {
		Set<Student> selectedStudents = studentMaterialSelector.getCurrentlySelectedStudents();
		if (selectedStudents != null) {
//			ByteArrayOutputStream baos = new PDFStudentList.Builder(selectedStudents).build().createByteArrayOutputStreamForPDF();
//			StreamResource sr = new StreamResource(new PDFHandler.PDFStreamSource(baos), STUDENT_LIST_PDF);
//
//			showPdfInWindow(sr, STUDENT_LIST_WINDOW_TITLE);
		}
		else {
			LOG.warn("No students selected. No list will be generated / shown.");
		}
	}

	private void showPdfInWindow(StreamResource sr, String title) {
		Window window = new Window(title);
		window.setSizeFull();

		Embedded embedded = new Embedded();
		embedded.setSizeFull();
		embedded.setType(Embedded.TYPE_BROWSER);
		// Set the right mime type
		sr.setMIMEType("application/pdf");

		embedded.setSource(sr);
		window.setContent(embedded);
		getUI().addWindow(window);
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
}