package org.openecomp.sdc.be.model;

import org.junit.Test;
import org.openecomp.sdc.be.datatypes.elements.DataTypeDataDefinition;

import java.util.List;


public class DataTypeDefinitionTest {

	private DataTypeDefinition createTestSubject() {
		return new DataTypeDefinition();
	}

	@Test
	public void testCtor() throws Exception {
		new DataTypeDefinition(new DataTypeDefinition());
		new DataTypeDefinition(new DataTypeDataDefinition());
	}
	
	@Test
	public void testGetConstraints() throws Exception {
		DataTypeDefinition testSubject;
		List<PropertyConstraint> result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.getConstraints();
	}

	
	@Test
	public void testSetConstraints() throws Exception {
		DataTypeDefinition testSubject;
		List<PropertyConstraint> constraints = null;

		// default test
		testSubject = createTestSubject();
		testSubject.setConstraints(constraints);
	}

	
	@Test
	public void testGetDerivedFrom() throws Exception {
		DataTypeDefinition testSubject;
		DataTypeDefinition result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.getDerivedFrom();
	}

	
	@Test
	public void testSetDerivedFrom() throws Exception {
		DataTypeDefinition testSubject;
		DataTypeDefinition derivedFrom = null;

		// default test
		testSubject = createTestSubject();
		testSubject.setDerivedFrom(derivedFrom);
	}

	
	@Test
	public void testGetProperties() throws Exception {
		DataTypeDefinition testSubject;
		List<PropertyDefinition> result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.getProperties();
	}

	
	@Test
	public void testSetProperties() throws Exception {
		DataTypeDefinition testSubject;
		List<PropertyDefinition> properties = null;

		// default test
		testSubject = createTestSubject();
		testSubject.setProperties(properties);
	}

	
	@Test
	public void testToString() throws Exception {
		DataTypeDefinition testSubject;
		String result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.toString();
	}
}