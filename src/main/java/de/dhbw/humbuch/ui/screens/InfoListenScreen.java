package de.dhbw.humbuch.ui.screens;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
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
public class InfoListenScreen extends AbstractBasicScreen {
	
	@WebServlet(value = "/infoListen", asyncSupported = true)
//    @VaadinServletConfiguration(productionMode = false, ui = InfoListenScreen.class, widgetset = "de.davherrmann.mvvm.demo.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }
	
	protected void init(VaadinRequest request, Panel panel) {		
		VerticalLayout vl = new VerticalLayout();
		vl.setMargin(true);
		vl.setSpacing(true);
		
		//TODO Mehrfachauswahl für Klassenauswahl
		ComboBox select1  = new ComboBox("Gesammelt für Klasse");
		select1.addItem("7a");
		select1.addItem("7b");
		select1.addItem("7c");
		HorizontalLayout hl1 = new HorizontalLayout();
		hl1.addComponent(select1);
		hl1.addComponent(new Button("Drucken"));
		
		Label l3 = new Label("Für einzelnen Schüler");
		TextField textField = new TextField();
		textField.setValue("Name");
		HorizontalLayout hl3 = new HorizontalLayout();
		hl3.addComponent(l3);
		hl3.addComponent(textField);
		hl3.addComponent(new Button("Drucken"));
		
		vl.addComponent(hl1);
		vl.addComponent(hl3);
		panel.setContent(vl);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
