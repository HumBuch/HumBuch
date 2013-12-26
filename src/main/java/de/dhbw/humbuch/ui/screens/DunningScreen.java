package de.dhbw.humbuch.ui.screens;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.viewmodel.DunningScreenModel;


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
	
	@Inject
	public DunningScreen(ViewModelComposer viewModelComposer, DunningScreenModel dunningScreenModel) {
		bindViewModel(viewModelComposer, dunningScreenModel);
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
		
		tableDunnings.setSelectable(true);
		tableDunnings.addContainerProperty(TABLE_FIRST_NAME, String.class, null);
		tableDunnings.addContainerProperty(TABLE_LAST_NAME, String.class, null);
		tableDunnings.addContainerProperty(TABLE_CLASS, String.class, null);
		tableDunnings.addContainerProperty(TABLE_TYPE, String.class, null);
		fillTableDunnings();
		
		tableSearchResults.setSelectable(true);
		tableSearchResults.addContainerProperty(TABLE_LAST_NAME, String.class, null);
		tableSearchResults.addContainerProperty(TABLE_FIRST_NAME, String.class, null);
		tableSearchResults.addContainerProperty(TABLE_CLASS, String.class, null);
		
		horizontalLayoutButtonBar.setSpacing(true);
		horizontalLayoutButtonBar.addComponent(buttonNewDunning);
		horizontalLayoutButtonBar.addComponent(buttonSecondDunning);
		horizontalLayoutButtonBar.addComponent(buttonReturnBook);
		
		verticalLayoutContent.setSpacing(true);
		verticalLayoutContent.setSizeFull();
		verticalLayoutContent.addComponent(tableDunnings);
		verticalLayoutContent.addComponent(horizontalLayoutButtonBar);
		verticalLayoutContent.addComponent(textFieldSearch);
		verticalLayoutContent.addComponent(tableSearchResults);
		verticalLayoutContent.addComponent(buttonCreateDunning);
		
		panel.setCaption("Mahnungs Uebersicht");
		panel.setContent(verticalLayoutContent);
	}
	
	private void fillTableDunnings() {
		tableDunnings.addItem(new Object[] {"Hans", "Wurst", "5a", "1. Mahnung"}, 1);
		tableDunnings.addItem(new Object[] {"Peter", "Lustig", "7b", "2. Mahnung"}, 2);
		tableDunnings.addItem(new Object[] {"Angela", "Merkel", "6c", "1. Mahnung generieren"}, 3);
		tableDunnings.addItem(new Object[] {"Max", "Muster", "7a", "1. Mahnung"}, 4);
		tableDunnings.addItem(new Object[] {"Super", "Richie", "6b", "2. Mahnung generieren"}, 5);
		tableDunnings.addItem(new Object[] {"Hannah", "Montana", "5a", "1. Mahnung"}, 6);
		tableDunnings.addItem(new Object[] {"Joko", "Winterscheidt", "8a", "2. Mahnung"}, 7);
		tableDunnings.addItem(new Object[] {"Test", "Name", "5a", "1. Mahnung generieren"}, 8);
		tableDunnings.addItem(new Object[] {"Er mag", "Zuege", "7a", "2. Mahnung"}, 9);
		tableDunnings.addItem(new Object[] {"Heino", "Kein plan", "8c", "2. Mahnung generieren"}, 10);
	}
	
	private void bindViewModel(ViewModelComposer viewModelComposer, Object... viewModels) {
		try {
			viewModelComposer.bind(this, viewModels);
		} catch (IllegalAccessException | NoSuchElementException
				| UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}	
	
	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}
