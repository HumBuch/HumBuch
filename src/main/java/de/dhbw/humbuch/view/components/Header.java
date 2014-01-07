package de.dhbw.humbuch.view.components;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.themes.BaseTheme;

import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindAction;
import de.dhbw.humbuch.viewmodel.LoginViewModel;
import de.dhbw.humbuch.viewmodel.LoginViewModel.DoLogout;


@Theme("mytheme")
public class Header extends CustomComponent {
	private static final long serialVersionUID = 5218684938845793342L;
	
	private HorizontalLayout horizontalLayoutHeader;
	private HorizontalLayout horizontalLayoutHeaderBar;
	private Button buttonSettings;
	private Button buttonHelp;
	private Image imageLogo;
	
	@BindAction(value = DoLogout.class, source = {})
	private Button buttonLogout = new Button();

	@Inject
	public Header(ViewModelComposer viewModelComposer,
			LoginViewModel loginViewModel) {
		init();
		buildLayout();
		bindViewModel(viewModelComposer, loginViewModel);
	}

	private void init() {
		horizontalLayoutHeader = new HorizontalLayout();
		horizontalLayoutHeaderBar = new HorizontalLayout();
		buttonLogout = new Button();
		buttonSettings = new Button();
		buttonHelp = new Button();

		imageLogo = new Image(null, new ThemeResource("images/humbuch_logo_red.png"));
		imageLogo.setHeight("100%");
		
		buttonLogout.setIcon(new ThemeResource("images/icons/32/icon_logout_red.png"));
		buttonLogout.setSizeFull();
		buttonLogout.setStyleName(BaseTheme.BUTTON_LINK);		
		
		buttonSettings.setIcon(new ThemeResource("images/icons/32/icon_settings_red.png"));
		buttonSettings.setSizeFull();
		buttonSettings.setStyleName(BaseTheme.BUTTON_LINK);
		
		buttonHelp.setIcon(new ThemeResource("images/icons/32/icon_help_red.png"));
		buttonHelp.setSizeFull();
		buttonHelp.setStyleName(BaseTheme.BUTTON_LINK);

		horizontalLayoutHeaderBar.setSpacing(true);
		horizontalLayoutHeader.setMargin(true);
		horizontalLayoutHeader.setWidth("100%");
	}
	
	private void buildLayout() {
		horizontalLayoutHeaderBar.addComponent(buttonHelp);
		horizontalLayoutHeaderBar.setComponentAlignment(buttonHelp, Alignment.TOP_RIGHT);
		horizontalLayoutHeaderBar.addComponent(buttonSettings);
		horizontalLayoutHeaderBar.setComponentAlignment(buttonSettings, Alignment.TOP_RIGHT);
		horizontalLayoutHeaderBar.addComponent(buttonLogout);
		horizontalLayoutHeaderBar.setComponentAlignment(buttonLogout, Alignment.TOP_RIGHT);

		horizontalLayoutHeader.addComponent(imageLogo);
		horizontalLayoutHeader.setComponentAlignment(imageLogo, Alignment.TOP_LEFT);
		horizontalLayoutHeader.addComponent(horizontalLayoutHeaderBar);
		horizontalLayoutHeader.setComponentAlignment(horizontalLayoutHeaderBar, Alignment.TOP_RIGHT);
		
		setCompositionRoot(horizontalLayoutHeader);
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
