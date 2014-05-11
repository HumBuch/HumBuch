package de.dhbw.humbuch.model;

import java.util.LinkedHashSet;
import java.util.Set;

import de.dhbw.humbuch.model.entity.Subject;

public final class SubjectHandler {
	
	public static Set<Subject> createProfile(String[] languageInformation, String religionInformation){
		Set<Subject> subjectSet = new LinkedHashSet<Subject>();
		for(int i = 0; i < languageInformation.length; i++){
			if(languageInformation[i].equals("E")){
				subjectSet.add(Subject.STANDARD);
			}
			if(languageInformation[i].equals("F")){
				if(i == 1){
					subjectSet.add(Subject.FRENCH2);
				}
				else if(i == 2){
					subjectSet.add(Subject.FRENCH3);
				}
			}
			if(languageInformation[i].equals("L")){
				subjectSet.add(Subject.LATIN);
			}
			
			if(i == 2 && languageInformation.equals("")){
				//subjectSet.add("NW");
			}
		}
		
		if(religionInformation.equals("rk")){
			subjectSet.add(Subject.ROMAN_CATHOLIC);
		}
		else if(religionInformation.equals("ev")){
			subjectSet.add(Subject.EVANGELIC);
		}
		else if(religionInformation.equals("Ethik")){
			subjectSet.add(Subject.ETHICS);
		}
		
		return subjectSet;
	}
	
	public static String getLanguageProfile(Set<Subject> subjectParam){
		String languageProfile = "";
		for(Subject subject : subjectParam){
			if(subject.equals(Subject.STANDARD)){
				languageProfile = "E";
			}
			else if(subject.equals(Subject.FRENCH2) || (subject.equals(Subject.FRENCH3))){
				if(languageProfile.equals("")){
					languageProfile = languageProfile + "F";
				}
				else{
					languageProfile = languageProfile + " F";
				}
			}
			else if(subject.equals(Subject.LATIN)){
				if(languageProfile.equals("")){
					languageProfile = languageProfile + "L";
				}
				else{
					languageProfile = languageProfile + " L";
				}
			}
		}

		return languageProfile;
	}
	
	public static String getReligionProfile(Set<Subject> subjectParam){
		for(Subject subject : subjectParam){
			if(subject.equals(Subject.EVANGELIC)){
				return "ev";
			}
			else if(subject.equals(Subject.ROMAN_CATHOLIC)){
				return "rk";
			}
			else if(subject.equals(Subject.ETHICS)){
				return "Ethik";
			}
		}
		
		return null;		
	}
}
