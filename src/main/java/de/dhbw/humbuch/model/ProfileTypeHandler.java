package de.dhbw.humbuch.model;

import java.util.LinkedHashSet;
import java.util.Set;

import de.dhbw.humbuch.model.entity.ProfileType;

public final class ProfileTypeHandler {
	
	public static Set<ProfileType> createProfile(String[] languageInformation, String religionInformation){
		Set<ProfileType> profileTypeSet = new LinkedHashSet<ProfileType>();
		for(int i = 0; i < languageInformation.length; i++){
			if(languageInformation[i].equals("E")){
				profileTypeSet.add(ProfileType.STANDARD);
			}
			if(languageInformation[i].equals("F")){
				if(i == 1){
					profileTypeSet.add(ProfileType.FRENCH2);
				}
				else if(i == 2){
					profileTypeSet.add(ProfileType.FRENCH3);
				}
			}
			if(languageInformation[i].equals("L")){
				profileTypeSet.add(ProfileType.LATIN);
			}
			
			if(i == 2 && languageInformation.equals("")){
				profileTypeSet.add(ProfileType.SCIENCE);
			}
		}
		
		if(religionInformation.equals("rk")){
			profileTypeSet.add(ProfileType.ROMANCATHOLIC);
		}
		else if(religionInformation.equals("ev")){
			profileTypeSet.add(ProfileType.EVANGELIC);
		}
		else if(religionInformation.equals("Ethik")){
			profileTypeSet.add(ProfileType.ETHICS);
		}
		
		return profileTypeSet;
	}
	
	public static String getLanguageProfile(Set<ProfileType> profileTypeParam){
		String languageProfile = "";
		for(ProfileType profileType : profileTypeParam){
			if(profileType.equals(ProfileType.STANDARD)){
				languageProfile = "E";
			}
			else if(profileType.equals(ProfileType.FRENCH2) || (profileType.equals(ProfileType.FRENCH3))){
				if(languageProfile.equals("")){
					languageProfile = languageProfile + "F";
				}
				else{
					languageProfile = languageProfile + " F";
				}
			}
			else if(profileType.equals(ProfileType.LATIN)){
				if(languageProfile.equals("")){
					languageProfile = languageProfile + "L";
				}
				else{
					languageProfile = languageProfile + " L";
				}
			}
		}
		
		System.out.println(profileTypeParam);
		System.out.println(languageProfile);
		return languageProfile;
	}
	
	public static String getReligionProfile(Set<ProfileType> profileTypeParam){
		for(ProfileType profileType : profileTypeParam){
			if(profileType.equals(ProfileType.EVANGELIC)){
				return "ev";
			}
			else if(profileType.equals(ProfileType.ROMANCATHOLIC)){
				return "rk";
			}
			else if(profileType.equals(ProfileType.ETHICS)){
				return "Ethik";
			}
		}
		
		return null;		
	}
}
