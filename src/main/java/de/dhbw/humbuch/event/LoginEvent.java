package de.dhbw.humbuch.event;

/**
 * Example event for the Guava EventBus
 * 
 * @author David Herrmann
 */
public class LoginEvent {
	public final String message;

	public LoginEvent(String message) {
		this.message = message;
	}
}
