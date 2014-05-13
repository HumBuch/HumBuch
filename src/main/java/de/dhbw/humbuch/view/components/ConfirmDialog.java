package de.dhbw.humbuch.view.components;

import java.io.Serializable;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

/**
 * @author David Vitt
 *
 */
public class ConfirmDialog extends Window {
	private static final long serialVersionUID = 5531902128467189080L;

	private static final String DEFAULT_WINDOW_CAPTION = "Bestätigen";
	private static final String DEFAULT_CONFIRM_CAPTION = "Ok";
	private static final String DEFAULT_CANCEL_CAPTION = "Abbrechen";
	
	public interface Listener extends Serializable {
		void onClose(ConfirmDialog dialog);
	}
	
	private Listener confirmListener = null;
	private boolean isConfirmed = false;
	
	public static void show(String message, Listener listener) {
		show(DEFAULT_WINDOW_CAPTION, message, listener);
	}
	
	public static void show(String caption, String message, Listener listener) {
		show(caption, message, DEFAULT_CONFIRM_CAPTION, DEFAULT_CANCEL_CAPTION, listener);
	}
	
	public static void show(String caption, String message, String confirmCaption, String cancelCaption, Listener listener) {
		new ConfirmDialog(caption, message, confirmCaption, cancelCaption, listener).show(UI.getCurrent());
	}
	
	private void show(UI parentWindow) {
		parentWindow.addWindow(this);
	}
	
	private ConfirmDialog(String caption, String message, String confirmCaption, String cancelCaption, Listener listener) {
		setListener(listener);
		setCaption(caption);

		addCloseListener(new Window.CloseListener() {
			private static final long serialVersionUID = -1251710433868337579L;

			@Override
			public void windowClose(CloseEvent e) {
				if(ConfirmDialog.this.isEnabled()) {
					ConfirmDialog.this.setEnabled(false);
					
					ConfirmDialog.this.setConfirmed(false);
					if(ConfirmDialog.this.getListener() != null) {
						ConfirmDialog.this.getListener().onClose(ConfirmDialog.this);
					}
				}
			}
		});
		
		// Create content
        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);
        content.setMargin(true);
        
        // Label for message
        Label text = new Label();
        text.setValue(message);
        text.setContentMode(ContentMode.HTML);
        
        // Panel for message
        Panel messagePanel = new Panel(text);
        messagePanel.setSizeFull();
        content.addComponent(messagePanel);
        content.setExpandRatio(messagePanel, 1f);

        // Layout for buttons
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        content.addComponent(buttons);
        content.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
        
        // Cancel button
        final Button cancelBtn = new Button(cancelCaption);
        cancelBtn.setClickShortcut(KeyCode.ESCAPE, null);
        cancelBtn.focus();
        buttons.addComponent(cancelBtn);
        
        // Confirm button
        final Button confirmBtn = new Button(confirmCaption);
        confirmBtn.addStyleName("default");
        buttons.addComponent(confirmBtn);
        

        // ClickListener for buttons
        Button.ClickListener cl = new Button.ClickListener() {
			private static final long serialVersionUID = 165308935170840683L;

			@Override
			public void buttonClick(ClickEvent event) {
				if(ConfirmDialog.this.isEnabled()) {
					ConfirmDialog.this.setEnabled(false);
					
					ConfirmDialog.this.setConfirmed(event.getButton() == confirmBtn);
					ConfirmDialog.this.close();
					
					if (ConfirmDialog.this.getListener() != null) {
						ConfirmDialog.this.getListener().onClose(ConfirmDialog.this);
                    }
				}
			}
		};
		confirmBtn.addClickListener(cl);
		cancelBtn.addClickListener(cl);
		
		setContent(content);
		setResizable(false);
		setModal(true);
	}
	
	private void setConfirmed(boolean confirmed) {
		isConfirmed = confirmed;
	}
	
	private Listener getListener() {
		return confirmListener;	
	}
	
	private void setListener(Listener listener) {
		confirmListener = listener;
	}
	
	public boolean isConfirmed() {
		return isConfirmed;
	}
}
