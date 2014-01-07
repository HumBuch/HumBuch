package de.dhbw.humbuch.view;

import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.util.CSVUploader;
import de.dhbw.humbuch.viewmodel.ImportViewModel;
import de.dhbw.humbuch.viewmodel.ImportViewModel.ImportResult;

/**
 * Stellt die Oberfläche für den Import von Schülerdaten zur Verfügung.
 * 
 * @author Johannes
 * @version 1.0
 *
 */

public class ImportView extends VerticalLayout implements View, ViewInformation {

	private static final long serialVersionUID = -739081142499192817L;

	private static final String TITLE = "Schüler Import";

	@BindState(ImportResult.class)
	private BasicState<String> importResult = new BasicState<String>(
			String.class);

	// @BindState(UploadButton.class)
	// private BasicState<Upload> uploadButton = new
	// BasicState<Upload>(Upload.class);
	private Upload upload;
	private CSVUploader csvUploader;

	private Label labelResult;

	@Inject
	public ImportView(ViewModelComposer viewModelComposer,
			ImportViewModel importViewModel) {
		this.csvUploader = new CSVUploader(importViewModel);
		init();
		buildLayout();
		bindViewModel(viewModelComposer, importViewModel);
	}

	private void init() {
		setMargin(true);
		setSpacing(true);

		// Create and configure upload component
		upload = new Upload("Upload der Schülerdatei (als CSV):", csvUploader);
		upload.setImmediate(false);

		// uploadForm.addSucceededListener(csvUploader);
		// uploadForm.addFailedListener(csvUploader);
		upload.setButtonCaption("Importieren");

		// Import results
		labelResult = new Label();

		importResult.addStateChangeListener(new StateChangeListener() {

			@Override
			public void stateChange(Object arg0) {
				labelResult.setCaption(importResult.get());
			}
		});
	}

	private void buildLayout() {
		addComponent(upload);
		addComponent(labelResult);
	}

	@Override
	public void enter(ViewChangeEvent event) {
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