package de.dhbw.humbuch.ui.screens;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
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

	private static final String CHOOSE_CLASS = "Klasse auswählen";
	private static final String LIST_EDIT = "Liste einpflegen";
	
	private String[] courses = {"5a","5b","5c","6a","6b","6c","7a","7b","7c","8a","8b","8c"};
	
	@WebServlet(value = "/classBook", asyncSupported = true)
//    @VaadinServletConfiguration(productionMode = false, ui = ClassBookScreen.class, widgetset = "de.davherrmann.mvvm.demo.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }
	
	protected void init(VaadinRequest request, Panel panel) {	
		VerticalLayout vl = new VerticalLayout(); 
		vl.setMargin(true);
		vl.setSpacing(true);
		
		ComboBox select  = new ComboBox(CHOOSE_CLASS);
		for(int i=0;i<courses.length;i++) {
			select.addItem(courses[i]);
		}
		vl.addComponent(select);
		
		Table table = new Table(LIST_EDIT);
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
		Button cancel = new Button("Abbrechen");
		Button save = new Button("Bestätigen");
		buttons.addComponent(cancel);
		buttons.addComponent(save);
		
		vl.addComponent(buttons);
		panel.setContent(vl);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}
