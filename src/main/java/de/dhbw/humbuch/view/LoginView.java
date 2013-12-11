package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindAction;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.viewmodel.LoginViewModel;
import de.dhbw.humbuch.viewmodel.LoginViewModel.DoLogin;
import de.dhbw.humbuch.viewmodel.LoginViewModel.LoginSuccessful;

public class LoginView extends VerticalLayout {
	private static final long serialVersionUID = 5187769743375079627L;
	
	private TextField textFieldUsername = new TextField();
	private TextField textFieldPassword = new TextField();
	
	@BindState(LoginSuccessful.class)
	private TextField textFieldSuccessful = new TextField();

	@BindAction(value = DoLogin.class, source = {"textFieldUsername", "textFieldPassword"})
	private Button buttonLogin = new Button("Login");
	
	@Inject
	public LoginView(ViewModelComposer viewModelComposer, LoginViewModel loginViewModel) {
		System.out.println("constructor");
		addComponents();
		bindViewModel(viewModelComposer, loginViewModel);
	}
	
	private void addComponents() {
		addComponent(textFieldUsername);
		addComponent(textFieldPassword);
		addComponent(buttonLogin);
		addComponent(textFieldSuccessful);
	}
	
	private void bindViewModel(ViewModelComposer viewModelComposer, Object... viewModels) {
		try {
			viewModelComposer.bind(this, viewModels);
		} catch (IllegalAccessException | NoSuchElementException
				| UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}
}
