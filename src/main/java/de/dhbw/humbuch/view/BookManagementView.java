package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.viewmodel.BookManagementViewModel;


public class BookManagementView extends VerticalLayout implements View, ViewInformation {

	private static final long serialVersionUID = -5063268947544706757L;

	private static final String TITLE = "Lehrmittel Verwaltung";
	private static final String NEW_BOOK = "Neues Buch";
	private static final String EDIT_BOOK = "Buch bearbeiten";
	private static final String SEARCH_BOOK = "Buecher suchen";
	private static final String TABLE_TITLE = "Titel";
	private static final String TABLE_PUBLISHER = "Verlag";
	private static final String TABLE_CLASS = "Klassenstufe";

	private HorizontalLayout horizontalLayoutButtonBar;
	private TextField textFieldSearchBar;
	private Table tableBooks;
	private Button buttonNewBook;
	private Button buttonEditBook;

	@Inject
	public BookManagementView(ViewModelComposer viewModelComposer,
			BookManagementViewModel bookManagementViewModel) {
		init();
		buildLayout();
		bindViewModel(viewModelComposer, bookManagementViewModel);
	}

	private void init() {
		horizontalLayoutButtonBar = new HorizontalLayout();

		setMargin(true);

		buttonNewBook = new Button(NEW_BOOK);
		buttonEditBook = new Button(EDIT_BOOK);

		textFieldSearchBar = new TextField(SEARCH_BOOK);

		tableBooks = new Table();
		tableBooks.setWidth("100%");
		tableBooks.addContainerProperty("", CheckBox.class, null);
		tableBooks.addContainerProperty(TABLE_TITLE, String.class, null);
		tableBooks.addContainerProperty(TABLE_CLASS, String.class, null);
		tableBooks.addContainerProperty(TABLE_PUBLISHER, String.class, null);
		populateTableWithTestData();
	}

	private void buildLayout() {
		horizontalLayoutButtonBar.addComponent(buttonNewBook);
		horizontalLayoutButtonBar.setComponentAlignment(buttonNewBook,
				Alignment.MIDDLE_LEFT);
		horizontalLayoutButtonBar.addComponent(buttonEditBook);
		horizontalLayoutButtonBar.setComponentAlignment(buttonEditBook,
				Alignment.MIDDLE_RIGHT);

		addComponent(textFieldSearchBar);
		addComponent(tableBooks);
		addComponent(horizontalLayoutButtonBar);
	}

	private void populateTableWithTestData() {
		tableBooks.addItem(new Object[] { new CheckBox(), "Mustermann", "6",
											"Springer" }, 1);
		tableBooks.addItem(new Object[] { new CheckBox(), "Maier", "5", "d.punkt" },
				2);
		tableBooks.addItem(new Object[] { new CheckBox(), "Mustermann", "8",
											"d.punkt" }, 3);
		tableBooks.addItem(new Object[] { new CheckBox(), "XYZ", "9", "XYZ" }, 4);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
	}

	private void bindViewModel(ViewModelComposer viewModelComposer,
			Object... viewModels) {
		try {
			viewModelComposer.bind(this, viewModels);
		}
		catch (IllegalAccessException | NoSuchElementException
				| UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getTitle() {
		return TITLE;
	}
}
