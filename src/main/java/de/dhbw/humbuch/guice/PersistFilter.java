package de.dhbw.humbuch.guice;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.UnitOfWork;

/**
 * Overrides Guice implementation to handle when the persistence service is
 * already started
 * 
 * @author davherrmann
 */
@Singleton
public final class PersistFilter implements Filter {
	private final UnitOfWork unitOfWork;
	private final PersistService persistService;

	private final static Logger log = LoggerFactory
			.getLogger(PersistFilter.class);

	@Inject
	public PersistFilter(UnitOfWork unitOfWork, PersistService persistService) {
		this.unitOfWork = unitOfWork;
		this.persistService = persistService;
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			persistService.start();
		} catch (IllegalStateException e) {
			log.error("persistService already started...");
		}
	}

	public void destroy() {
		persistService.stop();
	}

	public void doFilter(final ServletRequest servletRequest,
			final ServletResponse servletResponse, final FilterChain filterChain)
			throws IOException, ServletException {
		unitOfWork.begin();
		try {
			filterChain.doFilter(servletRequest, servletResponse);
		} finally {
			unitOfWork.end();
		}
	}
}