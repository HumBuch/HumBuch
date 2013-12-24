package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Panel;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.view.components.EnterDataComponent;
import de.dhbw.humbuch.view.components.ListSelector;
import de.dhbw.humbuch.viewmodel.LendingViewModel;


public class LendingView extends Panel implements View {

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

		setSizeFull();
		setCaption(TITLE);
	}

	private void buildLayout() {
		accordionContent.addTab(listSelector, SELECT_LIST);
		accordionContent.addTab(enterDataComponent, ENTER_DATA);
		accordionContent.setSelectedTab(enterDataComponent);

		setContent(accordionContent);
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

}
