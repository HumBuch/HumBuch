package de.dhbw.humbuch.view.components;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

public class PrintingComponent extends CustomComponent {

	private static final long serialVersionUID = 7241708455406233966L;

	private StreamResource streamResource;
	private String windowTitle;
	private Window window;

	public PrintingComponent(StreamResource streamResource, String windowTitle) {
		this.streamResource = streamResource;
		this.windowTitle = windowTitle;
		showWindow();
	}

	private void showWindow() {
		window = new Window(windowTitle);
		window.setSizeFull();
		window.setResizable(false);
		window.setDraggable(false);
		window.setModal(true);
		window.addStyleName("pdf-printing");
		
		BrowserFrame embedded = new BrowserFrame();
		embedded.setSizeFull();
		// Set the right mime type
		streamResource.setMIMEType("application/pdf");

		embedded.setSource(streamResource);
		window.setContent(embedded);
		UI.getCurrent().addWindow(window);
	}
}
