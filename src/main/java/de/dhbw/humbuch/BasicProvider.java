package de.dhbw.humbuch;

import com.google.inject.Inject;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

public class BasicProvider extends UIProvider {
	private static final long serialVersionUID = -8975200838602301472L;

	@Inject
	private Class<? extends UI> uiClass;

	@Override
	public UI createInstance(UICreateEvent event) {
		return BasicFilter.getInjector().getProvider(uiClass).get();
	}

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return uiClass;
	}

}
