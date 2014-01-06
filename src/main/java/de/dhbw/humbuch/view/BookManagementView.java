package de.dhbw.humbuch.view;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.inject.Inject;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindAction;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.model.entity.TeachingMaterial;
import de.dhbw.humbuch.viewmodel.BookManagementViewModel;
import de.dhbw.humbuch.viewmodel.BookManagementViewModel.BookFromGrade;
import de.dhbw.humbuch.viewmodel.BookManagementViewModel.BookIdentifyer;
import de.dhbw.humbuch.viewmodel.BookManagementViewModel.BookName;
import de.dhbw.humbuch.viewmodel.BookManagementViewModel.BookProducer;
import de.dhbw.humbuch.viewmodel.BookManagementViewModel.BookToGrade;
import de.dhbw.humbuch.viewmodel.BookManagementViewModel.DoGetBook;
import de.dhbw.humbuch.viewmodel.BookManagementViewModel.DoSaveBook;
import de.dhbw.humbuch.viewmodel.BookManagementViewModel.tableData;


public class BookManagementView extends VerticalLayout implements View, ViewInformation {
	private static final long serialVersionUID = -5063268947544706757L;
	/**
	 * Constants
	 */
	private static final String TITLE = "Lehrmittel Verwaltung";
	private static final String NEW_BOOK = "Neues Buch";
	private static final String EDIT_BOOK = "Buch bearbeiten";
	private static final String SEARCH_BOOK = "Bücher suchen";
	private static final String TABLE_TITLE = "Titel";
	private static final String TABLE_PUBLISHER = "Verlag";
	private static final String TABLE_CLASS = "Klassenstufe";
	private static final String TEXTFIELD_NAME = "Titel";
	private static final String TEXTFIELD_IDENTIFYER = "ISBN";
	private static final String WINDOW_NEW_BOOK ="Neues Buch eintragen";
	private static final String WINDOW_EDIT_BOOK ="Buch editieren";
	private static final String BUTTON_SAVE ="Speichern";
	private static final String BUTTON_CANCEL = "Abbrechen";
	private static final String TEXTFIELD_PRODUCER = "Hersteller/Verlag";
	private static final String TEXTFIELD_FROMGRADE = "von Klassenstufe";
	private static final String TEXTFIELD_TOGRADE = "bis Klassenstufe";
	private static final String TEXTFIELD_SEARCH_PLACEHOLDER = "Buch oder Verlag";


	/**
	 * Layout components
	 */
	private HorizontalLayout horizontalLayoutButtonBar;
	
	private TextField textFieldSearchBar;
	private Table tableTeachingMaterials;
	
	@BindAction(value = DoGetBook.class, source = {"bookId"})
	private Button buttonEditBook = new Button(EDIT_BOOK);
	private Button buttonNewBook;
		
	private TextField bookId = new TextField();
	
	@BindState(tableData.class)
	private State<Set<TeachingMaterial>> teachingMaterialData = new BasicState<Set<TeachingMaterial>>(Set.class);
	private IndexedContainer containerTable;
	
	/**
	 * All popup-window components and the corresponding binded states.
	 * The popup-window for adding a new teaching material and editing a teaching material is the same. Only the caption will be set differently
	 */
	private Window windowEditBook;
	private VerticalLayout verticalLayoutWindowContent;
	private HorizontalLayout horizontalLayoutWindowBar;
	@BindState(BookName.class)
	private TextField textFieldBookName = new TextField(TEXTFIELD_NAME);
	@BindState(BookIdentifyer.class)
	private TextField textFieldBookIdentifyer  = new TextField(TEXTFIELD_IDENTIFYER);
	@BindState(BookProducer.class)
	private TextField textFieldProducer = new TextField(TEXTFIELD_PRODUCER);
	@BindState(BookFromGrade.class)
	private TextField textFieldFromGrade = new TextField(TEXTFIELD_FROMGRADE);
	@BindState(BookToGrade.class)
	private TextField textFieldToGrade = new TextField(TEXTFIELD_TOGRADE);
	@BindAction(value = DoSaveBook.class, source = {"bookId","textFieldBookName", "textFieldBookIdentifyer", "textFieldProducer", "textFieldFromGrade", "textFieldToGrade"})
	private Button buttonWindowSave = new Button(BUTTON_SAVE);
	private Button buttonWindowCancel;
	
