package de.dhbw.humbuch.view;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

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
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
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
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.Category;
import de.dhbw.humbuch.viewmodel.SettingsViewModel;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.Categories;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.SchoolYears;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.ChangeStatus;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.UserEmail;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.UserName;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.DoUpdateUser;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.DoPasswordChange;
import de.dhbw.humbuch.viewmodel.SettingsViewModel.PasswordChangeStatus;

public class SettingsView extends VerticalLayout implements View,
		ViewInformation {
	private static final long serialVersionUID = 410136048295487570L;

	private static final String TITLE = "Einstellungen";

	private static final String YEAR_YEAR = "year";
	private static final String YEAR_FROM = "fromDate";
	private static final String YEAR_TO = "toDate";
	private static final String YEAR_END_FIRST = "endFirstTerm";
	private static final String YEAR_BEGIN_SEC = "beginSecondTerm";
	private static final String CAT_NAME = "name";
	private static final String CAT_DESCRIPTION = "description";

	private SettingsViewModel settingsViewModel;

	/**
	 * User
	 */
	private TabSheet tabs;
	private FormLayout tabUser = new FormLayout();
	private Button userChangePw = new Button("Kennwort ändern...");
	@BindState(UserName.class)
	private State<String> userName = new BasicState<>(String.class);
	@BindState(UserEmail.class)
	private State<String> userEmail = new BasicState<>(String.class);
	private TextField txtUserName = new TextField("Nutzername:");
	private TextField txtUserEmail = new TextField("E-Mail-Adresse: ");
	@BindAction(value = DoUpdateUser.class, source = { "txtUserName",
			"txtUserEmail" })
	private Button userSaveBtn = new Button("Speichern");

	private Window changePwWindow = new Window();;
	private PasswordField currentPassword = new PasswordField(
			"Aktuelles Passwort:");
	private PasswordField newPassword = new PasswordField("Neues Passwort:");
	private PasswordField newPasswordVerified = new PasswordField(
			"Neues Passwort wiederholen:");
	@BindAction(value = DoPasswordChange.class, source = { "currentPassword",
			"newPassword", "newPasswordVerified" })
	private Button changePwSave = new Button("Speichern");
	private Button changePwCancel = new Button("Abbrechen");
	@BindState(PasswordChangeStatus.class)
	private State<ChangeStatus> passwordChangeStatus = new BasicState<>(
			ChangeStatus.class);

	/**
	 * Due dates
	 */
	private Table yearTable;
	private BeanItemContainer<SchoolYear> yearData = new BeanItemContainer<SchoolYear>(
			SchoolYear.class);
	@BindState(SchoolYears.class)
	private State<Collection<SchoolYear>> schoolYears = new BasicState<>(
			Collection.class);
	private Button yearAdd = new Button("Hinzufügen");
	private Button yearDelete = new Button("Löschen");
	private Button yearEdit = new Button("Bearbeiten");
	private Button yearCancel = new Button("Abbrechen");
	private Button yearSave = new Button("Speichern");
	@SuppressWarnings("rawtypes")
	private List<Field> yearFields = new ArrayList<Field>();

	/**
	 * Categories
	 */
	private Table catTable = new Table();
	private BeanItemContainer<Category> catData = new BeanItemContainer<Category>(
			Category.class);
	@BindState(Categories.class)
	private State<Collection<Category>> categories = new BasicState<>(
			Collection.class);
	private Button catAdd = new Button("Hinzufügen");
	private Button catDelete = new Button("Löschen");
	private Button catEdit = new Button("Bearbeiten");
	private Button catCancel = new Button("Abbrechen");
	private Button catSave = new Button("Speichern");
	@SuppressWarnings("rawtypes")
	private List<Field> catFields = new ArrayList<Field>();

	@Inject
	public SettingsView(ViewModelComposer viewModelComposer,
			SettingsViewModel settingsViewModel) {
		this.settingsViewModel = settingsViewModel;
		init();
		bindViewModel(viewModelComposer, settingsViewModel);
	}

	public void init() {
		tabs = new TabSheet();
		tabs.setSizeFull();

		buildUserTab();
		buildCategoryTab();
		buildDueDatesTab();

		addListeners();
		addComponent(tabs);
		setMargin(true);
		setSizeFull();
	}

	/**
	 * User tab
	 */
	private void buildUserTab() {
		tabUser.setMargin(true);
		tabUser.setSpacing(true);

		tabUser.addComponent(userChangePw);

		txtUserName.setValue(userName.get());
		tabUser.addComponent(txtUserName);
		userName.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object arg0) {
				txtUserName.setValue(userName.get());
			}
		});

		txtUserEmail.setValue(userEmail.get());
		tabUser.addComponent(txtUserEmail);
		userEmail.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object arg0) {
				txtUserEmail.setValue(userEmail.get());
			}
		});

		userSaveBtn.addStyleName("default");
		tabUser.addComponent(userSaveBtn);

		// Change password window
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
		changePwButtons.addComponent(changePwCancel);
		changePwButtons.addComponent(changePwSave);

		changePwWindow.setContent(wContent);
		changePwWindow.center();
		changePwWindow.setClosable(false);
		changePwWindow.setResizable(false);
		changePwWindow.setModal(true);
		// <----------------------------------------

		tabs.addTab(tabUser, "Benutzer");
	}

	/**
	 * Categories
	 */
	@SuppressWarnings("serial")
	private void buildCategoryTab() {

		VerticalLayout tabCategories = new VerticalLayout();
		tabCategories.setMargin(true);
		tabCategories.setSpacing(true);
		tabCategories.setSizeFull();

		// Table
		catTable.setSizeFull();
		catTable.setSelectable(true);
		catTable.setImmediate(true);

		catTable.setContainerDataSource(catData);
		catTable.setVisibleColumns(new Object[] { CAT_NAME, CAT_DESCRIPTION });
		catTable.setColumnHeader(CAT_NAME, "Kategorie");
		catTable.setColumnHeader(CAT_DESCRIPTION, "Beschreibung");

		tabCategories.addComponent(catTable);
		tabCategories.setExpandRatio(catTable, 1);
		
		final HorizontalLayout catEditButtons = new HorizontalLayout();
		catEditButtons.setSpacing(true);

		catDelete.setEnabled(false);
		catEdit.setEnabled(false);
		catSave.setVisible(false);
		catSave.addStyleName("default");
		catCancel.setVisible(false);

		catEditButtons.addComponent(catEdit);
		catEditButtons.addComponent(catCancel);
		catEditButtons.addComponent(catSave);

		final HorizontalLayout catAddButtons = new HorizontalLayout();
		catAddButtons.setSpacing(true);
		catAddButtons.addComponent(catAdd);
		catAddButtons.addComponent(catDelete);

		tabCategories.addComponent(new HorizontalLayout() {
			{
				setWidth("100%");
				setHeight(null);
				addComponent(catAddButtons);
				addComponent(catEditButtons);
				setComponentAlignment(catEditButtons, Alignment.MIDDLE_RIGHT);
			}
		});
		
		tabs.addTab(tabCategories, "Lehrmittelkategorien");

		/*
		 * Listeners
		 */

		// Enable/Disable edit button
		catTable.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				Category item = (Category) catTable.getValue();
				catEdit.setEnabled(item != null);
				catDelete.setEnabled(item != null);
			}
		});

		// Edit
		catEdit.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				catConfigureEditable(true);
			}
		});

		// Cancel
		catCancel.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				catDiscard();
				catConfigureEditable(false);
			}
		});

		// Save
		catSave.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				catCommit();
				Category item = (Category) catTable.getValue();
				catConfigureEditable(false);
				settingsViewModel.doUpdateCategory(item);
			}
		});

		// Add
		catAdd.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Category item = new Category.Builder("").description("")
						.build();
				catTable.addItem(item);
				catTable.select(item);
				catConfigureEditable(true);
				catCancel.setVisible(false);
			}
		});

		// Delete
		catDelete.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Category item = (Category) catTable.getValue();
				settingsViewModel.doDeleteCategory(item);
				catConfigureEditable(false);
				catTable.select(null);
				
			}
		});

		// Fill table container
		categories.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object arg0) {
				catTable.removeAllItems();
				catData.addAll(categories.get());
			}
		});

		// Double click on a row: make it editable
		catTable.addItemClickListener(new ItemClickEvent.ItemClickListener() {
			@Override
			public void itemClick(ItemClickEvent itemClickEvent) {
				if (itemClickEvent.isDoubleClick() && !catTable.isEditable()) {
					catTable.setValue(itemClickEvent.getItemId());
					catConfigureEditable(true);
				}
			}
		});

		// define field factory
		catTable.setTableFieldFactory(new DefaultFieldFactory() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("rawtypes")
			@Override
			public Field<?> createField(Container container, Object itemId,
					Object propertyId, Component uiContext) {

				// If its not the currently selected item in the table, don't
				// generate fields
				if (!itemId.equals(catTable.getValue())) {
					return null;
				}

				Field field = super.createField(container, itemId, propertyId,
						uiContext);

				// Let us discard the value
				field.setBuffered(true);

				// Let's keep track of all of the attached fields
				field.addAttachListener(new AttachListener() {
					@Override
					public void attach(AttachEvent attachEvent) {
						catFields.add((Field) attachEvent.getConnector());
					}
				});
				field.addDetachListener(new DetachListener() {
					@Override
					public void detach(DetachEvent event) {
						catFields.remove((Field) event.getConnector());
					}
				});

				return field;

			}

		});

	}

	/**
	 * Commit all field edits.
	 * 
	 * NB: Should handle validation problems here
	 */
	@SuppressWarnings("rawtypes")
	protected void catCommit() {
		for (Field field : catFields) {
			field.commit();
		}
	}

	/**
	 * Discard any field edits
	 */
	@SuppressWarnings("rawtypes")
	protected void catDiscard() {
		for (Field field : catFields) {
			field.discard();
		}
	}

	/**
	 * Configure the categories-table for edit (or not)
	 * 
	 * @param editable
	 *            Whether the table should be editable or not
	 */
	public void catConfigureEditable(boolean editable) {
		catTable.setSelectable(!editable);
		catTable.setEditable(editable);
		catSave.setVisible(editable);
		catCancel.setVisible(editable);
		catEdit.setVisible(!editable);
		if (editable && !catFields.isEmpty()) {
			catFields.get(0).focus();
		}
	}

	/**
	 * Due dates
	 */
	@SuppressWarnings("serial")
	private void buildDueDatesTab() {

		VerticalLayout tabDates = new VerticalLayout();
		tabDates.setSizeFull();
		tabDates.setMargin(true);
		tabDates.setSpacing(true);

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

		tabDates.addComponent(yearTable);
		tabDates.setExpandRatio(yearTable, 1);
		
		final HorizontalLayout yearEditButtons = new HorizontalLayout();
		yearEditButtons.setSpacing(true);
		
		yearDelete.setEnabled(false);
		yearEdit.setEnabled(false);
		yearSave.setVisible(false);
		yearSave.addStyleName("default");
		yearCancel.setVisible(false);

		yearEditButtons.addComponent(yearEdit);
		yearEditButtons.addComponent(yearCancel);
		yearEditButtons.addComponent(yearSave);

		final HorizontalLayout yearAddButtons = new HorizontalLayout();
		yearAddButtons.setSpacing(true);
		yearAddButtons.addComponent(yearAdd);
		yearAddButtons.addComponent(yearDelete);

		tabDates.addComponent(new HorizontalLayout() {
			{
				setWidth("100%");
				setHeight(null);
				addComponent(yearAddButtons);
				addComponent(yearEditButtons);
				setComponentAlignment(yearEditButtons, Alignment.MIDDLE_RIGHT);
			}
		});
		
		tabs.addTab(tabDates, "Schuljahresdaten");

		/*
		 * Listeners
		 */

		// Enable/Disable edit + delete button
		yearTable.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				SchoolYear item = (SchoolYear) yearTable.getValue();
				yearEdit.setEnabled(item != null);
				yearDelete.setEnabled(item != null);
			}
		});

		// Edit
		yearEdit.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				yearConfigureEditable(true);

			}
		});

		// Cancel
		yearCancel.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				yearDiscard();
				yearConfigureEditable(false);
			}
		});

		// Save
		yearSave.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				yearCommit();
				yearConfigureEditable(false);
				SchoolYear item = (SchoolYear) yearTable.getValue();
				settingsViewModel.doUpdateSchoolYear(item);
			}
		});

		// Add
		yearAdd.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				SchoolYear item = new SchoolYear.Builder("", new Date(),
						new Date()).build();
				yearData.addBean(item);
				yearTable.select(item);
				yearConfigureEditable(true);
				yearCancel.setVisible(false);
			}
		});

		// Delete
		yearDelete.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				SchoolYear item = (SchoolYear) yearTable.getValue();
				settingsViewModel.doDeleteSchoolYear(item);
				yearConfigureEditable(false);
				yearTable.select(null);
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
					yearConfigureEditable(true);
				}
			}
		});

		// define field factory
		yearTable.setTableFieldFactory(new DefaultFieldFactory() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("rawtypes")
			@Override
			public Field<?> createField(Container container, Object itemId,
					Object propertyId, Component uiContext) {

				// If its not the currently selected item in the table, don't
				// generate fields
				if (!itemId.equals(yearTable.getValue())) {
					return null;
				}

				Field field = super.createField(container, itemId, propertyId,
						uiContext);

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
	}

	/**
	 * Commit all field edits.
	 * 
	 * NB: Should handle validation problems here
	 */
	@SuppressWarnings("rawtypes")
	protected void yearCommit() {
		for (Field field : yearFields) {
			field.commit();
		}
	}

	/**
	 * Discard any field edits
	 */
	@SuppressWarnings("rawtypes")
	protected void yearDiscard() {
		for (Field field : yearFields) {
			field.discard();
		}
	}

	/**
	 * Configure the year-table for edit (or not)
	 * 
	 * @param editable
	 *            Whether the table should be editable or not
	 */
	public void yearConfigureEditable(boolean editable) {
		yearTable.setSelectable(!editable);
		yearTable.setEditable(editable);
		yearSave.setVisible(editable);
		yearCancel.setVisible(editable);
		yearEdit.setVisible(!editable);
		if (editable && !yearFields.isEmpty()) {
			yearFields.get(0).focus();
		}
	}

	/**
	 * Adds all Listeners for the components
	 */
	private void addListeners() {

		/**
		 * Opens the password change window
		 */
		userChangePw.addClickListener(new ClickListener() {
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
		changePwCancel.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 5691533989323693312L;

			@Override
			public void buttonClick(ClickEvent event) {
				changePwWindow.close();
			}

		});

		/**
		 * Reacts on states from the doPasswordChange
		 */
		passwordChangeStatus.addStateChangeListener(new StateChangeListener() {

			@Override
			public void stateChange(Object arg0) {
				ChangeStatus status = passwordChangeStatus.get();

				currentPassword.setValue("");
				newPassword.setValue("");
				newPasswordVerified.setValue("");
				Notification warn = new Notification(
						"Bitte füllen Sie alle Felder aus.");
				switch (status) {
				case SUCCESSFULL:
					changePwWindow.close();
					Notification.show("Passwort geändert.");
					break;
				case CURRENT_PASSWORD_WRONG:
					Notification.show("Aktuelles Passwort nicht korrekt!");
					break;
				case EMPTY_FIELDS:

					warn.show(UI.getCurrent().getPage());
					System.out.println("Bitte alle Felder");
					break;
				case NEW_PASSWORD_NOT_EQUALS:
					Notification
							.show("Die beiden neuen Passwörter stimmen nicht überein.");
					break;
				default:
					break;
				}
			}
		});

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
	}

	@Override
	public String getTitle() {
		return TITLE;
	}

}