package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.viewmodel.HomeViewModel;


public class HomeView extends VerticalLayout implements View {

	private static final long serialVersionUID = 2068213441417298070L;

	private Button button;

	@Inject
	public HomeView(ViewModelComposer viewModelComposer, HomeViewModel homeViewModel) {
		init();
		bindViewModel(viewModelComposer, homeViewModel);
	}

	private void init() {
		button = new Button("navigate", new Button.ClickListener() {
			private static final long serialVersionUID = -8316755622964189310L;

			@Override
			public void buttonClick(ClickEvent event) {
				// TODO: HACK! save strings in enum
				getUI().getNavigator().navigateTo(MainUI.BOOK_MANAGEMENT_VIEW);
			}
		});

		addComponents();
	}

	private void addComponents() {
		addComponent(button);
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
