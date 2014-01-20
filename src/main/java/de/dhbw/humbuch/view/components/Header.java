package de.dhbw.humbuch.view.components;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.themes.BaseTheme;

import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindAction;
import de.dhbw.humbuch.view.MainUI;
import de.dhbw.humbuch.viewmodel.LoginViewModel;
import de.dhbw.humbuch.viewmodel.LoginViewModel.DoLogout;

public class Header extends HorizontalLayout {
	private static final long serialVersionUID = 5218684938845793342L;
	
	private HorizontalLayout root;
	@BindAction(value = DoLogout.class, source = {})
	private Button buttonLogout = new Button();
	private Button buttonSettings;
	private NativeButton buttonHelp;

	@Inject
	public Header(ViewModelComposer viewModelComposer,
			LoginViewModel loginViewModel) {
		init();
		buildLayout();
		bindViewModel(viewModelComposer, loginViewModel);
	}

	private void init() {
		
		setWidth("100%");
		
		buttonLogout = new Button();
		buttonSettings = new Button();
		buttonHelp = new NativeButton();
		
		buttonLogout.setIcon(new ThemeResource("images/icons/32/icon_logout_red.png"));
		buttonLogout.setSizeFull();
		buttonLogout.setStyleName(BaseTheme.BUTTON_LINK);		
		
		buttonSettings.setIcon(new ThemeResource("images/icons/32/icon_settings_red.png"));
		buttonSettings.setSizeFull();
		buttonSettings.setStyleName(BaseTheme.BUTTON_LINK);
		buttonSettings.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = -7667662717938776495L;

			@Override
			public void buttonClick(ClickEvent event) {
				if (!getUI().getNavigator().getState()
						.equals(MainUI.SETTINGS_VIEW)) {
					try {
						getUI().getNavigator().navigateTo(MainUI.SETTINGS_VIEW);
					}
					catch (IllegalArgumentException e) {
						getUI().getNavigator().navigateTo(MainUI.HOME_VIEW);
					}
				}
			}
			
		});
		
		buttonHelp.setIcon(new ThemeResource("images/icons/32/icon_help_red.png"));
		buttonHelp.setSizeFull();
		buttonHelp.setStyleName(BaseTheme.BUTTON_LINK);
		
		root = new HorizontalLayout();
		root.setSpacing(true);
		root.setMargin(true);
		
		root.addComponent(buttonHelp);
		root.addComponent(buttonSettings);
		root.addComponent(buttonLogout);
		
	}
	
	public Button getHelpButton() {
		return buttonHelp;
	}
	
	private void buildLayout() {		
		addComponent(root);
		setComponentAlignment(root, Alignment.MIDDLE_RIGHT);
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
