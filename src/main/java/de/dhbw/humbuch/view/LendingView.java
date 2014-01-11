package de.dhbw.humbuch.view;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.view.components.StudentMaterialSelector;
import de.dhbw.humbuch.viewmodel.LendingViewModel;
import de.dhbw.humbuch.viewmodel.LendingViewModel.StudentsWithUnreceivedBorrowedMaterials;


public class LendingView extends VerticalLayout implements View, ViewInformation {

	private static final long serialVersionUID = -6400075534193735694L;

	private static final String TITLE = "Ausleihe";
	private static final String SAVE_SELECTED_LENDING = "Ausgewählte Bücher zurückgegeben";
	private static final String CLASS_LIST = "Klassenliste für Auswahl drucken";
	private static final String STUDENT_LIST = "Schülerliste für Auswahl drücken";

	private HorizontalLayout horizontalLayoutButtonBar;
	private StudentMaterialSelector studentMaterialSelector;
	private Button buttonSaveSelectedData;
	private Button buttonStudentList;
	private Button buttonClassList;
	private ThemeResource themeResourceIconPrint;
	private LendingViewModel lendingViewModel;

	@BindState(StudentsWithUnreceivedBorrowedMaterials.class)
	private State<Map<Grade, List<Student>>> gradeAndStudents = new BasicState<Map<Grade, List<Student>>>(Map.class);

	@Inject
	public LendingView(ViewModelComposer viewModelComposer, LendingViewModel lendingViewModel) {
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
		themeResourceIconPrint = new ThemeResource("images/icons/16/icon_print_red.png");

		buttonClassList.setIcon(themeResourceIconPrint);
		buttonStudentList.setIcon(themeResourceIconPrint);
		buttonSaveSelectedData.setIcon(new ThemeResource("images/icons/16/icon_save_red.png"));

		horizontalLayoutButtonBar.setSpacing(true);
		setSpacing(true);
		setMargin(true);

		addListeners();
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
		gradeAndStudents.addStateChangeListener(new StateChangeListener() {

			@Override
			public void stateChange(Object value) {
				if (value == null) {
					return;
				}
				studentMaterialSelector.setStudentsWithUnreceivedBorrowedMaterials(gradeAndStudents.get());
			}
		});
		studentMaterialSelector.setStudentsWithUnreceivedBorrowedMaterials(gradeAndStudents.get());

		buttonClassList.addClickListener(new ClickListener() {

			private static final long serialVersionUID = -5697082042876285467L;

			@Override
			public void buttonClick(ClickEvent event) {
				LendingView.this.lendingViewModel.generateMaterialListGrades(studentMaterialSelector.getCurrentlySelectedGrades());
			}
		});
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
