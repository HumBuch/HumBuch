package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;
import com.vaadin.ui.themes.Runo;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.util.CSVUploader;
import de.dhbw.humbuch.viewmodel.ImportViewModel;
import de.dhbw.humbuch.viewmodel.ImportViewModel.ImportResult;


public class ImportView extends VerticalLayout implements View, ViewInformation {

	private static final long serialVersionUID = -739081142499192817L;

	private static final String TITLE = "Schüler Import";
	private static final String DESCRIPTION = "Betätigen Sie den Button um Schülerdaten zu importieren.";
	private static final String IMPORT = "Importieren";

	private Label labelDescription;

	@BindState(ImportResult.class)
	private BasicState<String> importResult = new BasicState<String>(String.class);

	//	@BindState(UploadButton.class)
	//	private BasicState<Upload> uploadButton = new BasicState<Upload>(Upload.class);
	private Upload uploadButton;
	private CSVUploader csvUploader;

	//@BindAction(value = DoImportStudents.class, source = { "uploadButton" })
	private Button buttonImport = new Button(IMPORT);

	private Label labelResult;

	@Inject
	public ImportView(ViewModelComposer viewModelComposer, ImportViewModel importViewModel) {
		this.csvUploader = new CSVUploader(importViewModel);
		init();
		buildLayout();
		bindViewModel(viewModelComposer, importViewModel);
	}

	private void init() {
		setMargin(true);
		setSpacing(true);

		labelResult = new Label("put result of import here. e.g. 4/5 successfully imported");

		labelDescription = new Label(DESCRIPTION);
		labelDescription.setStyleName(Runo.LABEL_H2);

		buttonImport.setIcon(new ThemeResource("images/icons/32/icon_upload_red.png"));
		buttonImport.setStyleName(BaseTheme.BUTTON_LINK);
		buttonImport.addClickListener(new ClickListener() {

			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				uploadButton.submitUpload();
			}
		});

		uploadButton = new Upload(null, this.csvUploader);
		uploadButton.addSucceededListener(this.csvUploader);
		uploadButton.addFailedListener(this.csvUploader);
		uploadButton.setButtonCaption(null);

		importResult.addStateChangeListener(new StateChangeListener() {

			@Override
			public void stateChange(Object arg0) {
				labelResult.setCaption(importResult.get());
			}
		});
	}

	private void buildLayout() {
		addComponent(labelDescription);
		addComponent(buttonImport);
		addComponent(labelResult);
		addComponent(uploadButton);
	}

	@Override
	public void enter(ViewChangeEvent event) {
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
	public String getTitle() {
		return TITLE;
	}
}