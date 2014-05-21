package de.dhbw.humbuch.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * {@link View} that is displayed if the user tries to navigate to a
 * non-existent view.
 * 
 * @author Johannes Idelhauser
 */
public class ErrorView extends VerticalLayout implements View, ViewInformation {

	private static final long serialVersionUID = -3694130687747862451L;
	private static final String TITLE = "Angeforderte Seite nicht vorhanden";

	private Label errorText;

	public ErrorView() {
		errorText = new Label("Der Link hat nichts zu zeigen, vielleicht haben Sie"
				+ "den Link falsch eingegeben oder sind einem falschen gefolgt.");
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
