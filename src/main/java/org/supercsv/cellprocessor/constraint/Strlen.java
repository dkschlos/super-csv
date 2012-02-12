package org.supercsv.cellprocessor.constraint;

import java.util.HashSet;
import java.util.Set;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.NullInputException;
import org.supercsv.exception.SuperCSVException;
import org.supercsv.util.CSVContext;

/**
 * This processor ensures that the input String has a length equal to any of the supplied lengths. The length
 * constraints must all be > 0 or an exception is thrown. Lookup time is O(1).
 * 
 * @author Kasper B. Graversen
 * @author Dominique De Vito
 * @author James Bassett
 */
public class Strlen extends CellProcessorAdaptor implements StringCellProcessor {
	
	private final Set<Integer> requiredLengths = new HashSet<Integer>();
	
	/**
	 * Constructs a new <tt>Strlen</tt> processor, which ensures that the input String has a length equal to any of the
	 * supplied lengths.
	 * 
	 * @param requiredLengths
	 *            one or more required lengths
	 * @throws NullPointerException
	 *             if requiredLengths is null
	 * @throws IllegalArgumentException
	 *             if requiredLengths is empty or contains a negative length
	 */
	public Strlen(final int... requiredLengths) {
		super();
		checkPreconditions(requiredLengths);
		checkAndAddLengths(requiredLengths);
	}
	
	/**
	 * Constructs a new <tt>Strlen</tt> processor, which ensures that the input String has a length equal to the
	 * supplied length, then calls the next processor in the chain.
	 * 
	 * @param requiredLength
	 *            the required length
	 * @param next
	 *            the next processor in the chain
	 * @throws NullPointerException
	 *             if next is null
	 * @throws IllegalArgumentException
	 *             if requiredLength is negative
	 */
	public Strlen(final int requiredLength, final CellProcessor next) {
		this(new int[] { requiredLength }, next);
	}
	
	/**
	 * Constructs a new <tt>Strlen</tt> processor, which ensures that the input String has a length equal to any of the
	 * supplied lengths, then calls the next processor in the chain.
	 * 
	 * @param requiredLengths
	 *            one or more required lengths
	 * @param next
	 *            the next processor in the chain
	 * @throws NullPointerException
	 *             if requiredLengths or next is null
	 * @throws IllegalArgumentException
	 *             if requiredLengths is empty or contains a negative length
	 */
	public Strlen(final int[] requiredLengths, final CellProcessor next) {
		super(next);
		checkPreconditions(requiredLengths);
		checkAndAddLengths(requiredLengths);
	}
	
	/**
	 * Checks the preconditions for creating a new Strlen processor.
	 * 
	 * @param requiredLengths
	 *            one or more required lengths
	 * @throws NullPointerException
	 *             if requiredLengths is null
	 * @throws IllegalArgumentException
	 *             if requiredLengths is empty
	 */
	private static void checkPreconditions(final int... requiredLengths) {
		if( requiredLengths == null ) {
			throw new NullPointerException("requiredLengths should not be null");
		} else if( requiredLengths.length == 0 ) {
			throw new IllegalArgumentException("requiredLengths should not be empty");
		}
	}
	
	/**
	 * Adds each required length, ensuring it isn't negative.
	 * 
	 * @param requiredLengths
	 *            one or more required lengths
	 * @throws IllegalArgumentException
	 *             if a supplied length is negative
	 */
	private void checkAndAddLengths(final int... requiredLengths) {
		for( final int length : requiredLengths ) {
			if( length < 0 ) {
				throw new IllegalArgumentException(String.format("required length cannot be negative but was %d",
					length));
			}
			this.requiredLengths.add(length);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @throws NullInputException
	 *             if value is null
	 * @throws SuperCSVException
	 *             if the length of value isn't one of the required lengths
	 */
	public Object execute(final Object value, final CSVContext context) {
		validateInputNotNull(value, context);
		
		final String stringValue = value.toString();
		final int length = stringValue.length();
		if( !requiredLengths.contains(length) ) {
			throw new SuperCSVException(String.format("the length (%d) of value '%s' not any of the required lengths",
				length, stringValue), context, this);
		}
		
		return next.execute(value, context);
	}
	
}
