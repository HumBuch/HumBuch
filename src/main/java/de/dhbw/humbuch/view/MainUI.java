package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.ComponentContainerViewDisplay;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import de.davherrmann.guice.vaadin.ScopedUI;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.event.LoginEvent;
import de.dhbw.humbuch.event.MessageEvent;
import de.dhbw.humbuch.util.ResourceLoader;
import de.dhbw.humbuch.view.components.Header;
import de.dhbw.humbuch.view.components.Sidebar;
import de.dhbw.humbuch.viewmodel.LoginViewModel;
import de.dhbw.humbuch.viewmodel.LoginViewModel.IsLoggedIn;

@Theme("humbuch")
@SuppressWarnings("serial")
@Widgetset("com.vaadin.DefaultWidgetSet")
@Title("HumBuch Schulbuchverwaltung")
public class MainUI extends ScopedUI {

	private final static Logger LOG = LoggerFactory.getLogger(MainUI.class);

	public static final String LOGIN_VIEW = "login_view";
	public static final String BOOK_MANAGEMENT_VIEW = "book_management_view";
	public static final String DUNNING_VIEW = "dunning_view";
	public static final String LENDING_VIEW = "lending_view";
	public static final String RETURN_VIEW = "return_view";
	public static final String STUDENT_INFORMATION_VIEW = "student_information_view";
	public static final String SETTINGS_VIEW = "settings_view";

	@Inject
	private LoginView loginView;
	@Inject
	private DunningView dunningView;
	@Inject
	private LendingView lendingView;
	@Inject
	private ReturnView returnView;
	@Inject
	private BookManagementView bookManagementView;
	@Inject
	private StudentInformationView studentInformationView;
	@Inject
	private SettingsView settingsView;

	private Header header = new Header();
	private VerticalLayout viewContainer = new VerticalLayout();;
	private GridLayout root;
	private ComponentContainerViewDisplay ccViewDisplay;
	private Sidebar sidebar;
	private Panel panelContent = new Panel();
	private View currentView;

	private LoginViewModel loginViewModel;
	public Navigator navigator;

	@BindState(IsLoggedIn.class)
	private BasicState<Boolean> isLoggedIn = new BasicState<Boolean>(
			Boolean.class);

	@Inject
	public MainUI(ViewModelComposer viewModelComposer,
			LoginViewModel loginViewModel, EventBus eventBus) {
		bindViewModel(viewModelComposer, loginViewModel);
		eventBus.register(this);
		this.loginViewModel = loginViewModel;
	}

	@Override
	protected void init(VaadinRequest request) {

		ccViewDisplay = new ComponentContainerViewDisplay(viewContainer);
		navigator = new Navigator(UI.getCurrent(), ccViewDisplay);
		
		navigator.setErrorView(lendingView);

		// TODO: Hack! Check how to save String in enums
		navigator.addView("", lendingView);
		navigator.addView(LOGIN_VIEW, loginView);
		navigator.addView(BOOK_MANAGEMENT_VIEW, bookManagementView);
		navigator.addView(DUNNING_VIEW, dunningView);
		navigator.addView(LENDING_VIEW, lendingView);
		navigator.addView(RETURN_VIEW, returnView);
		navigator.addView(STUDENT_INFORMATION_VIEW, studentInformationView);
		navigator.addView(SETTINGS_VIEW, settingsView);

		// Make the displayed view as big as possible
		viewContainer.setSizeFull();
		
		if (!isLoggedIn.get()) {
			buildMainView(true);
		} else {
			buildMainView(false);
		}

		attachListener();

	}

