package de.dhbw.humbuch.view.components;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
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

		Embedded embedded = new Embedded();
		embedded.setSizeFull();
		embedded.setType(Embedded.TYPE_BROWSER);
		// Set the right mime type
		streamResource.setMIMEType("application/pdf");

		embedded.setSource(streamResource);
		window.setContent(embedded);
		getUI().getCurrent().addWindow(window);
	}
}