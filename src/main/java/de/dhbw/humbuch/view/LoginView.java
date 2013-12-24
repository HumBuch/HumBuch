package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
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

public class LoginView extends CustomComponent implements View {

        private static final long serialVersionUID = 5187769743375079627L;

        private TextField user;
        private PasswordField password;

        @BindAction(value = DoLogin.class, source = { "user", "password" })
        private Button loginButton;

        @BindState(IsLoggedIn.class)
        private BasicState<Boolean> isLoggedIn = new BasicState<Boolean>(
                        Boolean.class);

        @Inject
        public LoginView(ViewModelComposer viewModelComposer,
                        LoginViewModel loginViewModel) {
                init();
                bindViewModel(viewModelComposer, loginViewModel);
        }

        private void init() {
                setSizeFull();

                // Create the user input field
                user = new TextField("User:");
                user.setWidth("300px");
                user.setRequired(true);
                user.setInputPrompt("Ihr Nutzername");
                user.setInvalidAllowed(false);

                // Create the password input field
                password = new PasswordField("Password:");
                password.setWidth("300px");
                password.setRequired(true);
                password.setValue("");
                password.setNullRepresentation("");

                final ShortcutListener enter = new ShortcutListener("Sign In",
                                KeyCode.ENTER, null) {

                        private static final long serialVersionUID = 2980349254427801100L;

                        @Override
                        public void handleAction(Object sender, Object target) {
                                loginButton.click();
                        }
                };

                // Create login button
                loginButton = new Button("Login");
                loginButton.addShortcutListener(enter);

                // Add both to a panel
                VerticalLayout fields = new VerticalLayout(user, password, loginButton);
                fields.setCaption("Bitte melden Sie sich an, um die Anwendung zu nutzen. Name: admin Pw: 1234");
                fields.setSpacing(true);
                fields.setMargin(new MarginInfo(true, true, true, false));
                fields.setSizeUndefined();

                // The view root layout
                VerticalLayout viewLayout = new VerticalLayout(fields);
                viewLayout.setSizeFull();
                viewLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
                viewLayout.setStyleName(Reindeer.LAYOUT_BLUE);
                setCompositionRoot(viewLayout);

                isLoggedIn.addStateChangeListener(new StateChangeListener() {
                        @Override
                        public void stateChange(Object arg0) {
                                if (isLoggedIn.get() == true) {

                                        /**
                                         * TODO: Why is the workaround necessary here? With no
                                         * if-clause the application would throw a NPE when
                                         * reloading the page
                                         */
                                        // Navigate to main view
                                        if (getUI() != null && getUI().getNavigator() != null) {
                                                getUI().getNavigator().navigateTo(MainUI.HOME_VIEW);
                                        }
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
                user.focus();
        }
}