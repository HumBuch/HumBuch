package de.dhbw.humbuch.ui.components;

import com.vaadin.annotations.Theme;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;


@Theme("mytheme")
public class NavigationBar implements IComponent {

	private static final String NAV_HOME = "Aufgaben";
	private static final String NAV_BORROW = "Ausleihe";
	private static final String NAV_RETURN = "Rueckgabe";
	private static final String NAV_MAINTAIN_CLASS = "Klasse einpflegen";
	private static final String NAV_MAINTAIN_PUPIL = "Schueler einpflegen";
	private static final String NAV_INFO_LIST = "Auskunftslisten";
	private static final String NAV_DUNNING = "Mahnverfahren";
	private static final String NAV_MANAGE_BOOKS = "Buecher Verwaltung";
	private static final String NAV_IMPORT = "Import";

	private VerticalLayout verticalLayoutNavBar;
	private Button buttonHome;
	private Button buttonBorrow;
	private Button buttonReturn;
	private Button buttonMaintainClass;
	private Button buttonMaintainPupil;
	private Button buttonInfoList;
	private Button buttonDunning;
	private Button buttonManageBooks;
	private Button buttonImport;

	public NavigationBar() {
		init();
	}

	private void init() {
		verticalLayoutNavBar = new VerticalLayout();
		buttonHome = new Button(NAV_HOME);
		buttonBorrow = new Button(NAV_BORROW);
		buttonReturn = new Button(NAV_RETURN);
		buttonMaintainClass = new Button(NAV_MAINTAIN_CLASS);
		buttonMaintainPupil = new Button(NAV_MAINTAIN_PUPIL);
		buttonInfoList = new Button(NAV_INFO_LIST);
		buttonDunning = new Button(NAV_DUNNING);
		buttonManageBooks = new Button(NAV_MANAGE_BOOKS);
		buttonImport = new Button(NAV_IMPORT);

		buttonHome.setWidth("100%");
		buttonBorrow.setWidth("100%");
		buttonReturn.setWidth("100%");
		buttonMaintainClass.setWidth("100%");
		buttonMaintainPupil.setWidth("100%");
		buttonInfoList.setWidth("100%");
		buttonDunning.setWidth("100%");
		buttonManageBooks.setWidth("100%");
		buttonImport.setWidth("100%");

		verticalLayoutNavBar.setSpacing(true);
		verticalLayoutNavBar.addComponent(buttonHome);
		verticalLayoutNavBar.addComponent(buttonBorrow);
		verticalLayoutNavBar.addComponent(buttonReturn);
		verticalLayoutNavBar.addComponent(buttonMaintainClass);
		verticalLayoutNavBar.addComponent(buttonMaintainPupil);
		verticalLayoutNavBar.addComponent(buttonInfoList);
		verticalLayoutNavBar.addComponent(buttonDunning);
		verticalLayoutNavBar.addComponent(buttonManageBooks);
		verticalLayoutNavBar.addComponent(buttonImport);
	}

	@Override
	public Component getComponent() {
		return verticalLayoutNavBar;
	}

}
