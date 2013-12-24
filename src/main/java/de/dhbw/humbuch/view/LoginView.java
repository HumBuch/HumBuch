package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindAction;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.viewmodel.LoginViewModel;
import de.dhbw.humbuch.viewmodel.LoginViewModel.DoLogin;
import de.dhbw.humbuch.viewmodel.LoginViewModel.IsLoggedIn;
import de.dhbw.humbuch.viewmodel.LoginViewModel.LoginError;

public class LoginView extends VerticalLayout implements View {

	private static final long serialVersionUID = 5187769743375079627L;

	private VerticalLayout viewLayout;
	
	private TextField username = new TextField("Nutzername:");
	private PasswordField password = new PasswordField("Passwort:");;

	@BindAction(value = DoLogin.class, source = { "username", "password" })
	private Button loginButton = new Button("Login");

	@BindState(IsLoggedIn.class)
	private BasicState<Boolean> isLoggedIn = new BasicState<Boolean>(
			Boolean.class);
	
	@BindState(LoginError.class)
	private BasicState<String> loginError = new BasicState<String>(
			String.class);

	@Inject
	public LoginView(ViewModelComposer viewModelComposer,
			LoginViewModel loginViewModel) {
		init();
		buildLayout();
		bindViewModel(viewModelComposer, loginViewModel);
	}

	private void init() {

		// Create the user input field
		username.setWidth("300px");
		username.setRequired(true);
		username.setInvalidAllowed(false);

		// Create the password input field
		password.setWidth("300px");
		password.setRequired(true);
		password.setValue("");
		password.setNullRepresentation("");
		
		// Add both to a panel
		final VerticalLayout loginPanel = new VerticalLayout(username, password, loginButton);
		loginPanel.setCaption("Bitte melden Sie sich an, um die Anwendung zu nutzen. Name: admin Pw: 1234");
		loginPanel.setSpacing(true);
		loginPanel.setMargin(new MarginInfo(true, true, true, false));
		loginPanel.setSizeUndefined();

		// The view root layout
		viewLayout = new VerticalLayout(loginPanel);
		viewLayout.setSizeFull();
		viewLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);
		viewLayout.setStyleName(Reindeer.LAYOUT_BLUE);

		final ShortcutListener enter = new ShortcutListener("Sign In",
				KeyCode.ENTER, null) {

			private static final long serialVersionUID = 2980349254427801100L;

			@Override
			public void handleAction(Object sender, Object target) {
				loginButton.click();
			}
		};

		// Listeners
		username.addShortcutListener(enter);
		password.addShortcutListener(enter);

		// Check for a successful login
		isLoggedIn.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object arg0) {
				if (isLoggedIn.get()) {

					// Navigate to main view
					if (getUI() != null && getUI().getNavigator() != null) {
						getUI().getNavigator().navigateTo(MainUI.HOME_VIEW);
					}
				}

			}
		});
		
		loginError.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object arg0) {
				if (!isLoggedIn.get()) {
					
                   if (loginPanel.getComponentCount() > 3) {
                        // Remove the previous error message
                        loginPanel.removeComponent(loginPanel.getComponent(3));
                    }
					
					Label error = new Label(loginError.get(), ContentMode.HTML);
					error.setStyleName("error-box");
					error.setSizeUndefined();
					loginPanel.addComponent(error);
				}

			}
		});
	}
	
	private void buildLayout() {
		setSizeFull();	
		
		addComponent(viewLayout);
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
		username.focus();
	}
}