package de.dhbw.humbuch.ui.components;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.themes.BaseTheme;


@Theme("mytheme")
public class Header implements IComponent {

	private HorizontalLayout horizontalLayoutHeader;
	private HorizontalLayout horizontalLayoutHeaderBar;
	private Button buttonLogout;
	private Button buttonSettings;
	private Button buttonHelp;
	private Image imageLogo;

	public Header() {
		init();
	}

	private void init() {
		horizontalLayoutHeader = new HorizontalLayout();
		horizontalLayoutHeaderBar = new HorizontalLayout();
		buttonLogout = new Button();
		buttonSettings = new Button();
		buttonHelp = new Button();

		imageLogo = new Image("", new ThemeResource("images/humbuch_logo_red.png"));
		imageLogo.setWidth(20, Unit.EM);

		buttonLogout.setIcon(new ThemeResource("images/icons/32/icon_logout_red.png"));
		buttonLogout.setStyleName(BaseTheme.BUTTON_LINK);
		buttonLogout.setHeight(32, Unit.PIXELS);
		buttonSettings.setIcon(new ThemeResource("images/icons/32/icon_settings_red.png"));
		buttonSettings.setHeight(32, Unit.PIXELS);
		buttonSettings.setStyleName(BaseTheme.BUTTON_LINK);
		buttonHelp.setIcon(new ThemeResource("images/icons/32/icon_help_red.png"));
		buttonHelp.setHeight(32, Unit.PIXELS);
		buttonHelp.setStyleName(BaseTheme.BUTTON_LINK);

		horizontalLayoutHeaderBar.setSpacing(true);
		horizontalLayoutHeaderBar.addComponent(buttonHelp);
		horizontalLayoutHeaderBar.addComponent(buttonSettings);
		horizontalLayoutHeaderBar.addComponent(buttonLogout);

		horizontalLayoutHeader.setWidth(100, Unit.PERCENTAGE);
		horizontalLayoutHeader.addComponent(imageLogo);
		horizontalLayoutHeader.setComponentAlignment(imageLogo, Alignment.TOP_LEFT);
		horizontalLayoutHeader.addComponent(horizontalLayoutHeaderBar);
		horizontalLayoutHeader.setComponentAlignment(horizontalLayoutHeaderBar, Alignment.MIDDLE_RIGHT);
	}

	public Component getComponent() {
		return horizontalLayoutHeader;
	}

}
