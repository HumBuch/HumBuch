package de.dhbw.humbuch.ui.components;


import com.vaadin.annotations.Theme;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
public class NavigationBar extends CustomComponent /*implements IComponent*/ {

	/**
	 * Each navigation button has to be added to the array "navigationButtons".
	 * First value of the array is the human readable caption of the button, the second
	 * one the name of the specific view to load.
	 */
	private static final String[][] navigationButtons = new String[][] {
		{"Aufgaben", "main"},
		{"Ausleihe", "manageBorrows"},
		{"R\u00FCckgabe", "manageReturns"},
		{"Sch\u00FCler", "managePupils"},
		{"B\u00FCcher", "manageBooks"},
		{"Mahnungen", "managedunnings"}
	};
	
	private VerticalLayout verticalLayoutNavBar;

	public NavigationBar() {

		/*TODO insert the correct constants for the corresponding screens
		 * 
		 */
		verticalLayoutNavBar = new VerticalLayout();
		
		
		for (final String[] view : navigationButtons) {
			NativeButton b = new NativeButton(view[0], new NativeButton.ClickListener() {
				@Override
				public void buttonClick(ClickEvent event) {
					System.out.println(getUI());
					event.getButton().addStyleName("selected");

					if(!getUI().getNavigator().getState().equals(view[1])) {
						getUI().getNavigator().navigateTo(view[1]);
					}
				}
			});
			b.addStyleName("icon-" + view);
			b.setWidth("100%");
			verticalLayoutNavBar.addComponent(b);
			verticalLayoutNavBar.setComponentAlignment(b, Alignment.MIDDLE_CENTER);
			
		}
		
		setCompositionRoot(verticalLayoutNavBar);
	}

//	@Override
//	public Component getComponent() {
//		return verticalLayoutNavBar;
//	}

}
