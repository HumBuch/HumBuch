package de.dhbw.humbuch.ui.components;

import com.vaadin.annotations.Theme;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

@Theme("mytheme")
public class SubTask extends CustomComponent implements IComponent {

	// TODO: enum mit allen task descriptions
	
	private HorizontalLayout horizontalLayoutContent; 
	private Label labelDescription;
	private CheckBox checkBoxTaskDone;
	
	private String description;
	
	protected SubTask(String description) {
		this.description = description;
		init();
	}
	
	private void init() {
		horizontalLayoutContent = new HorizontalLayout();
		labelDescription = new Label(description);
		checkBoxTaskDone = new CheckBox();
		
		horizontalLayoutContent.setSpacing(true);
		horizontalLayoutContent.addComponent(checkBoxTaskDone);
		horizontalLayoutContent.addComponent(labelDescription);
	}
	
	@Override
	public Component getComponent() {
		return horizontalLayoutContent;
	}
}
