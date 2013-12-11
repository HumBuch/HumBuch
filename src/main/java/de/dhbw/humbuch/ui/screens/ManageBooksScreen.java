package de.dhbw.humbuch.ui.screens;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
@SuppressWarnings("serial")
public class ManageBooksScreen extends AbstractBasicScreen {

	private static final String NEW_BOOK = "Neues Buch";
	private static final String EDIT_BOOK = "Buch bearbeiten";
	private static final String SEARCH_BOOK = "Buecher suchen";
	
	private static final String TABLE_TITLE = "Titel";
	private static final String TABLE_PUBLISHER = "Verlag";
	private static final String TABLE_CLASS = "Klassenstufe";
	
	private VerticalLayout verticalLayoutContent;
	private HorizontalLayout horizontalLayoutButtonBar;
	private TextField textFieldSearchBar;
	private Table tableBooks;
	private Button buttonNewBook;
	private Button buttonEditBook;
	
	@WebServlet(value = "/manageBooks", asyncSupported = true)
//    @VaadinServletConfiguration(productionMode = false, ui = ManageBooksScreen.class, widgetset = "de.davherrmann.mvvm.demo.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }
	
	@Override
	protected void init(VaadinRequest request, Panel panel) {
		verticalLayoutContent = new VerticalLayout();
		horizontalLayoutButtonBar = new HorizontalLayout();
		textFieldSearchBar = new TextField(SEARCH_BOOK);
		tableBooks = new Table();
		buttonNewBook = new Button(NEW_BOOK);
		buttonEditBook = new Button(EDIT_BOOK);
		
		tableBooks.addContainerProperty("", CheckBox.class, null);
		tableBooks.addContainerProperty(TABLE_TITLE, String.class, null);
		tableBooks.addContainerProperty(TABLE_CLASS, String.class, null);
		tableBooks.addContainerProperty(TABLE_PUBLISHER, String.class, null);
		
		horizontalLayoutButtonBar.addComponent(buttonNewBook);
		horizontalLayoutButtonBar.setComponentAlignment(buttonNewBook, Alignment.MIDDLE_LEFT);
		horizontalLayoutButtonBar.addComponent(buttonEditBook);
		horizontalLayoutButtonBar.setComponentAlignment(buttonEditBook, Alignment.MIDDLE_RIGHT);
		
		verticalLayoutContent.addComponent(textFieldSearchBar);
		verticalLayoutContent.addComponent(tableBooks);
		verticalLayoutContent.addComponent(horizontalLayoutButtonBar);
		
		panel.setContent(verticalLayoutContent);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

}
