package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindAction;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.viewmodel.LoginViewModel;
import de.dhbw.humbuch.viewmodel.LoginViewModel.DoLogin;
import de.dhbw.humbuch.viewmodel.LoginViewModel.IsLoggedIn;
import de.dhbw.humbuch.viewmodel.LoginViewModel.LoginError;

/**
 * Provides the UI for the login
 * 
 * @author Johannes
 * 
 */
public class LoginView extends VerticalLayout implements View {
	private static final long serialVersionUID = 5187769743375079627L;

	private VerticalLayout loginLayout;
	private CssLayout loginPanel;

	private TextField username = new TextField("Username");
	private PasswordField password = new PasswordField("Passwort");
	@BindAction(value = DoLogin.class, source = { "username", "password" })
	private Button btnLogin = new Button("Login");

	@BindState(IsLoggedIn.class)
	private BasicState<Boolean> isLoggedIn = new BasicState<Boolean>(
			Boolean.class);

	@BindState(LoginError.class)
	private BasicState<String> loginError = new BasicState<String>(String.class);

	@Inject
	public LoginView(ViewModelComposer viewModelComposer,
			LoginViewModel loginViewModel) {
		init();
		buildLayout();
		bindViewModel(viewModelComposer, loginViewModel);
	}

	private void init() {

		loginLayout = new VerticalLayout();
		loginLayout.setSizeFull();
		loginLayout.addStyleName("login-layout");

		loginPanel = new CssLayout();
		loginPanel.addStyleName("login-panel");

		// Labels
		HorizontalLayout labels = new HorizontalLayout();
		labels.setWidth("100%");
		labels.setMargin(true);
		labels.addStyleName("labels");
		loginPanel.addComponent(labels);

		// Welcome
		Label welcome = new Label("Herzlich Willkommen");
		welcome.setSizeUndefined();
		welcome.addStyleName("h4");
		labels.addComponent(welcome);
		labels.setComponentAlignment(welcome, Alignment.MIDDLE_LEFT);

		// HumBuch
		Label title = new Label("HumBuch<br />Schulbuchverwaltung",
				ContentMode.HTML);
		title.setSizeUndefined();
		title.addStyleName("h2");
		labels.addComponent(title);
		labels.setComponentAlignment(title, Alignment.MIDDLE_RIGHT);

		// Input fields
		HorizontalLayout fields = new HorizontalLayout();
		fields.setSpacing(true);
		fields.setMargin(true);
		fields.addStyleName("fields");

		// Username
		username.setNullRepresentation("");
		fields.addComponent(username);

		// Password
		password.setNullRepresentation("");
		fields.addComponent(password);

		// Button
		btnLogin.addStyleName("default");
		fields.addComponent(btnLogin);
		fields.setComponentAlignment(btnLogin, Alignment.BOTTOM_LEFT);

		loginPanel.addComponent(fields);

		loginLayout.addComponent(loginPanel);
		loginLayout.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);

		attachListeners();

	}

	private void attachListeners() {

		/**
		 * Checks for a successful login and navigates to home_view
		 */
		isLoggedIn.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object arg0) {
				if (isLoggedIn.get()) {
					// Navigate to main view
					if (getUI() != null && getUI().getNavigator() != null) {
						getUI().getNavigator().navigateTo(MainUI.LENDING_VIEW);
					}
				}

			}
		});

		/**
		 * Listens for login errors and displays them
		 */
		loginError.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object arg0) {
				if (!isLoggedIn.get()) {

					if (loginPanel.getComponentCount() > 2) {
						// Remove the previous error message
						loginPanel.removeComponent(loginPanel.getComponent(2));
					}

					Label error = new Label(loginError.get(), ContentMode.HTML);
					error.setStyleName("error");
					error.setSizeUndefined();
					// add animation
					error.addStyleName("v-animate-reveal");
					loginPanel.addComponent(error);
					username.focus();
				}

			}
		});

		/**
		 * Listens for key press <enter>
		 */
		final ShortcutListener enter = new ShortcutListener("Sign In",
				KeyCode.ENTER, null) {

			private static final long serialVersionUID = 2980349254427801100L;

			@Override
			public void handleAction(Object sender, Object target) {
				btnLogin.click();
			}
		};

		username.addShortcutListener(enter);
		password.addShortcutListener(enter);
	}

	private void buildLayout() {
		setSizeFull();
		addComponent(loginLayout);
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
		username.setValue("");
		password.setValue("");
		username.focus();
	}
}