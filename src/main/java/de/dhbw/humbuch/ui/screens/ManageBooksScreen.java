package de.dhbw.humbuch.ui.screens;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindAction;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.viewmodel.ManageBookScreenModel;
import de.dhbw.humbuch.viewmodel.ManageBookScreenModel.DoEdit;
import de.dhbw.humbuch.viewmodel.ManageBookScreenModel.Edit;

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
	
	@BindState(Edit.class)
	private TextField textFieldSearchBar;
	private Table tableBooks;
	
	@BindAction(value = DoEdit.class)
	private Button buttonNewBook;
	private Button buttonEditBook;
	
	@Inject
	public ManageBooksScreen(ViewModelComposer viewModelComposer, ManageBookScreenModel manageBookScreenModel) {
		bindViewModel(viewModelComposer, manageBookScreenModel);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.dhbw.humbuch.ui.screens.AbstractBasicScreen#init(com.vaadin.server.VaadinRequest, com.vaadin.ui.Panel)
	 * This function is called from the init function in AbstractBasicScreen
	 */
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
