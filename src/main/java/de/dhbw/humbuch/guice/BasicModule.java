package de.dhbw.humbuch.guice;

import com.google.common.eventbus.EventBus;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;
import com.google.inject.servlet.SessionScoped;
import com.vaadin.ui.UI;

import de.davherrmann.guice.vaadin.UIScoped;
import de.davherrmann.mvvm.ViewModelComposer;
import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.DAOImpl;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Category;
import de.dhbw.humbuch.model.entity.Dunning;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Parent;
import de.dhbw.humbuch.model.entity.SchoolYear;
import de.dhbw.humbuch.model.entity.SettingsEntry;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;
import de.dhbw.humbuch.model.entity.User;
import de.dhbw.humbuch.view.MVVMConfig;
import de.dhbw.humbuch.view.MainUI;
import de.dhbw.humbuch.viewmodel.LoginViewModel;
import de.dhbw.humbuch.viewmodel.Properties;


/**
 * Guice {@link Module} for binding classes to instances/providers in different scopes
 * 
 * @author davherrmann
 */
public class BasicModule extends ServletModule {

	@Override
	protected void configureServlets() {
		install(new JpaPersistModule("persistModule"));

		filter("/*").through(PersistFilter.class);
		serve("/*").with(BasicServlet.class);

		bind(new TypeLiteral<DAO<Student>>() {}).to(new TypeLiteral<DAOImpl<Student>>() {});
		bind(new TypeLiteral<DAO<BorrowedMaterial>>() {}).to(new TypeLiteral<DAOImpl<BorrowedMaterial>>() {});
		bind(new TypeLiteral<DAO<TeachingMaterial>>() {}).to(new TypeLiteral<DAOImpl<TeachingMaterial>>() {});
		bind(new TypeLiteral<DAO<Grade>>() {}).to(new TypeLiteral<DAOImpl<Grade>>() {});
		bind(new TypeLiteral<DAO<Parent>>() {}).to(new TypeLiteral<DAOImpl<Parent>>() {});
		bind(new TypeLiteral<DAO<User>>() {}).to(new TypeLiteral<DAOImpl<User>>() {});
		bind(new TypeLiteral<DAO<Category>>() {}).to(new TypeLiteral<DAOImpl<Category>>() {});
		bind(new TypeLiteral<DAO<SchoolYear>>() {}).to(new TypeLiteral<DAOImpl<SchoolYear>>() {});
		bind(new TypeLiteral<DAO<Dunning>>() {}).to(new TypeLiteral<DAOImpl<Dunning>>() {});
		bind(new TypeLiteral<DAO<SettingsEntry>>() {}).to(new TypeLiteral<DAOImpl<SettingsEntry>>() {});

		
		bind(ViewModelComposer.class).in(UIScoped.class);
		bind(MVVMConfig.class).in(UIScoped.class);
		bind(EventBus.class).in(UIScoped.class);

		bind(LoginViewModel.class).in(UIScoped.class);
		bind(Properties.class).in(SessionScoped.class);

		MapBinder<String, UI> mapbinder = MapBinder.newMapBinder(binder(), String.class, UI.class);
		mapbinder.addBinding(MainUI.class.getName()).to(MainUI.class);
	}

	@Provides
	private Class<? extends UI> provideUIClass() {
		return MainUI.class;
	}
}
