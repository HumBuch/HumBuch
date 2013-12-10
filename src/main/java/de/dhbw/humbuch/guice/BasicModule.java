package de.dhbw.humbuch.guice;

import com.google.inject.Provides;
import com.google.inject.servlet.ServletModule;
import com.vaadin.ui.UI;

import de.dhbw.humbuch.view.BasicUI;

public class BasicModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve("/*").with(BasicServlet.class);
	}

	@Provides
	private Class<? extends UI> provideUIClass() {
		return BasicUI.class;
	}
}
