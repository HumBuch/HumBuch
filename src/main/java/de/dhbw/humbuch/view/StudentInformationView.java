package de.dhbw.humbuch.view;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.NoSuchElementException;

import com.google.inject.Inject;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.VerticalLayout;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.StateChangeListener;
import de.davherrmann.mvvm.ViewModelComposer;
import de.davherrmann.mvvm.annotations.BindState;
import de.dhbw.humbuch.viewmodel.ImportViewModel;
import de.dhbw.humbuch.viewmodel.ImportViewModel.ImportResult;

public class StudentInformationView extends VerticalLayout implements View, ViewInformation {

	private static final long serialVersionUID = -739081142499192817L;

	private static final String TITLE = "Schüler Import";

	private Upload upload;
	private UploadReceiver receiver = new UploadReceiver(5242880); // 5MB = 5242880

	private Label labelResult;

	protected ImportViewModel importViewModel;

	@BindState(ImportResult.class)
	private BasicState<String> importResult = new BasicState<String>(
			String.class);

	@Inject
	public StudentInformationView(ViewModelComposer viewModelComposer,
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

	public class UploadReceiver implements Receiver, Upload.SucceededListener,
			Upload.ProgressListener {

		private static final long serialVersionUID = 1L;
		private ByteArrayOutputStream outputStream;
		private final long maxSize;
		private boolean interrupted;

		/**
		 * @param maxSize
		 *            The maximum file size that will be accepted (in
		 *            bytes). -1 in case of no limit. 100Kb = 100000
		 */
		public UploadReceiver(long maxSize) {
			this.maxSize = maxSize;
		}

		public OutputStream receiveUpload(String filename, String MIMEType) {

			this.outputStream = new ByteArrayOutputStream();
			return outputStream;

		}

		public void uploadSucceeded(Upload.SucceededEvent event) {
			if (!interrupted) {
				importViewModel.receiveUploadByteOutputStream(outputStream);
			}
		}

		/**
		 * Interrupts the current upload
		 */
		protected void interrupt() {
			upload.interruptUpload();
			interrupted = true;
			new Notification(
					"Import nicht möglich.",
					"Die ausgewählte Datei ist zu groß. Bitte kontaktieren Sie einen Administrator.",
					Notification.Type.WARNING_MESSAGE).show(Page.getCurrent());
		}

		@Override
		public void updateProgress(long readBytes, long contentLength) {
			if (readBytes > maxSize || contentLength > maxSize) {
				interrupt();
			}

		}
	}
}