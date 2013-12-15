package de.dhbw.humbuch.ui.screens;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;


@Theme("mytheme")
@SuppressWarnings("serial")
public class BookEditScreen extends AbstractBasicScreen {
	
	@WebServlet(value = "/bookEdit", asyncSupported = true)
    //@VaadinServletConfiguration(productionMode = false, ui = BookEditScreen.class, widgetset = "de.davherrmann.mvvm.demo.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }
	
	protected void init(VaadinRequest request, Panel panel) {
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		vl.setSpacing(true);
		//TODO Datenfelder einf�gen
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(new Button("Abbrechen"));
		hl.addComponent(new Button("Speichern"));
		vl.addComponent(hl);
		panel.setContent(vl);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
