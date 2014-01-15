package de.dhbw.humbuch.model.entity;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Profile {

	public static final Set<Subject> E = EnumSet.of(Subject.ENGLISH);
	public static final Set<Subject> EL = EnumSet.of(Subject.ENGLISH, Subject.LATIN);
	public static final Set<Subject> EF = EnumSet.of(Subject.ENGLISH, Subject.FRENCH2);
	public static final Set<Subject> ELF = EnumSet.of(Subject.ENGLISH, Subject.LATIN, Subject.FRENCH3);
	public static final Set<Subject> NONE = EnumSet.noneOf(Subject.class);
	
	public static Map<String, Set<Subject>> getProfileMap() {
		HashMap<String, Set<Subject>> profileMap = new HashMap<String, Set<Subject>>();
		profileMap.put("E", E);
		profileMap.put("EL", EL);
		profileMap.put("EF", EF);
		profileMap.put("ELF", ELF);
		profileMap.put("KEINS", NONE);
		
		return profileMap;
	}
	
}
