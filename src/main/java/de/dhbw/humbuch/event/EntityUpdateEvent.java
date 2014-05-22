package de.dhbw.humbuch.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.dhbw.humbuch.model.entity.Entity;

/**
 * Event should be used when {@link Entity}s which are used elsewhere have been
 * updated
 * 
 * @author davherrmann
 */
public class EntityUpdateEvent {
	public final List<Class<? extends Entity>> updatedEntityTypes;

	/**
	 * Create a new {@link EntityUpdateEvent} with a list of updated
	 * {@link Entity} types
	 * 
	 * @param updatedEntityTypes
	 *            types of the {@link Entity}s which were updated
	 */
	@SafeVarargs
	public EntityUpdateEvent(Class<? extends Entity>... updatedEntityTypes) {
		this.updatedEntityTypes = new ArrayList<>(
				Arrays.asList(updatedEntityTypes));
	}

	/**
	 * Check if the {@link EntityUpdateEvent} contains a certain {@link Entity}
	 * class which was updated
	 * 
	 * @param entityType
	 *            type of the {@link Entity} to be checked
	 * @return <code>true</code> if event contains the {@link Entity} type,
	 *         otherwise <code>false</code>
	 */
	public boolean contains(Class<? extends Entity> entityType) {
		return updatedEntityTypes.contains(entityType);
	}
}
