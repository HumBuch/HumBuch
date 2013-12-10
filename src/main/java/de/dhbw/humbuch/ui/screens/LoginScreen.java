package de.dhbw.humbuch.ui.screens;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import de.dhbw.humbuch.ui.components.Footer;

@Theme("mytheme")
@SuppressWarnings("serial")
public class LoginScreen extends Footer {
	
	@WebServlet(value = "/login", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = HomeScreen.class, widgetset = "de.davherrmann.mvvm.demo.AppWidgetSet")
	public static class Servlet extends VaadinServlet {
    }
	
	protected void init(VaadinRequest request, Panel panel) {
		VerticalLayout vl = new VerticalLayout();
		Label l = new Label("TEST124");
		vl.addComponent(l);
		panel.setContent(vl);
	}
}
