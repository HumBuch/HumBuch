package de.dhbw.humbuch.ui.components;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

@Theme("mytheme")
public class ClassList implements IComponent {

	private static final String CHOOSE_CLASS_CAPTION = "Klasse auswaehlen";
	
	private VerticalLayout verticalLayoutClassList;
	private HorizontalLayout horizontalLayoutContent;
	private ComboBox comboBoxChooseClass;
	private Button buttonPrint;
	private Label labelCaption;
	private String caption;
	
	public ClassList(String caption) {
		this.caption = caption;
		init();
	}	
	
	private void init() {
		verticalLayoutClassList = new VerticalLayout();
		horizontalLayoutContent = new HorizontalLayout();
		comboBoxChooseClass = new ComboBox(CHOOSE_CLASS_CAPTION);
		buttonPrint = new Button();
		labelCaption = new Label(caption);
		
		buttonPrint.setIcon(new ThemeResource("images/icons/32/icon_print_red.png"));
		buttonPrint.setStyleName(BaseTheme.BUTTON_LINK);

		horizontalLayoutContent.setSpacing(true);
		horizontalLayoutContent.addComponent(comboBoxChooseClass);
		horizontalLayoutContent.addComponent(buttonPrint);
		
		verticalLayoutClassList.addComponent(labelCaption);
		verticalLayoutClassList.addComponent(horizontalLayoutContent);
	}

	@Override
	public Component getComponent() {
		return verticalLayoutClassList;
	}	
}
