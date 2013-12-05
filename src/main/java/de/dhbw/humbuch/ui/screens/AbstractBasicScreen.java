package de.dhbw.humbuch.ui.screens;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;

import de.dhbw.humbuch.ui.components.Footer;
import de.dhbw.humbuch.ui.components.Header;
import de.dhbw.humbuch.ui.components.NavigationBar;

@Theme("mytheme")
@SuppressWarnings("serial")
public abstract class AbstractBasicScreen extends UI {

	private GridLayout gridLayoutScreen;
	private Header header;
	private Footer footer;
	private NavigationBar navigationBar;
	private Panel panelContent;
	private Component componentHeader;
	private Component componentFooter;
	private Component componentNavBar;
	
	@Override
	protected void init(VaadinRequest request) {
		gridLayoutScreen = new GridLayout(2, 3);
		header = new Header();
		footer = new Footer();
		navigationBar = new NavigationBar();
		panelContent = new Panel();
		
		this.init(request, panelContent);
		
		componentHeader = header.getComponent();
		componentHeader.setWidth("100%");
		componentHeader.setHeight("13%");
		componentFooter = footer.getComponent();
		componentFooter.setWidth("100%");
		componentFooter.setHeight("5%");
		componentNavBar = navigationBar.getComponent();
		componentNavBar.setWidth("100%");
		panelContent.setSizeFull();
		
		gridLayoutScreen.setSizeFull();
		gridLayoutScreen.setRowExpandRatio(0, 13);
		gridLayoutScreen.setRowExpandRatio(1, 82);
		gridLayoutScreen.setRowExpandRatio(2, 5);
		gridLayoutScreen.setColumnExpandRatio(0, 20);
		gridLayoutScreen.setColumnExpandRatio(1, 80);
		gridLayoutScreen.addComponent(header.getComponent(), 0, 0, 1, 0);
		gridLayoutScreen.addComponent(navigationBar.getComponent(), 0, 1);
		gridLayoutScreen.addComponent(panelContent, 1, 1);
		gridLayoutScreen.addComponent(footer.getComponent(), 0, 2, 1, 2);
		
		setContent(gridLayoutScreen);
	}
	
	protected abstract void init(VaadinRequest request, Panel panel);
}
