package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.viewmodel.BookManagementViewModel;


public class BookManagementView extends VerticalLayout implements View {
	private static final long serialVersionUID = -5063268947544706757L;
	
	private Label label;

	@Inject
	public BookManagementView(ViewModelComposer viewModelComposer, BookManagementViewModel bookManagementViewModel) {
		init();
		bindViewModel(viewModelComposer, bookManagementViewModel);
	}

	private void init() {
		label = new Label("welcome to book management");

		addComponents();
	}

	private void addComponents() {
		addComponent(label);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
	}

	private void bindViewModel(ViewModelComposer viewModelComposer, Object... viewModels) {
		try {
			viewModelComposer.bind(this, viewModels);
		}
		catch (IllegalAccessException | NoSuchElementException
				| UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}
}
