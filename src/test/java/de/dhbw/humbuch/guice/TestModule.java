package de.dhbw.humbuch.guice;

import com.google.inject.TypeLiteral;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.ServletModule;

import de.dhbw.humbuch.model.DAO;
import de.dhbw.humbuch.model.DAOImpl;
import de.dhbw.humbuch.model.entity.BorrowedMaterial;
import de.dhbw.humbuch.model.entity.Category;
import de.dhbw.humbuch.model.entity.Grade;
import de.dhbw.humbuch.model.entity.Parent;
import de.dhbw.humbuch.model.entity.Student;
import de.dhbw.humbuch.model.entity.TeachingMaterial;
import de.dhbw.humbuch.model.entity.User;

public class TestModule extends ServletModule {

	@Override
	protected void configureServlets() {
		install(new JpaPersistModule("testPersistModule"));
		
		bind(new TypeLiteral<DAO<BorrowedMaterial>>() {}).to(new TypeLiteral<DAOImpl<BorrowedMaterial>>() {});
		bind(new TypeLiteral<DAO<Category>>() {}).to(new TypeLiteral<DAOImpl<Category>>() {});
		bind(new TypeLiteral<DAO<Grade>>() {}).to(new TypeLiteral<DAOImpl<Grade>>() {});
		bind(new TypeLiteral<DAO<Parent>>() {}).to(new TypeLiteral<DAOImpl<Parent>>() {});
		bind(new TypeLiteral<DAO<Student>>() {}).to(new TypeLiteral<DAOImpl<Student>>() {});
		bind(new TypeLiteral<DAO<TeachingMaterial>>() {}).to(new TypeLiteral<DAOImpl<TeachingMaterial>>() {});
		bind(new TypeLiteral<DAO<User>>() {}).to(new TypeLiteral<DAOImpl<User>>() {});
	}
}
