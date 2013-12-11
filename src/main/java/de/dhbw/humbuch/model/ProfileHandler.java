package de.dhbw.humbuch.model;

import de.dhbw.humbuch.model.entity.Profile;

public final class ProfileHandler {
	
	public static Profile createProfile(String firstLanguage, String secondLanguage, String thirdLanguage){
		Profile profile = new Profile();
		
		if(firstLanguage.equals("E") || secondLanguage.equals("E") || thirdLanguage.equals("E")){
			profile.setEnglish(true);
		}
		if(firstLanguage.equals("F") || secondLanguage.equals("F") || thirdLanguage.equals("F")){
			profile.setFrench(true);
		}
		if(firstLanguage.equals("L") || secondLanguage.equals("L") || thirdLanguage.equals("L")){
			profile.setLatin(true);
		}

		return profile;
	}
	
	public static String getLanguageProfile(Profile profile){
		String languageProfile = "";
		if(profile.isEnglish()){
			languageProfile = "E";
		}
		if(profile.isFrench()){
			if(languageProfile.equals("")){
				languageProfile = languageProfile + "F";
			}
			else{
				languageProfile = languageProfile + " F";
			}
		}
		if(profile.isLatin()){
			if(languageProfile.equals("")){
				languageProfile = languageProfile + "L";
			}
			else{
				languageProfile = languageProfile + " L";
			}
		}
		
		return languageProfile;
	}
}
