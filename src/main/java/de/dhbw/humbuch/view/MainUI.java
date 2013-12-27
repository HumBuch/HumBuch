package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.ComponentContainerViewDisplay;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.guice.vaadin.ScopedUI;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.view.components.Footer;
import de.dhbw.humbuch.view.components.Header;
import de.dhbw.humbuch.view.components.NavigationBar;
import de.dhbw.humbuch.viewmodel.LoginViewModel;
import de.dhbw.humbuch.viewmodel.LoginViewModel.IsLoggedIn;


@Theme("mytheme")
@SuppressWarnings("serial")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class MainUI extends ScopedUI {

	private final static Logger LOG = LoggerFactory.getLogger(MainUI.class);

	public static final String LOGIN_VIEW = "login_view";
	public static final String HOME_VIEW = "home_view";
	public static final String BOOK_MANAGEMENT_VIEW = "book_management_view";
	public static final String DUNNING_VIEW = "dunning_view";
	public static final String LENDING_VIEW = "lending_view";
	public static final String RETURN_VIEW = "return_view";
	public static final String IMPORT_VIEW = "import_view";

	@Inject
	private LoginView loginView;
	@Inject
	private DunningView dunningView;
	@Inject
	private LendingView lendingView;
	@Inject
	private ReturnView returnView;
	@Inject
	private HomeView homeView;
	@Inject
	private BookManagementView bookManagementView;
	@Inject
	private ImportView importView;

	private GridLayout gridLayoutRoot;
	private VerticalLayout verticalLayoutContent;
	private ComponentContainerViewDisplay ccViewDisplay;
	private Header header;
	private Footer footer;
	private NavigationBar navigationBar;
	private Panel panelContent;

	public Navigator navigator;

	@BindState(IsLoggedIn.class)
	private BasicState<Boolean> isLoggedIn = new BasicState<Boolean>(
			Boolean.class);

	@Inject
	public MainUI(ViewModelComposer viewModelComposer,
			LoginViewModel loginViewModel) {
		bindViewModel(viewModelComposer, loginViewModel);
	}

	@Override
	protected void init(VaadinRequest request) {

		gridLayoutRoot = new GridLayout(2, 3);
		verticalLayoutContent = new VerticalLayout();
		panelContent = new Panel();

		header = new Header();
		footer = new Footer();
		navigationBar = new NavigationBar();

		panelContent.setSizeFull();
		header.setWidth("100%");
		footer.setWidth("100%");
		navigationBar.setWidth("100%");

		panelContent.setContent(verticalLayoutContent);

		gridLayoutRoot.setSizeFull();
		gridLayoutRoot.setRowExpandRatio(1, 1);
		gridLayoutRoot.setColumnExpandRatio(0, 20);
		gridLayoutRoot.setColumnExpandRatio(1, 80);

		gridLayoutRoot.addComponent(panelContent, 1, 1);

		// Hide Menus if not logged in
		if (isLoggedIn.get()) {
			gridLayoutRoot.addComponent(header, 0, 0, 1, 0);
			gridLayoutRoot.addComponent(navigationBar, 0, 1);
			gridLayoutRoot.addComponent(footer, 0, 2, 1, 2);
		}

		isLoggedIn.addStateChangeListener(new StateChangeListener() {

			@Override
			public void stateChange(Object arg0) {
				if (isLoggedIn.get()) {
					gridLayoutRoot.removeAllComponents();
					gridLayoutRoot.addComponent(panelContent, 1, 1);
					gridLayoutRoot.addComponent(header, 0, 0, 1, 0);
					gridLayoutRoot.addComponent(navigationBar, 0, 1);
					gridLayoutRoot.addComponent(footer, 0, 2, 1, 2);
				}
				else {
					// remove Components
					gridLayoutRoot.removeComponent(header);
					gridLayoutRoot.removeComponent(navigationBar);
					gridLayoutRoot.removeComponent(footer);
				}

			}

		});
		ccViewDisplay = new ComponentContainerViewDisplay(verticalLayoutContent);

		navigator = new Navigator(UI.getCurrent(), ccViewDisplay);

		// TODO: Hack! Check how to save String in enums
		navigator.addView("", homeView);
		navigator.addView(LOGIN_VIEW, loginView);
		navigator.addView(HOME_VIEW, homeView);
		navigator.addView(BOOK_MANAGEMENT_VIEW, bookManagementView);
		navigator.addView(DUNNING_VIEW, dunningView);
		navigator.addView(LENDING_VIEW, lendingView);
		navigator.addView(RETURN_VIEW, returnView);
		navigator.addView(IMPORT_VIEW, importView);

		/**
		 * TODO I am not sure if this belongs here. Should the MainUI implement
		 * ViewChangeListener? What is the best practice to solve this?
		 * */
		navigator.addViewChangeListener(new ViewChangeListener() {

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				boolean isLoginView = event.getNewView() instanceof LoginView;

				if (!isLoggedIn.get() && !isLoginView) {
					// Redirect to login view always if a user has not yet
					// logged in
					getNavigator().navigateTo(MainUI.LOGIN_VIEW);
					return false;

				}
				else if (isLoggedIn.get() && isLoginView) {
					// If someone tries to access to login view while logged in,
					// then cancel
					return false;
				}

				return true;
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {
				View newView = event.getNewView();
				if (newView instanceof ViewInformation) {
					panelContent.setCaption(((ViewInformation) newView).getTitle());
				}
				else {
					LOG.warn("New View does not implement ViewInformation interface." +
							" Could not set caption of panel correctly.");
				}
			}

		});

		setContent(gridLayoutRoot);
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
}