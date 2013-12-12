package de.dhbw.humbuch.ui;

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.ComponentContainerViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.guice.vaadin.ScopedUI;
import de.dhbw.humbuch.ui.screens.HomeScreen;
import de.dhbw.humbuch.ui.screens.ManageBooksScreen;

@Theme("mytheme")
@SuppressWarnings("serial")
public class NavigationUI extends ScopedUI {

	public Navigator navigator;
	
	@Inject
	private HomeScreen homeScreen;
	@Inject
	private ManageBooksScreen manageBookScreen;
	
	public static final String MAINVIEW = "main";
	public static final String MANAGE_BOOKS = "manageBooks";
	
	@Override
	protected void init(VaadinRequest request) {
		final VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSpacing(true);
		setContent(layout);
		ComponentContainerViewDisplay viewDisplay = new ComponentContainerViewDisplay(layout);
		navigator = new Navigator(UI.getCurrent(), viewDisplay);
        navigator.addView("", homeScreen);
        /* Each Screen must be added here with its corresponding URL
         * Corresponding URL should be a constant
         * Example: navigator.addView("dunning", new DunningScreen());
         * If you add a book here, dont forget to change the ClickListener for the corresponding button in NavigationBar
         */
      	navigator.addView(MANAGE_BOOKS, manageBookScreen);
      	navigator.addView(MAINVIEW, homeScreen);
	}

}
