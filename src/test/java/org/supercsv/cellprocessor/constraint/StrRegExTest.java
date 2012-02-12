package org.supercsv.cellprocessor.constraint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.supercsv.TestConstants.ANONYMOUS_CSVCONTEXT;

import java.util.regex.PatternSyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.exception.NullInputException;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.mock.IdentityTransform;

/**
 * Tests the StrRegEx constraint.
 * 
 * @author James Bassett
 */
public class StrRegExTest {
	
	private static final String REGEX = "\\$[0-9]+\\.[0-9]{2}";
	private static final String MSG = "Must be a valid dollar amount, e.g. $123.45";
	
	private CellProcessor processor;
	private CellProcessor processorChain;
	
	/**
	 * Sets up the processors for the test using all constructor combinations.
	 */
	@Before
	public void setUp() {
		processor = new StrRegEx(REGEX);
		processorChain = new StrRegEx(REGEX, new IdentityTransform());
		StrRegEx.registerMessage(REGEX, MSG);
	}
	
	/**
	 * Tests unchained/chained execution with a String matching the regex.
	 */
	@Test
	public void testValidInput() {
		String input = "$123.45";
		assertEquals(input, processor.execute(input, ANONYMOUS_CSVCONTEXT));
		assertEquals(input, processorChain.execute(input, ANONYMOUS_CSVCONTEXT));
	}
	
	/**
	 * Tests execution with input that doesn't match the regex.
	 */
	@Test
	public void testInvalidInput() {
		String input = "12345";
		try {
			processor.execute(input, ANONYMOUS_CSVCONTEXT);
			fail("should have thrown SuperCSVException");
		}
		catch(SuperCSVException e) {
			// exception msg should contain registered message
			assertTrue(e.getMessage().contains(MSG));
		}
	}
	
	/**
	 * Tests execution with input that doesn't match the regex (and no message is registered).
	 */
	@Test
	public void testInvalidInputWithNoMessage() {
		processor = new StrRegEx("\\s"); // only whitespace
		String input = "12345";
		try {
			processor.execute(input, ANONYMOUS_CSVCONTEXT);
			fail("should have thrown SuperCSVException");
		}
		catch(SuperCSVException e) {
			// exception msg should not contain the registered message (it's a different regex)
			assertFalse(e.getMessage().contains(MSG));
		}
	}
	
	/**
	 * Tests construction of the processor with a null regex (should throw an Exception).
	 */
	@Test(expected = NullPointerException.class)
	public void testWithNullRegex() {
		new StrRegEx(null);
	}
	
	/**
	 * Tests construction of the processor with an empty regex (should throw an Exception).
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testWithEmptyRegex() {
		new StrRegEx("");
	}
	
	/**
	 * Tests construction of the processor with an invalid regex (should throw an Exception).
	 */
	@Test(expected = PatternSyntaxException.class)
	public void testWithInvalidRegex() {
		new StrRegEx("*****");
	}
	
	/**
	 * Tests execution with a null input (should throw an Exception).
	 */
	@Test(expected = NullInputException.class)
	public void testWithNull() {
		processor.execute(null, ANONYMOUS_CSVCONTEXT);
	}
	
}
