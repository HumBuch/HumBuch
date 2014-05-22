package de.dhbw.humbuch.event;

/**
 * Event for the EventBus containing a message and a message type
 * 
 * @author davherrmann
 */
public class MessageEvent {

	public final String caption;
	public final String message;
	public final Type type;

	public enum Type {
		INFO, TRAYINFO, WARNING, ERROR;
	}

	/**
	 * Creates an event with the specified caption and {@link Type}.INFO as
	 * standard type
	 * 
	 * @param message
	 *            {@link String} containing the message
	 */
	public MessageEvent(String caption) {
		this(caption, "", Type.INFO);
	}

	/**
	 * Creates an event with the specified caption, message and {@link Type}.INFO as
	 * standard type
	 * 
	 * @param caption
	 * 			  {@link String} containing the caption
	 * @param message
	 *            {@link String} containing the message
	 */
	public MessageEvent(String caption, String message) {
		this(caption, message, Type.INFO);
	}
	
	/**
	 * Creates an event with the specified caption, empty message and type
	 * 
	 * @param caption
	 * 			  {@link String} containing the caption
	 * @param message
	 *            {@link String} containing the message
	 */
	public MessageEvent(String caption, Type type) {
		this(caption, "", type);
	}
	
	/**
	 * Creates an event with the specified caption, message and type
	 * 
	 * @param caption
	 * 			  {@link String} containing the caption
	 * @param message
	 *            {@link String} containing the message
	 * @param type
	 *            {@link Type} defining the message type
	 */
	public MessageEvent(String caption, String message, Type type) {
		this.caption = caption;
		this.message = message;
		this.type = type;
	}
}
