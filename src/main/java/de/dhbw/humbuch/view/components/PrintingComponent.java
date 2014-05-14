package de.dhbw.humbuch.view.components;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import elemental.events.KeyboardEvent.KeyCode;

/**
 * @author Johannes Idelhauser
 * @author Benjamin Räthlein
 *
 */
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
		window.setWidth("70%");
		window.setHeight("90%");

		window.setResizable(false);
		window.setDraggable(true);
		window.setModal(true);
		window.setCloseShortcut(KeyCode.ESC, null);
		
		BrowserFrame embedded = new BrowserFrame();
		embedded.setWidth("100%");
		embedded.setHeight("99%");
		// Set the right mime type
		streamResource.setMIMEType("application/pdf");

		embedded.setSource(streamResource);
		window.setContent(embedded);
		UI.getCurrent().addWindow(window);
	}
}
