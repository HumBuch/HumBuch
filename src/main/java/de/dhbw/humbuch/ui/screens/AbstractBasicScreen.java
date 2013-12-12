package de.dhbw.humbuch.ui.screens;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
//import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import de.dhbw.humbuch.ui.components.Footer;
import de.dhbw.humbuch.ui.components.Header;
import de.dhbw.humbuch.ui.components.NavigationBar;

@Theme("mytheme")
@SuppressWarnings("serial")
public abstract class AbstractBasicScreen extends VerticalLayout implements View {

	private GridLayout gridLayoutScreen;
	private Header header;
	private Footer footer;
	private NavigationBar navigationBar;
	private Panel panelContent;
//	private Component componentHeader;
//	private Component componentFooter;
//	private Component componentNavBar;
	private VerticalLayout verticalLayoutPanel;
	
	public AbstractBasicScreen() {
		init(null);
	}
	
	//@Override
	protected void init(VaadinRequest request) {
		System.out.println("Ausführung");
		gridLayoutScreen = new GridLayout(2, 3);
		verticalLayoutPanel = new VerticalLayout();
		header = new Header();
		footer = new Footer();
		navigationBar = new NavigationBar();
		panelContent = new Panel();
		
		
		
		//panelContent.setStyleName(Runo.PANEL_LIGHT);
		verticalLayoutPanel.setMargin(true);
		verticalLayoutPanel.setSizeFull();
		verticalLayoutPanel.addComponent(panelContent);
		
//		componentHeader = header.getComponent();
//		componentHeader.setSizeFull();
//		componentFooter = footer.getComponent();
//		componentFooter.setSizeFull();
//		componentNavBar = navigationBar.getComponent();
//		componentNavBar.setWidth("100%");
		navigationBar.setWidth("100%");
		panelContent.setSizeFull();
		
		gridLayoutScreen.setSizeFull();
		gridLayoutScreen.setRowExpandRatio(0, 15);
		gridLayoutScreen.setRowExpandRatio(1, 80);
		gridLayoutScreen.setRowExpandRatio(2, 5);
		gridLayoutScreen.setColumnExpandRatio(0, 20);
		gridLayoutScreen.setColumnExpandRatio(1, 80);
//		gridLayoutScreen.addComponent(header.getComponent(), 0, 0, 1, 0);
		gridLayoutScreen.addComponent(header, 0, 0, 1, 0);
//		gridLayoutScreen.addComponent(navigationBar.getComponent(), 0, 1);
		gridLayoutScreen.addComponent(navigationBar, 0, 1);
		gridLayoutScreen.addComponent(verticalLayoutPanel, 1, 1);
//		gridLayoutScreen.addComponent(footer.getComponent(), 0, 2, 1, 2);
		gridLayoutScreen.addComponent(footer, 0, 2, 1, 2);
		addComponent(gridLayoutScreen);
		
		this.init(request, panelContent);
//		setContent(gridLayoutScreen);
	}
	
	protected abstract void init(VaadinRequest request, Panel panel);
}
