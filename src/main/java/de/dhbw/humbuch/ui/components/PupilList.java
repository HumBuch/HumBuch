package de.dhbw.humbuch.ui.components;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

@Theme("mytheme")
public class PupilList implements IComponent {

	private static final String SEARCH_PUPIL_CAPTION = "Schueler suchen";
	
	private VerticalLayout verticalLayoutPupilList;
	private HorizontalLayout horizontalLayoutContent;
	private TextField textFieldSearch;
	private Button buttonPrint;
	private Button buttonSearch;
	private Label labelCaption;
	String caption;
	
	public PupilList(String caption) {
		this.caption = caption;
		init();
	}

	protected void init() {
		verticalLayoutPupilList = new VerticalLayout();
		horizontalLayoutContent = new HorizontalLayout();
		textFieldSearch = new TextField();
		buttonPrint = new Button();
		buttonSearch = new Button();
		labelCaption =  new Label("Schueler");
		
		textFieldSearch.setCaption(SEARCH_PUPIL_CAPTION);
		
		buttonPrint.setIcon(new ThemeResource("images/icons/32/icon_print_red.png"));
		buttonPrint.setStyleName(BaseTheme.BUTTON_LINK);
		buttonSearch.setIcon(new ThemeResource("images/icons/32/icon_search_red.png"));
		buttonSearch.setStyleName(BaseTheme.BUTTON_LINK);
		
		horizontalLayoutContent.addComponent(textFieldSearch);
		horizontalLayoutContent.addComponent(buttonSearch);
		horizontalLayoutContent.addComponent(buttonPrint);
		
		verticalLayoutPupilList.addComponent(labelCaption);
		verticalLayoutPupilList.addComponent(horizontalLayoutContent);
	}

	@Override
	public Component getComponent() {
		return verticalLayoutPupilList;
	}
}
