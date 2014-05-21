package de.dhbw.humbuch.guice;

import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.ui.UI;

import de.davherrmann.guice.vaadin.ScopedUIProvider;
import de.davherrmann.guice.vaadin.UIKeyProvider;

/**
 * {@link Provider} for providing scoped {@link UI}s
 *
 * @author davherrmann
 */
public class BasicProvider extends ScopedUIProvider {
	private static final long serialVersionUID = -8975200838602301472L;
	
	@Inject
	private Class<? extends UI> uiClass;
	
	@Inject
	protected BasicProvider(Map<String, Provider<UI>> uiProMap,
			UIKeyProvider uiKeyProvider) {
		super(uiProMap, uiKeyProvider);
	}

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return uiClass;
	}

}