	/**
	 * 
	 * @param viewModelComposer Is injected automatically by Guice
	 * @param bookManagementViewModel Is injected automatically by Guice
	 */
	@Inject
	public BookManagementView(ViewModelComposer viewModelComposer,
			BookManagementViewModel bookManagementViewModel) {
		init();
		buildLayout();
		
		teachingMaterialData.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object value) {
				try {
					containerTable.removeAllItems();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				HashSet<TeachingMaterial> tableData = (HashSet<TeachingMaterial>) value;
				for(TeachingMaterial book: tableData) {
					tableTeachingMaterials.addItem(new Object[] {book.getName(),book.getFromGrade()+"-"+book.getToGrade(),book.getProducer()},book.getId());
				}
			}
		});
		bindViewModel(viewModelComposer, bookManagementViewModel);
	}

	/**
	 * Initializes the components and sets attributes.
	 */
	private void init() {
		horizontalLayoutButtonBar = new HorizontalLayout();

		setMargin(true);
		setSpacing(true);

		buttonNewBook = new Button(NEW_BOOK);

		textFieldSearchBar = new TextField(SEARCH_BOOK);
		textFieldSearchBar.setImmediate(true);
		textFieldSearchBar.setInputPrompt(TEXTFIELD_SEARCH_PLACEHOLDER);
		
		textFieldSearchBar.setTextChangeEventMode(TextChangeEventMode.EAGER);
		
		tableTeachingMaterials = new Table();
		tableTeachingMaterials.setSelectable(true);
		tableTeachingMaterials.setImmediate(true);
		tableTeachingMaterials.setWidth("100%");
		
		containerTable = new IndexedContainer();
		containerTable.addContainerProperty(TABLE_TITLE, String.class, null);
		containerTable.addContainerProperty(TABLE_CLASS, String.class, null);
		containerTable.addContainerProperty(TABLE_PUBLISHER, String.class, null);
		tableTeachingMaterials.setContainerDataSource(containerTable);
		
		windowEditBook = new Window();
		windowEditBook.center();
		verticalLayoutWindowContent = new VerticalLayout();
		verticalLayoutWindowContent.setMargin(true);
		horizontalLayoutWindowBar = new HorizontalLayout();
		
		buttonWindowCancel = new Button(BUTTON_CANCEL);
		this.addListener();
	}
	
	/**
	 * Adds all listener to their corresponding components.
	 */
	private void addListener() {
		/**
		 * Closes the popup-window
		 */
		ClickListener popupClose = new ClickListener() {
			private static final long serialVersionUID = 4111242410277636186L;
			@Override
			public void buttonClick(ClickEvent event) {
				windowEditBook.close();
			}
		};
		buttonWindowCancel.addClickListener(popupClose);	
		buttonWindowSave.addClickListener(popupClose);
		/**
		 * Sets the bookId state with the selected row id. 
		 */
		tableTeachingMaterials.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 741255095335535493L;
			@Override
			public void valueChange(ValueChangeEvent event) {
				bookId.setValue(tableTeachingMaterials.getValue().toString());
			}
		});
		/**
		 * Provides the live search of the teaching material table by adding a filter after every keypress in the search field.
		 * Currently the publisher and title search are supported.
		 */
		textFieldSearchBar.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = -1684545652234105334L;
			@Override
			public void textChange(TextChangeEvent event) {
				Filter filter = new Or(new SimpleStringFilter(TABLE_TITLE,event.getText(), true, false),
						new SimpleStringFilter(TABLE_PUBLISHER, event.getText(), true, false));
				containerTable.removeAllContainerFilters();
				containerTable.addContainerFilter(filter);
			}
		});
		/**
		 * Opens the popup-window for adding a new book.
		 */
		buttonNewBook.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -7433701329516481457L;
			@Override
			public void buttonClick(ClickEvent event) {
				emptyWindowFields();
				bookId.setValue("");
				windowEditBook.setCaption(WINDOW_NEW_BOOK);
				UI.getCurrent().addWindow(windowEditBook);
			}
		});
		/**
		 * Opens the popup-window for editing a book.
		 */
		buttonEditBook.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 563232762007381515L;
			@Override
			public void buttonClick(ClickEvent event) {
				windowEditBook.setCaption(WINDOW_EDIT_BOOK);
				UI.getCurrent().addWindow(windowEditBook);
			}
		});
	}

	/**
	 * Builds the layout by adding all components in their specific order.
	 */
	private void buildLayout() {
		horizontalLayoutButtonBar.addComponent(buttonNewBook);
		horizontalLayoutButtonBar.setComponentAlignment(buttonNewBook,
				Alignment.MIDDLE_LEFT);
		horizontalLayoutButtonBar.addComponent(buttonEditBook);
		horizontalLayoutButtonBar.setComponentAlignment(buttonEditBook,
				Alignment.MIDDLE_RIGHT);

		addComponent(textFieldSearchBar);
		addComponent(tableTeachingMaterials);
		addComponent(horizontalLayoutButtonBar);
		
		verticalLayoutWindowContent.addComponent(textFieldBookName);
		verticalLayoutWindowContent.addComponent(textFieldBookIdentifyer);
		verticalLayoutWindowContent.addComponent(textFieldProducer);
		verticalLayoutWindowContent.addComponent(textFieldFromGrade);
		verticalLayoutWindowContent.addComponent(textFieldToGrade);
		
		horizontalLayoutWindowBar.addComponent(buttonWindowCancel);
		horizontalLayoutWindowBar.addComponent(buttonWindowSave);
		verticalLayoutWindowContent.addComponent(horizontalLayoutWindowBar);
		windowEditBook.setContent(verticalLayoutWindowContent);
	}
	
	/**
	 * Empties the fields of the popup-window to prevent that TextFields already have content form previous edits.
	 */
	private void emptyWindowFields() {
		textFieldBookName.setValue("");
		textFieldBookIdentifyer.setValue("");
		textFieldProducer.setValue("");
		textFieldFromGrade.setValue("");
		textFieldToGrade.setValue("");
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	private void bindViewModel(ViewModelComposer viewModelComposer,
			Object... viewModels) {
		try {
			viewModelComposer.bind(this, viewModels);
		} catch (IllegalAccessException | NoSuchElementException
				| UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getTitle() {
		return TITLE;
	}
}
