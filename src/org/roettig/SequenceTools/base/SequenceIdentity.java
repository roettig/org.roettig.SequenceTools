/**
 * 
 */
package org.roettig.SequenceTools.base;

/**
 * Interface for classes that compute specific sequence identities like
 * GlobalSequenceIdentity or AlignedSequenceIdentity.
 * 
 *   
 * @author roettig
 */
public interface SequenceIdentity
{
    /* Calculate the sequence identity between the two given sequence strings.
     * 
     * @param String first sequence
     * @param String second sequence
     * @return sequence identity
     */
    public double calculate(String s1, String s2);
}
