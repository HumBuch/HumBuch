package de.dhbw.humbuch;

import com.google.inject.Provides;
import com.google.inject.servlet.ServletModule;
import com.vaadin.ui.UI;

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
