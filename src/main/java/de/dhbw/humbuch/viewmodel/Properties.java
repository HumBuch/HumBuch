package de.dhbw.humbuch.viewmodel;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;

import de.davherrmann.mvvm.BasicState;
import de.davherrmann.mvvm.State;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.SettingsEntry;
import de.dhbw.humbuch.model.entity.User;

/**
 * Provides a sessionscoped set of properties. 
 * 
 * @author David Vitt
 *
 */
public class Properties {
	
	private DAO<SettingsEntry> daoSettingsEnty;
	
	public final State<User> currentUser = new BasicState<>(User.class);
	public final State<Map<String, String>> settings = new BasicState<>(Map.class);
	
	/**
	 * Constructor
	 * 
	 * @param daoSettingsEntry
	 */
	@Inject
	public Properties(DAO<SettingsEntry> daoSettingsEntry) {
		this.daoSettingsEnty = daoSettingsEntry;
		setSettings();
	}
	
	private void setSettings() {
		Map<String, String> settingsMap = new HashMap<>();
		for(SettingsEntry entry : daoSettingsEnty.findAll()) {
			settingsMap.put(entry.getKey(), entry.getValue());
		}
		
		settings.set(settingsMap);
	}
}
