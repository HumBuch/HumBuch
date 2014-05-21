package de.dhbw.humbuch.view.components;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;

/**
 * 
 * @author Johannes Idelhauser
 * @
 */
public class Header extends HorizontalLayout {
	private static final long serialVersionUID = 5218684938845793342L;
	
	private Button buttonHelp;

	public Header() {
		init();
	}

	private void init() {
		setWidth("100%");
		addStyleName("header");
		
		HorizontalLayout root = new HorizontalLayout();
		
		buttonHelp = new NativeButton("Hilfe");
		buttonHelp.addStyleName("icon-help");
		
		root.addComponent(buttonHelp);
		
		addComponent(root);
		setComponentAlignment(root, Alignment.MIDDLE_RIGHT);
	}
	
	public Button getHelpButton() {
		return buttonHelp;
	}
}
