package de.dhbw.humbuch.ui.components;

import com.vaadin.annotations.Theme;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.VerticalLayout;

import de.dhbw.humbuch.ui.NavigationUI;


@Theme("mytheme")
public class NavigationBar extends CustomComponent /*implements IComponent*/ {

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

		/*TODO insert the correct constants for the corresponding screens
		 * 
		 */
		verticalLayoutNavBar = new VerticalLayout();
		buttonHome = new Button(NAV_HOME, new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				System.out.println(getUI());
				getUI().getNavigator().navigateTo(NavigationUI.MAINVIEW);
			}
		});
		buttonBorrow = new Button(NAV_BORROW, new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				System.out.println(getUI());
				getUI().getNavigator().navigateTo(NavigationUI.MANAGE_BOOKS);
			}
		});
		buttonReturn = new Button(NAV_RETURN, new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				System.out.println(getUI());
				getUI().getNavigator().navigateTo(NavigationUI.MANAGE_BOOKS);
			}
		});
		buttonMaintainClass = new Button(NAV_MAINTAIN_CLASS, new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				System.out.println(getUI());
				getUI().getNavigator().navigateTo(NavigationUI.MANAGE_BOOKS);
			}
		});
		buttonMaintainPupil = new Button(NAV_MAINTAIN_PUPIL, new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				System.out.println(getUI());
				getUI().getNavigator().navigateTo(NavigationUI.MANAGE_BOOKS);
			}
		});
		buttonInfoList = new Button(NAV_INFO_LIST, new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				System.out.println(getUI());
				getUI().getNavigator().navigateTo(NavigationUI.MANAGE_BOOKS);
			}
		});
		buttonDunning = new Button(NAV_DUNNING, new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				System.out.println(getUI());
				getUI().getNavigator().navigateTo(NavigationUI.DUNNING);
			}
		});
		buttonManageBooks = new Button(NAV_MANAGE_BOOKS, new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				System.out.println(getUI());
				getUI().getNavigator().navigateTo(NavigationUI.MANAGE_BOOKS);
			}
		});
		buttonImport = new Button(NAV_IMPORT, new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				System.out.println(getUI());
				getUI().getNavigator().navigateTo(NavigationUI.MANAGE_BOOKS);
			}
		});

		
		String width = "100%";
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
		verticalLayoutNavBar.setMargin(true);
		verticalLayoutNavBar.addComponent(buttonHome);
		verticalLayoutNavBar.setComponentAlignment(buttonHome, Alignment.MIDDLE_CENTER);
		verticalLayoutNavBar.addComponent(buttonBorrow);
		verticalLayoutNavBar.setComponentAlignment(buttonBorrow, Alignment.MIDDLE_CENTER);
		verticalLayoutNavBar.addComponent(buttonReturn);
		verticalLayoutNavBar.setComponentAlignment(buttonReturn, Alignment.MIDDLE_CENTER);
		verticalLayoutNavBar.addComponent(buttonMaintainClass);
		verticalLayoutNavBar.setComponentAlignment(buttonMaintainClass, Alignment.MIDDLE_CENTER);
		verticalLayoutNavBar.addComponent(buttonMaintainPupil);
		verticalLayoutNavBar.setComponentAlignment(buttonMaintainPupil, Alignment.MIDDLE_CENTER);
		verticalLayoutNavBar.addComponent(buttonInfoList);
		verticalLayoutNavBar.setComponentAlignment(buttonInfoList, Alignment.MIDDLE_CENTER);
		verticalLayoutNavBar.addComponent(buttonDunning);
		verticalLayoutNavBar.setComponentAlignment(buttonDunning, Alignment.MIDDLE_CENTER);
		verticalLayoutNavBar.addComponent(buttonManageBooks);
		verticalLayoutNavBar.setComponentAlignment(buttonManageBooks, Alignment.MIDDLE_CENTER);
		verticalLayoutNavBar.addComponent(buttonImport);
		verticalLayoutNavBar.setComponentAlignment(buttonImport, Alignment.MIDDLE_CENTER);
		
		setCompositionRoot(verticalLayoutNavBar);
	}

//	@Override
//	public Component getComponent() {
//		return verticalLayoutNavBar;
//	}

}
