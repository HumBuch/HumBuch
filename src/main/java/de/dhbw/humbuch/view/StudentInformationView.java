package de.dhbw.humbuch.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.data.Property;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.event.MessageEvent;
import de.dhbw.humbuch.event.MessageEvent.Type;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.util.PDFHandler;
import de.dhbw.humbuch.util.PDFStudentList;
import de.dhbw.humbuch.view.components.ConfirmDialog;
import de.dhbw.humbuch.view.components.PrintingComponent;
import de.dhbw.humbuch.viewmodel.StudentInformationViewModel;
import de.dhbw.humbuch.viewmodel.StudentInformationViewModel.Students;

/**
 * Provides an overview over all students and the possibility to import new
 * ones.
 * 
 * @author Johannes
 */
@SuppressWarnings("serial")
public class StudentInformationView extends VerticalLayout implements View,
		ViewInformation {
	private static final long serialVersionUID = -739081142499192817L;

	private final static Logger LOG = LoggerFactory
			.getLogger(StudentInformationView.class);

	private static final String TITLE = "Schülerübersicht";
	private static final String TABLE_FIRSTNAME = "firstname";
	private static final String TABLE_LASTNAME = "lastname";
	private static final String TABLE_GRADE = "grade";
	private static final String TABLE_BIRTHDAY = "birthday";
	private static final String TABLE_GENDER = "gender";

	private Upload upload;
	private UploadReceiver receiver = new UploadReceiver(5242880); // =5MB
	private HorizontalLayout head;
	private TextField filter;
	private Table studentsTable;
	private Button showMaterials;

	protected StudentInformationViewModel studentInformationViewModel;

	@BindState(Students.class)
	public State<Collection<Student>> students = new BasicState<>(
			Collection.class);
	private BeanItemContainer<Student> tableData;
	private EventBus eventBus;

	/**
	 * constructor
	 * 
	 * @param viewModelComposer
	 * @param importViewModel
	 */
	@Inject
	public StudentInformationView(ViewModelComposer viewModelComposer,
			StudentInformationViewModel importViewModel, EventBus eventBus) {
		this.studentInformationViewModel = importViewModel;
		this.eventBus = eventBus;
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

		// Filter
		filter = new TextField();
		filter.setImmediate(true);
		filter.setInputPrompt("Name, Vorname oder Klasse...");
		filter.setWidth("50%");
		filter.setTextChangeEventMode(TextChangeEventMode.EAGER);

		head.addComponent(filter);
		head.setExpandRatio(filter, 1);
		head.setComponentAlignment(filter, Alignment.MIDDLE_LEFT);

		HorizontalLayout buttons = new HorizontalLayout();

		showMaterials = new Button("Materialien anzeigen");
		showMaterials.setEnabled(false);
		buttons.addComponent(showMaterials);
		buttons.setSpacing(true);

		// Import button
		upload = new Upload();
		upload.setReceiver(receiver);
		upload.setImmediate(true);
		upload.addSucceededListener(receiver);
		upload.setButtonCaption("Import");
		buttons.addComponent(upload);

		head.addComponent(buttons);
		head.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);

		// Table
		studentsTable = new Table() {
			private static final long serialVersionUID = 1885098955441122118L;

			final SimpleDateFormat df = new SimpleDateFormat();
			
			{
				df.applyPattern("dd.MM.yyyy");
			}

			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				if (colId.equals(TABLE_BIRTHDAY)) {
					if (property.getValue() == null) {
						return null;
					} else {
						return df.format(((Date) property.getValue()).getTime());
					}
				}
				return super.formatPropertyValue(rowId, colId, property);
			}
		};
		studentsTable.setSelectable(true);
		studentsTable.setImmediate(true);
		studentsTable.setSizeFull();
		studentsTable.setColumnCollapsingAllowed(true);

		tableData = new BeanItemContainer<Student>(Student.class);
		studentsTable.setContainerDataSource(tableData);

		studentsTable.setVisibleColumns(new Object[] { "lastname", "firstname",
				"grade", "birthday", "gender" });
		studentsTable.setColumnHeader(TABLE_LASTNAME, "Name");
		studentsTable.setColumnHeader(TABLE_FIRSTNAME, "Vorname");
		studentsTable.setColumnHeader(TABLE_GRADE, "Klasse");
		studentsTable.setColumnHeader(TABLE_BIRTHDAY, "Geburtstag");
		studentsTable.setColumnHeader(TABLE_GENDER, "Geschlecht");

		this.addListener();
	}

	/**
	 * Adds all listener to their corresponding components
	 */
	private void addListener() {

		/**
		 * Implements the button click event to show all borrowed materials of a
		 * student
		 */
		showMaterials.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1947881830091475265L;

			@Override
			public void buttonClick(ClickEvent event) {
				doStudentListPrinting();
			}
		});

		/**
		 * Enables/disables the show materials button
		 */
		studentsTable
				.addValueChangeListener(new Property.ValueChangeListener() {
					private static final long serialVersionUID = 1L;

					@Override
					public void valueChange(ValueChangeEvent event) {
						Student item = (Student) studentsTable.getValue();
						showMaterials.setEnabled(item != null);
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

				SimpleStringFilter cond1 = new SimpleStringFilter(
						TABLE_LASTNAME, event.getText(), true, false);
				SimpleStringFilter cond2 = new SimpleStringFilter(
						TABLE_FIRSTNAME, event.getText(), true, false);
				SimpleStringFilter cond3 = new SimpleStringFilter(TABLE_GRADE,
						event.getText(), true, false);
				Filter filter = new Or(cond1, cond2, cond3);
				tableData.removeAllContainerFilters();
				tableData.addContainerFilter(filter);
			}
		});

		/**
		 * Listens for changes in all students collection an adds them to the
		 * container
		 */
		students.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object value) {
				tableData.removeAllItems();
				for (Student student : students.get()) {
					studentsTable.addItem(student);
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
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		addComponent(head);
		addComponent(studentsTable);
		setExpandRatio(studentsTable, 1);
	}

	/**
	 * Creates a PDF from the currently selected student in the table. The
	 * generated PDF is displayed in a new Window.
	 */
	private void doStudentListPrinting() {

		Student item = (Student) studentsTable.getValue();

		if (item != null) {

			List<BorrowedMaterial> borrowedMaterials = new ArrayList<BorrowedMaterial>();
			;

			for (BorrowedMaterial bm : item.getBorrowedMaterials()) {
				if (bm.isReceived() && bm.getReturnDate() == null) {
					borrowedMaterials.add(bm);
				}
			}

			if (!borrowedMaterials.isEmpty()) {
				PDFStudentList.Builder builder = new PDFStudentList.Builder()
						.borrowedMaterialList(borrowedMaterials);
				ByteArrayOutputStream boas = new PDFStudentList(builder)
						.createByteArrayOutputStreamForPDF();
				StreamResource sr = new StreamResource(
						new PDFHandler.PDFStreamSource(boas), "Infoliste_"
								+ item.getLastname() + "_"
								+ item.getFirstname() + ".pdf");
				new PrintingComponent(sr, "Ausgeliehene Materialien von "
						+ item.getLastname() + ", " + item.getFirstname());
			} else {
				eventBus.post(new MessageEvent("Der Schüler '"
						+ item.getLastname() + ", " + item.getFirstname()
						+ "' hat keine Materialien ausgeliehen."));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		/*
		 * BUGFIX: Set the value of the table to null so the user has to update
		 * the selection if he/she wants to edit the same item again.
		 */
		studentsTable.setValue(null);
		studentInformationViewModel.refresh();
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		return TITLE;
	}

	/**
	 * Creates a Window with the question whether the import is a full import or
	 * a delta import.
	 */
	public void selectImportTypeDialog(final ByteArrayOutputStream outputStream) {
		ConfirmDialog
				.show("Import-Typ auswählen",
						"Wie soll die Datei importiert werden? Bei einem Delta Import werden keine Schüler gelöscht",
						"Vollständiger Import", "Delta Import",
						new ConfirmDialog.Listener() {

							@Override
							public void onClose(ConfirmDialog dialog) {
								boolean fullImport;
								if (dialog.isConfirmed()) {
									fullImport = true;
								} else {
									fullImport = false;
								}
								studentInformationViewModel
										.receiveUploadByteOutputStream(
												outputStream, fullImport);
							}
						});
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
			if (!MIMEType.equals("text/csv")
					&& !MIMEType.equals("application/vnd.ms-excel")) {
				upload.interruptUpload();
				interrupted = true;
				eventBus.post(new MessageEvent(
						"Import nicht möglich.",
						"Die ausgewählte Datei ist keine CSV-Datei." + MIMEType,
						Type.ERROR));
			}
			reset();
			this.outputStream = new ByteArrayOutputStream();
			return outputStream;

		}

		public void uploadSucceeded(Upload.SucceededEvent event) {
			if (!interrupted) {
				selectImportTypeDialog(this.outputStream);
			}
		}

		/**
		 * Resets the upload
		 */
		public void reset() {
			interrupted = false;
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException ex) {
					LOG.trace("Couldn't close previous OutputStream");
				}
			}
			outputStream = null;
		}

		/**
		 * Interrupts the current upload
		 */
		protected void interrupt() {
			upload.interruptUpload();
			interrupted = true;
			eventBus.post(new MessageEvent(
					"Import nicht möglich.",
					"Die ausgewählte Datei ist zu groß. Bitte kontaktieren Sie einen Entwickler.",
					Type.ERROR));
		}

		@Override
		public void updateProgress(long readBytes, long contentLength) {
			if (readBytes > maxSize || contentLength > maxSize) {
				interrupt();
			}

		}
	}
}