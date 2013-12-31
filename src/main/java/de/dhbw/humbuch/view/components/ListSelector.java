package de.dhbw.humbuch.view.components;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.dhbw.humbuch.model.SubjectHandler;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.Subject;
import de.dhbw.humbuch.model.entity.TeachingMaterial;
import de.dhbw.humbuch.util.PDFHandler;
import de.dhbw.humbuch.util.PDFStudentList;


public class ListSelector extends CustomComponent {

	private static final long serialVersionUID = 5363626372866373934L;

	private final static Logger LOG = LoggerFactory.getLogger(ListSelector.class);


	public static enum Process {
		LENDING,
		RETURNING
	};

	private static final String SEARCH_STUDENT = "Schüler suchen";
	private static final String SEARCH = "Suchen";
	private static final String MATERIAL_LIST = "Materiallisten für ausgewählte Klassen drucken";
	private static final String CLASS_LIST = "Schülerlisten für ausgewählte Klassen drucken";
	private static final String STUDENT_LIST = "Schülerliste drucken";

	private static final String FIRST_NAME = "Vorname";
	private static final String LAST_NAME = "Nachname";
	private static final String CLASS = "Klasse";

	private HorizontalLayout horizontalLayoutContent;
	private VerticalLayout verticalLayoutClass;
	private VerticalLayout verticalLayoutStudent;
	private HorizontalLayout horizontalLayoutSearch;
	private MultiClassChooser multiClassChooser;
	private TextField textFieldSearchBar;
	private Button buttonSearch;
	private Button buttonMaterialList;
	private Button buttonClassList;
	private Button buttonStudentList;
	private Table tableSearchResults;
	private ThemeResource resourceIconPrint;

	public ListSelector(Process process) {
		init();
		buildLayout();
		// TODO: dirty, since you should check process before
		adaptToProcess(process);
	}

	private void init() {
		horizontalLayoutContent = new HorizontalLayout();
		verticalLayoutClass = new VerticalLayout();
		verticalLayoutStudent = new VerticalLayout();
		horizontalLayoutSearch = new HorizontalLayout();
		multiClassChooser = new MultiClassChooser();
		textFieldSearchBar = new TextField(SEARCH_STUDENT);
		buttonSearch = new Button(SEARCH);
		buttonMaterialList = new Button(MATERIAL_LIST);
		buttonClassList = new Button(CLASS_LIST);
		buttonStudentList = new Button(STUDENT_LIST);
		tableSearchResults = new Table();
		resourceIconPrint = new ThemeResource("images/icons/16/icon_print_red.png");

		horizontalLayoutContent.setWidth("100%");
		horizontalLayoutContent.setSpacing(true);
		verticalLayoutClass.setWidth("100%");
		verticalLayoutClass.setSpacing(true);
		verticalLayoutClass.setMargin(true);
		verticalLayoutStudent.setWidth("100%");
		verticalLayoutStudent.setSpacing(true);
		verticalLayoutStudent.setMargin(true);
		horizontalLayoutSearch.setWidth("100%");
		horizontalLayoutSearch.setSpacing(true);

		textFieldSearchBar.setWidth("100%");

		buttonSearch.setIcon(new ThemeResource("images/icons/16/icon_search_red.png"));
		buttonSearch.setWidth("100%");
		buttonMaterialList.setIcon(resourceIconPrint);
		buttonMaterialList.setWidth("100%");
		buttonMaterialList.addClickListener(new ListListener());
		buttonClassList.setIcon(resourceIconPrint);
		buttonClassList.setWidth("100%");
		buttonClassList.addClickListener(new ListListener());
		buttonStudentList.setIcon(resourceIconPrint);
		buttonStudentList.setWidth("100%");
		buttonStudentList.addClickListener(new ListListener());

		tableSearchResults.setWidth("100%");
		tableSearchResults.setPageLength(0);
		tableSearchResults.addContainerProperty(FIRST_NAME, String.class, null);
		tableSearchResults.addContainerProperty(LAST_NAME, String.class, null);
		tableSearchResults.addContainerProperty(CLASS, String.class, null);
	}

	private void buildLayout() {
		verticalLayoutClass.addComponent(multiClassChooser);
		verticalLayoutClass.addComponent(buttonMaterialList);
		verticalLayoutClass.setComponentAlignment(buttonMaterialList, Alignment.BOTTOM_CENTER);
		verticalLayoutClass.addComponent(buttonClassList);
		verticalLayoutClass.setComponentAlignment(buttonClassList, Alignment.BOTTOM_CENTER);

		horizontalLayoutSearch.addComponent(textFieldSearchBar);
		horizontalLayoutSearch.addComponent(buttonSearch);
		horizontalLayoutSearch.setComponentAlignment(buttonSearch, Alignment.BOTTOM_RIGHT);

		verticalLayoutStudent.addComponent(horizontalLayoutSearch);
		verticalLayoutStudent.addComponent(tableSearchResults);
		verticalLayoutStudent.addComponent(buttonStudentList);
		verticalLayoutStudent.setComponentAlignment(buttonStudentList, Alignment.BOTTOM_CENTER);

		horizontalLayoutContent.addComponent(verticalLayoutClass);
		horizontalLayoutContent.addComponent(verticalLayoutStudent);

		setCompositionRoot(horizontalLayoutContent);
	}

