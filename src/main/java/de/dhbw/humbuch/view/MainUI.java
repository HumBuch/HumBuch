package de.dhbw.humbuch.view;

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.Navigator.ComponentContainerViewDisplay;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.guice.vaadin.ScopedUI;
import de.dhbw.humbuch.view.components.Footer;
import de.dhbw.humbuch.view.components.Header;
import de.dhbw.humbuch.view.components.NavigationBar;

@Theme("mytheme")
@SuppressWarnings("serial")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class MainUI extends ScopedUI {
	
	public static final String HOME_VIEW = "home_view";
	public static final String BOOK_MANAGEMENT_VIEW = "book_management_view";
	public static final String DUNNING_VIEW = "dunning_view";
	public static final String LENDING_VIEW = "lending_view";
	public static final String RETURN_VIEW = "return_view";
	public static final String IMPORT_VIEW = "import_view";
	
	@Inject
	private LoginView loginView;
	@Inject
	private DunningView dunningView;
	@Inject
	private LendingView lendingView;
	@Inject
	private ReturnView returnView;
	@Inject
	private HomeView homeView;
	@Inject
	private BookManagementView bookManagementView;
	@Inject
	private ImportView importView;
	
	private GridLayout gridLayoutRoot;
	private VerticalLayout verticalLayoutContent;
	private ComponentContainerViewDisplay ccViewDisplay;
	private Header header;
	private Footer footer;
	private NavigationBar navigationBar;
	private Panel panelContent;

	public Navigator navigator;
	
	@Override
	protected void init(VaadinRequest request) {
		
		gridLayoutRoot = new GridLayout(2,3);
		verticalLayoutContent = new VerticalLayout();
		panelContent = new Panel();
		
		header = new Header();
		footer = new Footer();
		navigationBar = new NavigationBar();
		
		panelContent.setSizeFull();
		//verticalLayoutContent.setSizeFull();
		header.setWidth("100%");
		footer.setWidth("100%");
		navigationBar.setWidth("100%");
		
		panelContent.setContent(verticalLayoutContent);
		
		gridLayoutRoot.setSizeFull();
		gridLayoutRoot.setRowExpandRatio(1, 1);
		gridLayoutRoot.setColumnExpandRatio(0, 20);
		gridLayoutRoot.setColumnExpandRatio(1, 80);
		gridLayoutRoot.addComponent(header, 0, 0, 1, 0);
		gridLayoutRoot.addComponent(navigationBar, 0, 1);
		gridLayoutRoot.addComponent(panelContent, 1, 1);
		gridLayoutRoot.addComponent(footer, 0, 2, 1, 2);
		
		ccViewDisplay = new ComponentContainerViewDisplay(verticalLayoutContent);
		
		navigator = new Navigator(UI.getCurrent(), ccViewDisplay);
		
		// TODO: Hack! Check how to save String in enums
		navigator.addView("", homeView);
		navigator.addView(HOME_VIEW, homeView);
		navigator.addView(BOOK_MANAGEMENT_VIEW, bookManagementView);
		navigator.addView(DUNNING_VIEW, dunningView);
		navigator.addView(LENDING_VIEW, lendingView);
		navigator.addView(RETURN_VIEW, returnView);
		navigator.addView(IMPORT_VIEW, importView);
		
		navigator.addViewChangeListener(new ViewChangeListener() {

			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				return true;
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {
				try {
				ViewInformation cv = (ViewInformation) event.getNewView();
				panelContent.setCaption(cv.getTitle());
				} catch(Exception e) {
					System.out.println("exception afterViewChange");
				}
			}
			
		});
		
		setContent(gridLayoutRoot);
	}

}
