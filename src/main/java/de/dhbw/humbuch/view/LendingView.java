package de.dhbw.humbuch.view;

import java.util.Collection;
import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.view.components.EnterDataComponent;
import de.dhbw.humbuch.view.components.ListSelector;
import de.dhbw.humbuch.view.components.MultiClassChooser;
import de.dhbw.humbuch.viewmodel.LendingViewModel;
import de.dhbw.humbuch.viewmodel.LendingViewModel.Students;


public class LendingView extends VerticalLayout implements View, ViewInformation {

	private static final long serialVersionUID = -6400075534193735694L;

	private static final String TITLE = "Ausleihe";
	private static final String SELECT_LIST = "Ausleihlisten drucken";
	private static final String ENTER_DATA = "Ausleihlisten ins System einpflegen";

	private Accordion accordionContent;
	private ListSelector listSelector;
	private EnterDataComponent enterDataComponent;
	
	@BindState(Students.class)
	private State<Collection<Student>> students = new BasicState<>(Collection.class);
	
	private LendingViewModel lendingViewModel;
	
	@Inject
	private MultiClassChooser multiClassChooser;

	@Inject
	public LendingView(ViewModelComposer viewModelComposer, LendingViewModel lendingViewModel, MultiClassChooser multiClassChooser) {
		this.lendingViewModel = lendingViewModel;
		this.multiClassChooser = multiClassChooser;
		bindViewModel(viewModelComposer, lendingViewModel);
		init();
		buildLayout();
	}

	private void init() {
		accordionContent = new Accordion();
		listSelector = new ListSelector(multiClassChooser, ListSelector.Process.LENDING);
		enterDataComponent = new EnterDataComponent(EnterDataComponent.Process.LENDING);
		enterDataComponent.addSaveButtonClickedListener(new ClickListener() {
			private static final long serialVersionUID = 5631960105849600201L;

			@Override
			public void buttonClick(ClickEvent event) {
				Collection<BorrowedMaterial> selectedBorrowedMaterials = enterDataComponent.getSelectedBorrowedMaterials();
				LendingView.this.lendingViewModel.setBorrowedMaterialsReceived(selectedBorrowedMaterials);
			}
		});
		
		students.addStateChangeListener(new StateChangeListener() {
			
			@Override
			public void stateChange(Object value) {
				if(value == null) {
					return;
				}
				enterDataComponent.setStudents(students.get());
			}
		});
	}

	private void buildLayout() {
		accordionContent.addTab(listSelector, SELECT_LIST);
		accordionContent.addTab(enterDataComponent, ENTER_DATA);
		accordionContent.setSelectedTab(enterDataComponent);
		accordionContent.addSelectedTabChangeListener(new TabChangeListener());

		addComponent(accordionContent);
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
	
	private class TabChangeListener implements SelectedTabChangeListener {
		private static final long serialVersionUID = 1993690272458696837L;
		
		@Override
		public void selectedTabChange(SelectedTabChangeEvent event) {
			Component selectedTab = event.getTabSheet().getSelectedTab();
			if(selectedTab.equals(listSelector)) {
				
			} else if(selectedTab.equals(enterDataComponent)) {

			}
		}
	}
}
