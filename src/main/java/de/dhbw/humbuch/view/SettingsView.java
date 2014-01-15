package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.viewmodel.SettingsViewModel;

public class SettingsView extends VerticalLayout implements View, ViewInformation {
	private static final long serialVersionUID = 616250739634877057L;
	
	private static final String TITLE = "Einstellungen";
	
	@Inject
	public SettingsView(ViewModelComposer viewModelComposer, SettingsViewModel settingsViewModel) {
		bindViewModel(viewModelComposer, settingsViewModel);
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
