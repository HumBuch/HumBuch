package de.dhbw.humbuch.model.entity;

/**
 * @author David Vitt
 *
 */
public enum Subject {

	STANDARD("Standard"),
	FRENCH2("Französisch 2"),
	FRENCH3("Französisch 3"),
	LATIN("Latein"),
	SCIENCE("NWT"),
	
	EVANGELIC("Evangelisch"),
	ROMAN_CATHOLIC("Römisch-Katholisch"),
	ETHICS("Ethik");
	
	private String value;
	
	private Subject(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}