	/**
	 * Build the MainView with header, navigation bar and footer
	 * 
	 * @param cancel
	 *            Whether the MainView should be build or not
	 */
	private void buildMainView(boolean cancel) {
		if (cancel) {
			setContent(viewContainer);
			return;
		}

		root = new GridLayout(2, 2);
		
		header.setWidth("100%");
		sidebar = new Sidebar();
		sidebar.setWidth("150px");
		sidebar.getLogoutButton().addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				loginViewModel.doLogout(new Object());
			}
		});
		
		panelContent.setContent(viewContainer);
		panelContent.setSizeFull();

		root.setSizeFull();
		root.setRowExpandRatio(1, 1);
		root.setColumnExpandRatio(0, 0);
		root.setColumnExpandRatio(1, 1);
		root.addStyleName("main-view");

		root.addComponent(panelContent, 1, 1);
		root.addComponent(header, 1, 0);
		root.addComponent(sidebar, 0, 0, 0, 1);

		setContent(root);
	}

	private void attachListener() {

		/**
		 * Checks if the user is logged in before the view changes
		 */
		navigator.addViewChangeListener(new ViewChangeListener() {

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				currentView = event.getNewView();
				boolean isLoginView = currentView instanceof LoginView;

				if (!isLoggedIn.get() && !isLoginView) {
					// Redirect to login view always if a user has not yet
					// logged in
					getNavigator().navigateTo(MainUI.LOGIN_VIEW);
					return false;

				} else if (isLoggedIn.get() && isLoginView) {
					// If someone tries to access to login view while logged in,
					// then cancel
					return false;
				}

				return true;
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {
				if (isLoggedIn.get()) {
					View newView = event.getNewView();
					if (newView instanceof ViewInformation) {
						panelContent.setCaption(((ViewInformation) newView)
								.getTitle());
					} else {
						LOG.warn("New View does not implement ViewInformation interface."
								+ " Could not set caption of panel correctly.");
					}
				}
			}

		});

		/**
		 * Listens for a login or logout of a user an constructs the UI
		 * accordingly
		 */
		isLoggedIn.addStateChangeListener(new StateChangeListener() {

			@Override
			public void stateChange(Object arg0) {
				if (isLoggedIn.get()) {
					buildMainView(false);
				} else {
					buildMainView(true);
					getNavigator().navigateTo(MainUI.LOGIN_VIEW);
				}

			}

		});

		header.getHelpButton().addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				Window window = createHelpWindow(new ResourceLoader("help/"
						+ currentView.getClass().getSimpleName() + ".html")
						.getContent());
				getUI().addWindow(window);
				getUI().setFocusedComponent(window);
			}
		});
	}

	/**
	 * Creates a {@link Window} with a specified help text
	 * 
	 * @param helpText
	 *            {@link String} containing the help text
	 * @return {@link Window}
	 */
	protected Window createHelpWindow(String helpText) {
		HelpView helpView = new HelpView();
		if (helpText != null) {
			helpView.setHelpText(helpText);
		}

		Window window = new Window("Hilfe", helpView);
		window.center();
		window.setModal(true);
		window.setResizable(false);
		window.setCloseShortcut(KeyCode.ESCAPE, null);

		return window;
	}

	/**
	 * Example for handling events posted via the {@link EventBus}
	 * 
	 * @param loginEvent
	 *            a {@link LoginEvent}
	 */
	@Subscribe
	public void handleLoginEvent(LoginEvent loginEvent) {
		Notification.show(loginEvent.message);
	}

	/**
	 * Handles {@link MessageEvent}s showing the message in a Vaadin
	 * {@link Notification}
	 * 
	 * @param messageEvent
	 *            {@link MessageEvent} containing the message to show
	 */
	@Subscribe
	public void handleMessageEvent(MessageEvent messageEvent) {
		Type notificationType;
		switch (messageEvent.type) {
		case ERROR:
			notificationType = Type.ERROR_MESSAGE;
			break;
		case WARNING:
			notificationType = Type.WARNING_MESSAGE;
			break;
		case TRAYINFO:
			notificationType = Type.TRAY_NOTIFICATION;
			break;
		case INFO:
		default:
			notificationType = Type.HUMANIZED_MESSAGE;
		}
		Notification.show(messageEvent.caption, messageEvent.message, notificationType);
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
}