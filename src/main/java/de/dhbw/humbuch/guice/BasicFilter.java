package de.dhbw.humbuch.guice;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

import de.davherrmann.guice.vaadin.UIScopeModule;

/**
 * {@link Filter} for creating a Guice {@link Injector}
 *
 * @author davherrmann
 */
public class BasicFilter extends GuiceFilter {

	private static Injector INJECTOR;

	public static Injector getInjector() {
		return INJECTOR;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (INJECTOR != null) {
			throw new ServletException("Injector already created?!");
		}
		INJECTOR = Guice.createInjector(new BasicModule(), new UIScopeModule());
		filterConfig.getServletContext().log(
				"Created injector with " + INJECTOR.getAllBindings().size()
						+ " bindings.");
		super.init(filterConfig);
	}
}
