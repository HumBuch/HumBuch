package de.dhbw.humbuch.viewmodel;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provider;

import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;

/**
 * @author David Herrmann (davherrmann)
 * 
 * Base class for Tests using persistence and {@link DAO}s
 */
@Ignore("Base test")
public class BaseTest {
	private final static Logger LOG = LoggerFactory.getLogger(BaseTest.class);

	private Provider<EntityManager> emProvider;
	
	/**
	 * Inject necessary objects, called by child test class
	 * 
	 * @param persistenceInitialiser - {@link TestPersistenceInitialiser}
	 * @param emProvider - {@link Provider}
	 */
	public void setInjected(TestPersistenceInitialiser persistenceInitialiser,
			Provider<EntityManager> emProvider) {
		this.emProvider = emProvider;
	}
	
	/**
	 * Log a warning if child class has not injected necessary objects
	 */
	@Before
	public void checkInjection() {
		if (emProvider == null) {
			LOG.error("setInjected() in BaseTest might not have been called"); 
		}
	}

	/**
	 * Clear all tables after running a test
	 */
	@After
	public void clearAllTables() {
		LOG.info("deleting data in all tables");
		Session session = emProvider.get().unwrap(Session.class);
		Transaction transaction = session.beginTransaction();
		List<?> all = session.createQuery("from java.lang.Object").list();
		Iterator<?> it = all.iterator();
		while (it.hasNext()) {
			session.delete(it.next());
		}

		transaction.commit();
	}
}
