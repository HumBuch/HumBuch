package de.dhbw.humbuch.view;

import static de.dhbw.humbuch.test.TestUtils.randomUser;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.View;

import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.entity.TestPersistenceInitialiser;
import de.dhbw.humbuch.model.entity.User;
import de.dhbw.humbuch.viewmodel.Properties;

@Ignore("Base test")
public class BaseTest {
	
	private final static Logger LOG = LoggerFactory.getLogger(BaseTest.class);

	private Object viewObject;
	private View view;
	private ViewInformation viewInformation;
	private Properties properties;
	private DAO<User> daoUser;

	public void setInjected(MVVMConfig mvvmConfig,
			TestPersistenceInitialiser testPersistenceInitialiser, Object view,
			Properties properties, DAO<User> daoUser) {
		this.properties = properties;
		this.daoUser = daoUser;
		setView(view);
	}

	private void setView(Object view) {
		this.viewObject = view;
		StringBuilder logStringBuilder = new StringBuilder("view: ");
		if (view instanceof View) {
			this.view = (View) view;
			logStringBuilder.append(View.class.getName()).append(" ");
		}
		if (view instanceof ViewInformation) {
			this.viewInformation = (ViewInformation) view;
			logStringBuilder.append(ViewInformation.class.getName());
		}
		LOG.info(logStringBuilder.toString());
	}

	@Before
	public void setLoggedInUser() {
		properties.currentUser.set(daoUser.insert(randomUser()));
	}

	@Test
	public void testInstantiation() {
		assertThat(viewObject, notNullValue());
	}

	@Test
	public void testEnter() {
		assumeThat(view, notNullValue());
		view.enter(null);
	}

	@Test
	public void testTitleNotNull() {
		assumeThat(viewInformation, notNullValue());
		assertThat(viewInformation.getTitle(), notNullValue());
	}
}
