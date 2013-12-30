package de.dhbw.humbuch.view.components;

import com.vaadin.annotations.Theme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

@Theme("mytheme")
public class MultiClassChooser extends CustomComponent {
	private static final long serialVersionUID = -5343607078508459759L;
	private GridLayout gridLayout;

	protected CheckBox checkBoxAll;

	public MultiClassChooser() {
		init();
	}

	protected void init() {
		gridLayout = new GridLayout(5, 6);
		gridLayout.setWidth("300px");

		setCompositionRoot(gridLayout);

		gridLayout.addComponent(new Label(""));
		gridLayout.addComponent(new Label("a"));
		gridLayout.addComponent(new Label("b"));
		gridLayout.addComponent(new Label("c"));
		gridLayout.addComponent(new Label("alle"));

		for (int i = 5; i <= 10; i++) { // from class 5 to 10..

			Label label = new Label("Klasse " + i);
			label.setWidth("35px");
			gridLayout.addComponent(label);

			for (int j = 0; j < 3; j++) { // add 3 classes for each step
				CheckBox checkBox = new CheckBox();
				checkBox.setData(i);
				gridLayout.addComponent(checkBox);
			}

			// configure check-all
			checkBoxAll = new CheckBox();
			checkBoxAll.setData(i);
			checkBoxAll.addValueChangeListener(new ValueChangeListener() {
				private static final long serialVersionUID = 3622458437057996372L;

				@Override
				public void valueChange(ValueChangeEvent event) {
					/**
					 * TODO: Copy the value to the other checkboxes
					 */

				}
			});

			gridLayout.addComponent(checkBoxAll);

		}

	}
}