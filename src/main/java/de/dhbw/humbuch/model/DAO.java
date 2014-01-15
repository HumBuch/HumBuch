package de.dhbw.humbuch.model;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.hibernate.criterion.Criterion;

import de.dhbw.humbuch.model.entity.Entity;

public interface DAO<EntityType extends Entity> {

	/**
	 * Persist the indicated entity to database
	 * 
	 * @param entity
	 * @return the primary key
	 */
	EntityType insert(EntityType entity);

	/**
	 * Retrieve an object using indicated ID
	 * 
	 * @param id
	 * @return
	 */
	EntityType find(final Object id);

	/**
	 * Update indicated entity to database
	 * 
	 * @param entity
	 */
	void update(EntityType entity);

	/**
	 * Delete indicated entity from database
	 * 
	 * @param entity
	 */
	void delete(EntityType entity);

	/**
	 * Return the entity class
	 * 
	 * @return
	 */
	Class<EntityType> getEntityClass();

	/**
	 * Get the entity manager
	 * 
	 * @return
	 */
	EntityManager getEntityManager();

	/**
	 * 
	 * @return
	 */
	Collection<EntityType> findAll();

	/**
	 * 
	 * @return
	 */
	Collection<EntityType> findAllWithCriteria(Criterion... criteriaArray);

}
