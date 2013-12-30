package de.dhbw.humbuch.view.components;

import com.vaadin.annotations.Theme;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;


@Theme("mytheme")
public class MultiClassChooser extends CustomComponent {

	private static final long serialVersionUID = -5343607078508459759L;

	private static final String CHOOSE_CLASS = "Klassen auswählen";
	
	private VerticalLayout verticalLayoutContent;
	private TreeTable treeTableClasses;

	public MultiClassChooser() {
		init();
		buildLayout();
	}

	private void init() {
		verticalLayoutContent = new VerticalLayout();
		treeTableClasses = new TreeTable();
		
		treeTableClasses.setWidth("100%");
		treeTableClasses.setPageLength(0);
		treeTableClasses.addContainerProperty(CHOOSE_CLASS, CheckBox.class, null);
		
		populateWithTestData();
	}

	private void buildLayout() {
		verticalLayoutContent.addComponent(treeTableClasses);

		setCompositionRoot(verticalLayoutContent);
	}
	
	private void populateWithTestData() {
		// root
		treeTableClasses.addItem(new Object[] {new CheckBox("Alle Klassen")}, 1);
		// branches
		treeTableClasses.addItem(new Object[] {new CheckBox("Klassenstufe 5")}, 2);
		treeTableClasses.addItem(new Object[] {new CheckBox("Klassenstufe 6")}, 3);
		treeTableClasses.addItem(new Object[] {new CheckBox("Klassenstufe 7")}, 4);
		treeTableClasses.addItem(new Object[] {new CheckBox("Klassenstufe 8")}, 5);
		// leaves
		treeTableClasses.addItem(new Object[] {new CheckBox("5a")}, 6);
		treeTableClasses.addItem(new Object[] {new CheckBox("5b")}, 7);
		treeTableClasses.addItem(new Object[] {new CheckBox("5c")}, 8);
		treeTableClasses.addItem(new Object[] {new CheckBox("6a")}, 9);
		treeTableClasses.addItem(new Object[] {new CheckBox("6b")}, 10);
		treeTableClasses.addItem(new Object[] {new CheckBox("7a")}, 11);
		treeTableClasses.addItem(new Object[] {new CheckBox("7b")}, 12);
		treeTableClasses.addItem(new Object[] {new CheckBox("8a")}, 13);
		treeTableClasses.addItem(new Object[] {new CheckBox("8b")}, 14);
		treeTableClasses.addItem(new Object[] {new CheckBox("8c")}, 15);
		
		// build hierarchy
		treeTableClasses.setParent(2, 1);
		treeTableClasses.setParent(3, 1);
		treeTableClasses.setParent(4, 1);
		treeTableClasses.setParent(5, 1);
		
		treeTableClasses.setParent(6, 2);
		treeTableClasses.setParent(7, 2);
		treeTableClasses.setParent(8, 2);
		treeTableClasses.setParent(9, 3);
		treeTableClasses.setParent(10, 3);
		treeTableClasses.setParent(11, 4);
		treeTableClasses.setParent(12, 4);
		treeTableClasses.setParent(13, 5);
		treeTableClasses.setParent(14, 5);
		treeTableClasses.setParent(15, 5);
		
		// The childs may not have additional childs
		for (int i = 6; i <= 15; i++) {
			treeTableClasses.setChildrenAllowed(i, false);
		}
		
		treeTableClasses.setCollapsed(1, false);
	}
}