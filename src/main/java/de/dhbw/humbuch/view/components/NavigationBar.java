package de.dhbw.humbuch.view.components;

import com.vaadin.annotations.Theme;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;

import de.dhbw.humbuch.view.MainUI;


@Theme("mytheme")
public class NavigationBar extends CustomComponent {

	private static final long serialVersionUID = -2263554457706891669L;

	/**
	 * Each navigation button has to be added to the array "navigationButtons".
	 * First value of the array is the human readable caption of the button, the
	 * second one the name of the specific view to load.
	 */
	// TODO refactor when a good navigation method is found
	private static final String[][] navigationButtons = new String[][] {
			{ "Aufgaben", MainUI.HOME_VIEW }, { "Ausleihe", MainUI.LENDING_VIEW },
			{ "Rückgabe", MainUI.RETURN_VIEW }, { "Lehrmittel", MainUI.BOOK_MANAGEMENT_VIEW },
			{ "Mahnungen", MainUI.DUNNING_VIEW }, { "Schüler", MainUI.STUDENT_INFORMATION_VIEW } };

	private VerticalLayout verticalLayoutNavBar;

	public NavigationBar() {
		/*
		 * TODO insert the correct constants for the corresponding screens
		 */
		verticalLayoutNavBar = new VerticalLayout();

		for (final String[] view : navigationButtons) {
			NativeButton b = new NativeButton(view[0],
					new NativeButton.ClickListener() {

						private static final long serialVersionUID = -5330895560377816934L;

						@Override
						public void buttonClick(ClickEvent event) {
							System.out.println(getUI());
							event.getButton().addStyleName("selected");

							if (!getUI().getNavigator().getState()
									.equals(view[1])) {
								try {
									getUI().getNavigator().navigateTo(view[1]);
								}
								catch (IllegalArgumentException e) {
									getUI().getNavigator().navigateTo("main");
								}
							}
						}
					});
			b.setWidth("100%");
			verticalLayoutNavBar.addComponent(b);
			verticalLayoutNavBar.setComponentAlignment(b,
					Alignment.MIDDLE_CENTER);
		}
		setCompositionRoot(verticalLayoutNavBar);
	}
}
