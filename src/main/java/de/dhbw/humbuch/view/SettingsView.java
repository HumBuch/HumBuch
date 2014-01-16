package de.dhbw.humbuch.view;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
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
import de.dhbw.humbuch.viewmodel.SettingsViewModel;
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
	@BindState(SchoolYears.class)
	private State<Collection<SchoolYear>> schoolYears = new BasicState<>(
			Collection.class);
	private Table yearTable;
	private BeanItemContainer<SchoolYear> yearData = new BeanItemContainer<SchoolYear>(
			SchoolYear.class);
	private FormLayout yearDetails = new FormLayout();
	private FieldGroup yearFields = new FieldGroup();
	private TextField year;
	private DateField yearFrom;
	private DateField yearTo;
	private DateField yearEndFirstTerm;
	private DateField yearBeginSecondTerm;
	private Button yearSave;
	private Button yearCancel;
	// TODO: @BindAction(value = DeleteYear.class, source = { "year" })
	private Button yearDelete;
	private Button yearNew;

	/**
	 * Categories
	 */
	private Table catTable = new Table();
	private FormLayout catEditor = new FormLayout();

	@Inject
	public SettingsView(ViewModelComposer viewModelComposer,
			SettingsViewModel settingsViewModel) {
		this.settingsViewModel = settingsViewModel;
		init();
		bindViewModel(viewModelComposer, settingsViewModel);
	}

	public void init() {
		tabs = new TabSheet();

		buildUserTab();
		buildCategoryTab();
		buildDueDatesTab();

		addListeners();
		addComponent(tabs);
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

		tabs.addTab(tabUser, "User");
	}

	/**
	 * Categories
	 */
	private void buildCategoryTab() {

		VerticalLayout tabCategories = new VerticalLayout();
		HorizontalLayout catContent = new HorizontalLayout();
		catContent.setMargin(true);
		catContent.setSpacing(true);

		// Table
		catTable.addContainerProperty("Lehrmittelkategorie", String.class, null);
		catTable.addContainerProperty("Beschreibung", String.class, null);
		catTable.setEditable(true);
		catContent.addComponent(catTable);

		// Editor
		catEditor.setMargin(true);
		catContent.addComponent(catEditor);

		// Add to parent component
		tabCategories.addComponent(catContent);
		tabs.addTab(tabCategories, "Lehrmittelkategorien");

	}

	/**
	 * Due dates
	 */
	private void buildDueDatesTab() {

		HorizontalSplitPanel tabDates = new HorizontalSplitPanel();

		initYearTable();
		initYearDetails();

		VerticalLayout yearLeftLayout = new VerticalLayout();
		yearLeftLayout.setSizeFull();
		yearLeftLayout.addComponent(yearTable);

		yearNew = new Button("Schuljahr hinzufügen", new ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				BeanItem<SchoolYear> item = new BeanItem<SchoolYear>(
						new SchoolYear.Builder("Neues Schuljahr", new Date(),
								new Date()).build());
				if (item != null) {
					yearFields.setItemDataSource(item);
				}

				yearDetails.setVisible(item != null);
			}
		});
		yearDelete = new Button("Schuljahr löschen", new ClickListener() {
			private static final long serialVersionUID = 1L;

			public void buttonClick(ClickEvent event) {
				SchoolYear item = (SchoolYear) yearTable.getValue();
				if (item != null) {
					settingsViewModel.doDeleteSchoolYear(item);
				}
			}
		});

		yearLeftLayout.addComponent(new HorizontalLayout() {
			private static final long serialVersionUID = 1L;

			{
				addComponent(yearNew);
				addComponent(yearDelete);
			}

		});

		tabDates.addComponent(yearLeftLayout);
		tabDates.addComponent(yearDetails);

		tabs.addTab(tabDates, "Schuljahresdaten");

	}

	private void initYearTable() {

		yearTable = new Table() {
			private static final long serialVersionUID = 1L;

			@Override
			protected String formatPropertyValue(Object rowId, Object colId,
					Property<?> property) {
				if (!colId.equals(YEAR_YEAR)) {
					SimpleDateFormat df = new SimpleDateFormat();
					df.applyPattern("MM.dd.yyyy");
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
				YEAR_TO, YEAR_END_FIRST }); // , YEAR_BEGIN_SEC
		yearTable.setColumnHeader(YEAR_YEAR, "Jahr");
		yearTable.setColumnHeader(YEAR_FROM, "Beginn");
		yearTable.setColumnHeader(YEAR_TO, "Ende");
		yearTable.setColumnHeader(YEAR_END_FIRST, "Ende 1. Halbjahr");
		yearTable.setColumnHeader(YEAR_BEGIN_SEC, "Begin 2. Halbjahr");

		/**
		 * Listens for changes in schoolYears state
		 */
		schoolYears.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object arg0) {
				yearData.removeAllItems();
				for (SchoolYear schoolYear : schoolYears.get()) {
					yearData.addBean(schoolYear);
				}
			}
		});

		/**
		 * When clicked on a item in the table, the details view gets populated
		 */
		yearTable.addValueChangeListener(new Property.ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				SchoolYear yearId = (SchoolYear) yearTable.getValue();

				if (yearId != null) {
					yearFields.setItemDataSource(yearTable.getItem(yearId));
				}

				yearDetails.setVisible(yearId != null);
			}

		});

	}

	private void initYearDetails() {

		yearDetails.setMargin(true);
		yearDetails.setVisible(false);

		year = new TextField("Schuljahr:");
		yearDetails.addComponent(year);
		yearFields.bind(year, YEAR_YEAR);

		yearFrom = new DateField("Beginn:");
		yearFrom.setDateFormat("dd.MM.yyyy");
		yearDetails.addComponent(yearFrom);
		yearFields.bind(yearFrom, YEAR_FROM);

		yearTo = new DateField("Ende:");
		yearTo.setDateFormat("dd.MM.yyyy");
		yearDetails.addComponent(yearTo);
		yearFields.bind(yearTo, YEAR_TO);

		yearEndFirstTerm = new DateField("Ende 1. Halbjahr:");
		yearEndFirstTerm.setDateFormat("dd.MM.yyyy");
		yearDetails.addComponent(yearEndFirstTerm);
		yearFields.bind(yearEndFirstTerm, YEAR_END_FIRST);

		yearBeginSecondTerm = new DateField("Anfang 2. Halbjahr:");
		yearBeginSecondTerm.setDateFormat("dd.MM.yyyy");
		yearDetails.addComponent(yearBeginSecondTerm);
		yearFields.bind(yearBeginSecondTerm, YEAR_BEGIN_SEC);

		HorizontalLayout buttons = new HorizontalLayout();
		yearDetails.addComponent(buttons);

		yearCancel = new Button("Abbrechen");
		buttons.addComponent(yearCancel);

		yearSave = new Button("Speichern");
		yearSave.addStyleName("default");
		buttons.addComponent(yearSave);
		
		yearCancel.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				yearTable.select(null);
				yearDetails.setVisible(false);
			}
		});
		
		yearSave.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -901115327116315724L;

			@SuppressWarnings("unchecked")
			@Override
			public void buttonClick(ClickEvent event) {
				
				// Update item
				try {
					yearFields.commit();
					SchoolYear fieldsItem = ((BeanItem<SchoolYear>) yearFields.getItemDataSource()).getBean();
					settingsViewModel.doUpdateSchoolYear(fieldsItem);
				} catch (CommitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		yearFields.setBuffered(true);

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

				switch (status) {
				case SUCCESSFULL:
					changePwWindow.close();
					Notification.show("Passwort geändert.");
					break;
				case CURRENT_PASSWORD_WRONG:
					Notification.show("Aktuelles Passwort nicht korrekt!");
					break;
				case EMPTY_FIELDS:
					Notification.show("Bitte füllen Sie alle Felder aus.");
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
		// TODO Auto-generated method stub
	}

	@Override
	public String getTitle() {
		return TITLE;
	}

}