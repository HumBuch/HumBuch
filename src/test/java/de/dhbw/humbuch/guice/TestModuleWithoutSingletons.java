package de.dhbw.humbuch.guice;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.persist.jpa.JpaPersistModule;

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

public class TestModuleWithoutSingletons extends AbstractModule {

	@Override
	protected void configure() {
		install(new JpaPersistModule("testPersistModule"));
		
		bind(new TypeLiteral<DAO<BorrowedMaterial>>() {}).to(new TypeLiteral<DAOImpl<BorrowedMaterial>>() {});
		bind(new TypeLiteral<DAO<Category>>() {}).to(new TypeLiteral<DAOImpl<Category>>() {});
		bind(new TypeLiteral<DAO<Dunning>>() {}).to(new TypeLiteral<DAOImpl<Dunning>>() {});
		bind(new TypeLiteral<DAO<Grade>>() {}).to(new TypeLiteral<DAOImpl<Grade>>() {});
		bind(new TypeLiteral<DAO<Parent>>() {}).to(new TypeLiteral<DAOImpl<Parent>>() {});
		bind(new TypeLiteral<DAO<SchoolYear>>() {}).to(new TypeLiteral<DAOImpl<SchoolYear>>() {});
		bind(new TypeLiteral<DAO<Student>>() {}).to(new TypeLiteral<DAOImpl<Student>>() {});
		bind(new TypeLiteral<DAO<TeachingMaterial>>() {}).to(new TypeLiteral<DAOImpl<TeachingMaterial>>() {});
		bind(new TypeLiteral<DAO<User>>() {}).to(new TypeLiteral<DAOImpl<User>>() {});
		bind(new TypeLiteral<DAO<SettingsEntry>>() {}).to(new TypeLiteral<DAOImpl<SettingsEntry>>() {});
	}
}
