package de.dhbw.humbuch.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Table.CellStyleGenerator;
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
import de.dhbw.humbuch.event.ConfirmEvent;
import de.dhbw.humbuch.event.MessageEvent;
import de.dhbw.humbuch.event.MessageEvent.Type;
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.Category;
import de.dhbw.humbuch.model.entity.SettingsEntry;
import de.dhbw.humbuch.viewmodel.SettingsViewModel;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.Categories;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.SettingsEntries;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.SchoolYears;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.ChangeStatus;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.UserEmail;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.UserName;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.DoUpdateUser;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.DoPasswordChange;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.PasswordChangeStatus;

/**
 * Provides a {@link View} to manage the application settings.
 * @author Johannes Idelhauser
 */
@SuppressWarnings({"rawtypes", "serial"})
public class SettingsView extends VerticalLayout implements View, ViewInformation {
	private static final long serialVersionUID = 410136048295487570L;

	private static final String TITLE = "Einstellungen";

	private static final String YEAR_YEAR = "year";
	private static final String YEAR_FROM = "fromDate";
	private static final String YEAR_TO = "toDate";
	private static final String YEAR_END_FIRST = "endFirstTerm";
	private static final String YEAR_BEGIN_SEC = "beginSecondTerm";
	
	private static final String CAT_NAME = "name";
	private static final String CAT_DESCRIPTION = "description";
	
	private static final String SETTINGS_KEY = "settingKey";
	private static final String SETTINGS_VALUE = "settingValue";
	private static final String SETTINGS_STANDARD_VALUE = "settingStandardValue";

	/**
	 * User
	 */
	private TabSheet tabs;
	private Button btnChangePw = new Button("Kennwort ändern...");
	@BindState(UserName.class)
	private State<String> userName = new BasicState<>(String.class);
	@BindState(UserEmail.class)
	private State<String> userEmail = new BasicState<>(String.class);
	private TextField txtUserName = new TextField("Nutzername:");
	private TextField txtUserEmail = new TextField("E-Mail-Adresse: ");
	@BindAction(value = DoUpdateUser.class, source = { "txtUserName", "txtUserEmail" })
	private Button userSaveBtn = new Button("Speichern");
	private Window changePwWindow = new Window();;
	private PasswordField currentPassword = new PasswordField("Aktuelles Passwort:");
	private PasswordField newPassword = new PasswordField("Neues Passwort:");
	private PasswordField newPasswordVerified = new PasswordField("Neues Passwort wiederholen:");
	@BindAction(value = DoPasswordChange.class, source = { "currentPassword", "newPassword", "newPasswordVerified" })
	private Button changePwSave = new Button("Speichern");
	private Button btnChangePwCancel = new Button("Abbrechen");
	@BindState(PasswordChangeStatus.class)
	private State<ChangeStatus> passwordChangeStatus = new BasicState<>(ChangeStatus.class);

	/**
	 * Due dates
	 */
	private Table yearTable;
	private BeanItemContainer<SchoolYear> yearData = new BeanItemContainer<SchoolYear>(SchoolYear.class);
	@BindState(SchoolYears.class)
	private State<Collection<SchoolYear>> schoolYears = new BasicState<>(Collection.class);
	private List<Field> yearFields = new ArrayList<Field>();

	/**
	 * Categories
	 */
	private Table categoryTable = new Table();
	private BeanItemContainer<Category> categoryData = new BeanItemContainer<Category>(Category.class);
	@BindState(Categories.class)
	private State<Collection<Category>> categories = new BasicState<>(Collection.class);
	private List<Field> categoryFields = new ArrayList<Field>();
	
	/**
	 * Settings
	 */
	private Table settingsTable = new Table();
	private BeanItemContainer<SettingsEntry> settingsData = new BeanItemContainer<SettingsEntry>(SettingsEntry.class);
	private List<Field> settingsFields = new ArrayList<Field>();
	@BindState(SettingsEntries.class)
	private State<Collection<SettingsEntry>> settingsEntries = new BasicState<>(Collection.class);

	private EventBus eventBus;
	private SettingsViewModel settingsViewModel;
	

