package de.dhbw.humbuch.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;


public class ManualLendingPopupWindow extends Window {

	private static final long serialVersionUID = -6517435259424504689L;

	private static final Logger LOG = LoggerFactory.getLogger(ManualLendingPopupWindow.class);

	private static final String SEARCH_MATERIALS = "Materialien durchsuchen";
	private static final String SAVE = "Ausgewählte Materialien ausleihen";
	private static final String CANCEL = "Manuelle Ausleihe abbrechen";
	private static final String TEACHING_MATERIAL_HEADER = "Verfügbare Lehrmittel";

	private VerticalLayout verticalLayoutContent;
	private HorizontalLayout horizontalLayoutHeaderBar;
	private TextField textFieldSearchBar;
	private Table tableTeachingMaterials;
	private Button buttonSave;
	private Button buttonCancel;
	private IndexedContainer containerTableTeachingMaterials;
	private ArrayList<TeachingMaterial> teachingMaterials;
	private ArrayList<TeachingMaterial> currentlySelectedMaterials;
	private LendingView lendingView;
	private Student selectedStudent;

	public ManualLendingPopupWindow(LendingView lendingView, Student selectedStudent) {
		super("Manuelle Ausleihe für " + selectedStudent.getFirstname() + " " + selectedStudent.getLastname());
		this.lendingView = lendingView;
		this.selectedStudent = selectedStudent;
		init();
		buildLayout();
	}

	private void init() {
		verticalLayoutContent = new VerticalLayout();
		horizontalLayoutHeaderBar = new HorizontalLayout();
		textFieldSearchBar = new TextField(SEARCH_MATERIALS);
		tableTeachingMaterials = new Table();
		buttonSave = new Button(SAVE);
		buttonCancel = new Button(CANCEL);
		currentlySelectedMaterials = new ArrayList<TeachingMaterial>();
		containerTableTeachingMaterials = new IndexedContainer();

		textFieldSearchBar.setWidth("100%");
		buttonSave.setIcon(new ThemeResource("images/icons/16/icon_save_red.png"));

		containerTableTeachingMaterials.addContainerProperty(TEACHING_MATERIAL_HEADER, String.class, null);

		tableTeachingMaterials.setContainerDataSource(containerTableTeachingMaterials);
		tableTeachingMaterials.setWidth("100%");
		tableTeachingMaterials.setSelectable(true);
		tableTeachingMaterials.setMultiSelect(true);
		tableTeachingMaterials.setImmediate(true);
		setTableListener();
		updateTableContent();

		horizontalLayoutHeaderBar.setSpacing(true);
		verticalLayoutContent.setSpacing(true);
		verticalLayoutContent.setMargin(true);

		center();
		setModal(true);
		setResizable(false);
		setDraggable(false);

		addListeners();
	}

	private void buildLayout() {
		horizontalLayoutHeaderBar.addComponent(textFieldSearchBar);
		horizontalLayoutHeaderBar.addComponent(buttonCancel);
		horizontalLayoutHeaderBar.addComponent(buttonSave);
		horizontalLayoutHeaderBar.setComponentAlignment(buttonCancel, Alignment.MIDDLE_CENTER);
		horizontalLayoutHeaderBar.setComponentAlignment(buttonSave, Alignment.MIDDLE_CENTER);

		verticalLayoutContent.addComponent(horizontalLayoutHeaderBar);
		verticalLayoutContent.addComponent(tableTeachingMaterials);

		setContent(verticalLayoutContent);
	}

	private void updateTableContent() {
		teachingMaterials = lendingView.getTeachingMaterials();

		if (teachingMaterials == null) {
			return;
		}

		int i = 1;
		for (TeachingMaterial teachingMaterial : teachingMaterials) {
			tableTeachingMaterials.addItem(new Object[] { teachingMaterial.getName() }, i);
			i++;
		}
	}

	private void setTableListener() {
		tableTeachingMaterials.addValueChangeListener(new ValueChangeListener() {

			private static final long serialVersionUID = -8774191239600142741L;

			@SuppressWarnings("unchecked")
			@Override
			public void valueChange(ValueChangeEvent event) {
				currentlySelectedMaterials.clear();
				Object selectedIds = tableTeachingMaterials.getValue();
				if (selectedIds instanceof Set<?>) {
					Set<Integer> ids = (Set<Integer>) selectedIds;
					for (Integer id : ids) {
						currentlySelectedMaterials.add(teachingMaterials.get(id - 1));
					}
				}
				else {
					LOG.warn("Table selection is not an instance of Set<?>");
				}
			}
		});
	}

	private void addListeners() {
		buttonCancel.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 3353625484974813579L;

			@Override
			public void buttonClick(ClickEvent event) {
				close();
			}
		});

		buttonSave.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 4375804067002022079L;

			@Override
			public void buttonClick(ClickEvent event) {
				saveTeachingMaterialsForStudent();
			}
		});

		textFieldSearchBar.addTextChangeListener(new TextChangeListener() {

			private static final long serialVersionUID = -6281243106168356850L;

			@Override
			public void textChange(TextChangeEvent event) {
				Filter filter = new SimpleStringFilter(TEACHING_MATERIAL_HEADER, event.getText(), true, false);
				containerTableTeachingMaterials.removeAllContainerFilters();
				containerTableTeachingMaterials.addContainerFilter(filter);
			}
		});
	}

	public void setTeachingMaterials(Collection<TeachingMaterial> teachingMaterials) {
		this.teachingMaterials = new ArrayList<TeachingMaterial>(teachingMaterials);
		updateTableContent();
	}

	private void saveTeachingMaterialsForStudent() {
		Map<Student, List<TeachingMaterial>> studentsWithMaterials = new HashMap<Student, List<TeachingMaterial>>();
		studentsWithMaterials.put(selectedStudent, currentlySelectedMaterials);

		lendingView.saveTeachingMaterialsForStudents(studentsWithMaterials);
	}
}
