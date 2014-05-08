package de.dhbw.humbuch.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class ErrorView extends VerticalLayout implements View, ViewInformation{
	
	private static final long serialVersionUID = -3694130687747862451L;
	private static final String TITLE = "Error 404";
	
	private Label errorText;
	
	public ErrorView() {
		errorText = new Label("Die angeforderte Seite existiert nicht. Bitte kontaktieren Sie einen Administrator.");
		addComponent(errorText);
		setMargin(true);
	}
	@Override
	public void enter(ViewChangeEvent event) {
	}
	
	@Override
	public String getTitle() {
		return TITLE;
	}

}
