/**
 * 
 */
package org.roettig.SequenceTools;

/**
 * AlignedSequenceIdentity implements the SequenceIdentity interface and computes
 * the aligned sequence identity which is given by the number of matching positions
 * divided by the number of aligned positions (i.e. positions having no gap).
 *  
 * @author roettig
 *
 */
public class AlignedSequenceIdentity implements SequenceIdentity
{
	private static SequenceIdentity instance = null;

	private AlignedSequenceIdentity()
	{
	}

	public static SequenceIdentity getInstance() 
	{
		if (instance == null) 
		{
			instance = new AlignedSequenceIdentity();
		}
		return instance;
	}


	/**
	 * Calculate the aligned sequence identity between the two given sequence strings.
	 * 
	 * @param s1 first sequence
	 * @param s2 second sequence
	 * @return aligned sequence identity
	 */
	@Override
	public double calculate(String s1, String s2)
	{
		double matches    = 0.0;
		double mismatches = 0.0;

		for(int i=0;i<s1.length();i++)
		{
			char symb1 = s1.charAt(i);
			char symb2 = s2.charAt(i);
			if(symb1=='-' || symb2 =='-')
				continue;
			if(symb1==symb2)
				matches++;
			else
				mismatches++;
		}
		return matches/(matches+mismatches);
	}
}
