package de.dhbw.humbuch.view;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.viewmodel.ImportViewModel;
import de.dhbw.humbuch.viewmodel.ImportViewModel.ImportResult;


public class ImportView extends VerticalLayout implements View, ViewInformation {

	private static final long serialVersionUID = -739081142499192817L;

	private static final String TITLE = "Schüler Import";
	
	private Upload upload;
	private UploadReceiver receiver = new UploadReceiver();

	private Label labelResult;
	
	protected ImportViewModel importViewModel;

	@BindState(ImportResult.class)
	private BasicState<String> importResult = new BasicState<String>(
			String.class);

	@Inject
	public ImportView(ViewModelComposer viewModelComposer,
			ImportViewModel importViewModel) {
		this.importViewModel = importViewModel;
		init();
		buildLayout();
		bindViewModel(viewModelComposer, importViewModel);
	}

	private void init() {
		setMargin(true);
		setSpacing(true);

		// Create and configure upload component
		upload = new Upload("Upload der Schülerdatei (als CSV):", receiver);
		upload.setImmediate(false);

		upload.addSucceededListener(receiver);

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

	public class UploadReceiver implements Upload.Receiver,
			Upload.SucceededListener, Upload.FailedListener {

		private static final long serialVersionUID = 1L;

		private ByteArrayOutputStream outputStream;
		//private final long maxFileSize = 10;

		public OutputStream receiveUpload(String filename, String MIMEType) {

		    this.outputStream = new ByteArrayOutputStream();
		    return outputStream;
		    
		}

		public void uploadSucceeded(Upload.SucceededEvent event) {
			importViewModel.receiveUploadByteOutputStream(outputStream);
		}

		@Override
		public void uploadFailed(FailedEvent event) {
			// TODO Auto-generated method stub
			
		}
	}
}