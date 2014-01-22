package de.dhbw.humbuch.view;

import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.dhbw.humbuch.model.entity.Student;


public class SelectStudentPopupWindow extends Window {

	private static final long serialVersionUID = 4748807796813638121L;

	private static final String TITLE = "Manueller Ausleihvorgang";
	private static final String CHOOSE_STUDENT = "Schüler auswählen";
	private static final String CANCEL = "Manuelle Ausleihe abbrechen";
	private static final String CONTINUE = "Manuelle Ausleihe starten";

	private VerticalLayout verticalLayoutContent;
	private HorizontalLayout horizontalLayoutButtonBar;
	private ComboBox comboBoxStudents;
	private Button buttonContinue;
	private Button buttonCancel;
	private ArrayList<Student> allStudents;
	private LendingView lendingView;

	public SelectStudentPopupWindow(LendingView lendingView, Collection<Student> allStudents) {
		super(TITLE);

		this.lendingView = lendingView;
		this.allStudents = new ArrayList<Student>(allStudents);

		init();
		buildLayout();
	}

	private void init() {
		verticalLayoutContent = new VerticalLayout();
		horizontalLayoutButtonBar = new HorizontalLayout();
		comboBoxStudents = new ComboBox(CHOOSE_STUDENT);
		buttonContinue = new Button(CONTINUE);
		buttonCancel = new Button(CANCEL);

		buttonContinue.setIcon(new ThemeResource("images/icons/16/icon_checked_red.png"));
		buttonContinue.setEnabled(false);

		comboBoxStudents.setWidth("100%");
		comboBoxStudents.setImmediate(true);

		verticalLayoutContent.setSpacing(true);
		verticalLayoutContent.setMargin(true);
		horizontalLayoutButtonBar.setSpacing(true);

		setImmediate(true);
		center();
		setModal(true);
		setResizable(false);

		fillComboBox();
		addListeners();
	}

	private void buildLayout() {
		horizontalLayoutButtonBar.addComponent(buttonCancel);
		horizontalLayoutButtonBar.addComponent(buttonContinue);

		verticalLayoutContent.addComponent(comboBoxStudents);
		verticalLayoutContent.addComponent(horizontalLayoutButtonBar);

		setContent(verticalLayoutContent);
	}

	private void addListeners() {
		comboBoxStudents.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = 5865059270341130362L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (comboBoxStudents.getValue() == null || comboBoxStudents.getValue() == "") {
					buttonContinue.setEnabled(false);
				}
				else {
					buttonContinue.setEnabled(true);
				}
			}

		});

		buttonCancel.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 481430670731285908L;

			@Override
			public void buttonClick(ClickEvent event) {
				closeMe();
			}
		});

		buttonContinue.addClickListener(new ClickListener() {

			private static final long serialVersionUID = -6743301861593920408L;

			@Override
			public void buttonClick(ClickEvent event) {
				showManualLending();
			}
		});
	}

	private void fillComboBox() {
		comboBoxStudents.removeAllItems();

		for (Student student : allStudents) {
			CustomStudent customStudentObject = new CustomStudent(student);
			comboBoxStudents.addItem(customStudentObject);
		}
	}

	private void showManualLending() {
		CustomStudent customStudent = (CustomStudent) comboBoxStudents.getValue();

		if (customStudent != null) {
			ManualLendingPopupWindow mlpw = new ManualLendingPopupWindow(lendingView, customStudent.getStudent());
			getUI().addWindow(mlpw);
			closeMe();
		}
		else {
			// TODO: UI Notification
		}
	}

	private void closeMe() {
		getUI().removeWindow(this);
		close();
	}


	private class CustomStudent {

		private Student student;

		public CustomStudent(Student student) {
			this.student = student;
		}

		public String toString() {
			return "" + student.getFirstname() + " " + student.getLastname();
		}

		public Student getStudent() {
			return student;
		}
	}
}