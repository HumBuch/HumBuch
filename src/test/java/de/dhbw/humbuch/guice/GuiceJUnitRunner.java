package de.dhbw.humbuch.guice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.runner.Runner;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * JUnit {@link Runner} using Guice to inject test classes annotated with
 * {@link GuiceModules}
 */
public class GuiceJUnitRunner extends BlockJUnit4ClassRunner {
	private Injector injector;

	/**
	 * Specifies the Guice {@link Module} classes which should be used when
	 * injecting a JUnit test class
	 */
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public @interface GuiceModules {
		public Class<? extends Module>[] value();
	}

	/**
	 * Initialise {@link Runner}, configure Guice using the {@link Module}s
	 * configured in the {@link GuiceModules} annotation, inject members into
	 * test class
	 * 
	 * @param testClass
	 *            JUnit test class
	 * @throws InitializationError
	 *             thrown when an error occurs during the initialisation of
	 *             Guice modules or injection of members
	 */
	public GuiceJUnitRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		Set<Class<? extends Module>> moduleClasses = getModuleClassesFor(testClass);
		injector = createInjectorFor(moduleClasses);
	}

	@Override
	public Object createTest() throws Exception {
		Object object = super.createTest();
		injector.injectMembers(object);
		return object;
	}

	/**
	 * Creates a Guice {@link Injector} with a given {@link Set} of
	 * {@link Module} classes
	 * 
	 * @param moduleClasses
	 *            {@link Set} of {@link Module} classes
	 * @return a Guice {@link Injector} with the specified modules
	 * @throws InitializationError
	 *             thrown when an error occurs instantiating the {@link Module}
	 *             class
	 */
	private Injector createInjectorFor(
			Set<Class<? extends Module>> moduleClasses)
			throws InitializationError {
		Set<Module> modules = new HashSet<>();
		for (Class<?> moduleClass : moduleClasses) {
			try {
				modules.add((Module) moduleClass.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				throw new InitializationError(e);
			}
		}
		return Guice.createInjector(modules);
	}

	/**
	 * Reads the {@link Module} classes configured in a {@link GuiceModules}
	 * annotation of the test class
	 * 
	 * @param testClass
	 *            class with the {@link GuiceModules} annotation
	 * @return a {@link Set} of Guice {@link Module} classes
	 * @throws InitializationError
	 *             thrown when no {@link GuiceModules} annotation is present
	 */
	private Set<Class<? extends Module>> getModuleClassesFor(Class<?> testClass)
			throws InitializationError {
		GuiceModules annotation = testClass.getAnnotation(GuiceModules.class);
		if (annotation == null)
			throw new InitializationError(
					"Missing @GuiceModules annotation for unit test '"
							+ testClass.getName() + "'");
		return new HashSet<>(Arrays.asList(annotation.value()));
	}
}
