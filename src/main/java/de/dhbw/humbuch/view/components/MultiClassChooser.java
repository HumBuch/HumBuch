package de.dhbw.humbuch.view.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.viewmodel.LendingViewModel;
import de.dhbw.humbuch.viewmodel.LendingViewModel.Grades;


@Theme("mytheme")
public class MultiClassChooser extends CustomComponent {

	private static final long serialVersionUID = -5343607078508459759L;

	private static final String CHOOSE_CLASS = "Klassen auswählen";
	private static final String ALL_CLASSES = "Alle Klassen";
	private static final String CLASS_LEVEL = "Klassenstufe ";

	private VerticalLayout verticalLayoutContent;
	private TreeTable treeTableClasses;
	private HashMap<String, ArrayList<Grade>> selectableNodes;
	private HashMap<String, Grade> descriptionWithObject;
	private ArrayList<Grade> currentlySelectedGrades;

	@BindState(Grades.class)
	private State<Collection<Grade>> allGrades = new BasicState<>(Collection.class);

	@Inject
	public MultiClassChooser(ViewModelComposer viewModelComposer, LendingViewModel lendingViewModel) {
		bindViewModel(viewModelComposer, lendingViewModel);
		init();
		buildLayout();
	}

	private void init() {
		currentlySelectedGrades = new ArrayList<Grade>();
		selectableNodes = new HashMap<String, ArrayList<Grade>>();
		verticalLayoutContent = new VerticalLayout();
		treeTableClasses = new TreeTable();

		treeTableClasses.setWidth("100%");
		treeTableClasses.setPageLength(0);
		treeTableClasses.addContainerProperty(CHOOSE_CLASS, CheckBox.class, null);

		populateWithClasses();
	}

	private void buildLayout() {
		verticalLayoutContent.addComponent(treeTableClasses);

		setCompositionRoot(verticalLayoutContent);
	}

	private void populateWithClasses() {
		// All Grade objects from the provided state
		Collection<Grade> collAllGrades = allGrades.get();
		// Stores all class levels
		HashSet<Integer> uniqueClassLevels = new HashSet<Integer>();
		// Stores all class descriptions
		ArrayList<String> classDescriptions = new ArrayList<String>();
		// store all class descriptions and corresponding Grade object for later matching
		descriptionWithObject = new HashMap<String, Grade>();
		// counts all items inside the tree
		int numberOfItems = 1;

		// Add root element
		CheckBox checkBoxRoot = new CheckBox(ALL_CLASSES);
		checkBoxRoot.addValueChangeListener(new ClassSelectedListener());
		checkBoxRoot.setImmediate(true);
		treeTableClasses.addItem(new Object[] { checkBoxRoot }, 1);
		selectableNodes.put(ALL_CLASSES, new ArrayList<Grade>(collAllGrades));
		numberOfItems += 1;

		// Collect information before building tree
		for (Grade g : collAllGrades) {
			// Collect all branches
			uniqueClassLevels.add(g.getGrade());

			// Create names of leaves
			String classDescription = "" + g.getGrade() + g.getSuffix();
			classDescriptions.add(classDescription);
			descriptionWithObject.put(classDescription, g);
		}

		// Sort the items to be displayed in the right order
		ArrayList<Integer> sortedGrades = new ArrayList<Integer>(uniqueClassLevels);
		Collections.sort(sortedGrades);
		Collections.sort(classDescriptions);

		// Build the tree and its hierarchy
		int lastMarker = 0;
		for (Integer grade : sortedGrades) {
			// Add branch element
			String classLevel = CLASS_LEVEL + grade;
			ArrayList<Grade> classesOfBranch = new ArrayList<Grade>();
			CheckBox checkBoxBranch = new CheckBox(classLevel);
			checkBoxBranch.addValueChangeListener(new ClassSelectedListener());
			checkBoxBranch.setImmediate(true);
			treeTableClasses.addItem(new Object[] { checkBoxBranch }, numberOfItems);

			// put branch under the root element
			treeTableClasses.setParent(numberOfItems, 1);

			// Add leaf elements for current branch
			int leafesForBranch = 0;
			for (; lastMarker < classDescriptions.size(); lastMarker++) {
				String classDescription = classDescriptions.get(lastMarker);

				if (classDescription.contains("" + grade)) {
					leafesForBranch += 1;

					// Add leaf element
					CheckBox checkBoxLeaf = new CheckBox(classDescription);
					checkBoxLeaf.addValueChangeListener(new ClassSelectedListener());
					checkBoxLeaf.setImmediate(true);
					treeTableClasses.addItem(new Object[] { checkBoxLeaf }, leafesForBranch + numberOfItems);
					treeTableClasses.setChildrenAllowed(leafesForBranch + numberOfItems, false);

					// put leaf under current branch
					treeTableClasses.setParent(leafesForBranch + numberOfItems, numberOfItems);

					ArrayList<Grade> singleGradeList = new ArrayList<Grade>();
					singleGradeList.add(descriptionWithObject.get(classDescription));
					selectableNodes.put(classDescription, singleGradeList);
					classesOfBranch.add(descriptionWithObject.get(classDescription));
				}
				else {
					numberOfItems = leafesForBranch + numberOfItems;
					break;
				}
			}

			selectableNodes.put(classLevel, classesOfBranch);
			numberOfItems++;
		}

		treeTableClasses.setCollapsed(1, false);
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

	public ArrayList<Grade> getCurrentSelection() {
		return currentlySelectedGrades;
	}
	
	private class ClassSelectedListener implements ValueChangeListener {

		private static final long serialVersionUID = 5537796492030278819L;

		@Override
		public void valueChange(ValueChangeEvent event) {
			CheckBox source = (CheckBox) event.getProperty();
			String classDescription = source.getCaption();

			if (source.getValue() == true) {
				currentlySelectedGrades.addAll(selectableNodes.get(classDescription));
			}
			else {
				currentlySelectedGrades.removeAll(selectableNodes.get(classDescription));
			}
		}

	}
}