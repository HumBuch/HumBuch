package de.dhbw.humbuch.ui.components;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.BaseTheme;

@Theme("mytheme")
public class PupilSearchBar extends CustomComponent {

	private static final String SEARCH_PUPIL = "Schueler suchen";
	private static final String SEARCH = "Suchen";
	
	private HorizontalLayout horizontalLayoutContent;
	private TextField textFieldSearchBar;
	// TODO: Button not really necessary since you should be able to just hit enter
	private Button buttonSearch;
	// TODO: add code to narrow search on some classes -> multiclass chooser
	
	public PupilSearchBar() {
		init();
	}
	
	private void init() {
		horizontalLayoutContent = new HorizontalLayout();
		textFieldSearchBar = new TextField(SEARCH_PUPIL);
		buttonSearch = new Button(SEARCH);
		
		buttonSearch.setIcon(new ThemeResource("images/icons/32/icon_search_red.png"));
		buttonSearch.setStyleName(BaseTheme.BUTTON_LINK);
		
		horizontalLayoutContent.setWidth("100%");
		horizontalLayoutContent.addComponent(textFieldSearchBar);
		horizontalLayoutContent.addComponent(buttonSearch);
		
		setCompositionRoot(horizontalLayoutContent);
	}
}
