package de.dhbw.humbuch.ui.components;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.BaseTheme;

@Theme("mytheme")
public class SubTask extends CustomComponent {

	// TODO: enum mit allen task descriptions
	/* TODO: rethink how the handling of button click should be done. 
	 * maybe one generic class which manages the navigation to the subtargets
	 */
	
	private HorizontalLayout horizontalLayoutContent; 
	private Button buttonSubTask;
	
	private String description;
	private boolean done;
	
	protected SubTask(String description, boolean done) {
		this.description = description;
		this.done = done;
		init();
	}
	
	private void init() {
		horizontalLayoutContent = new HorizontalLayout();
		buttonSubTask = new Button(description);
		if(done == true) {
			buttonSubTask.setIcon(new ThemeResource("images/icons/16/icon_checked_red.png"));
		}
		else {
			buttonSubTask.setIcon(new ThemeResource("images/icons/16/icon_unchecked_red.png"));
		}
		buttonSubTask.setStyleName(BaseTheme.BUTTON_LINK);
		
		//horizontalLayoutContent.setSpacing(true);
		horizontalLayoutContent.setMargin(true);
		horizontalLayoutContent.addComponent(buttonSubTask);
		
		setCompositionRoot(horizontalLayoutContent);
	}
}
