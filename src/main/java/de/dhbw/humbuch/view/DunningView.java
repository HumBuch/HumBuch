package de.dhbw.humbuch.view;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.event.MessageEvent;
import de.dhbw.humbuch.event.MessageEvent.Type;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Dunning;
import de.dhbw.humbuch.util.PDFDunning;
import de.dhbw.humbuch.util.PDFHandler;
import de.dhbw.humbuch.view.components.PrintingComponent;
import de.dhbw.humbuch.viewmodel.DunningViewModel;
import de.dhbw.humbuch.viewmodel.DunningViewModel.StudentsDunned;
import de.dhbw.humbuch.viewmodel.DunningViewModel.StudentsToDun;

public class DunningView extends VerticalLayout implements View,
		ViewInformation {

	private static final long serialVersionUID = 1284094636968999625L;

	private static final String TITLE = "Mahnungs Übersicht";
	private static final String DUNNING_SENT = "Mahnung als versendet markieren";
	private static final String SHOW_DUNNING = "Mahnung anzeigen";

	// TODO: Dynamically / directly from database
	private static final String TABLE_LAST_NAME = "Nachname";
	private static final String TABLE_FIRST_NAME = "Vorname";
	private static final String TABLE_CLASS = "Klasse";
	private static final String TABLE_TYPE = "Typ";

	private EventBus eventBus;
	private DunningViewModel dunningViewModel;
	
	@BindState(StudentsDunned.class)
	public final State<Collection<Dunning>> studentsDunned = new BasicState<>(Collection.class);
	
	@BindState(StudentsToDun.class)
	public final State<Collection<Dunning>> studentsToDun = new BasicState<>(Collection.class);

	private Map<Integer,Dunning> allDunnings = new HashMap<>();
	private Dunning selectedDunning;
	
	/**
	 * Layout components
	 */
	private HorizontalLayout horizontalLayoutButtonBar;
	private Button btnDunningSent = new Button(DUNNING_SENT);
	private Button btnShowDunning = new Button(SHOW_DUNNING);
	private Table tableDunnings;
	

	@Inject
	public DunningView(ViewModelComposer viewModelComposer,
			DunningViewModel dunningViewModel, EventBus eventBus) {
		this.dunningViewModel = dunningViewModel;
		this.eventBus = eventBus;
		init();
		buildLayout();
		bindViewModel(viewModelComposer, dunningViewModel);
	}
	
	private void init() {
		horizontalLayoutButtonBar = new HorizontalLayout();
		btnDunningSent.setEnabled(false);
		btnShowDunning.setEnabled(false);
		tableDunnings = new Table();

		tableDunnings.setSelectable(true);
		tableDunnings.setSizeFull();
		tableDunnings
				.addContainerProperty(TABLE_FIRST_NAME, String.class, null);
		tableDunnings.addContainerProperty(TABLE_LAST_NAME, String.class, null);
		tableDunnings.addContainerProperty(TABLE_CLASS, String.class, null);
		tableDunnings.addContainerProperty(TABLE_TYPE, String.class, null);
		setSpacing(true);
		setMargin(true);

		horizontalLayoutButtonBar.setSpacing(true);
		addListener();
	}
	
	private void buildLayout() {
		setSpacing(true);
		setMargin(true);
		setSizeFull();
		horizontalLayoutButtonBar.addComponent(btnDunningSent);
		horizontalLayoutButtonBar.addComponent(btnShowDunning);
		addComponent(horizontalLayoutButtonBar);
		addComponent(tableDunnings);
		setExpandRatio(tableDunnings, 1);
	}


	private void addListener() {
		StateChangeListener stateChange = new StateChangeListener() {
			@Override
			public void stateChange(Object value) {
				Collection<Dunning> tableData = (Collection<Dunning>) value;
				for (Dunning dunning : tableData) {
					allDunnings.put(dunning.getId(), dunning);
					tableDunnings.addItem(new Object[] {
							dunning.getStudent().getFirstname(),
							dunning.getStudent().getLastname(),
							dunning.getStudent().getGrade().toString(),
							dunning.getType().toString() + " " + dunning.getStatus().toString() },
							dunning.getId());
				}
			}

		};
		studentsDunned.addStateChangeListener(stateChange);
		studentsToDun.addStateChangeListener(stateChange);
		tableDunnings.setImmediate(true);
		tableDunnings.addValueChangeListener(new Table.ValueChangeListener() {
			private static final long serialVersionUID = -4224382328843243771L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				if(tableDunnings.size()==0 || tableDunnings.getValue() == null) {
					btnShowDunning.setEnabled(false);
					btnDunningSent.setEnabled(false);
					btnDunningSent.removeStyleName("default");
					return;
				}
				selectedDunning = allDunnings.get(Integer.parseInt(tableDunnings.getValue().toString()));
				if(selectedDunning.getType() == Dunning.Type.TYPE1 && selectedDunning.getStatus() == Dunning.Status.OPENED) {
					btnDunningSent.setEnabled(true);
					btnDunningSent.addStyleName("default");
					btnShowDunning.setEnabled(true);
				}
				else if(selectedDunning.getType() == Dunning.Type.TYPE2 && selectedDunning.getStatus() == Dunning.Status.OPENED) {
					btnDunningSent.setEnabled(true);
					btnDunningSent.addStyleName("default");
					btnShowDunning.setEnabled(true);
				}
				else if(selectedDunning.getStatus() == Dunning.Status.SENT) {
					btnShowDunning.setEnabled(true);
					btnDunningSent.setEnabled(false);
					btnDunningSent.removeStyleName("default");
				}
				else {
					btnDunningSent.setEnabled(false);
					btnDunningSent.removeStyleName("default");
					btnShowDunning.setEnabled(true);
					
				}
			}
		});
		btnDunningSent.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 7963891536949402850L;

			@Override
			public void buttonClick(ClickEvent event) {
				selectedDunning.setStatus(Dunning.Status.SENT);
				dunningViewModel.doUpdateDunning(selectedDunning);
				tableDunnings.removeAllItems();
				dunningViewModel.refresh();
			}
		});
		btnShowDunning.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -1285703858095198175L;

			@Override
			public void buttonClick(ClickEvent event) {
				Set<List<BorrowedMaterial>> setBorrowedMaterial = new HashSet<List<BorrowedMaterial>>(); 
				setBorrowedMaterial.add(new ArrayList<BorrowedMaterial>(selectedDunning.getBorrowedMaterials()));
				ByteArrayOutputStream baos;
				if(selectedDunning.getType() == Dunning.Type.TYPE1) {
					baos = PDFDunning.createFirstDunning(setBorrowedMaterial).createByteArrayOutputStreamForPDF();
				}
				else {
					baos = PDFDunning.createSecondDunning(setBorrowedMaterial).createByteArrayOutputStreamForPDF();
				}
				String fileNameIncludingHash = ""+ new Date().hashCode() + "_MAHNUNG_"+selectedDunning.getStudent().getFirstname()+"_"+selectedDunning.getStudent().getLastname();
				if(baos == null) {
					eventBus.post(new MessageEvent("Fehler", "PDF konnte nicht erstellt werden", Type.ERROR));
					return;
				}
				StreamResource sr = new StreamResource(new PDFHandler.PDFStreamSource(baos), fileNameIncludingHash);
				new PrintingComponent(sr, "Mahnung");
			}
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		dunningViewModel.refresh();
	}

	private void bindViewModel(ViewModelComposer viewModelComposer,
			Object... viewModels) {
		try {
			viewModelComposer.bind(this, viewModels);
		} catch (IllegalAccessException | NoSuchElementException
				| UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getTitle() {
		return TITLE;
	}
}
