package de.dhbw.humbuch.event;

/**
 * Event for the EventBus holding a message after a successful/unsuccessful login
 * 
 * @author davherrmann
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
