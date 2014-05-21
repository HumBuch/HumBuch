package de.dhbw.humbuch.model;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import de.dhbw.humbuch.model.entity.Entity;

/**
 * Data Access Object for inserting, updating, finding or deleting one or more
 * entities of the indicated type in a database
 * 
 * @author davherrmann
 * @param <EntityType>
 *            type of the {@link Entity}s handled by the {@link DAO}
 */
public interface DAO<EntityType extends Entity> {

	/**
	 * Should an event be fired?
	 */
	public enum FireUpdateEvent { YES, NO }

	/**
	 * Persist the indicated entity to database, don't fire an update event
	 * 
	 * @param entity
	 * @return the primary key
	 */
	EntityType insert(EntityType entity);

	/**
	 * Persist the indicated entity to database, fire an update event when 
	 * {@link FireUpdateEvent.YES} is passed
	 * 
	 * @param entity
	 * @param fireUpdateEvent
	 * @return the primary key
	 */
	EntityType insert(EntityType entity, FireUpdateEvent fireUpdateEvent);
	
	/**
	 * Persist the indicated entity {@link Collection} to database, don't fire an update event
	 * 
	 * @param entity
	 * @return the primary key
	 */
	Collection<EntityType> insert(Collection<EntityType> entities);
	
	/**
	 * Persist the indicated entity {@link Collection} to database, fire an update event when 
	 * {@link FireUpdateEvent.YES} is passed
	 * 
	 * @param entity
	 * @param fireUpdateEvent
	 * @return the primary key
	 */
	Collection<EntityType> insert(Collection<EntityType> entities, FireUpdateEvent fireUpdateEvent);

	/**
	 * Update indicated entity to database, don't fire an update event
	 * 
	 * @param entity
	 */
	void update(EntityType entity);

	/**
	 * Update indicated entity to database, fire an update event when 
	 * {@link FireUpdateEvent.YES} is passed
	 * 
	 * @param entity
	 * @param fireUpdateEvent
	 */
	void update(EntityType entity, FireUpdateEvent fireUpdateEvent);

	/**
	 * Update indicated entity {@link Collection} to database, don't fire an update event
	 * 
	 * @param entity
	 */
	void update(Collection<EntityType> entities);
	
	/**
	 * Update indicated entity {@link Collection} to database, fire an update event when 
	 * {@link FireUpdateEvent.YES} is passed
	 * 
	 * @param entity
	 * @param fireUpdateEvent
	 */
	void update(Collection<EntityType> entities, FireUpdateEvent fireUpdateEvent);
	
	/**
	 * Delete indicated entity from database, don't fire an update event
	 * 
	 * @param entity
	 */
	void delete(EntityType entity);

	/**
	 * Delete indicated entity from database, fire an update event when 
	 * {@link FireUpdateEvent.YES} is passed
	 * 
	 * @param entity
	 * @param fireUpdateEvent
	 */
	void delete(EntityType entity, FireUpdateEvent fireUpdateEvent);

	/**
	 * Delete indicated entity {@link Collection} from database, don't fire an update event
	 * 
	 * @param entity
	 */
	void delete(Collection<EntityType> entities);
	
	/**
	 * Delete indicated entity {@link Collection} from database, fire an update event when 
	 * {@link FireUpdateEvent.YES} is passed
	 * 
	 * @param entity
	 * @param fireUpdateEvent
	 */
	void delete(Collection<EntityType> entities, FireUpdateEvent fireUpdateEvent);
	
	/**
	 * Return the entity class
	 * 
	 * @return entity
	 */
	Class<EntityType> getEntityClass();

	/**
	 * Get the entity manager
	 * 
	 * @return entity
	 */
	EntityManager getEntityManager();

	/**
	 * Retrieve an object using indicated ID
	 * 
	 * @param id
	 * @return entity
	 */
	EntityType find(final Object id);

	/**
	 * Retrieve all entities of the type indicated by the {@link DAO}
	 * 
	 * @return {@link Collection} of entities
	 */
	List<EntityType> findAll();

	/**
	 * Retrieve all entities of the type indicated by the {@link DAO} with the
	 * given {@link Criteria}
	 * 
	 * @param criteriaArray
	 *            - {@link Criterion}s, separated by commas
	 * @return {@link Collection} of entities
	 */
	List<EntityType> findAllWithCriteria(Criterion... criteriaArray);

	/**
	 * Retrieve all entities of the type indicated by the {@link DAO} with the
	 * given {@link Criteria} in the given {@link Order}
	 * 
	 * @param order
	 * 			  - {@link Order}
	 * @param criteriaArray
	 *            - {@link Criterion}s, separated by commas
	 * @return {@link Collection} of entities
	 */
	List<EntityType> findAllWithCriteria(Order order, Criterion... criteriaArray);
	
	/**
	 * Retrieve <b>a single entity</b> of the type indicated by the {@link DAO}
	 * with the given {@link Criteria}. Only use this method when you are sure
	 * there is only one entity retrieved from the database - this just frees
	 * you from the hassle of getting the first and only element out of a
	 * {@link Collection}
	 * 
	 * @param criteriaArray
	 *            - {@link Criterion}s, separated by commas
	 * @return <b>a single entity</b> if the amount of entities
	 *         found in the database is greater than 0, otherwise <i>null</i>
	 */
	EntityType findSingleWithCriteria(Criterion... criteriaArray);
	
	/**
	 * Retrieve <b>a single entity</b> of the type indicated by the {@link DAO}
	 * with the given {@link Criteria} in the given {@link Order}. 
	 * Only use this method when you are sure
	 * there is only one entity retrieved from the database - this just frees
	 * you from the hassle of getting the first and only element out of a
	 * {@link Collection}
	 * 
	 * @param order
	 * 			  - {@link Order}
	 * @param criteriaArray
	 *            - {@link Criterion}s, separated by commas
	 * @return <b>a single entity</b> if the amount of entities
	 *         found in the database is greater than 0, otherwise <i>null</i>
	 */
	EntityType findSingleWithCriteria(Order order, Criterion... criteriaArray);
	
	
	/**
	 * Fire an update event
	 */
	void fireUpdateEvent();
}
