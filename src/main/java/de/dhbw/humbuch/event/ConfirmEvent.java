package de.dhbw.humbuch.event;

import java.util.UUID;

/**
 * Event for showing a confirm dialogue holding two runnables which can be called 
 * for either of two events: cancel, confirm
 * 
 * @author David Vitt
 */
public class ConfirmEvent {
	
	public final UUID id;
	
	public final String caption;
	public final String message;
	public final String confirmCaption;
	public final String cancelCaption;
	private final Runnable confirmRunnable;
	private final Runnable cancelRunnable;
	
	private static final String DEFAULT_WINDOW_CAPTION = "Bestätigen";
	private static final String DEFAULT_CONFIRM_CAPTION = "Ok";
	private static final String DEFAULT_CANCEL_CAPTION = "Abbrechen";
	
	public void confirm() {
		if(confirmRunnable != null)
			confirmRunnable.run();
	}

	public void cancel() {
		if(cancelRunnable != null)
			cancelRunnable.run();
	}
	
	public static class Builder {
		private final String message;
		private String caption;
		private String confirmCaption;
		private String cancelCaption;
		private Runnable confirmRunnable;
		private Runnable cancelRunnable;
		
		public Builder(String message) {
			this.message = message;
			this.caption = DEFAULT_WINDOW_CAPTION;
			this.confirmCaption = DEFAULT_CONFIRM_CAPTION;
			this.cancelCaption = DEFAULT_CANCEL_CAPTION;
		}
		
		public Builder caption(String caption) {
			this.caption = caption;
			return this;
		}
		
		public Builder confirmCaption(String confirmCaption) {
			this.confirmCaption = confirmCaption;
			return this;
		}
		
		public Builder cancelCaption(String cancelCaption) {
			this.cancelCaption = cancelCaption;
			return this;
		}
		
		public Builder confirmRunnable(Runnable runnable) {
			this.confirmRunnable = runnable;
			return this;
		}
		
		public Builder cancelRunnable(Runnable runnable) {
			this.cancelRunnable = runnable;
			return this;
		}
		
		public ConfirmEvent build() {
			return new ConfirmEvent(this);
		}
	}
	
	private ConfirmEvent(Builder builder) {
		this.caption = builder.caption;
		this.message = builder.message;
		this.confirmCaption = builder.confirmCaption;
		this.cancelCaption = builder.cancelCaption;
		this.confirmRunnable = builder.confirmRunnable;
		this.cancelRunnable = builder.cancelRunnable;
		
		id = UUID.randomUUID();
	}
}
