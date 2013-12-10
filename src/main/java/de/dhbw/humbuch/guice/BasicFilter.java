package de.dhbw.humbuch.guice;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

@WebFilter(urlPatterns = "/*")
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
		INJECTOR = Guice.createInjector(new BasicModule());
		filterConfig.getServletContext().log(
				"Created injector with " + INJECTOR.getAllBindings().size()
						+ " bindings.");
		super.init(filterConfig);
	}
}
