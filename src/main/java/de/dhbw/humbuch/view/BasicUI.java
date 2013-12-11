package de.dhbw.humbuch.view;

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;

import de.davherrmann.guice.vaadin.ScopedUI;

@Theme("mytheme")
@SuppressWarnings("serial")
public class BasicUI extends ScopedUI {
	
	@Inject
	private LoginView loginView;

	@Override
	protected void init(VaadinRequest request) {
		setContent(loginView);
	}

}
