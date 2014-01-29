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
 * @param <EntityType>
 *            type of the {@link Entity}s handled by the {@link DAO}
 */
public interface DAO<EntityType extends Entity> {

	public enum FireUpdateEvent { YES, NO }

	/**
	 * Persist the indicated entity to database
	 * 
	 * @param entity
	 * @return the primary key
	 */
	EntityType insert(EntityType entity);

	EntityType insert(EntityType entity, FireUpdateEvent fireUpdateEvent);
	
	/**
	 * Retrieve an object using indicated ID
	 * 
	 * @param id
	 * @return entity
	 */
	EntityType find(final Object id);

	/**
	 * Update indicated entity to database
	 * 
	 * @param entity
	 */
	void update(EntityType entity);

	void update(EntityType entity, FireUpdateEvent fireUpdateEvent);

	/**
	 * Delete indicated entity from database
	 * 
	 * @param entity
	 */
	void delete(EntityType entity);

	void delete(EntityType entity, FireUpdateEvent fireUpdateEvent);

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
	
	EntityType findSingleWithCriteria(Order order, Criterion... criteriaArray);
	
	void fireUpdateEvent();
	
}
