package de.dhbw.humbuch.view.components;

import java.util.ArrayList;
import java.util.Collection;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Student;


public class EnterDataComponent extends CustomComponent {

	private static final long serialVersionUID = 2174076688116026576L;


	public static enum Process {
		LENDING,
		RETURNING
	};

	private static final String SAVE = "Auswahl speichern";
	private static final String FIRST_NAME = "Vorname";
	private static final String LAST_NAME = "Nachname";
	private static final String CLASS = "Klasse";

	private static final String RECEIVED_BOOKS = "Erhaltene Bücher";
	private static final String ALL_BOOKS_RECEIVED = "Alle Bücher erhalten";
	private static final String RETURNED_BOOKS = "Züruckgegebene Bücher";
	private static final String ALL_BOOKS_RETURNED = "Alle Bücher zurückgegeben";

	private String enterDataHeader;
	private String enterDataLabel;

	private VerticalLayout verticalLayoutContent;
	private TreeTable treeTableStudents;
	private Button buttonSave;
	private FilterSearchBar filterSearchBar;

	public EnterDataComponent(Process process) {
		initProcess(process);
		init();
		buildLayout();
	}

	private void initProcess(Process process) {
		switch (process) {
		case LENDING:
			enterDataHeader = RECEIVED_BOOKS;
			enterDataLabel = ALL_BOOKS_RECEIVED;
			break;

		case RETURNING:
			enterDataHeader = RETURNED_BOOKS;
			enterDataLabel = ALL_BOOKS_RETURNED;
			break;
		}
	}

	private void init() {
		verticalLayoutContent = new VerticalLayout();
		buttonSave = new Button(SAVE);
		treeTableStudents = new TreeTable();
		filterSearchBar = new FilterSearchBar();

		verticalLayoutContent.setWidth("100%");
		verticalLayoutContent.setSpacing(true);
		verticalLayoutContent.setMargin(true);

		filterSearchBar.setWidth("100%");

		buttonSave.setIcon(new ThemeResource("images/icons/16/icon_save_red.png"));

		treeTableStudents.setWidth("100%");
		treeTableStudents.setPageLength(0);
		treeTableStudents.addContainerProperty(enterDataHeader, CheckBox.class, null);
		treeTableStudents.addContainerProperty(FIRST_NAME, String.class, null);
		treeTableStudents.addContainerProperty(LAST_NAME, String.class, null);
		treeTableStudents.addContainerProperty(CLASS, Grade.class, null);
	}

	private void buildLayout() {
		verticalLayoutContent.addComponent(filterSearchBar);
		verticalLayoutContent.addComponent(treeTableStudents);
		verticalLayoutContent.addComponent(buttonSave);
		verticalLayoutContent.setComponentAlignment(buttonSave, Alignment.BOTTOM_RIGHT);

		setCompositionRoot(verticalLayoutContent);
	}

	public void setStudents(Collection<Student> students) {
		treeTableStudents.removeAllItems();
		
		for (Student student : students) {
			if(!student.hasUnreceivedBorrowedMaterials()) {
				continue;
			}
				
			final CheckBox checkBoxStudent = new CheckBox(enterDataLabel);
			treeTableStudents.addItem(new Object[] {
					checkBoxStudent, student.getFirstname(), student.getLastname(), student.getGrade()}, student.hashCode());
			
			final Collection<CheckBox> checkBoxBorrowedMaterials = new ArrayList<CheckBox>(); 
			for (BorrowedMaterial borrowedMaterial : student.getBorrowedList()) {
				if(borrowedMaterial.isReceived()) {
					continue;
				}
				
				CheckBox checkBoxBorrowedMaterial = new CheckBox(borrowedMaterial.getTeachingMaterial().getName());
				checkBoxBorrowedMaterial.setData(borrowedMaterial);
				checkBoxBorrowedMaterials.add(checkBoxBorrowedMaterial);
				
				Object itemId = treeTableStudents.addItem(new Object[] {checkBoxBorrowedMaterial, null, null, null}, null);
				treeTableStudents.setChildrenAllowed(itemId, false);
				treeTableStudents.setParent(itemId, student.hashCode());
			}
			
			checkBoxStudent.addValueChangeListener(new ValueChangeListener() {
				private static final long serialVersionUID = 2108861783325156662L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					for(CheckBox checkBox : checkBoxBorrowedMaterials) {
						checkBox.setValue(checkBoxStudent.getValue());
					}
				}
			});
		}
		
		treeTableStudents.sort(new String[] {CLASS, LAST_NAME, FIRST_NAME}, new boolean[] {true, true, true});
	}
	
	public void addSaveButtonClickedListener(ClickListener clickListener) {
		buttonSave.addClickListener(clickListener);
	}
	
	public Collection<BorrowedMaterial> getSelectedBorrowedMaterials() {
		Collection<BorrowedMaterial> selectedBorrowedMaterials = new ArrayList<BorrowedMaterial>();
		for(Object itemId : treeTableStudents.getItemIds()) {
			CheckBox itemCheckBox = (CheckBox) treeTableStudents.getItem(itemId).getItemProperty(enterDataHeader).getValue();
			if(itemCheckBox.getValue()) {
				if(itemCheckBox.getData() instanceof BorrowedMaterial) {
					selectedBorrowedMaterials.add((BorrowedMaterial) itemCheckBox.getData());
				}
			}
		}
		
		return selectedBorrowedMaterials;
	}
}
