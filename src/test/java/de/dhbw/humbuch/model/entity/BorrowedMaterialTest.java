package de.dhbw.humbuch.model.entity;

import static de.dhbw.humbuch.test.TestUtils.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.Date;

import org.junit.Test;

public class BorrowedMaterialTest {

	private BorrowedMaterial instantiateEntity() {
		BorrowedMaterial borrowedMaterial = new BorrowedMaterial.Builder(
				studentInGrade(6), teachingMaterialInBothTermsOfGrade(6),
				todayPlusDays(-10)).borrowUntil(todayPlusDays(10))
				.returnDate(null).received(false).build();
		borrowedMaterial.setId(rInt());
		return borrowedMaterial;
	}

	@Test
	public void testEntityInstantiation() {
		assertThat(instantiateEntity(), notNullValue());
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testEntityInstantiationWithHibernateConstructor() {
		assertThat(new BorrowedMaterial(), notNullValue());
	}

	@Test
	public void testSetIdGetId() {
		final int id = rInt();

		BorrowedMaterial entity = instantiateEntity();
		entity.setId(id);
		assertThat(entity.getId(), is(id));
	}

	@Test
	public void testSetStudentGetStudent() {
		final Student student = studentInGrade(6);

		BorrowedMaterial entity = instantiateEntity();
		entity.setStudent(student);
		assertThat(entity.getStudent(), is(student));
	}

	@Test
	public void testSetTeachingMaterialGetTeachingMaterial() {
		final TeachingMaterial teachingMaterial = teachingMaterialInBothTermsOfGrade(6);

		BorrowedMaterial entity = instantiateEntity();
		entity.setTeachingMaterial(teachingMaterial);
		assertThat(entity.getTeachingMaterial(), is(teachingMaterial));
	}

	@Test
	public void testSetBorrowFromGetBorrowFrom() {
		final Date borrowFrom = todayPlusDays(-10);

		BorrowedMaterial entity = instantiateEntity();
		entity.setBorrowFrom(borrowFrom);
		assertThat(entity.getBorrowFrom(), is(borrowFrom));
	}

	@Test
	public void testSetBorrowUntilGetBorrowUntil() {
		final Date borrowUntil = todayPlusDays(10);

		BorrowedMaterial entity = instantiateEntity();
		entity.setBorrowUntil(borrowUntil);
		assertThat(entity.getBorrowUntil(), is(borrowUntil));
	}

	@Test
	public void testSetReturnDateGetReturnDate() {
		final Date returnDate = todayPlusDays(5);

		BorrowedMaterial entity = instantiateEntity();
		entity.setReturnDate(returnDate);
		assertThat(entity.getReturnDate(), is(returnDate));
	}

	@Test
	public void testSetReceivedIsReceived() {
		BorrowedMaterial entity = instantiateEntity();
		entity.setReceived(true);
		assertThat(entity.isReceived(), is(true));
	}

	@Test
	public void testIsReturnedTrue() {
		final Date returnDate = todayPlusDays(5);

		BorrowedMaterial entity = instantiateEntity();
		entity.setReturnDate(returnDate);
		assertThat(entity.isReturned(), is(true));
	}

	@Test
	public void testIsReturnedFalse() {
		BorrowedMaterial entity = instantiateEntity();
		assertThat(entity.isReturned(), is(false));
	}

	@Test
	public void testEqualsTrueSameObject() {
		BorrowedMaterial entity = instantiateEntity();
		assertThat(entity.equals(entity), is(true));
	}

	@Test
	public void testEqualsFalseNullObject() {
		BorrowedMaterial entity = instantiateEntity();
		assertThat(entity.equals(null), is(false));
	}

	@Test
	public void testEqualsFalseNotInstanceOf() {
		BorrowedMaterial entity = instantiateEntity();
		assertThat(entity.equals(new Object()), is(false));
	}

	@Test
	public void testEqualsFalseInstanceOfDifferentId() {
		BorrowedMaterial entity = instantiateEntity();
		BorrowedMaterial entity2 = instantiateEntity();
		entity2.setId(rInt());
		assertThat(entity.equals(entity2), is(false));
	}

	@Test
	public void testEqualsTrueInstanceOfSameId() {
		BorrowedMaterial entity = instantiateEntity();
		BorrowedMaterial entity2 = instantiateEntity();
		entity2.setId(entity.getId());
		assertThat(entity.equals(entity2), is(true));
	}

	@Test
	public void testSameHashCodeInstanceOfSameId() {
		BorrowedMaterial entity = instantiateEntity();
		BorrowedMaterial entity2 = instantiateEntity();
		entity2.setId(entity.getId());
		assertThat(entity.hashCode(), is(entity2.hashCode()));
	}

	@Test
	public void testDifferentHashCodeInstanceOfDifferentId() {
		BorrowedMaterial entity = instantiateEntity();
		BorrowedMaterial entity2 = instantiateEntity();
		entity2.setId(rInt());
		assertThat(entity.hashCode(), not(entity2.hashCode()));
	}

	@Test
	public void testCompareToSameObject() {
		BorrowedMaterial entity = instantiateEntity();
		assertThat(entity.compareTo(entity), is(0));
	}

	@Test
	public void testCompareToInstanceOfDifferentId() {
		BorrowedMaterial entity = instantiateEntity();
		BorrowedMaterial entity2 = instantiateEntity();
		assertThat(entity.compareTo(entity2), not(0));
	}
	
	@Test
	public void testCompareToInstanceOfDifferentIdDifferentTeachingMaterial() {
		BorrowedMaterial entity = instantiateEntity();
		BorrowedMaterial entity2 = instantiateEntity();
		entity2.setTeachingMaterial(teachingMaterialInFirstTermOfGrade(6));
		assertThat(entity.compareTo(entity2), not(0));
	}
}
