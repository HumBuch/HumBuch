package de.dhbw.humbuch.view;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.dhbw.humbuch.model.entity.Student;
import elemental.events.KeyboardEvent.KeyCode;

/**
 * Popup window to select a student. Normally appears before a manual process
 * starts and no students has been selected.
 * 
 * @author Henning Muszynski
 * */
public class SelectStudentPopupWindow extends Window {

	private static final long serialVersionUID = 4748807796813638121L;

	private static final Logger LOG = LoggerFactory
			.getLogger(SelectStudentPopupWindow.class);

	private static final String CHOOSE_STUDENT = "Schüler auswählen";
	private static final String CANCEL = "Abbrechen";
	private static final String CONTINUE = "Fortfahren";

	private VerticalLayout verticalLayoutContent;
	private HorizontalLayout horizontalLayoutButtonBar;
	private ComboBox comboBoxStudents;
	private Button buttonContinue;
	private Button buttonCancel;
	private ArrayList<Student> allStudents;
	private LendingView lendingView;
	private ReturnView returnView;

	/**
	 * Constructor taking a LendingView as parameter. When creating the
	 * SelectStudentPopupWindow with this constructor a press on the continue
	 * button is going to trigger the manual lending process.
	 * 
	 * @param title
	 *            the title of the window
	 * @param lendingView
	 *            the lendingView used for the manual lending process
	 * @param allStudents
	 *            Collection of students which can be selected and qualify for
	 *            the process
	 * */
	public SelectStudentPopupWindow(String title, LendingView lendingView,
			Collection<Student> allStudents) {
		super(title);

		this.lendingView = lendingView;
		this.allStudents = new ArrayList<Student>(allStudents);

		init();
		buildLayout();
	}

	/**
	 * Constructor taking a ReturnView as parameter. When creating the
	 * SelectStudentPopupWindow with this constructor a press on the continue
	 * button is going to trigger the manual return process.
	 * 
	 * @param title
	 *            the title of the window
	 * @param lendingView
	 *            the lendingView used for the manual lending process
	 * @param allStudents
	 *            Collection of students which can be selected and qualify for
	 *            the process
	 * */
	public SelectStudentPopupWindow(String title, ReturnView returnView,
			Collection<Student> allStudents) {
		super(title);

		this.returnView = returnView;
		this.allStudents = new ArrayList<Student>(allStudents);

		init();
		buildLayout();
	}

	/*
	 * Initializes and configures all member variables.
	 */
	private void init() {
		verticalLayoutContent = new VerticalLayout();
		horizontalLayoutButtonBar = new HorizontalLayout();
		comboBoxStudents = new ComboBox(CHOOSE_STUDENT);
		buttonContinue = new Button(CONTINUE);
		buttonCancel = new Button(CANCEL);

		buttonContinue.setEnabled(false);
		buttonContinue.addStyleName("default");
		buttonContinue.setClickShortcut(KeyCode.ENTER, null);

		comboBoxStudents.setWidth("100%");
		comboBoxStudents.setImmediate(true);
		comboBoxStudents.focus();

		center();
		setImmediate(true);
		setModal(true);
		setCloseShortcut(KeyCode.ESC, null);
		setResizable(false);

		fillComboBox();
		addListeners();
	}

	private void buildLayout() {
		horizontalLayoutButtonBar.setSpacing(true);
		horizontalLayoutButtonBar.addComponent(buttonCancel);
		horizontalLayoutButtonBar.addComponent(buttonContinue);

		verticalLayoutContent.setSpacing(true);
		verticalLayoutContent.setMargin(true);
		verticalLayoutContent.addComponent(comboBoxStudents);
		verticalLayoutContent.addComponent(horizontalLayoutButtonBar);

		setContent(verticalLayoutContent);
	}

	/*
	 * Add listeners to combo box and buttons.
	 */
	private void addListeners() {
		/*
		 * When a student gets selected in the ComboBox enable the continue
		 * button. If no student is selected the button gets disabled.
		 */
		comboBoxStudents.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = 5865059270341130362L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if (comboBoxStudents.getValue() == null
						|| comboBoxStudents.getValue() == "") {
					buttonContinue.setEnabled(false);
				} else {
					buttonContinue.setEnabled(true);
				}
			}

		});

		/*
		 * When clicking the cancel button the window closes. Nothing else
		 * happens.
		 */
		buttonCancel.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 481430670731285908L;

			@Override
			public void buttonClick(ClickEvent event) {
				closeMe();
			}
		});

		/*
		 * When clicking the continue button a process, determined by the
		 * constructor, is triggered.
		 * 
		 * @see SelectStudentPopupWindow.continueWithProcess
		 */
		buttonContinue.addClickListener(new ClickListener() {

			private static final long serialVersionUID = -6743301861593920408L;

			@Override
			public void buttonClick(ClickEvent event) {
				continueWithProcess();
			}
		});
	}

	/*
	 * Fills the ComboBox with all students. It is using CustomStudents to
	 * display the firstname and lastname of a student and additionally be able
	 * to access the Student object later.
	 */
	private void fillComboBox() {
		comboBoxStudents.removeAllItems();

		for (Student student : allStudents) {
			StudentWrapper customStudentObject = new StudentWrapper(student);
			comboBoxStudents.addItem(customStudentObject);
		}
	}

	/*
	 * This methods decides with which process to continue. When the
	 * SelectStudentPopupWindow was constructed with a LendingView the
	 * ManualLendingPopupWindow is opened. When it was created with a ReturnView
	 * a ManualReturnPopupWindow is opened. After opening the corresponding
	 * window. This window is closed.
	 */
	private void continueWithProcess() {
		StudentWrapper studentWrapper = (StudentWrapper) comboBoxStudents
				.getValue();

		if (studentWrapper != null) {
			ManualProcessPopupWindow mppw = null;
			if (lendingView != null) {
				mppw = new ManualProcessPopupWindow(lendingView,
						studentWrapper.getStudent());

			} else if (returnView != null) {
				mppw = new ManualProcessPopupWindow(returnView,
						studentWrapper.getStudent());
			}

			if (mppw != null) {
				getUI().addWindow(mppw);
				closeMe();
			} else {
				LOG.error("Error occured while trying to start a new process. ManualProcessPopupWindow could not be created.");
			}
		} else {
			LOG.error("No valid student has been selected.");
		}
	}

	/*
	 * Closes this window. Removing it from the UI and calling the Window.close
	 * method
	 */
	private void closeMe() {
		getUI().removeWindow(this);
		close();
	}

	/*
	 * Wrapper class for a Student object. Holding a Student object and
	 * redefining the toString method.
	 */
	private class StudentWrapper {

		private Student student;

		/**
		 * Constructor. Simply sets the Student object hold by Wrapper class
		 * */
		public StudentWrapper(Student student) {
			this.student = student;
		}

		/**
		 * Returns a concatenation of firstname, a space and the lastname of the
		 * wrapped student object
		 * */
		public String toString() {
			return "" + student.getFirstname() + " " + student.getLastname()
					+ " (" + student.getGrade() + ")";
		}

		/**
		 * @return returns the wrapped student object
		 * */
		public Student getStudent() {
			return student;
		}
	}
}