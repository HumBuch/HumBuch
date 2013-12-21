package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.ComponentContainerViewDisplay;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.guice.vaadin.ScopedUI;
import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindAction;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.view.components.Footer;
import de.dhbw.humbuch.view.components.Header;
import de.dhbw.humbuch.view.components.NavigationBar;
import de.dhbw.humbuch.viewmodel.LoginViewModel;
import de.dhbw.humbuch.viewmodel.LoginViewModel.DoLogin;
import de.dhbw.humbuch.viewmodel.LoginViewModel.DoLogout;
import de.dhbw.humbuch.viewmodel.LoginViewModel.IsLoggedIn;

@Theme("mytheme")
@SuppressWarnings("serial")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class MainUI extends ScopedUI {

	public static final String HOME_VIEW = "home_view";
	public static final String BOOK_MANAGEMENT_VIEW = "book_management_view";
	public static final String DUNNING_VIEW = "dunning_view";
	public static final String LENDING_VIEW = "lending_view";
	public static final String RETURN_VIEW = "return_view";

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
    //@Inject
    //private ImportView importView;

	private CssLayout root = new CssLayout();
	
	private VerticalLayout loginLayout;
	private CssLayout content = new CssLayout();
	
	private GridLayout gridLayoutRoot;
	private VerticalLayout verticalLayoutContent;
	private ComponentContainerViewDisplay ccViewDisplay;
	private Header header;
	private Footer footer;
	private NavigationBar navigationBar;

	public Navigator navigator;

	// Fields
	private TextField username = new TextField("Username");
	private PasswordField password = new PasswordField("Passwort");
	
	// Buttons
	@BindAction(value = DoLogout.class, source = {})
	private Button logoutButton = new Button("Logout");
	
	@BindAction(value = DoLogin.class, source = { "username", "password" })
	private Button loginButton = new Button("Login");

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
		
		setContent(root);
		root.setSizeFull();

        if (!isLoggedIn.get()) {
        	buildLoginView(false);
        } else if (isLoggedIn.get()) {
        	buildMainView();
        }
		
	}
	
	
    private void buildLoginView(boolean exit) {
        if (exit) {
            root.removeAllComponents();
        }
        addStyleName("login");

        loginLayout = new VerticalLayout();
        loginLayout.setSizeFull();
        loginLayout.addStyleName("login-layout");
        root.addComponent(loginLayout);

        final CssLayout loginPanel = new CssLayout();
        loginPanel.addStyleName("login-panel");

        HorizontalLayout labels = new HorizontalLayout();
        labels.setWidth("100%");
        labels.setMargin(true);
        labels.addStyleName("labels");
        loginPanel.addComponent(labels);

        Label welcome = new Label("Welcome");
        welcome.setSizeUndefined();
        welcome.addStyleName("h4");
        labels.addComponent(welcome);
        labels.setComponentAlignment(welcome, Alignment.MIDDLE_LEFT);

        Label title = new Label("HumBuch Schulbuchverwaltung");
        title.setSizeUndefined();
        title.addStyleName("h2");
        title.addStyleName("light");
        labels.addComponent(title);
        labels.setComponentAlignment(title, Alignment.MIDDLE_RIGHT);

        HorizontalLayout fields = new HorizontalLayout();
        fields.setSpacing(true);
        fields.setMargin(true);
        fields.addStyleName("fields");

        username.focus();
        fields.addComponent(username);

        fields.addComponent(password);

        loginButton.addStyleName("default");
        fields.addComponent(loginButton);
        fields.setComponentAlignment(loginButton, Alignment.BOTTOM_LEFT);

        final ShortcutListener enter = new ShortcutListener("Login",
                KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
            	loginButton.click();
            }
        };

        isLoggedIn.addStateChangeListener(new StateChangeListener() {

			@Override
			public void stateChange(Object arg0) {
				if (isLoggedIn.get()) {
					buildMainView();
				}
				
			}
        	
        	
        });

        loginButton.addShortcutListener(enter);

        loginPanel.addComponent(fields);

        loginLayout.addComponent(loginPanel);
        loginLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
    }

    private void buildMainView() {

        root.removeAllComponents();
        
        gridLayoutRoot = new GridLayout(2,3);
        verticalLayoutContent = new VerticalLayout();
        
        header = new Header();
        footer = new Footer();
        navigationBar = new NavigationBar();
        
        header.setSizeFull();
        footer.setSizeFull();
        navigationBar.setWidth("100%");
        
        gridLayoutRoot.setSizeUndefined();
        
        gridLayoutRoot.setSizeFull();
        gridLayoutRoot.setRowExpandRatio(0, 15);
        gridLayoutRoot.setRowExpandRatio(1, 80);
        gridLayoutRoot.setRowExpandRatio(2, 5);
        gridLayoutRoot.setColumnExpandRatio(0, 20);
        gridLayoutRoot.setColumnExpandRatio(1, 80);
        gridLayoutRoot.addComponent(header, 0, 0, 1, 0);
        gridLayoutRoot.addComponent(navigationBar, 0, 1);
        gridLayoutRoot.addComponent(verticalLayoutContent, 1, 1);
        gridLayoutRoot.addComponent(footer, 0, 2, 1, 2);
        
        ccViewDisplay = new ComponentContainerViewDisplay(verticalLayoutContent);
        
        navigator = new Navigator(UI.getCurrent(), ccViewDisplay);
        
        // TODO: Hack! Check how to save String in enums
        navigator.addView("", homeView);
        navigator.addView(HOME_VIEW, homeView);
        navigator.addView(BOOK_MANAGEMENT_VIEW, bookManagementView);
        navigator.addView(DUNNING_VIEW, dunningView);
        navigator.addView(LENDING_VIEW, lendingView);
        navigator.addView(RETURN_VIEW, returnView);

        root.addComponent(gridLayoutRoot);

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