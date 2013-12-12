package de.dhbw.humbuch.model.entity;

public enum ProfileType {
	EVANGELIC("evangelisch"),
	ROMANCATHOLIC("römisch-katholisch"),
	ETHICS("Ethik"),
	STANDARD("Standard"),
	LATIN("Latein"),
	FRENCH2("Franz2"),
	FRENCH3("Franz3"),
	SCIENCE("NWT");
	
	private String value;
	
	private ProfileType(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return value;
	}
}
