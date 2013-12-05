package de.dhbw.humbuch.ui.components;

import com.vaadin.annotations.Theme;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;


/**
 * Missing features: 
 * TODO: add more design via css-files 
 * TODO: instead of hardcoding the version try to get it dynamically 
 * TODO: Think of a better groupname 
 * TODO: Put all components in bottom-left corner instead of spanning
 * along the whole bottom side
 * */
@Theme("mytheme")
public class Footer implements IComponent {

	private static final String GROUPNAME = "DH-Ware";
	private static final String VERSION = "0.1a";
	private static final String APP_NAME = "HumBuch";

	private HorizontalLayout horizontalLayoutFooter;
	private Label labelGroupname;
	private Label labelVersion;
	private Label labelAppName;

	public Footer() {
		initLayout();
	}

	private void initLayout() {
		horizontalLayoutFooter = new HorizontalLayout();
		labelGroupname = new Label(GROUPNAME);
		labelVersion = new Label(VERSION);
		labelAppName = new Label(APP_NAME);

		horizontalLayoutFooter.setWidth("100%");
		horizontalLayoutFooter.setSpacing(true);
		/*
		 * TODO: Different Background color (red)
		 * To achieve this:
		 * 1. Create a class in the style.css-file:
		 *  .footer_background {background: black}
		 * 2. hlFooter.setStyleName(footer_background)
		 * */

		horizontalLayoutFooter.addComponent(labelGroupname);
		horizontalLayoutFooter.addComponent(labelAppName);
		horizontalLayoutFooter.addComponent(labelVersion);
	}

	public HorizontalLayout getComponent() {
		return horizontalLayoutFooter;
	}
}
