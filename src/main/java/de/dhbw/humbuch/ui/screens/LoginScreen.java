package de.dhbw.humbuch.ui.screens;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.dhbw.humbuch.ui.components.Footer;

@Theme("mytheme")
@SuppressWarnings("serial")
public class LoginScreen extends UI {
	
	private static final String USERNAME = "Benutername";
	private static final String PASSWORD = "Passwort";
	private static final String LOGIN = "Anmelden";
	
	private GridLayout gridLayoutContent;
	private VerticalLayout verticalLayoutLoginForm;
	private Image imageLogo;
	private TextField textFieldUsername;
	private PasswordField passwordField;
	private Button buttonLogin;
	private Footer footer;
	private Component footerComponent;
	
	@WebServlet(value = "/login", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = LoginScreen.class, widgetset = "de.davherrmann.mvvm.demo.AppWidgetSet")
	public static class Servlet extends VaadinServlet {
    }
	
	protected void init(VaadinRequest request) {
		gridLayoutContent = new GridLayout(1, 2);
		verticalLayoutLoginForm = new VerticalLayout();
		textFieldUsername = new TextField(USERNAME);
		passwordField = new PasswordField(PASSWORD);
		buttonLogin = new Button(LOGIN);
		footer = new Footer();
		footerComponent = footer.getComponent();
		
		imageLogo = new Image(null, new ThemeResource("images/humbuch_logo_red.png"));
		imageLogo.setWidth("30%");
		
		textFieldUsername.setWidth("30%");
		passwordField.setWidth("30%");
		buttonLogin.setWidth("30%");
		
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
		
		setContent(gridLayoutContent);
	}
}
