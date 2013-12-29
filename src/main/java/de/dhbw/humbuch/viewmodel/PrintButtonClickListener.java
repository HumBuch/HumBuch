package de.dhbw.humbuch.viewmodel;

import java.io.ByteArrayOutputStream;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Window;

import de.dhbw.humbuch.util.PDFHandler;

public class PrintButtonClickListener implements Button.ClickListener {

	private static final long serialVersionUID = 1L;
	ByteArrayOutputStream byteArrayOutputStream;
	
	public PrintButtonClickListener(ByteArrayOutputStream byteArrayOutputStream){
		this.byteArrayOutputStream = byteArrayOutputStream;
	}
	
	@Override
	public void buttonClick(ClickEvent event) {
		Button button = (Button) event.getSource();
		Window window = new Window("Window to print");
		window.setSizeFull();
		
		Embedded embedded = new Embedded();
		embedded.setSizeFull();
		embedded.setType(Embedded.TYPE_BROWSER);

        // Here we create a new StreamResource which downloads our StreamSource,
        // which is our pdf.
        StreamResource resource = new StreamResource(new PDFHandler.PDFStreamSource(this.byteArrayOutputStream), "PDF.pdf");
        // Set the right mime type
		resource.setMIMEType("application/pdf");

		embedded.setSource(resource);	
		window.setContent(embedded);
		button.getUI().addWindow(window);		
	}
}