package de.dhbw.humbuch.ui.components;

import com.vaadin.annotations.Theme;
import com.vaadin.ui.Alignment;
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

		
		String width = "90%";
		buttonHome.setWidth(width);
		buttonBorrow.setWidth(width);
		buttonReturn.setWidth(width);
		buttonMaintainClass.setWidth(width);
		buttonMaintainPupil.setWidth(width);
		buttonInfoList.setWidth(width);
		buttonDunning.setWidth(width);
		buttonManageBooks.setWidth(width);
		buttonImport.setWidth(width);

		verticalLayoutNavBar.setSpacing(true);
		verticalLayoutNavBar.addComponent(buttonHome);
		verticalLayoutNavBar.setComponentAlignment(buttonHome, Alignment.MIDDLE_LEFT);
		verticalLayoutNavBar.addComponent(buttonBorrow);
		verticalLayoutNavBar.setComponentAlignment(buttonBorrow, Alignment.MIDDLE_LEFT);
		verticalLayoutNavBar.addComponent(buttonReturn);
		verticalLayoutNavBar.setComponentAlignment(buttonReturn, Alignment.MIDDLE_LEFT);
		verticalLayoutNavBar.addComponent(buttonMaintainClass);
		verticalLayoutNavBar.setComponentAlignment(buttonMaintainClass, Alignment.MIDDLE_LEFT);
		verticalLayoutNavBar.addComponent(buttonMaintainPupil);
		verticalLayoutNavBar.setComponentAlignment(buttonMaintainPupil, Alignment.MIDDLE_LEFT);
		verticalLayoutNavBar.addComponent(buttonInfoList);
		verticalLayoutNavBar.setComponentAlignment(buttonInfoList, Alignment.MIDDLE_LEFT);
		verticalLayoutNavBar.addComponent(buttonDunning);
		verticalLayoutNavBar.setComponentAlignment(buttonDunning, Alignment.MIDDLE_LEFT);
		verticalLayoutNavBar.addComponent(buttonManageBooks);
		verticalLayoutNavBar.setComponentAlignment(buttonManageBooks, Alignment.MIDDLE_LEFT);
		verticalLayoutNavBar.addComponent(buttonImport);
		verticalLayoutNavBar.setComponentAlignment(buttonImport, Alignment.MIDDLE_LEFT);
	}

	@Override
	public Component getComponent() {
		return verticalLayoutNavBar;
	}

}
