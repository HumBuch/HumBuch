package de.dhbw.humbuch.view.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
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

	private VerticalLayout verticalLayoutContent;
	private TreeTable treeTableClasses;

	@BindState(Grades.class)
	private State<Collection<Grade>> allGrades = new BasicState<>(Collection.class);

	@Inject
	public MultiClassChooser(ViewModelComposer viewModelComposer, LendingViewModel lendingViewModel) {
		bindViewModel(viewModelComposer, lendingViewModel);
		init();
		buildLayout();
	}

	private void init() {
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
		Collection<Grade> collAllGrades = allGrades.get();
		HashSet<Integer> grades = new HashSet<Integer>();
		ArrayList<String> classesWithSuffix = new ArrayList<String>();
		int numberOfItems = 1;

		// Add root element
		treeTableClasses.addItem(new Object[] { new CheckBox("Alle Klassen") }, 1);
		numberOfItems += 1;

		// Collect information before building tree
		for (Grade g : collAllGrades) {
			// Collect all branches
			grades.add(g.getGrade());
			// Create names of leaves
			classesWithSuffix.add("" + g.getGrade() + g.getSuffix());
		}

		ArrayList<Integer> sortedGrades = new ArrayList<Integer>(grades);
		Collections.sort(sortedGrades);
		Collections.sort(classesWithSuffix);

		int lastMarker = 0;
		for (Integer grade : sortedGrades) {
			// Add branch elements
			treeTableClasses.addItem(new Object[] { new CheckBox("Klassenstufe " + grade) }, numberOfItems);
			// put branch under the root element
			treeTableClasses.setParent(numberOfItems, 1);

			// Add leaf elements
			int leafesForBranch = 0;
			for (; lastMarker < classesWithSuffix.size(); lastMarker++) {
				String classWithSuffix = classesWithSuffix.get(lastMarker);

				if (classWithSuffix.contains("" + grade)) {
					leafesForBranch += 1;
					treeTableClasses.addItem(new Object[] { new CheckBox(classWithSuffix) }, leafesForBranch + numberOfItems);
					treeTableClasses.setChildrenAllowed(leafesForBranch + numberOfItems, false);
					// put leaf under current branch
					treeTableClasses.setParent(leafesForBranch + numberOfItems, numberOfItems);
				}
				else {
					numberOfItems = leafesForBranch + numberOfItems;
					break;
				}
			}
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
}