package de.dhbw.humbuch.view;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
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
import de.dhbw.humbuch.viewmodel.DunningViewModel.Dunnings;

/**
 * Provides the {@link View} to display and manage {@link Dunning}s
 * @author Johannes Idelhauser
 * @author Martin Wentzel
 */
public class DunningView extends VerticalLayout implements View,
		ViewInformation {

	private static final long serialVersionUID = 1284094636968999625L;

	private static final String TITLE = "Mahnungen";

	private static final String TABLE_LASTNAME = "student.lastname";
	private static final String TABLE_FIRSTNAME = "student.firstname";
	private static final String TABLE_GRADE = "student.grade";
	private static final String TABLE_TYPE = "type";
	private static final String TABLE_STATUS = "status";
	
	private CheckBox cbOpenDunnings = new CheckBox("Nur offene Mahnungen anzeigen");
	private Button btnDunningSent = new Button("Als versendet markieren");
	private Button btnShowDunning = new Button("Mahnung anzeigen");
	private Table tblDunnings;
	private TextField txtFilter;

	private DunningViewModel dunningViewModel;
	
	@BindState(Dunnings.class)
	public final State<Collection<Dunning>> dunnings = new BasicState<>(Collection.class);
	private EventBus eventBus;
	private BeanItemContainer<Dunning> tableData;

	@Inject
	public DunningView(ViewModelComposer viewModelComposer, DunningViewModel dunningViewModel, EventBus eventBus) {
		this.dunningViewModel = dunningViewModel;
		this.eventBus = eventBus;
		init();
		bindViewModel(viewModelComposer, dunningViewModel);
	}
	
	/**
	 * Creates UI elements and initializes the View 
	 */
	private void init() {
		HorizontalLayout head = new HorizontalLayout();
		head.setWidth("100%");
		head.setSpacing(true);
		
		//Filter
		VerticalLayout filterLayout = new VerticalLayout();
		
		txtFilter = new TextField();
		txtFilter.setImmediate(true);
		txtFilter.setInputPrompt("Nach Name, Vorname oder Klasse filtern...");
		txtFilter.setWidth("50%");
		txtFilter.setTextChangeEventMode(TextChangeEventMode.EAGER);
		
		filterLayout.addComponent(txtFilter);
		filterLayout.addComponent(cbOpenDunnings);
		
		head.addComponent(filterLayout);
		head.setExpandRatio(filterLayout, 1);
		head.setComponentAlignment(filterLayout, Alignment.MIDDLE_LEFT);
		
		//Buttons
		HorizontalLayout buttons = new HorizontalLayout();
		buttons.setSpacing(true);
		buttons.addComponent(btnDunningSent);
		buttons.addComponent(btnShowDunning);
		
		btnDunningSent.setEnabled(false);
		btnShowDunning.setEnabled(false);
		
		head.addComponent(buttons);
		head.setComponentAlignment(buttons, Alignment.TOP_RIGHT);
		addComponent(head);
		
		//Table
		tblDunnings = new Table();
		tblDunnings.setSelectable(true);
		tblDunnings.setSizeFull();
		tblDunnings.setImmediate(true);
		tblDunnings.setColumnCollapsingAllowed(true);
		
		tableData = new BeanItemContainer<Dunning>(Dunning.class);
		tableData.addNestedContainerProperty(TABLE_LASTNAME);
		tableData.addNestedContainerProperty(TABLE_FIRSTNAME);
		tableData.addNestedContainerProperty(TABLE_GRADE);
		
		tblDunnings.setContainerDataSource(tableData);

		tblDunnings.setVisibleColumns(new Object[] { TABLE_LASTNAME, TABLE_FIRSTNAME, TABLE_GRADE, TABLE_TYPE, TABLE_STATUS });
		tblDunnings.setColumnHeader(TABLE_LASTNAME, "Name");
		tblDunnings.setColumnHeader(TABLE_FIRSTNAME, "Vorname");
		tblDunnings.setColumnHeader(TABLE_GRADE, "Klasse");
		tblDunnings.setColumnHeader(TABLE_TYPE, "Typ");
		tblDunnings.setColumnHeader(TABLE_STATUS, "Status");
		
		addComponent(tblDunnings);
		setExpandRatio(tblDunnings, 1);
		
		setSizeFull();
		setSpacing(true);
		setMargin(true);
		
		addListener();
	}

	/**
	 * Adds listeners to different UI elements as well as to states.
	 */
	private void addListener() {
		
		/**
		 * Listens for changes in all dunning collection an adds them to the
		 * container
		 */
		dunnings.addStateChangeListener(new StateChangeListener() {
			@Override
			public void stateChange(Object value) {
				tableData.removeAllItems();
				for (Dunning dunning : dunnings.get()) {
					tblDunnings.addItem(dunning);
				}
			}

		});

		/**
		 * Enables/disables the buttons
		 */
		tblDunnings.addValueChangeListener(new Table.ValueChangeListener() {
			private static final long serialVersionUID = -4224382328843243771L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Dunning item = (Dunning) tblDunnings.getValue();
				btnShowDunning.setEnabled(item != null);
				
				//Check if the selected dunning can be marked as sent
				if (item != null && item.getStatus() == Dunning.Status.OPENED) {
					btnDunningSent.setEnabled(item != null);
				} else {
					btnDunningSent.setEnabled(false);
				}
			}
		});
		
		/**
		 * Set the send status of the dunning
		 */
		btnDunningSent.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 7963891536949402850L;

			@Override
			public void buttonClick(ClickEvent event) {
				Dunning item = (Dunning) tblDunnings.getValue();
				item.setStatus(Dunning.Status.SENT);
				dunningViewModel.doUpdateDunning(item);
			}
		});
		
		/**
		 * Open the pdf with the dunning
		 */
		btnShowDunning.addClickListener(new ClickListener() {
			private static final long serialVersionUID = -1285703858095198175L;

			@Override
			public void buttonClick(ClickEvent event) {
				Dunning item = (Dunning) tblDunnings.getValue();
				
				Set<List<BorrowedMaterial>> setBorrowedMaterial = new HashSet<List<BorrowedMaterial>>(); 
				setBorrowedMaterial.add(new ArrayList<BorrowedMaterial>(item.getBorrowedMaterials()));
				ByteArrayOutputStream baos;
				
				if(item.getType() == Dunning.Type.TYPE1) {
					baos = PDFDunning.createFirstDunning(setBorrowedMaterial).createByteArrayOutputStreamForPDF();
				}
				else {
					baos = PDFDunning.createSecondDunning(setBorrowedMaterial).createByteArrayOutputStreamForPDF();
				}
				String fileNameIncludingHash = ""+ new Date().hashCode() + "_MAHNUNG_"+item.getStudent().getFirstname()+"_"+item.getStudent().getLastname();
				if(baos == null) {
					eventBus.post(new MessageEvent("Fehler", "PDF konnte nicht erstellt werden", Type.ERROR));
					return;
				}
				StreamResource sr = new StreamResource(new PDFHandler.PDFStreamSource(baos), fileNameIncludingHash);
				new PrintingComponent(sr, "Mahnung");
			}
		});
		
		/**
		 * Provides the live search of the table by adding a filter after every
		 * keypress in the search field.
		 */
		txtFilter.addTextChangeListener(new TextChangeListener() {
			private static final long serialVersionUID = -1684545652234105334L;

			Filter filter = null;
			
			@Override
			public void textChange(TextChangeEvent event) {
				if (filter != null) {
					tableData.removeContainerFilter(filter);
				}
				SimpleStringFilter cond1 = new SimpleStringFilter(TABLE_LASTNAME, event.getText(), true, false);
				SimpleStringFilter cond2 = new SimpleStringFilter(TABLE_FIRSTNAME, event.getText(), true, false);
				SimpleStringFilter cond3 = new SimpleStringFilter(TABLE_GRADE, event.getText(), true, false);
				filter = new Or(cond1, cond2, cond3);
                tableData.addContainerFilter(filter);
			}
		});
		
		cbOpenDunnings.addValueChangeListener(new ValueChangeListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void valueChange(ValueChangeEvent event) {
				Filter filter = new Not(new Equal(TABLE_STATUS, Dunning.Status.CLOSED));
				if(cbOpenDunnings.getValue() == true) {
					tableData.addContainerFilter(filter);
				} else {
					tableData.removeContainerFilter(filter);
				}
				tblDunnings.setValue(null);
			}
		});
	}

	@Override
	public void enter(ViewChangeEvent event) {
		tblDunnings.setValue(null);
		dunningViewModel.refresh();
		cbOpenDunnings.setValue(true);
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
