package de.dhbw.humbuch.event;

/**
 * Object for the Guava EventBus containing a message and a message tpye
 * 
 */
public class MessageEvent {

	public final String message;
	public final Type type;

	public enum Type {
		INFO, WARNING, ERROR;
	}

	/**
	 * Creates an event with the specified message and {@link Type}.INFO as
	 * standard type
	 * 
	 * @param message
	 *            {@link String} containing the message
	 */
	public MessageEvent(String message) {
		this(message, Type.INFO);
	}

	/**
	 * Creates an event with the specified message and type
	 * 
	 * @param message
	 *            {@link String} containing the message
	 * @param type
	 *            {@link Type} defining the message type
	 */
	public MessageEvent(String message, Type type) {
		this.message = message;
		this.type = type;
	}
}
