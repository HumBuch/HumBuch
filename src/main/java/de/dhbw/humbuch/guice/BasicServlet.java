package de.dhbw.humbuch.guice;

import java.util.Properties;

import javax.servlet.Servlet;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinServlet;

/**
 * {@link Singleton} {@link Servlet} - initialise {@link DeploymentConfiguration} and 
 * {@link UIProvider}
 * 
 * @author davherrmann
 */
@Singleton
public class BasicServlet extends VaadinServlet implements
		SessionInitListener {
	private static final long serialVersionUID = -608546493704080500L;
	
	@Inject
	private BasicProvider basicProvider;

	@Override
	protected DeploymentConfiguration createDeploymentConfiguration(
			Properties initParameters) {
		initParameters.setProperty(SERVLET_PARAMETER_PRODUCTION_MODE, "true");
		initParameters.setProperty("disable-xsrf-protection", "false");
		return super.createDeploymentConfiguration(initParameters);
	}

	@Override
	protected void servletInitialized() {
		getService().addSessionInitListener(this);
	}

	@Override
	public void sessionInit(SessionInitEvent event) throws ServiceException {
		event.getSession().addUIProvider(basicProvider);
	}

}
