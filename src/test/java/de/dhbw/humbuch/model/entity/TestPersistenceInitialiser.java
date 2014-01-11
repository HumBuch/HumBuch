package de.dhbw.humbuch.model.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.persist.PersistService;

public class TestPersistenceInitialiser {
	private final static Logger LOG = LoggerFactory
			.getLogger(TestPersistenceInitialiser.class);

	@Inject
	public TestPersistenceInitialiser(PersistService persistService) {
		LOG.info("STARTING PERSISTSERVICE...");
		persistService.start();
	}
}
