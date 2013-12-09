package de.dhbw.humbuch.ui.screens;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
public class AusleihListenScreen extends AbstractBasicScreen {

	@WebServlet(value = "/ausleihListen", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = AusleihListenScreen.class, widgetset = "de.davherrmann.mvvm.demo.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }
	
	protected void init(VaadinRequest request, Panel panel) {		
		VerticalLayout vl = new VerticalLayout();
		
		//TODO Mehrfachauswahl für Klassenauswahl
		Label l1 = new Label("Materialübersicht für Klasse");
		ComboBox select1  = new ComboBox("Klasse auswählen");
		select1.addItem("7a");
		select1.addItem("7b");
		select1.addItem("7c");
		HorizontalLayout hl1 = new HorizontalLayout();
		hl1.addComponent(l1);
		hl1.addComponent(select1);
		hl1.addComponent(new Button("Drucken"));
		
		//TODO Mehrfachauswahl für Klassenauswahl
		Label l2 = new Label("Ausleihliste für Klasse");
		ComboBox select2  = new ComboBox("Klasse auswählen");
		select2.addItem("7a");
		select2.addItem("7b");
		select2.addItem("7c");
		HorizontalLayout hl2 = new HorizontalLayout();
		hl2.addComponent(l2);
		hl2.addComponent(select2);
		hl2.addComponent(new Button("Drucken"));
		
		Label l3 = new Label("Ausleihliste für einzelnen Schüler");
		TextField textField = new TextField();
		textField.setValue("Name");
		HorizontalLayout hl3 = new HorizontalLayout();
		hl3.addComponent(l3);
		hl3.addComponent(textField);
		hl3.addComponent(new Button("Drucken"));
		
		vl.addComponent(hl1);
		vl.addComponent(hl2);
		vl.addComponent(hl3);
		panel.setContent(vl);
	}
}
