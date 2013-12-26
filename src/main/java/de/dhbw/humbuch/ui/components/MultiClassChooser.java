package de.dhbw.humbuch.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
public class MultiClassChooser extends CustomComponent implements IComponent {
	
	private GridLayout gridLayout;
	private VerticalLayout verticalLayout;
	
	protected CheckBox checkBoxAll;
	protected List<CheckBox> allCheckBoxes = new ArrayList<CheckBox>();
	
	public MultiClassChooser() {
		init();
	}
	
	protected void init() {
		
		/* TODO:
		 * Size and population of the GridLayout should be handled dynamically.
		 * For example: a class with the name 5d is not yet supported.
		 */
		/*
		verticalLayout = new VerticalLayout();
		verticalLayout.setWidth("300px");
		
        //CompositionRoot has to be set
        setCompositionRoot(verticalLayout);

		final Table table = new Table();
			
		table.addContainerProperty("Stufe", String.class, null);
		table.addContainerProperty("a", CheckBox.class, null);
		table.addContainerProperty("b", CheckBox.class, null);
		table.addContainerProperty("c", CheckBox.class, null);
		table.addContainerProperty("alle", CheckBox.class, null);
        
		for (int i = 5; i <= 10; i++) { // from class 5 to 10..
			
			// configure check-all
			checkBoxAll = new CheckBox();
			checkBoxAll.addValueChangeListener(new ValueChangeListener() {	
				@Override
				public void valueChange(ValueChangeEvent event) {
					table.getR
			    }
			});
			
			table.addItem(new Object[] {
				    "" + i, new CheckBox(), new CheckBox(), new CheckBox(), checkBoxAll}, i);
		}
		
		verticalLayout.addComponent(table);
		*/
		
		gridLayout = new GridLayout(5, 6);
		gridLayout.setWidth("300px");
		
		setCompositionRoot(gridLayout);
		
		gridLayout.addComponent(new Label(""));
		gridLayout.addComponent(new Label ("a"));
		gridLayout.addComponent(new Label ("b"));
		gridLayout.addComponent(new Label ("c"));
		gridLayout.addComponent(new Label ("alle"));
		
		for (int i = 5; i <= 10; i++) { // from class 5 to 10..
			
			Label label = new Label("Klasse " + i);
			label.setWidth("35px");
			gridLayout.addComponent(label);
			
			for (int j = 0; j < 3; j++) { // add 3 classes for each step
				CheckBox checkBox = new CheckBox();
				checkBox.setData(i);
				gridLayout.addComponent(checkBox);
				allCheckBoxes.add(checkBox);
			}
			
			// configure check-all
			checkBoxAll = new CheckBox();
			checkBoxAll.setData(i);
			checkBoxAll.addValueChangeListener(new ValueChangeListener() {	
				@Override
				public void valueChange(ValueChangeEvent event) {
					
			        /** TODO:
			         *  Copy the value to the other checkboxes
			         */
					for (Component comp : gridLayout) {
						
							checkBoxAll.setValue(checkBoxAll.getValue());
						
					}
			    }
			});
			
			gridLayout.addComponent(checkBoxAll);
			
		}
		
	}

	@Override
	public Component getComponent() {
		return this;
	}

}
