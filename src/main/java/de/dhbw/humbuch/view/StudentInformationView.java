package de.dhbw.humbuch.view;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.Action;
import com.vaadin.event.Action.Handler;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.data.Property;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.viewmodel.StudentInformationViewModel;
import de.dhbw.humbuch.viewmodel.StudentInformationViewModel.ImportResult;
import de.dhbw.humbuch.viewmodel.StudentInformationViewModel.Students;

/**
 * Provides a overview over all students and the possibility to import new ones.
 * 
 * @author Johannes
 */
public class StudentInformationView extends VerticalLayout implements View,
		ViewInformation {

	private static final long serialVersionUID = -739081142499192817L;

	/**
	 * Constants
	 */
	private static final String TITLE = "Schülerübersicht";
	private static final String TABLE_FIRSTNAME = "Vorname";
	private static final String TABLE_NAME = "Name";
	private static final String TABLE_GRADE = "Klasse";
	private static final String TABLE_BIRTHDAY = "Geburtsdatum";
	private static final String TABLE_GENDER = "Geschlecht";

	/**
	 * Layout
	 */
	private Upload upload;
	private UploadReceiver receiver = new UploadReceiver(5242880); // =5MB
	private HorizontalLayout head;
	private TextField filter;
	private Table studentsTable;

	protected StudentInformationViewModel studenInformationViewModel;
	@BindState(ImportResult.class)
	private BasicState<String> importResult = new BasicState<String>(
			String.class);

	/**
	 * Layout for Windows
	 */
	private Window showBooksWindow;
	private Button buttonWindowClose;

	@BindState(Students.class)
	public State<Collection<Student>> students = new BasicState<>(
			Collection.class);
	private IndexedContainer tableData;

	/**
	 * constructor
	 * 
	 * @param viewModelComposer
	 * @param importViewModel
	 */
	@Inject
	public StudentInformationView(ViewModelComposer viewModelComposer,
			StudentInformationViewModel importViewModel) {
		this.studenInformationViewModel = importViewModel;
		init();
		buildLayout();
		bindViewModel(viewModelComposer, importViewModel);
	}

	/**
	 * Initializes the components and sets attributes.
	 */
	private void init() {

		head = new HorizontalLayout();
		head.setWidth("100%");
		head.setSpacing(true);
		head.setMargin(true);

		// Filter
		filter = new TextField();
		filter.setImmediate(true);
		filter.setInputPrompt("Filter");
		filter.setTextChangeEventMode(TextChangeEventMode.EAGER);

		head.addComponent(filter);
		head.setExpandRatio(filter, 1);
		head.setComponentAlignment(filter, Alignment.MIDDLE_LEFT);

		// Upload
		upload = new Upload();
		upload.setReceiver(receiver);
		upload.setImmediate(true);
		upload.addSucceededListener(receiver);
		upload.setButtonCaption("Importieren");

		head.addComponent(upload);
		head.setComponentAlignment(upload, Alignment.MIDDLE_RIGHT);

		// Table
		studentsTable = new Table() {
			private static final long serialVersionUID = 1885098955441122118L;

			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				if (colId.equals(TABLE_BIRTHDAY)) {
					SimpleDateFormat df = new SimpleDateFormat();
					df.applyPattern("MM.dd.yyyy");
					if (property.getValue() == null) {
						return null;
					} else {
						return df
								.format(((Date) property.getValue()).getTime());
					}
				}
				return super.formatPropertyValue(rowId, colId, property);
			}
		};
		studentsTable.setSelectable(true);
		studentsTable.setImmediate(true);
		studentsTable.setSizeFull();
		studentsTable.setColumnCollapsingAllowed(true);
		studentsTable.setColumnReorderingAllowed(true);

		tableData = new IndexedContainer();
		tableData.addContainerProperty(TABLE_NAME, String.class, null);
		tableData.addContainerProperty(TABLE_FIRSTNAME, String.class, null);
		tableData.addContainerProperty(TABLE_GRADE, String.class, null);
		tableData.addContainerProperty(TABLE_BIRTHDAY, Date.class, null);
		tableData.addContainerProperty(TABLE_GENDER, String.class, null);

		studentsTable.setContainerDataSource(tableData);

		/**
		 * TODO: Sorting of the table seems not to work studentsTable.sort(new
		 * Object[] { TABLE_NAME }, new boolean[] { true });
		 */

		// show all books window
		showBooksWindow = new Window();
		showBooksWindow.center();

		VerticalLayout windowContent = new VerticalLayout();
		windowContent.setMargin(true);
		windowContent.setSpacing(true);
		buttonWindowClose = new Button("Schließen");
		windowContent.addComponent(buttonWindowClose);
		showBooksWindow.setContent(windowContent);

		this.addListener();
	}

	/**
	 * Adds all listener to their corresponding components
	 */
	private void addListener() {

		/**
		 * Closes the popup window
		 */
		buttonWindowClose.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 4111242410277636186L;

			@Override
			public void buttonClick(ClickEvent event) {
				showBooksWindow.close();
			}
		});

		/**
		 * Implements the right click menu
		 */
		studentsTable.addActionHandler(new Handler() {

			private static final long serialVersionUID = 5717528972959000947L;

			private Action details = new Action("Details anzeigen");

			private Action showBooks = new Action("Entliehene Bücher anzeigen");

			@Override
			public void handleAction(Action action, Object sender, Object target) {
				if (action == details) {
					Notification.show("Not yet implemented");
				} else if (action == showBooks) {
					Item item = ((Table) sender).getItem(target);

					if (item != null) {
						/**
						 * TODO: Provide the corresponding PDF (Hat-Zustand)
						 */
						UI.getCurrent().addWindow(showBooksWindow);
						showBooksWindow.focus();
					}
				}
			}

			@Override
			public Action[] getActions(Object target, Object sender) {
				return new Action[] { details, showBooks };
			}
		});

		/**
		 * Displays the import result
		 */
		importResult.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object arg0) {
				Notification.show(importResult.get());
			}
		});

		/**
		 * Provides the live search of the table by adding a filter after every
		 * keypress in the search field.
		 */
		filter.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = -1684545652234105334L;

			@Override
			public void textChange(TextChangeEvent event) {

				SimpleStringFilter cond1 = new SimpleStringFilter(TABLE_NAME,
						event.getText(), true, false);
				SimpleStringFilter cond2 = new SimpleStringFilter(
						TABLE_FIRSTNAME, event.getText(), true, false);
				Filter filter = new Or(cond1, cond2);
				tableData.removeAllContainerFilters();
				tableData.addContainerFilter(filter);
			}
		});

		students.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object value) {

				tableData.removeAllItems();
				for (Student student : students.get()) {
					studentsTable.addItem(
							new Object[] { student.getLastname(),
									student.getFirstname(),
									student.getGrade().toString(),
									student.getBirthday(), student.getGender() },
							student.getId());
				}
			}
		});

		filter.addShortcutListener(new ShortcutListener("Clear",
				KeyCode.ESCAPE, null) {

			private static final long serialVersionUID = 7058759679522434521L;

			@Override
			public void handleAction(Object sender, Object target) {
				filter.setValue("");
				tableData.removeAllContainerFilters();
			}
		});

	}

	/**
	 * Builds the layout by adding the components to the view
	 */
	private void buildLayout() {
		addComponent(head);
		addComponent(studentsTable);
		setExpandRatio(studentsTable, 1);
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	private void bindViewModel(ViewModelComposer viewModelComposer,
			Object... viewModels) {
		try {
			viewModelComposer.bind(this, viewModels);
		} catch (IllegalAccessException | NoSuchElementException
				| UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getTitle() {
		return TITLE;
	}

	/**
	 * Implementation of a vaadin receiver
	 * 
	 * @author Johannes
	 */
	public class UploadReceiver implements Receiver, Upload.SucceededListener,
			Upload.ProgressListener {

		private static final long serialVersionUID = 1L;
		private ByteArrayOutputStream outputStream;
		private final long maxSize;
		private boolean interrupted;

		/**
		 * @param maxSize
		 *            The maximum file size that will be accepted (in bytes). -1
		 *            in case of no limit. 100Kb = 100000
		 */
		public UploadReceiver(long maxSize) {
			this.maxSize = maxSize;
		}

		public OutputStream receiveUpload(String filename, String MIMEType) {

			this.outputStream = new ByteArrayOutputStream();
			return outputStream;

		}

		public void uploadSucceeded(Upload.SucceededEvent event) {
			if (!interrupted) {
				studenInformationViewModel
						.receiveUploadByteOutputStream(outputStream);
			}
		}

		/**
		 * Interrupts the current upload
		 */
		protected void interrupt() {
			upload.interruptUpload();
			interrupted = true;
			new Notification(
					"Import nicht möglich.",
					"Die ausgewählte Datei ist zu groß. Bitte kontaktieren Sie einen Administrator.",
					Notification.Type.WARNING_MESSAGE).show(Page.getCurrent());
		}

		@Override
		public void updateProgress(long readBytes, long contentLength) {
			if (readBytes > maxSize || contentLength > maxSize) {
				interrupt();
			}

		}
	}
}