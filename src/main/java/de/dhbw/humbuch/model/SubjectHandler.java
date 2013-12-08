package de.dhbw.humbuch.model;

import de.dhbw.humbuch.model.entity.Subject;


public class SubjectHandler {
	
	public static Subject createSubject(String name){
		Subject subject = new Subject();
		subject.setName(name);
		
		return subject;
	}

}
