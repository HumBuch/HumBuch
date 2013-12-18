package de.dhbw.humbuch.view;

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.ComponentContainerViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.guice.vaadin.ScopedUI;

@Theme("mytheme")
@SuppressWarnings("serial")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class MainUI extends ScopedUI {
	
	public static final String HOME_VIEW = "home_view";
	public static final String BOOK_MANAGEMENT_VIEW = "book_management";
	
	@Inject
	private LoginView loginView;
	
	@Inject
	private HomeView homeView;
	@Inject
	private BookManagementView bookManagementView;
	
	private VerticalLayout verticalLayoutRoot;
	private VerticalLayout verticalLayoutContent;
	private ComponentContainerViewDisplay ccViewDisplay;

	public Navigator navigator;
	
	@Override
	protected void init(VaadinRequest request) {
		
		verticalLayoutRoot = new VerticalLayout();
		verticalLayoutContent = new VerticalLayout();
		
		verticalLayoutRoot.addComponent(verticalLayoutContent);
		
		ccViewDisplay = new ComponentContainerViewDisplay(verticalLayoutContent);
		
		navigator = new Navigator(UI.getCurrent(), ccViewDisplay);
		
		// TODO: Hack! Check how to save String in enums
		navigator.addView("", homeView);
		navigator.addView(BOOK_MANAGEMENT_VIEW, bookManagementView);
//		navigator.addView(NavigationTarget.HOME_VIEW.name(), homeView);
		
		setContent(verticalLayoutRoot);
	}

}
