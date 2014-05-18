package de.dhbw.humbuch.view;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class HelpView extends VerticalLayout {
	
	private static final long serialVersionUID = 8409200222541248610L;

	private Label labelHelpText = new Label("keine Hilfe vorhanden...");
	
	public HelpView() {
		setMargin(true);
		setStyleName("help-view");
		labelHelpText.setContentMode(ContentMode.HTML);
		addComponent(labelHelpText);
	}
	
	public void setHelpText(String helpText) {
		labelHelpText.setValue(helpText);
	}
}
