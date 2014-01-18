package de.dhbw.humbuch.view;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.inject.Inject;
import com.vaadin.data.Container.Filter;
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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.model.entity.Category;
import de.dhbw.humbuch.model.entity.Profile;
import de.dhbw.humbuch.model.entity.Subject;
import de.dhbw.humbuch.model.entity.TeachingMaterial;
import de.dhbw.humbuch.model.entity.TeachingMaterial.Term;
import de.dhbw.humbuch.viewmodel.BookManagementViewModel;
import de.dhbw.humbuch.viewmodel.BookManagementViewModel.Categories;
import de.dhbw.humbuch.viewmodel.BookManagementViewModel.TeachingMaterialInfo;
import de.dhbw.humbuch.viewmodel.BookManagementViewModel.TeachingMaterials;

/**
 * 
 * @author Martin Wentzel
 * 
 */
public class BookManagementView extends VerticalLayout implements View,
		ViewInformation {
	private static final long serialVersionUID = -5063268947544706757L;

	private BookManagementViewModel bookManagementViewModel;
	private boolean editTeachingMaterial = false;
	private String teachingMaterialProfile = "";
	
	/**
	 * Constants
	 * 
	 */
	private static final String TITLE = "Lehrmittelverwaltung";
	private static final String NEW_TEACHING_MATERIAL = "Neues Lehrmittel";
	private static final String EDIT_TEACHING_MATERIAL = "Lehrmittel bearbeiten";
	private static final String DELETE_TEACHING_MATERIAL = "Lehrmittel löschen";
	private static final String SEARCH_TEACHING_MATERIAL = "Lehrmittel suchen";
	private static final String TABLE_TITLE = "Titel";
	private static final String TABLE_PRODUCER = "Hersteller";
	private static final String TABLE_IDENTIFYER = "Eindeutige Nummer";
	private static final String TABLE_CATEGORY = "Kategorie";
	private static final String TABLE_CLASS_FROM = "von Klasse";
	private static final String TABLE_CLASS_TO = "bis Klasse";
	private static final String TABLE_TERM_FROM = "von Halbjahr";
	private static final Object TABLE_TERM_TO = "bis Halbjahr";
	private static final String TABLE_COMMENT = "Kommentar";
	private static final String TABLE_PROFILE = "Profil";
	private static final String WINDOW_NEW_TEACHING_MATERIAL = "Neues Lehrmittel eintragen";
	private static final String WINDOW_EDIT_TEACHING_MATERIAL = "Lehrmittel editieren";
	private static final String BUTTON_SAVE = "Speichern";
	private static final String BUTTON_CANCEL = "Abbrechen";
	private static final String TEXTFIELD_NAME = "Titel";
	private static final String TEXTFIELD_IDENTIFYER = "Eindeutige Nummer";
	private static final String TEXTFIELD_PRODUCER = "Hersteller/Verlag";
	private static final String TEXTFIELD_FROMGRADE = "von Klassenstufe";
	private static final String TEXTFIELD_TOGRADE = "bis Klassenstufe";
	private static final String TEXTFIELD_COMMENT = "Kommentar";
	private static final String TEXTFIELD_SEARCH_PLACEHOLDER = "Lehrmittel oder Hersteller";
	private static final String TERM = "Halbjahr";
	private static final String CATEGORY = "Kategorie";

	/**
	 * Layout components
	 * 
	 */
	private HorizontalLayout horizontalLayoutButtonBar;

	private TextField textFieldSearchBar = new TextField(SEARCH_TEACHING_MATERIAL);
	private Table tableTeachingMaterials;

	private Button buttonEditTeachingMaterial = new Button(EDIT_TEACHING_MATERIAL);
	private Button buttonNewTeachingMaterial = new Button(NEW_TEACHING_MATERIAL);
	private Button buttonDeleteTeachingMaterial = new Button(DELETE_TEACHING_MATERIAL);

	@BindState(TeachingMaterials.class)
	public final State<Collection<TeachingMaterial>> teachingMaterials = new BasicState<>(Collection.class);
	
	@BindState(TeachingMaterialInfo.class)
	public final State<TeachingMaterial> teachingMaterialInfo = new BasicState<>(TeachingMaterial.class);
	
	@BindState(Categories.class)
	public final State<Map<Integer, Category>> categories = new BasicState<>(Map.class);

	private IndexedContainer containerTable;

	/**
	 * All popup-window components and the corresponding binded states. The
	 * popup-window for adding a new teaching material and editing a teaching
	 * material is the same. Only the caption will be set differently
	 */
	private Window windowEditTeachingMaterial;
	private VerticalLayout verticalLayoutWindowContent;
	private HorizontalLayout horizontalLayoutWindowBar;
	private TextField textFieldTeachingMaterialName = new TextField(TEXTFIELD_NAME);
	private TextField textFieldTeachingMaterialIdentifyer = new TextField(TEXTFIELD_IDENTIFYER);
	private TextField textFieldProducer = new TextField(TEXTFIELD_PRODUCER);
	private TextField textFieldFromGrade = new TextField(TEXTFIELD_FROMGRADE);
	private TextField textFieldToGrade = new TextField(TEXTFIELD_TOGRADE);
	private ComboBox comboBoxProfiles = new ComboBox(TABLE_PROFILE);
	private ComboBox comboBoxFromTerm = new ComboBox(TERM);
	private ComboBox comboBoxToTerm = new ComboBox(TERM);
	private ComboBox comboBoxCategory = new ComboBox(CATEGORY);
	private TextArea textAreaComment = new TextArea(TEXTFIELD_COMMENT);
	private Button buttonWindowSave = new Button(BUTTON_SAVE);
	private Button buttonWindowCancel = new Button(BUTTON_CANCEL);

	/**
	 * 
	 * @param viewModelComposer
	 *            Is injected automatically by Guice
	 * @param bookManagementViewModel
	 *            Is injected automatically by Guice
	 */
	@Inject
	public BookManagementView(ViewModelComposer viewModelComposer,
			BookManagementViewModel bookManagementViewModel) {
		this.bookManagementViewModel = bookManagementViewModel;
		init();
		buildLayout();

		teachingMaterials.addStateChangeListener(new StateChangeListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void stateChange(Object value) {
				containerTable.removeAllItems();
				
				for (TeachingMaterial teachingMaterial : (Collection<TeachingMaterial>) value) {
				// Gets the string representatives of each teaching material profile 
					for (Entry<String, Set<Subject>> entry : Profile.getProfileMap().entrySet()) {
						if (teachingMaterial.getProfile().equals(entry.getValue())) {
							teachingMaterialProfile = entry.getKey().toString();
							break;
						}
					}
					
					tableTeachingMaterials.addItem(
							new Object[] {
									teachingMaterial.getName(),
									teachingMaterial.getProducer(),
									teachingMaterial.getIdentifyingNumber(),
									teachingMaterialProfile,
									teachingMaterial.getFromGrade(),
									teachingMaterial.getFromTerm(),
									teachingMaterial.getToGrade(),
									teachingMaterial.getToTerm(),
									teachingMaterial.getCategory(),
									teachingMaterial.getComment()},
							teachingMaterial.getId());
				}
			}
		});
		
		bindViewModel(viewModelComposer, bookManagementViewModel);
	}
	
	/**
	 * Initializes the components and sets attributes.
	 * 
	 */
	private void init() {
		horizontalLayoutButtonBar = new HorizontalLayout();

		setMargin(true);
		setSpacing(true);

		textFieldSearchBar.setImmediate(true);
		textFieldSearchBar.setTextChangeEventMode(TextChangeEventMode.EAGER);
		
		comboBoxFromTerm.addItem(Term.FIRST);
		comboBoxFromTerm.addItem(Term.SECOND);
		
		comboBoxToTerm.addItem(Term.FIRST);
		comboBoxToTerm.addItem(Term.SECOND);
		
		Map<String, Set<Subject>> profiles = Profile.getProfileMap();
		for (Map.Entry<String, Set<Subject>> profile : profiles.entrySet()) {
			comboBoxProfiles.addItem(profile.getValue());
			comboBoxProfiles.setItemCaption(profile.getValue(),	profile.getKey());
		}

		tableTeachingMaterials = new Table();
		tableTeachingMaterials.setSelectable(true);
		tableTeachingMaterials.setImmediate(true);
		tableTeachingMaterials.setWidth("100%");

		containerTable = new IndexedContainer();
		containerTable.addContainerProperty(TABLE_TITLE, String.class, null);
		containerTable.addContainerProperty(TABLE_PRODUCER, String.class, null);
		containerTable.addContainerProperty(TABLE_IDENTIFYER, String.class,	null);
		containerTable.addContainerProperty(TABLE_PROFILE, String.class, null);
		containerTable.addContainerProperty(TABLE_CLASS_FROM, Integer.class, null);
		containerTable.addContainerProperty(TABLE_TERM_FROM, Term.class, null);
		containerTable.addContainerProperty(TABLE_CLASS_TO, Integer.class, null);
		containerTable.addContainerProperty(TABLE_TERM_TO, Term.class, Term.SECOND);
		containerTable.addContainerProperty(TABLE_CATEGORY, Category.class, null);
		containerTable.addContainerProperty(TABLE_COMMENT, String.class, null);
		tableTeachingMaterials.setContainerDataSource(containerTable);

		windowEditTeachingMaterial = new Window();
		windowEditTeachingMaterial.center();
		windowEditTeachingMaterial.setModal(true);
		windowEditTeachingMaterial.setResizable(false);
		windowEditTeachingMaterial.setDraggable(false);
		verticalLayoutWindowContent = new VerticalLayout();
		verticalLayoutWindowContent.setMargin(true);
		horizontalLayoutWindowBar = new HorizontalLayout();

		this.addListener();
		this.setInputPrompts();
		this.setFormOptions();
	}

	private void setFormOptions() {
		comboBoxCategory.setNullSelectionAllowed(false);
		comboBoxProfiles.setNullSelectionAllowed(false);
		comboBoxFromTerm.setNullSelectionAllowed(false);
		comboBoxToTerm.setNullSelectionAllowed(false);
		textFieldTeachingMaterialIdentifyer.setRequired(true);
		textFieldTeachingMaterialName.setRequired(true);
		textFieldFromGrade.setRequired(true);
		textFieldToGrade.setRequired(true);
		textFieldProducer.setRequired(true);
		comboBoxCategory.setRequired(true);
		comboBoxFromTerm.setRequired(true);
		comboBoxToTerm.setRequired(true);
		comboBoxProfiles.setRequired(true);
	}

	/**
	 * Adds input prompts to textfields and textareas.
	 * 
	 */
	private void setInputPrompts() {
		textFieldSearchBar.setInputPrompt(TEXTFIELD_SEARCH_PLACEHOLDER);
		textFieldTeachingMaterialIdentifyer.setInputPrompt("ISBN");
		textFieldTeachingMaterialName.setInputPrompt("Titel oder Name");
		textFieldProducer.setInputPrompt("z.B. Klett");
		textFieldFromGrade.setInputPrompt("z.B. 5");
		textFieldToGrade.setInputPrompt("z.B. 7");
		textAreaComment.setInputPrompt("Zusätzliche Informationen");
	}

	/**
	 * Adds all listener to their corresponding components.
	 * 
	 */
	private void addListener() {
		/**
		 * Show a confirm popup and deletes the teaching material on confirmation
		 */
		buttonDeleteTeachingMaterial.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 563232762007381515L;
			@Override
			public void buttonClick(ClickEvent event) {
				if(tableTeachingMaterials.getValue() == null) {
					Notification.show("Bitte ein Lehrmittel auswählen");
					return;
				}
				bookManagementViewModel.doFetchTeachingMaterial(Integer	.parseInt(tableTeachingMaterials.getValue().toString()));
				bookManagementViewModel.doDeleteTeachingMaterial(teachingMaterialInfo.get());
			}
		});
		
		/**
		 * Closes the popup window
		 * 
		 */
		buttonWindowCancel.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 4111242410277636186L;

			@Override
			public void buttonClick(ClickEvent event) {
				windowEditTeachingMaterial.close();
			}
		});
		
		/**
		 * Saves the teachingMaterial and closes the popup-window
		 * 
		 */
		buttonWindowSave.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 907615005539877724L;

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				if(FormFieldsValid()) {
					TeachingMaterial teachingMaterial;
					if (!editTeachingMaterial) {
						teachingMaterial = new TeachingMaterial.Builder(categories.get().get((int) comboBoxCategory.getValue()),
								textFieldTeachingMaterialName.getValue(),
								textFieldTeachingMaterialIdentifyer.getValue(), new Date())	.build();
						teachingMaterial.setProducer(textFieldProducer.getValue());
						teachingMaterial.setFromGrade(Integer.parseInt(textFieldFromGrade.getValue()));
						teachingMaterial.setToGrade(Integer.parseInt(textFieldToGrade.getValue()));
						teachingMaterial.setFromTerm((Term) comboBoxFromTerm.getValue());
						teachingMaterial.setToTerm((Term) comboBoxToTerm.getValue());
						teachingMaterial.setComment(textAreaComment.getValue());
						teachingMaterial.setProfile((Set<Subject>) comboBoxProfiles.getValue());
					} else {
						teachingMaterial = teachingMaterialInfo.get();
						teachingMaterial.setName(textFieldTeachingMaterialName.getValue());
						teachingMaterial.setIdentifyingNumber(textFieldTeachingMaterialIdentifyer.getValue());
						teachingMaterial.setProducer(textFieldProducer.getValue());
						teachingMaterial.setFromGrade(Integer.parseInt(textFieldFromGrade.getValue()));
						teachingMaterial.setToGrade(Integer.parseInt(textFieldToGrade.getValue()));
						teachingMaterial.setFromTerm((Term) comboBoxFromTerm.getValue());
						teachingMaterial.setToTerm((Term) comboBoxToTerm.getValue());
						teachingMaterial.setComment(textAreaComment.getValue());
						teachingMaterial.setCategory(categories.get().get((int) comboBoxCategory.getValue()));
						teachingMaterial.setProfile((Set<Subject>) comboBoxProfiles.getValue());
					}

					bookManagementViewModel
							.doUpdateTeachingMaterial(teachingMaterial);
					windowEditTeachingMaterial.close();
				}				
			}
		});

		/**
		 * Provides the live search of the teaching material table by adding a
		 * filter after every keypress in the search field. Currently the
		 * publisher and title search are supported.
		 */
		textFieldSearchBar.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = -1684545652234105334L;

			@Override
			public void textChange(TextChangeEvent event) {
				Filter filter = new Or(new SimpleStringFilter(TABLE_TITLE,
						event.getText(), true, false), new SimpleStringFilter(
						TABLE_PRODUCER, event.getText(), true, false));
				containerTable.removeAllContainerFilters();
				containerTable.addContainerFilter(filter);
			}
		});
		
		/**
		 * Opens the popup-window for adding a new book, empties all fields and
		 * add the categories to the comboBox.
		 */
		buttonNewTeachingMaterial.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -7433701329516481457L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				editTeachingMaterial = false;
				// teachingMaterialInfo.set(new TeachingMaterial());
				clearWindowFields();
				windowEditTeachingMaterial.setCaption(WINDOW_NEW_TEACHING_MATERIAL);
				UI.getCurrent().addWindow(windowEditTeachingMaterial);
				for (Map.Entry<Integer, Category> category : categories.get().entrySet()) {
					comboBoxCategory.addItem(category.getValue().getId());
					comboBoxCategory.setItemCaption(category.getValue().getId(), category.getValue().getName());
				}
			}
		});
		
		/**
		 * Opens the popup-window for editing a book and inserts the data from
		 * the teachingMaterialInfo-State.
		 */
		buttonEditTeachingMaterial.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 563232762007381515L;
			
			@Override
			public void buttonClick(ClickEvent event) {
				if(tableTeachingMaterials.getValue() == null) {
					Notification.show("Bitte ein Lehrmittel auswählen!", Type.HUMANIZED_MESSAGE);
				} else {
					editTeachingMaterial = true;
					windowEditTeachingMaterial.setCaption(WINDOW_EDIT_TEACHING_MATERIAL);
					UI.getCurrent().addWindow(windowEditTeachingMaterial);				
					bookManagementViewModel.doFetchTeachingMaterial(Integer.parseInt(tableTeachingMaterials.getValue().toString()));
					TeachingMaterial teachingMaterial = teachingMaterialInfo.get();
					textFieldTeachingMaterialName.setValue(teachingMaterial.getName());
					textFieldTeachingMaterialIdentifyer.setValue(teachingMaterial.getIdentifyingNumber());
					textFieldProducer.setValue(teachingMaterial.getProducer());
					textFieldFromGrade.setValue(Integer.toString(teachingMaterial.getFromGrade()));
					textFieldToGrade.setValue(Integer.toString(teachingMaterial.getToGrade()));
					textAreaComment.setValue((teachingMaterial.getComment() == null) ? "" : teachingMaterial.getComment() );
					comboBoxFromTerm.setValue(teachingMaterial.getFromTerm());
					comboBoxToTerm.setValue(teachingMaterial.getToTerm());
					
					for (Map.Entry<Integer, Category> category : categories.get().entrySet()) {
						comboBoxCategory.addItem(category.getValue().getId());
						comboBoxCategory.setItemCaption(category.getValue().getId(), category.getValue().getName());
					}
					
					comboBoxCategory.setValue(teachingMaterial.getCategory().getId());
					comboBoxProfiles.setValue(teachingMaterial.getProfile());
				}
			}
		});
	}

	private boolean FormFieldsValid() {
		//Validate if a field is empty
		if(textFieldTeachingMaterialName.getValue() == null
				|| textFieldTeachingMaterialIdentifyer.getValue() == null
				|| textFieldFromGrade.getValue() == null
				|| textFieldProducer.getValue() == null
				|| textFieldToGrade.getValue() == null
				|| comboBoxCategory.getValue() == null
				|| comboBoxProfiles.getValue() == null) {
			Notification.show("Bitte alle Pflichtfelder ausfüllen");
			return false;
		}
		//No field is empty, validate now for right values and lengths
		else {
			if(textFieldTeachingMaterialName.getValue().length() < 2){
				Notification.show("Der Titel muss mindestens 2 Zeichen enthalten");
				return false;
			}
			if(textFieldFromGrade.getValue().length() > 2 
					|| textFieldToGrade.getValue().length() > 2 ){
				Notification.show("Die Klassenstufen dürfen höchstens 2 Zeichen enthalten");
				return false;
			}
			try{
				Integer.parseInt(textFieldToGrade.getValue());
				Integer.parseInt(textFieldFromGrade.getValue());
			} catch(NumberFormatException e) {
				Notification.show("Die Klassenstufen dürfen nur Zahlen enthalten");
				return false;
			}
			return true;
		}
	}

	/**
	 * Builds the layout by adding all components in their specific order.
	 */
	private void buildLayout() {
		horizontalLayoutButtonBar.addComponent(buttonDeleteTeachingMaterial);
		horizontalLayoutButtonBar.setComponentAlignment(buttonDeleteTeachingMaterial, Alignment.MIDDLE_LEFT);
		horizontalLayoutButtonBar.addComponent(buttonNewTeachingMaterial);
		horizontalLayoutButtonBar.setComponentAlignment(buttonNewTeachingMaterial, Alignment.MIDDLE_CENTER);
		horizontalLayoutButtonBar.addComponent(buttonEditTeachingMaterial);
		horizontalLayoutButtonBar.setComponentAlignment(buttonEditTeachingMaterial, Alignment.MIDDLE_RIGHT);

		addComponent(textFieldSearchBar);
		addComponent(tableTeachingMaterials);
		addComponent(horizontalLayoutButtonBar);

		verticalLayoutWindowContent.addComponent(textFieldTeachingMaterialName);
		verticalLayoutWindowContent.addComponent(textFieldTeachingMaterialIdentifyer);
		verticalLayoutWindowContent.addComponent(comboBoxCategory);
		verticalLayoutWindowContent.addComponent(textFieldProducer);
		verticalLayoutWindowContent.addComponent(comboBoxProfiles);
		verticalLayoutWindowContent.addComponent(textFieldFromGrade);
		verticalLayoutWindowContent.addComponent(comboBoxFromTerm);
		verticalLayoutWindowContent.addComponent(textFieldToGrade);
		verticalLayoutWindowContent.addComponent(comboBoxToTerm);
		verticalLayoutWindowContent.addComponent(textAreaComment);

		horizontalLayoutWindowBar.addComponent(buttonWindowCancel);
		horizontalLayoutWindowBar.addComponent(buttonWindowSave);
		verticalLayoutWindowContent.addComponent(horizontalLayoutWindowBar);
		windowEditTeachingMaterial.setContent(verticalLayoutWindowContent);
	}

	/**
	 * Empties the fields of the popup-window to prevent that TextFields already
	 * have content form previous edits.
	 */
	private void clearWindowFields() {
		textFieldTeachingMaterialName.setValue("");
		textFieldTeachingMaterialIdentifyer.setValue("");
		textFieldProducer.setValue("");
		textFieldFromGrade.setValue("");
		textFieldToGrade.setValue("");
		textAreaComment.setValue("");
		textFieldProducer.setValue("");
		comboBoxFromTerm.setValue(Term.FIRST);
		comboBoxToTerm.setValue(Term.SECOND);
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
