package de.dhbw.humbuch.ui.screens;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.ui.NavigationUI;
import de.dhbw.humbuch.ui.components.Footer;
import de.dhbw.humbuch.viewmodel.LoginViewModel;

@Theme("mytheme")
@SuppressWarnings("serial")
// TODO: Center in middle instead of right border
public class LoginScreen extends VerticalLayout implements View {
	
	private static final String USERNAME = "Benutername";
	private static final String PASSWORD = "Passwort";
	private static final String LOGIN = "Anmelden";
	
	private GridLayout gridLayoutContent;
	private VerticalLayout verticalLayoutLoginForm;
	private Image imageLogo;
	private TextField textFieldUsername;
	private PasswordField passwordField;
	private Footer footer;
	private Component footerComponent;
	
	// TODO: BIG PROBLEM. Cannot access UI from View Model to trigger navigation!
	//@BindAction(value = DoLogin.class)
	private Button buttonLogin;
	
	@Inject
	public LoginScreen(ViewModelComposer viewModelComposer, LoginViewModel loginViewModel) {
		bindViewModel(viewModelComposer, loginViewModel);
		init();
	}
	
	protected void init() {
		gridLayoutContent = new GridLayout(1, 2);
		verticalLayoutLoginForm = new VerticalLayout();
		textFieldUsername = new TextField(USERNAME);
		passwordField = new PasswordField(PASSWORD);
		buttonLogin = new Button(LOGIN);
		footer = new Footer();
		footerComponent = footer;
		
		imageLogo = new Image(null, new ThemeResource("images/humbuch_logo_red.png"));
		imageLogo.setWidth("30%");
		
		textFieldUsername.setWidth("30%");
		passwordField.setWidth("30%");
		buttonLogin.setWidth("30%");
		
		// TODO: JUST FOR TEST! Implement in view model ... some how
		buttonLogin.addClickListener(new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				System.out.println(getUI());
				getUI().getNavigator().navigateTo(NavigationUI.MAINVIEW);
			}
		});
				
		verticalLayoutLoginForm.setSpacing(true);
		verticalLayoutLoginForm.addComponent(imageLogo);
		verticalLayoutLoginForm.addComponent(textFieldUsername);
		verticalLayoutLoginForm.addComponent(passwordField);
		verticalLayoutLoginForm.addComponent(buttonLogin);
		
		
		
		gridLayoutContent.setSizeFull();
		gridLayoutContent.setRowExpandRatio(0, 95);
		gridLayoutContent.setRowExpandRatio(1, 5);
		gridLayoutContent.addComponent(verticalLayoutLoginForm, 0, 0);
		gridLayoutContent.setComponentAlignment(verticalLayoutLoginForm, Alignment.MIDDLE_RIGHT);
		gridLayoutContent.addComponent(footerComponent, 0, 1);
		gridLayoutContent.setComponentAlignment(footerComponent, Alignment.BOTTOM_CENTER);
		
		addComponent(gridLayoutContent);
	}

	private void bindViewModel(ViewModelComposer viewModelComposer, Object... viewModels) {
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
}
