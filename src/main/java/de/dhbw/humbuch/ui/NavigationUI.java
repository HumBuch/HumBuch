package de.dhbw.humbuch.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.ComponentContainerViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.guice.vaadin.ScopedUI;
import de.dhbw.humbuch.ui.screens.HomeScreen;
import de.dhbw.humbuch.ui.screens.LoginScreen;

@SuppressWarnings("serial")
public class NavigationUI extends ScopedUI {

	public Navigator navigator;
	
	public static final String MAINVIEW = "main";
	
	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);
		ComponentContainerViewDisplay viewDisplay = new ComponentContainerViewDisplay(layout);
		navigator = new Navigator(UI.getCurrent(), viewDisplay);
//        navigator.addView("", new LoginScreen());
        navigator.addView("", new HomeScreen());
	}

}
