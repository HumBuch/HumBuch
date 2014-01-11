package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.view.components.StudentMaterialSelector;
import de.dhbw.humbuch.viewmodel.ReturnViewModel;


public class ReturnView extends VerticalLayout implements View, ViewInformation {

	private static final long serialVersionUID = -525078997965992622L;

	private static final String TITLE = "Rückgabe";

	private StudentMaterialSelector studentMaterialSelector;

	@Inject
	public ReturnView(ViewModelComposer viewModelComposer, ReturnViewModel returnViewModel) {
		bindViewModel(viewModelComposer, returnViewModel);
		init();
		buildLayout();
	}

	private void init() {
//		studentMaterialSelector = new StudentMaterialSelector(StudentMaterialSelector.Process.RETURNING);
		
		setSpacing(true);
		setMargin(true);
	}

	private void buildLayout() {
//		addComponent(studentMaterialSelector);
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
