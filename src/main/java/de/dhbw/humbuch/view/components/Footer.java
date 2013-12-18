package de.dhbw.humbuch.view.components;

import com.vaadin.annotations.Theme;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * TODO: instead of hardcoding the version try to get it dynamically TODO: Think
 * of a better groupname
 * */
@Theme("mytheme")
public class Footer extends CustomComponent {
	private static final long serialVersionUID = 4727970027087506179L;

	private static final String GROUPNAME = "DH-Ware";
	private static final String VERSION = "0.1a";
	private static final String APP_NAME = "HumBuch";

	private HorizontalLayout horizontalLayoutFooter;
	private HorizontalLayout horizontalLayoutItems;
	private Label labelGroupname;
	private Label labelVersion;
	private Label labelAppName;

	public Footer() {
		init();
		buildLayout();
	}

	private void init() {
		horizontalLayoutFooter = new HorizontalLayout();
		horizontalLayoutItems = new HorizontalLayout();
		labelGroupname = new Label(GROUPNAME);
		labelVersion = new Label(VERSION);
		labelAppName = new Label(APP_NAME);

		labelGroupname.setStyleName("footer_font");
		labelGroupname.setSizeFull();
		labelVersion.setStyleName("footer_font");
		labelVersion.setSizeFull();
		labelAppName.setStyleName("footer_font");
		labelAppName.setSizeFull();

		horizontalLayoutItems.setSpacing(true);

		horizontalLayoutFooter.setWidth("100%");
		horizontalLayoutFooter.setStyleName("footer_background");

	}

	private void buildLayout() {
		horizontalLayoutItems.addComponent(labelGroupname);
		horizontalLayoutItems.addComponent(labelAppName);
		horizontalLayoutItems.addComponent(labelVersion);

		horizontalLayoutFooter.addComponent(horizontalLayoutItems);

		setCompositionRoot(horizontalLayoutFooter);
	}
}
