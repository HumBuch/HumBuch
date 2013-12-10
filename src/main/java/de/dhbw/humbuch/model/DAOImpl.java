package de.dhbw.humbuch.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;

import de.dhbw.humbuch.model.entity.Entity;

public abstract class DAOImpl<EntityType extends Entity> implements
		DAO<EntityType> {

	@Inject
	private Provider<EntityManager> emProvider;

	private Class<EntityType> entityClass;

	@Transactional
	public EntityType insert(EntityType entity) {
		getEntityManager().persist(entity);
		return entity;
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

	public void delete(EntityType entity) {
		getEntityManager().remove(entity);
	}

	public EntityManager getEntityManager() {
		return emProvider.get();
	}

	@SuppressWarnings("unchecked")
	public Class<EntityType> getEntityClass() {
		if (entityClass == null) {
			Class<?> clazz = getClass();

			if (DAOImpl.class.isAssignableFrom(clazz)) {
				Type type;
				while (!DAOImpl.class.equals(clazz.getSuperclass())) {
					clazz = clazz.getSuperclass();
				}
				type = clazz.getGenericSuperclass();

				ParameterizedType paramType = (ParameterizedType) type;

				entityClass = (Class<EntityType>) paramType
						.getActualTypeArguments()[0];

			}
		}
		return entityClass;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<EntityType> findAll() {
		return getEntityManager().createQuery(
				"from " + getEntityClass().getSimpleName())
				.getResultList();
	}
}