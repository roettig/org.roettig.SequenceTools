/**
 * 
 */
package org.roettig.SequenceTools;

/**
 * GlobalSequenceIdentity implements the SequenceIdentity interface and computes
 * the global sequence identity which is given by the number of matching positions
 * divided by the alignment length (i.e. including positions having a gap).
 *  
 * @author roettig
 *
 */
public class GlobalSequenceIdentity implements SequenceIdentity
{
    private static SequenceIdentity instance = null;

    private GlobalSequenceIdentity()
    {
    }

    public static SequenceIdentity getInstance() 
    {
	if (instance == null) 
	{
	    instance = new GlobalSequenceIdentity();
	}
	return instance;
    }

    /**
     *  Calculate the global sequence identity between the two given sequence strings.
     * 
     * @param s1 first sequence
     * @param s2 second sequence
     * @return global sequence identity
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
	    if(symb1==symb2&&symb1!='-')
		matches++;
	    else		
		mismatches++;
	}
	return matches/(matches+mismatches);
    }

}