package de.dhbw.humbuch.ui.screens;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;


@Theme("mytheme")
@SuppressWarnings("serial")
public class DunningScreen extends AbstractBasicScreen {
	
	private static final String SECOND_DUNNING = "2. Mahnung";
	private static final String RETURN_BOOK = "Buch zurueck";
	private static final String NEW_DUNNING = "Neue Mahnung";
	private static final String CREATE_DUNNING = "Mahnung erzeugen";
	private static final String SEARCH_PUPIL = "Schueler suchen";
	
	// TODO: Dynamically / directly from database
	private static final String TABLE_LAST_NAME = "Nachname";
	private static final String TABLE_FIRST_NAME = "Vorname";
	private static final String TABLE_CLASS = "Klasse";
	private static final String TABLE_TYPE = "Typ";

	private VerticalLayout verticalLayoutContent;
	private HorizontalLayout horizontalLayoutButtonBar;
	private Button buttonSecondDunning;
	private Button buttonReturnBook;
	private Button buttonNewDunning;
	private Table tableDunnings;
	private TextField textFieldSearch;
	private Button buttonCreateDunning;
	private Table tableSearchResults;
	
	@WebServlet(value = "/dunnings", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DunningScreen.class, widgetset = "de.davherrmann.mvvm.demo.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }
	
	@Override
	protected void init(VaadinRequest request, Panel panel) {
		verticalLayoutContent = new VerticalLayout();
		horizontalLayoutButtonBar = new HorizontalLayout();
		buttonSecondDunning = new Button(SECOND_DUNNING);
		buttonReturnBook = new Button(RETURN_BOOK);
		buttonNewDunning = new Button(NEW_DUNNING);
		buttonCreateDunning = new Button(CREATE_DUNNING);
		textFieldSearch = new TextField(SEARCH_PUPIL);
		tableDunnings = new Table();
		tableSearchResults = new Table();
		
		tableDunnings.addContainerProperty("", CheckBox.class, null);
		tableDunnings.addContainerProperty(TABLE_LAST_NAME, String.class, null);
		tableDunnings.addContainerProperty(TABLE_FIRST_NAME, String.class, null);
		tableDunnings.addContainerProperty(TABLE_CLASS, String.class, null);
		tableDunnings.addContainerProperty(TABLE_TYPE, String.class, null);
		
		tableSearchResults.addContainerProperty(TABLE_LAST_NAME, String.class, null);
		tableSearchResults.addContainerProperty(TABLE_FIRST_NAME, String.class, null);
		tableSearchResults.addContainerProperty(TABLE_CLASS, String.class, null);
		
		horizontalLayoutButtonBar.addComponent(buttonNewDunning);
		horizontalLayoutButtonBar.addComponent(buttonSecondDunning);
		horizontalLayoutButtonBar.addComponent(buttonReturnBook);
		
		verticalLayoutContent.addComponent(tableDunnings);
		verticalLayoutContent.addComponent(horizontalLayoutButtonBar);
		verticalLayoutContent.addComponent(textFieldSearch);
		verticalLayoutContent.addComponent(tableSearchResults);
		verticalLayoutContent.addComponent(buttonCreateDunning);
		
		panel.setContent(verticalLayoutContent);
	}
}
