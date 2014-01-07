package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.view.components.EnterDataComponent;
import de.dhbw.humbuch.view.components.ListSelector;
import de.dhbw.humbuch.view.components.MultiClassChooser;
import de.dhbw.humbuch.viewmodel.ReturnViewModel;


public class ReturnView extends VerticalLayout implements View, ViewInformation {

	private static final long serialVersionUID = -525078997965992622L;

	private static final String TITLE = "Rückgabe";
	private static final String SELECT_LIST = "Rückgabelisten drucken";
	private static final String ENTER_DATA = "Rückgabelisten ins System einpflegen";

	private Accordion accordionContent;
	private ListSelector listSelector;
	private EnterDataComponent enterDataComponent;
	private MultiClassChooser multiClassChooser;

	@Inject
	public ReturnView(ViewModelComposer viewModelComposer, ReturnViewModel returnViewModel, MultiClassChooser multiClassChooser) {
		bindViewModel(viewModelComposer, returnViewModel);
		this.multiClassChooser = multiClassChooser;
		init();
		buildLayout();
	}

	private void init() {
		accordionContent = new Accordion();
		listSelector = new ListSelector(multiClassChooser, ListSelector.Process.RETURNING);
		enterDataComponent = new EnterDataComponent(EnterDataComponent.Process.RETURNING);
	}

	private void buildLayout() {
		accordionContent.addTab(listSelector, SELECT_LIST);
		accordionContent.addTab(enterDataComponent, ENTER_DATA);
		accordionContent.setSelectedTab(enterDataComponent);

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
}
