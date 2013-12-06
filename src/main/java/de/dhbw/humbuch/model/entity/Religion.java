package de.dhbw.humbuch.model.entity;

public enum Religion {
	EVANGELIC("evangelisch"),
	ROMANCATHOLIC("r�misch-katholisch"),
	ETHICS("Ethik");
	
	private String value;
	
	private Religion(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