	@Inject
	public SettingsView(ViewModelComposer viewModelComposer, SettingsViewModel settingsViewModel, EventBus eventBus) {
		this.settingsViewModel = settingsViewModel;
		this.eventBus = eventBus;
		init();
		bindViewModel(viewModelComposer, settingsViewModel);
	}

	/**
	 * Initializes the {@link Tabsheet}
	 */
	public void init() {
		tabs = new TabSheet();
		tabs.setSizeFull();

		tabs.addTab(buildUserTab(), "Benutzer");
		tabs.addTab(buildCategoryTab(), "Lehrmittelkategorien");
		tabs.addTab(buildDueDatesTab(), "Schuljahre");
		tabs.addTab(buildSettingsTab(), "Weitere Einstellungen");

		addComponent(tabs);
		setMargin(true);
		setSizeFull();
	}

	/**
	 * Constructs the user tab of the {@link Tabsheet}
	 * @return The {@link Component} with the constructed tab
	 */
	private Component buildUserTab() {
		FormLayout tab = new FormLayout();
		tab.setMargin(true);
		tab.setSpacing(true);

		tab.addComponent(btnChangePw);

		txtUserName.setValue(userName.get());
		tab.addComponent(txtUserName);
		userName.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object arg0) {
				txtUserName.setValue(userName.get());
			}
		});

		txtUserEmail.setValue(userEmail.get());
		tab.addComponent(txtUserEmail);
		userEmail.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object arg0) {
				txtUserEmail.setValue(userEmail.get());
			}
		});

		userSaveBtn.addStyleName("default");
		tab.addComponent(userSaveBtn);

		/*
		 * Change password window
		 */
		VerticalLayout wContent = new VerticalLayout();
		wContent.setMargin(true);
		wContent.setSpacing(true);

		currentPassword.setRequired(true);
		newPassword.setRequired(true);
		newPasswordVerified.setRequired(true);

		wContent.addComponent(currentPassword);
		wContent.addComponent(newPassword);
		wContent.addComponent(newPasswordVerified);

		HorizontalLayout changePwButtons = new HorizontalLayout();
		wContent.addComponent(changePwButtons);
		wContent.setComponentAlignment(changePwButtons, Alignment.MIDDLE_RIGHT);

		changePwSave.addStyleName("default");
		changePwButtons.addComponent(btnChangePwCancel);
		changePwButtons.addComponent(changePwSave);
		changePwButtons.setSpacing(true);

		changePwWindow.setContent(wContent);
		changePwWindow.setCaption("Passwort ändern");
		changePwWindow.center();
		changePwWindow.setClosable(false);
		changePwWindow.setResizable(false);
		changePwWindow.setModal(true);
		
		/**
		 * Opens the password change window
		 */
		btnChangePw.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 5691533989323693312L;

			@Override
			public void buttonClick(ClickEvent event) {
				UI.getCurrent().addWindow(changePwWindow);
				changePwWindow.focus();
			}

		});

		/**
		 * Closes the password change window
		 */
		btnChangePwCancel.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 5691533989323693312L;

			@Override
			public void buttonClick(ClickEvent event) {
				changePwWindow.close();
			}

		});

		/**
		 * Reacts on states from the doPasswordChange state
		 */
		passwordChangeStatus.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object arg0) {
				ChangeStatus status = passwordChangeStatus.get();
				// Clear the password fields
				currentPassword.setValue("");
				newPassword.setValue("");
				newPasswordVerified.setValue("");
				// Close the window if successful
				if (status == ChangeStatus.SUCCESSFULL) {
					changePwWindow.close();
				}
			}
		});
		return tab;
	}

	/**
	 * Constructs the tab to manage categories
	 * @return The {@link Component} with the constructed tab
	 */
	private Component buildCategoryTab() {

		VerticalLayout tab = new VerticalLayout();
		tab.setMargin(true);
		tab.setSpacing(true);
		tab.setSizeFull();
		
		final HorizontalLayout editButtons = new HorizontalLayout();
		editButtons.setSpacing(true);
		

		final Button btnEdit = new Button("Bearbeiten");
		final Button btnCancel = new Button("Abbrechen");
		final Button btnSave = new Button("Speichern");

		btnEdit.setEnabled(false);
		btnSave.setVisible(false);
		btnSave.addStyleName("default");
		btnCancel.setVisible(false);

		editButtons.addComponent(btnEdit);
		editButtons.addComponent(btnCancel);
		editButtons.addComponent(btnSave);

		final HorizontalLayout addButtons = new HorizontalLayout();
		addButtons.setSpacing(true);
		
		final Button btnAdd = new Button("Hinzufügen");
		final Button btnDelete = new Button("Löschen");
		
		btnDelete.setEnabled(false);
		
		addButtons.addComponent(btnAdd);
		addButtons.addComponent(btnDelete);

		tab.addComponent(new HorizontalLayout() {
			{
				setWidth("100%");
				setHeight(null);
				addComponent(addButtons);
				addComponent(editButtons);
				setComponentAlignment(editButtons, Alignment.MIDDLE_RIGHT);
			}
		});

		// Table
		categoryTable.setSizeFull();
		categoryTable.setSelectable(true);
		categoryTable.setImmediate(true);

		categoryTable.setContainerDataSource(categoryData);
		categoryTable.setVisibleColumns(new Object[] { CAT_NAME, CAT_DESCRIPTION });
		categoryTable.setColumnHeader(CAT_NAME, "Kategorie");
		categoryTable.setColumnHeader(CAT_DESCRIPTION, "Beschreibung");

		tab.addComponent(categoryTable);
		tab.setExpandRatio(categoryTable, 1);

		/*
		 * Listeners
		 */

		// Enable/Disable edit button
		categoryTable.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				Category item = (Category) categoryTable.getValue();
				btnEdit.setEnabled(item != null);
				btnDelete.setEnabled(item != null);
			}
		});

		// Edit
		btnEdit.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				configureEditable(categoryTable, new Button[]{btnSave, btnCancel}, new Button[]{btnEdit}, categoryFields, true);
			}
		});

		// Cancel
		btnCancel.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				discardFields(categoryFields);
				configureEditable(categoryTable, new Button[]{btnSave, btnCancel}, new Button[]{btnEdit}, categoryFields, false);
				categories.notifyAllListeners();
			}
		});

		// Save
		btnSave.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				commitFields(categoryFields);
				Category item = (Category) categoryTable.getValue();
				configureEditable(categoryTable, new Button[]{btnSave, btnCancel}, new Button[]{btnEdit}, categoryFields, false);
				settingsViewModel.doUpdateCategory(item);
			}
		});

		// Add
		btnAdd.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Category item = new Category.Builder("").description("")
						.build();
				categoryTable.addItem(item);
				categoryTable.select(item);
				configureEditable(categoryTable, new Button[]{btnSave, btnCancel}, new Button[]{btnEdit}, categoryFields, true);
			}
		});

		// Delete
		btnDelete.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Runnable confirmRunnable = new Runnable() {
					@Override
					public void run() {
						Category item = (Category) categoryTable.getValue();
						settingsViewModel.doDeleteCategory(item);
						configureEditable(categoryTable, new Button[]{btnSave, btnCancel}, new Button[]{btnEdit}, categoryFields, false);
						categoryTable.select(null);
					}
				};
				eventBus.post(new ConfirmEvent.Builder("Wollen Sie diese Kategorie wirklich löschen?")
					.caption("Löschen").confirmRunnable(confirmRunnable).build());
			}
		});

		// Fill table container
		categories.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object arg0) {
				categoryTable.removeAllItems();
				categoryData.addAll(categories.get());
			}
		});

		// Double click on a row: make it editable
		categoryTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent itemClickEvent) {
				if (itemClickEvent.isDoubleClick() && !categoryTable.isEditable()) {
					categoryTable.setValue(itemClickEvent.getItemId());
					configureEditable(categoryTable, new Button[]{btnSave, btnCancel}, new Button[]{btnEdit}, categoryFields, true);
				}
			}
		});

		// define field factory
		categoryTable.setTableFieldFactory(new DefaultFieldFactory() {
			private static final long serialVersionUID = 1L;
			@Override
			public Field<?> createField(Container container, Object itemId,Object propertyId, Component uiContext) {
				//if its not the currently selected item in the table, don't
				// generate fields
				if (!itemId.equals(categoryTable.getValue())) {
					return null;
				}
				TextField field = new TextField();
				field.setNullRepresentation("");
				//discard the value
				field.setBuffered(true);
				//keep track of all of the attached fields
				field.addAttachListener(new AttachListener() {
					@Override
					public void attach(AttachEvent attachEvent) {
						categoryFields.add((Field) attachEvent.getConnector());
					}
				});
				field.addDetachListener(new DetachListener() {
					@Override
					public void detach(DetachEvent event) {
						categoryFields.remove((Field) event.getConnector());
					}
				});
				return field;
			}

		});
		return tab;
	}


	/**
	 * Constructs the tab to manage school years
	 * @return The {@link Component} with the constructed tab
	 */
	private Component buildDueDatesTab() {

		VerticalLayout tab = new VerticalLayout();
		tab.setSizeFull();
		tab.setMargin(true);
		tab.setSpacing(true);

		//Edit buttons
		final HorizontalLayout editButtons = new HorizontalLayout();
		editButtons.setSpacing(true);

		final Button btnEdit = new Button("Bearbeiten");
		final Button btnCancel = new Button("Abbrechen");
		final Button btnSave = new Button("Speichern");
		
		btnEdit.setEnabled(false);
		btnSave.setVisible(false);
		btnSave.addStyleName("default");
		btnCancel.setVisible(false);

		editButtons.addComponent(btnEdit);
		editButtons.addComponent(btnCancel);
		editButtons.addComponent(btnSave);

		//Add/Delete buttons
		final HorizontalLayout addButtons = new HorizontalLayout();
		addButtons.setSpacing(true);
		
		final Button btnAdd = new Button("Hinzufügen");
		final Button btnDelete = new Button("Löschen");
		
		btnDelete.setEnabled(false);
		
		addButtons.addComponent(btnAdd);
		addButtons.addComponent(btnDelete);

		tab.addComponent(new HorizontalLayout() {
			{
				setWidth("100%");
				setHeight(null);
				addComponent(addButtons);
				addComponent(editButtons);
				setComponentAlignment(editButtons, Alignment.MIDDLE_RIGHT);
			}
		});
		
		yearTable = new Table() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				if (!colId.equals(YEAR_YEAR)) {
					SimpleDateFormat df = new SimpleDateFormat();
					df.applyPattern("dd.MM.yyyy");
					if (property.getValue() == null) {
						return null;
					} else {
						return df
								.format(((Date) property.getValue()).getTime());
					}
				}
				return super.formatPropertyValue(rowId, colId, property);
			}
		};

		yearTable.setSizeFull();
		yearTable.setSelectable(true);
		yearTable.setImmediate(true);

		yearTable.setContainerDataSource(yearData);

		yearTable.setVisibleColumns(new Object[] { YEAR_YEAR, YEAR_FROM,
				YEAR_TO, YEAR_END_FIRST, YEAR_BEGIN_SEC });
		yearTable.setColumnHeader(YEAR_YEAR, "Schuljahr");
		yearTable.setColumnHeader(YEAR_FROM, "Beginn");
		yearTable.setColumnHeader(YEAR_TO, "Ende");
		yearTable.setColumnHeader(YEAR_END_FIRST, "Ende 1. Halbjahr");
		yearTable.setColumnHeader(YEAR_BEGIN_SEC, "Begin 2. Halbjahr");

		yearTable.setColumnAlignment(YEAR_FROM, Table.Align.RIGHT);
		yearTable.setColumnAlignment(YEAR_TO, Table.Align.RIGHT);
		yearTable.setColumnAlignment(YEAR_END_FIRST, Table.Align.RIGHT);
		yearTable.setColumnAlignment(YEAR_BEGIN_SEC, Table.Align.RIGHT);

		tab.addComponent(yearTable);
		tab.setExpandRatio(yearTable, 1);

		/*
		 * Listeners
		 */
		// Enable/Disable edit + delete button
		yearTable.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				SchoolYear item = (SchoolYear) yearTable.getValue();
				btnEdit.setEnabled(item != null);
				btnDelete.setEnabled(item != null);
			}
		});

		// Edit
		btnEdit.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				configureEditable(yearTable, new Button[]{btnCancel, btnSave}, new Button[]{btnEdit}, yearFields, true);

			}
		});

		// Cancel
		btnCancel.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				discardFields(yearFields);
				configureEditable(yearTable, new Button[]{btnCancel, btnSave}, new Button[]{btnEdit}, yearFields, false);
				schoolYears.notifyAllListeners();
			}
		});

		// Save
		btnSave.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				commitFields(yearFields);
				SchoolYear item = (SchoolYear) yearTable.getValue();
				if (item.getYear().isEmpty() || item.getFromDate() == null || item.getToDate() == null || item.getBeginSecondTerm() == null || item.getEndFirstTerm() == null) {
					eventBus.post(new MessageEvent("Speichern nicht möglich!",
							"Alle Felder müssen ausgefüllt sein.", Type.WARNING));
				} else if (!item.getFromDate().before(item.getEndFirstTerm())) {
					eventBus.post(new MessageEvent("Speichern nicht möglich!",
							"Das Anfangsdatum muss vor dem Ende des 1. Halbjahres liegen.", Type.WARNING));
				} else if (!item.getEndFirstTerm().before(item.getBeginSecondTerm())) {
					eventBus.post(new MessageEvent("Speichern nicht möglich!",
							"Das Enddatum des 1. Halbjahres muss vor dem Anfangsdatum des 2. Halbjahres liegen.", Type.WARNING));
				} else if (!item.getBeginSecondTerm().before(item.getToDate())) {
					eventBus.post(new MessageEvent("Speichern nicht möglich!",
							"Das Anfangsdatum des 2. Halbjahres muss vor dem Enddatum des gesamten Schuljahres liegen.", Type.WARNING));
				} else {
					configureEditable(yearTable, new Button[]{btnCancel, btnSave}, new Button[]{btnEdit}, yearFields, false);
					settingsViewModel.doUpdateSchoolYear(item);
				}
			}
		});

		// Add
		btnAdd.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				SchoolYear item = new SchoolYear.Builder("", new Date(),
						new Date()).build();
				yearData.addBean(item);
				yearTable.select(item);
				configureEditable(yearTable, new Button[]{btnCancel, btnSave}, new Button[]{btnEdit}, yearFields, true);
			}
		});

		// Delete
		btnDelete.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Runnable confirmRunnable = new Runnable() {
					@Override
					public void run() {
						SchoolYear item = (SchoolYear) yearTable.getValue();
						settingsViewModel.doDeleteSchoolYear(item);
						configureEditable(yearTable, new Button[]{btnCancel, btnSave}, new Button[]{btnEdit}, yearFields, false);
						yearTable.select(null);
					}
				};
				eventBus.post(new ConfirmEvent.Builder("Wollen Sie dieses Schuljahr wirklich löschen?")
					.caption("Löschen").confirmRunnable(confirmRunnable).build());
			}
		});

		// Fill table container
		schoolYears.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object arg0) {
				yearTable.removeAllItems();
				yearData.addAll(schoolYears.get());
			}
		});

		// Double click on a row: make it editable
		yearTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent itemClickEvent) {

				if (itemClickEvent.isDoubleClick() && !yearTable.isEditable()) {
					yearTable.setValue(itemClickEvent.getItemId());
					configureEditable(yearTable, new Button[]{btnCancel, btnSave}, new Button[]{btnEdit}, yearFields, true);
				}
			}
		});

		// define field factory
		yearTable.setTableFieldFactory(new DefaultFieldFactory() {
			private static final long serialVersionUID = 1L;
			@Override
			public Field<?> createField(Container container, Object itemId,
					Object propertyId, Component uiContext) {
				// If its not the currently selected item in the table, don't
				// generate fields
				if (!itemId.equals(yearTable.getValue())) {
					return null;
				}
				Field field = super.createField(container, itemId, propertyId,uiContext);
				// Possibility to discard the value
				field.setBuffered(true);
				// keep track of all of the attached fields
				field.addAttachListener(new AttachListener() {
					@Override
					public void attach(AttachEvent attachEvent) {
						yearFields.add((Field) attachEvent.getConnector());
					}
				});
				field.addDetachListener(new DetachListener() {
					@Override
					public void detach(DetachEvent event) {
						yearFields.remove((Field) event.getConnector());
					}
				});
				return field;
			}
		});

		return tab;
	}

	
	/**
	 * Creates the general settings tab
	 * @return A Component with the generated tab
	 */
	private Component buildSettingsTab() {
		/*
		 * Initialize the tab
		 */
		final VerticalLayout tab = new VerticalLayout();
		tab.setMargin(true);
		tab.setSpacing(true);
		tab.setSizeFull();
		
		/*
		 * Define edit, save and cancel buttons
		 */
		final HorizontalLayout editButtons = new HorizontalLayout();
		editButtons.setSpacing(true);
		tab.addComponent(editButtons);
		tab.setComponentAlignment(editButtons, Alignment.MIDDLE_RIGHT);
		
		final Button btnEdit = new Button("Bearbeiten");
		final Button btnSave = new Button("Speichern");
		final Button btnCancel = new Button("Abbrechen");
		
		editButtons.addComponent(btnEdit);
		editButtons.addComponent(btnCancel);
		editButtons.addComponent(btnSave);
		
		btnEdit.setEnabled(false);
		btnSave.setVisible(false);
		btnSave.addStyleName("default");
		btnCancel.setVisible(false);

		/*
		 * Define table
		 */
		settingsTable.setSizeFull();
		settingsTable.setSelectable(true);
		settingsTable.setImmediate(true);

		settingsTable.setContainerDataSource(settingsData);
		settingsTable.setVisibleColumns(new Object[] { SETTINGS_KEY, SETTINGS_VALUE, SETTINGS_STANDARD_VALUE });
		settingsTable.setColumnHeader(SETTINGS_KEY, "Schlüssel");
		settingsTable.setColumnHeader(SETTINGS_VALUE, "Wert");
		settingsTable.setColumnHeader(SETTINGS_STANDARD_VALUE, "Standardwert");
		
		tab.addComponent(settingsTable);
		tab.setExpandRatio(settingsTable, 1);
		
		/*
		 * Define listeners and factories
		 */
		//Warning that is displayed when settings tab is accessed
		tabs.addSelectedTabChangeListener(new SelectedTabChangeListener() {
			
            boolean preventEvent = false;
            
			@Override
			public void selectedTabChange(SelectedTabChangeEvent event) {
                if (preventEvent) {
                    preventEvent = false;
                    return;
                }
				
				final TabSheet source = (TabSheet) event.getSource();
				
				if(source.getSelectedTab() == tab) {
					source.setSelectedTab(0);
					
					Runnable runnable = new Runnable() {
						@Override
						public void run() {
							preventEvent = true;
							source.setSelectedTab(tab);
						}
					};
					eventBus.post(new ConfirmEvent.Builder(
							"Änderungen der Standardwerte können unter Umständen dazu führen, dass die Anwendung<br>" 
							+ "nicht mehr wie erwartet funktioniert. Sie sollten nur fortfahren, wenn Sie genau wissen,<br>"
							+ "was Sie tun.")
							.caption("Achtung, hier endet möglicherweise die Gewährleistung!")
							.cancelCaption("Abbrechen")
							.confirmCaption("Ich werde vorsichtig sein, versprochen!").confirmRunnable(runnable)
							.build());
				}
			}
		});
		
		//Print cell bold if not equal to standard value
		settingsTable.setCellStyleGenerator(new CellStyleGenerator() {
			@Override
			public String getStyle(Table source, Object itemId, Object propertyId) {
				if (!source.getItem(itemId).getItemProperty(SETTINGS_VALUE).getValue()
						.equals(source.getItem(itemId).getItemProperty(SETTINGS_STANDARD_VALUE).getValue())) {
					if(SETTINGS_VALUE.equals(propertyId))
						return "bold";
				}
				return null;
			}
		});
		
		//Edit button listeners
		btnEdit.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				configureEditable(settingsTable, new Button[]{btnSave, btnCancel}, new Button[]{btnEdit}, settingsFields, true);
			}
		});

		//Cancel button listeners
		btnCancel.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				discardFields(settingsFields);
				configureEditable(settingsTable, new Button[]{btnSave, btnCancel}, new Button[]{btnEdit}, settingsFields, false);
			}
		});

		//Save button listeners
		btnSave.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				commitFields(settingsFields);
				SettingsEntry item = (SettingsEntry) settingsTable.getValue();
				configureEditable(settingsTable, new Button[]{btnSave, btnCancel}, new Button[]{btnEdit}, settingsFields, false);
				settingsViewModel.doUpdateSettingsEntry(item);
				
			}
		});
		
		//Enable/Disable edit button
		settingsTable.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				SettingsEntry item = (SettingsEntry) settingsTable.getValue();
				btnEdit.setEnabled(item != null);
			}
		});
		
		//Double click on a row to make it editable
		settingsTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent itemClickEvent) {
				if (itemClickEvent.isDoubleClick() && !settingsTable.isEditable()) {
					settingsTable.setValue(itemClickEvent.getItemId());
					configureEditable(settingsTable, new Button[]{btnSave, btnCancel}, new Button[]{btnEdit}, settingsFields, true);
				}
			}
		});

		// Fill table container
		settingsEntries.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object arg0) {
				settingsTable.removeAllItems();
				settingsData.addAll(settingsEntries.get());
			}
		});

		// define field factory
		settingsTable.setTableFieldFactory(new DefaultFieldFactory() {
			private static final long serialVersionUID = 1L;
			@Override
			public Field<?> createField(Container container, Object itemId,
					Object propertyId, Component uiContext) {
				//if its not the currently selected item in the table, don't
				// generate fields
				if (!itemId.equals(settingsTable.getValue())) {
					return null;
				}
				if(!SETTINGS_VALUE.equals(propertyId)) {
					return null;
				}
				TextField field = new TextField();
				field.setNullRepresentation("");
				//discard the value
				field.setBuffered(true);
				//keep track of all of the attached fields
				field.addAttachListener(new AttachListener() {
					@Override
					public void attach(AttachEvent attachEvent) {
						settingsFields.add((Field) attachEvent.getConnector());
					}
				});
				field.addDetachListener(new DetachListener() {
					@Override
					public void detach(DetachEvent event) {
						settingsFields.remove((Field) event.getConnector());
					}
				});
				return field;
			}
		});
		return tab;
	}
	

	/**
	 * Configures the given table to be editable or not and changes the buttons visibility accordingly.
	 * @param table The {@link Table} to be configured
	 * @param editButtons {@link Button}s to be displayed when the table is editable
	 * @param nonEditButtons {@link Button}s to be displayed when the table is not editable
	 * @param fields {@link List} with generated {@link Field}s in row to edit
	 * @param editable Whether the table should be editable or not
	 */
	public void configureEditable(Table table, Button[] editButtons,
			Button[] nonEditButtons, List<Field> fields, boolean editable) {
		//Set the table editable
		table.setSelectable(!editable);
		table.setEditable(editable);
		
		//Switch visibility of buttons
		for (Button btn : editButtons) {
			btn.setVisible(editable);
		}
		for (Button btn : nonEditButtons) {
			btn.setVisible(!editable);
		}
		
		//Set focus to the first field
		if (editable && !fields.isEmpty()) {
			fields.get(0).focus();
		}
	}
	
	
	/**
	 * Discard any field edits
	 * @param fields {@link List} of fields
	 */
	protected void discardFields(List<Field> fields) {
		for (Field field : fields) {
			field.discard();
		}
	}
	
	
	/**
	 * Commit all field edits and handle handle validation problems here
	 * @param fields {@link List} of fields to be committed
	 */
	protected void commitFields(List<Field> fields) {
		for (Field field : fields) {
			field.commit();
		}
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
	public void enter(ViewChangeEvent event) {
		settingsViewModel.refresh();
		tabs.setSelectedTab(0);
	}

	@Override
	public String getTitle() {
		return TITLE;
	}

}