package de.dhbw.humbuch.ui.screens;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import de.dhbw.humbuch.ui.components.Task;

@Theme("mytheme")
@SuppressWarnings("serial")
public class HomeScreen extends AbstractBasicScreen {

    @WebServlet(value = "/home", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = HomeScreen.class, widgetset = "de.davherrmann.mvvm.demo.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }
	
	protected void init(VaadinRequest request, Panel panel) {
		VerticalLayout vl = new VerticalLayout();
		Task t = new Task();
		vl.addComponent(t.getComponent());
		panel.setCaption("Aufgaben Management");
		panel.setContent(vl);
	}
}
