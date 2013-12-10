package de.dhbw.humbuch.ui.screens;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
public class StudentBookScreen extends AbstractBasicScreen {

	@WebServlet(value = "/studentBook", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = StudentBookScreen.class, widgetset = "de.davherrmann.mvvm.demo.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }
	
	protected void init(VaadinRequest request, Panel panel) {		
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		vl.setSpacing(true);
		
		Label name = new Label("Martin Wentzel");
		Label course = new Label("7a");
		
		CheckBox math = new CheckBox("Mathe");
		CheckBox german = new CheckBox("Deutsch");
		CheckBox english = new CheckBox("Englisch");
		CheckBox french = new CheckBox("Franz");
		
		vl.addComponents(name, course, math, german, english, french);
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(new Button("Abbrechen"));
		hl.addComponent(new Button("Bestätigen"));

		vl.addComponent(hl);
		panel.setContent(vl);
	}
}
