package de.dhbw.humbuch.ui.screens;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.ui.components.Task;
import de.dhbw.humbuch.viewmodel.HomeScreenModel;

@Theme("mytheme")
@SuppressWarnings("serial")
public class HomeScreen extends AbstractBasicScreen {
	
	@Inject
	public HomeScreen(ViewModelComposer viewModelComposer, HomeScreenModel homeScreenModel) {
		bindViewModel(viewModelComposer, homeScreenModel);
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.dhbw.humbuch.ui.screens.AbstractBasicScreen#init(com.vaadin.server.VaadinRequest, com.vaadin.ui.Panel)
	 * This function is called from the init function in AbstractBasicScreen
	 */
	protected void init(VaadinRequest request, Panel panel) {
		System.out.println("HomeScreen");
		// TODO: the tasks should be loaded from database and added to the ui in loop. just a proof of concept here
		VerticalLayout vl = new VerticalLayout();
		Task t = new Task(Task.TASK_DUMMY);
		Task t2 = new Task(Task.TASK_DUMMY);
		
		vl.setSizeFull();
		vl.addComponent(t);
		vl.addComponent(t2);
		panel.setCaption("Aufgaben Management");

		panel.setContent(vl);
	}
	
	private void bindViewModel(ViewModelComposer viewModelComposer, Object... viewModels) {
		try {
			viewModelComposer.bind(this, viewModels);
		} catch (IllegalAccessException | NoSuchElementException
				| UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void enter(ViewChangeEvent event) {
	}
}
