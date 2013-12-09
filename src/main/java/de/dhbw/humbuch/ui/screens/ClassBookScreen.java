package de.dhbw.humbuch.ui.screens;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
public class ClassBookScreen extends AbstractBasicScreen {

	@WebServlet(value = "/classBook", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ClassBookScreen.class, widgetset = "de.davherrmann.mvvm.demo.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }
	
	protected void init(VaadinRequest request, Panel panel) {		
		VerticalLayout vl = new VerticalLayout();
		ComboBox select  = new ComboBox("Klasse auswählen");
		select.addItem("7a");
		select.addItem("7b");
		select.addItem("7c");
		vl.addComponent(select);
		
		Table table = new Table("Liste einpflegen");
		
		//TODO Dynamically add new Columns, based on the database design
		table.addContainerProperty("Name", String.class, null);
		table.addContainerProperty("M", CheckBox.class, null);
		table.addContainerProperty("E", CheckBox.class, null);
		table.addContainerProperty("F", CheckBox.class, null);
		CheckBox math = new CheckBox();
		CheckBox english = new CheckBox();
		CheckBox french = new CheckBox();
		
		table.addItem(new Object[]{"Martin", math, english, french}, new Integer(1));
		
		vl.addComponent(table);
		
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.addComponent(new Button("Abbrechen"));
		buttons.addComponent(new Button("Bestätigen"));
		
		vl.addComponent(buttons);
		panel.setContent(vl);
	}
}
