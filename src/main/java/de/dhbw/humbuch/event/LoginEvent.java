package de.dhbw.humbuch.event;

/**
 * Example event for the Guava EventBus
 * 
 * @author David Herrmann
 */
public class LoginEvent {
	private final String message;

	public LoginEvent(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
