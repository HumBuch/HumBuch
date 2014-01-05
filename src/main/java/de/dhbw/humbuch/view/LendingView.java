package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.view.components.EnterDataComponent;
import de.dhbw.humbuch.view.components.ListSelector;
import de.dhbw.humbuch.viewmodel.LendingViewModel;


public class LendingView extends VerticalLayout implements View, ViewInformation {

	private static final long serialVersionUID = -6400075534193735694L;

	private static final String TITLE = "Ausleihe";
	private static final String SELECT_LIST = "Ausleihlisten drucken";
	private static final String ENTER_DATA = "Ausleihlisten ins System einpflegen";

	private Accordion accordionContent;
	private ListSelector listSelector;
	private EnterDataComponent enterDataComponent;

	@Inject
	public LendingView(ViewModelComposer viewModelComposer, LendingViewModel lendingViewModel) {
		init();
		buildLayout();
		bindViewModel(viewModelComposer, lendingViewModel);
	}

	private void init() {
		accordionContent = new Accordion();
		listSelector = new ListSelector(ListSelector.Process.LENDING);
		enterDataComponent = new EnterDataComponent(EnterDataComponent.Process.LENDING);
	}

	private void buildLayout() {
		accordionContent.addTab(listSelector, SELECT_LIST);
		accordionContent.addTab(enterDataComponent, ENTER_DATA);
		accordionContent.setSelectedTab(enterDataComponent);
		accordionContent.addSelectedTabChangeListener(new TabChangeListener());

		addComponent(accordionContent);
	}

	private void bindViewModel(ViewModelComposer viewModelComposer,
			Object... viewModels) {
		try {
			viewModelComposer.bind(this, viewModels);
		}
		catch (IllegalAccessException | NoSuchElementException
				| UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTitle() {
		return TITLE;
	}
	
	private class TabChangeListener implements SelectedTabChangeListener {
		private static final long serialVersionUID = 1993690272458696837L;
		
		@Override
		public void selectedTabChange(SelectedTabChangeEvent event) {
			Component selectedTab = event.getTabSheet().getSelectedTab();
			if(selectedTab.equals(listSelector)) {
				System.out.println("---------------------------------------------------------------------------------------LIST SELECTOR");
				
			} else if(selectedTab.equals(enterDataComponent)) {
				System.out.println("---------------------------------------------------------------------------------------ENTERDATACOMPONENT");
			}
		}
		
	}
}
