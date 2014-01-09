package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.view.components.StudentMaterialSelector;
import de.dhbw.humbuch.viewmodel.LendingViewModel;


public class LendingView extends VerticalLayout implements View, ViewInformation {

	private static final long serialVersionUID = -6400075534193735694L;

	private static final String TITLE = "Ausleihe";

	private StudentMaterialSelector studentMaterialSelector;

	@Inject
	public LendingView(ViewModelComposer viewModelComposer, LendingViewModel lendingViewModel) {
		bindViewModel(viewModelComposer, lendingViewModel);
		init();
		buildLayout();
	}

	private void init() {
		studentMaterialSelector = new StudentMaterialSelector(StudentMaterialSelector.Process.LENDING);
		
		setSpacing(true);
		setMargin(true);
	}

	private void buildLayout() {
		addComponent(studentMaterialSelector);
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