	private void adaptToProcess(Process process) {
		switch (process) {
		case LENDING:
			// nothing to do
			break;
		case RETURNING:
			verticalLayoutClass.removeComponent(buttonMaterialList);
			break;
		}
	}


	private class ListListener implements ClickListener {

		private static final long serialVersionUID = 2214186111484851012L;

		@Override
		public void buttonClick(ClickEvent event) {
			Button clickedButton = event.getButton();
			StreamResource sr;
			String title = "Default Liste";
			// TODO: get all entities needed for printing actual lists.
			if (clickedButton == buttonClassList) {
				title = "Klassen Liste";
				Set<Student> s = createTestStudents();
				ByteArrayOutputStream baos = new PDFStudentList(s).createByteArrayOutputStreamForPDF();
				sr = new StreamResource(new PDFHandler.PDFStreamSource(baos), "ClassList.pdf");
			}
			else if (clickedButton == buttonMaterialList) {
				title = "Material Liste";
				Student s = createTestStudent();
				ByteArrayOutputStream baos = new PDFStudentList(s).createByteArrayOutputStreamForPDF();
				sr = new StreamResource(new PDFHandler.PDFStreamSource(baos), "MaterialList.pdf");
			}
			else if (clickedButton == buttonStudentList) {
				title = "Schüler Liste";
				Student s = createTestStudent();
				ByteArrayOutputStream baos = new PDFStudentList(s).createByteArrayOutputStreamForPDF();
				sr = new StreamResource(new PDFHandler.PDFStreamSource(baos), "StudentList.pdf");
			}
			else {
				LOG.warn("Could not determine which button was pressed.");
				return;
			}

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
	}

	public static Student createTestStudent() {
		Set<Subject> profileTypeSet = SubjectHandler.createProfile(new String[] { "E", "", "F" }, "ev");
		List<BorrowedMaterial> borrowedMaterialList = new ArrayList<BorrowedMaterial>();

		TeachingMaterial teachingMaterial = new TeachingMaterial();

		teachingMaterial.setToGrade(6);
		teachingMaterial.setName("Bio1 - Bugs");
		teachingMaterial.setPrice(79.75);
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);

		teachingMaterial = new TeachingMaterial();

		teachingMaterial.setToGrade(11);
		teachingMaterial.setName("German1 - Faust");
		teachingMaterial.setPrice(22.49);
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);

		Date date = null;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.04.1970");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}
		Grade grade = new Grade.Builder("11au").build();
		return new Student.Builder(4, "Karl", "August", date, grade).profile(profileTypeSet).borrowedList(borrowedMaterialList).build();

	}
	
	public static Set<Student> createTestStudents(){
		Set<Student> students = new LinkedHashSet<Student>();
		
		Set<Subject> profileTypeSet = SubjectHandler.createProfile(new String[]{"E", "", "F"}, "ev");
		List<BorrowedMaterial> borrowedMaterialList = new ArrayList<BorrowedMaterial>();
		
		TeachingMaterial teachingMaterial = new TeachingMaterial();

		teachingMaterial.setToGrade(6);
		teachingMaterial.setName("Bio1 - Bugs");
		teachingMaterial.setPrice(79.75);
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		teachingMaterial = new TeachingMaterial();

		teachingMaterial.setToGrade(11);
		teachingMaterial.setName("German1 - Faust");
		teachingMaterial.setPrice(22.49);
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		Date date = null;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.04.1970");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}		
		Grade grade = new Grade.Builder("11au").build();
		students.add(new Student.Builder(4,"Karl","August", date, grade).profile(profileTypeSet).borrowedList(borrowedMaterialList).build());
		
		profileTypeSet = SubjectHandler.createProfile(new String[]{"E", "", "F"}, "ev");
		borrowedMaterialList = new ArrayList<BorrowedMaterial>();
		
		teachingMaterial = new TeachingMaterial();

		teachingMaterial.setToGrade(6);
		teachingMaterial.setName("Math");
		teachingMaterial.setPrice(79.75);
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		teachingMaterial = new TeachingMaterial();

		teachingMaterial.setToGrade(11);
		teachingMaterial.setName("Chemistry");
		teachingMaterial.setPrice(25.49);
		borrowedMaterial = new BorrowedMaterial();
		borrowedMaterial.setTeachingMaterial(teachingMaterial);
		borrowedMaterialList.add(borrowedMaterial);
		
		date = null;
		try {
			date = new SimpleDateFormat("dd.mm.yyyy", Locale.GERMAN).parse("12.03.1970");
		}
		catch (ParseException e) {
			System.err.println("Could not format date " + e.getStackTrace());
		}		
		grade = new Grade.Builder("11au").build();
		students.add(new Student.Builder(5,"Berta","Bussy", date, grade).profile(profileTypeSet).borrowedList(borrowedMaterialList).build());
		
		return students;		
	}
}