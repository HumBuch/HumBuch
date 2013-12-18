package de.dhbw.humbuch.model.entity;

public enum Subject {
	
	ENGLISH("Englisch"),
	LATIN("Latein"),
	FRENCH2("Französisch 2"),
	FRENCH3("Französisch 3"),
	
	EVANGELIC("evangelisch"),
	ROMAN_CATHOLIC("römisch-katholisch"),
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