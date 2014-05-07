package de.dhbw.humbuch.model.entity;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="setting")
public class SettingsEntry implements Map.Entry<String, String>, de.dhbw.humbuch.model.entity.Entity, Serializable  {
	private static final long serialVersionUID = -2998393509203242955L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	private String key;
	private String value;
	
	/**
	 * Required by Hibernate.<p>
	 * Use the {@link Builder} instead.
	 * 
	 * @see Builder
	 */
	@Deprecated
	public SettingsEntry() {}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String setValue(String value) {
		return this.value = value;
	}
	
	public static class Builder {
		private String key;
		private String value;
		
		public Builder(String key, String value) {
			this.key = key;
			this.value = value;
		}
		
		public SettingsEntry build() {
			return new SettingsEntry(this);
		}
	}
	
	public SettingsEntry(Builder builder) {
		this.key = builder.key;
		this.value = builder.value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SettingsEntry))
			return false;
		SettingsEntry other = (SettingsEntry) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
