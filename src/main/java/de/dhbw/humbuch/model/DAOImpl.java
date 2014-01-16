package de.dhbw.humbuch.model;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.Transactional;

import de.dhbw.humbuch.model.entity.Entity;

public class DAOImpl<EntityType extends Entity> implements
		DAO<EntityType> {

	@Inject
	private Provider<EntityManager> emProvider;

	private Class<EntityType> entityClass;
	
	@SuppressWarnings("unchecked")
	@Inject
	public DAOImpl(TypeLiteral<EntityType> entityType) {
		entityClass = (Class<EntityType>) entityType.getRawType();
	}

	@Transactional
	public EntityType insert(EntityType entity) {
		return getEntityManager().merge(entity);
	}

	public EntityType find(final Object id) {
		return (EntityType) getEntityManager().find(getEntityClass(),
				id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<EntityType> findAllWithCriteria(
			final Criterion... criteriaArray) {
		Session session = (Session) getEntityManager().getDelegate();
		Criteria criteria = session.createCriteria(getEntityClass());
		for (Criterion criterion : criteriaArray) {
			criteria.add(criterion);
		}
		return criteria.list();
	}

	@Transactional
	public void update(EntityType entity) {
		getEntityManager().merge(entity);
	}

	@Transactional
	public void delete(EntityType entity) {
		getEntityManager().remove(getEntityManager().merge(entity));
	}

	public EntityManager getEntityManager() {
		return emProvider.get();
	}

	public Class<EntityType> getEntityClass() {
		return entityClass;
	}

	@Override
	public Collection<EntityType> findAll() {
		return getEntityManager().createQuery(
				"from " + getEntityClass().getSimpleName(), getEntityClass())
				.getResultList();
	}
}