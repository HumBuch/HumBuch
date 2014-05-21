package de.dhbw.humbuch.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.Transactional;

import de.dhbw.humbuch.event.EntityUpdateEvent;
import de.dhbw.humbuch.model.entity.Entity;

/**
 * Generic {@link DAO} implementation
 *
 * @author davherrmann
 * @param <EntityType>
 */
public class DAOImpl<EntityType extends Entity> implements DAO<EntityType> {

	private Provider<EntityManager> emProvider;
	private EventBus eventBus;
	
	private Class<EntityType> entityClass;

	@SuppressWarnings("unchecked")
	@Inject
	public DAOImpl(TypeLiteral<EntityType> entityType, Provider<EntityManager> emProvider, EventBus eventBus) {
		this.emProvider = emProvider;
		this.eventBus = eventBus;
		entityClass = (Class<EntityType>) entityType.getRawType();
	}
	
	@Override
	@Transactional
	public EntityType insert(EntityType entity) {
		return insert(entity, FireUpdateEvent.NO);
	}
	
	@Override
	@Transactional
	public EntityType insert(EntityType entity, FireUpdateEvent fireUpdateEvent) {
		getEntityManager().clear();
		EntityType mergedEntity = getEntityManager().merge(entity);
		if(fireUpdateEvent == FireUpdateEvent.YES) {
			fireUpdateEvent();
		}
		
		return mergedEntity;
	}
	
	@Override
	@Transactional
	public Collection<EntityType> insert(Collection<EntityType> entities) {
		return insert(entities, FireUpdateEvent.NO);
	}

	@Override
	@Transactional
	public Collection<EntityType> insert(Collection<EntityType> entities, FireUpdateEvent fireUpdateEvent) {
		getEntityManager().clear();
		Collection<EntityType> mergedEntities = new ArrayList<>();
		for (EntityType entity : entities) {
			mergedEntities.add(getEntityManager().merge(entity));
		}
		if(fireUpdateEvent == FireUpdateEvent.YES) {
			fireUpdateEvent();
		}
		
		return mergedEntities;
	}

	@Override
	public EntityType find(final Object id) {
		getEntityManager().clear();
		return (EntityType) getEntityManager().find(getEntityClass(),
				id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EntityType> findAllWithCriteria(
			final Criterion... criteriaArray) {
		getEntityManager().clear();
		Session session = (Session) getEntityManager().getDelegate();
		Criteria criteria = session.createCriteria(getEntityClass());
		for (Criterion criterion : criteriaArray) {
			criteria.add(criterion);
		}
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EntityType> findAllWithCriteria(Order order,
			Criterion... criteriaArray) {
		getEntityManager().clear();
		Session session = (Session) getEntityManager().getDelegate();
		Criteria criteria = session.createCriteria(getEntityClass());
		criteria.addOrder(order);
		for(Criterion criterion : criteriaArray) {
			criteria.add(criterion);
		}
		
		return criteria.list();
	}

	@Override
	public List<EntityType> findAll() {
		getEntityManager().clear();
		return getEntityManager().createQuery(
				"from " + getEntityClass().getSimpleName(), getEntityClass())
				.getResultList();
	}

	@Override
	public EntityType findSingleWithCriteria(Criterion... criteriaArray) {
		getEntityManager().clear();
		List<EntityType> resultList = findAllWithCriteria(criteriaArray);
		if (resultList.size() > 0) {
			return resultList.get(0);
		} else {
			return null;
		}
	}

	@Override
	public EntityType findSingleWithCriteria(Order order,
			Criterion... criteriaArray) {
		getEntityManager().clear();
		List<EntityType> resultList = findAllWithCriteria(order, criteriaArray);
		if(resultList.size() > 0) {
			return resultList.get(0);
		} else {
			return null;
		}
	}

	@Override
	@Transactional
	public void update(EntityType entity) {
		update(entity, FireUpdateEvent.NO);
	}

	@Override
	@Transactional
	public void update(EntityType entity, FireUpdateEvent fireUpdateEvent) {
		getEntityManager().clear();
		getEntityManager().merge(entity);
		if(fireUpdateEvent == FireUpdateEvent.YES) {
			fireUpdateEvent();
		}
	}

	@Override
	@Transactional
	public void update(Collection<EntityType> entities) {
		update(entities, FireUpdateEvent.NO);
	}

	@Override
	@Transactional
	public void update(Collection<EntityType> entities, FireUpdateEvent fireUpdateEvent) {
		getEntityManager().clear();
		for (EntityType entity : entities) {
			getEntityManager().merge(entity);
		}
		if(fireUpdateEvent == FireUpdateEvent.YES) {
			fireUpdateEvent();
		}
	}

	@Override
	@Transactional
	public void delete(EntityType entity) {
		delete(entity, FireUpdateEvent.NO);
	}

	@Override
	@Transactional
	public void delete(EntityType entity, FireUpdateEvent fireUpdateEvent) {
		getEntityManager().clear();
		getEntityManager().remove(getEntityManager().merge(entity));
		if(fireUpdateEvent == FireUpdateEvent.YES) {
			fireUpdateEvent();
		}
	}
	
	@Override
	@Transactional
	public void delete(Collection<EntityType> entities) {
		delete(entities, FireUpdateEvent.NO);
	}

	@Override
	@Transactional
	public void delete(Collection<EntityType> entities, FireUpdateEvent fireUpdateEvent) {
		getEntityManager().clear();
		for (EntityType entity : entities) {
			getEntityManager().remove(getEntityManager().merge(entity));
		}
		if(fireUpdateEvent == FireUpdateEvent.YES) {
			fireUpdateEvent();
		}
	}

	@Override
	public EntityManager getEntityManager() {
		return emProvider.get();
	}
	
	@Override
	public Class<EntityType> getEntityClass() {
		return entityClass;
	}

	@Override
	public void fireUpdateEvent() {
		eventBus.post(new EntityUpdateEvent(getEntityClass()));
	}
}