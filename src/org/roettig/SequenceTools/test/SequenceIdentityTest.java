/**
 * 
 */
package org.roettig.SequenceTools.test;

import junit.framework.TestCase;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.utils.ChangeVetoException;
import org.junit.Test;
import org.roettig.SequenceTools.AlignedSequenceIdentity;
import org.roettig.SequenceTools.GlobalSequenceIdentity;
import org.roettig.SequenceTools.PairwiseAlignment;
import org.roettig.SequenceTools.SeqTools;
import org.roettig.SequenceTools.SequenceIdentity;


/**
 * @author roettig
 *
 */
public class SequenceIdentityTest extends TestCase
{
    @Test
    public void testAlignedSequenceIdentity()
    {
	SequenceIdentity calc_sid = AlignedSequenceIdentity.getInstance();
	Sequence s1 = SeqTools.makeProteinSequence("1", "LKWPETER");
	Sequence s2 = SeqTools.makeProteinSequence("2", "LKHPETE");
	PairwiseAlignment pwa = new PairwiseAlignment();
	double sid=0;
	try
	{
	    sid = pwa.align(s1, s2, calc_sid );
	} 
	catch (IllegalSymbolException e)
	{
	    e.printStackTrace();
	} 
	catch (ChangeVetoException e)
	{
	    e.printStackTrace();
	}
	assertEquals(sid,0.8571428571428571,1e-8);
    }
    
    @Test
    public void testGlobalSequenceIdentity()
    {
	SequenceIdentity calc_sid = GlobalSequenceIdentity.getInstance();
	Sequence s1 = SeqTools.makeProteinSequence("1", "LKWPETER");
	Sequence s2 = SeqTools.makeProteinSequence("2", "LKHPETE");
	PairwiseAlignment pwa = new PairwiseAlignment();
	double sid=0;
	try
	{
	    sid = pwa.align(s1, s2, calc_sid );
	} 
	catch (IllegalSymbolException e)
	{
	    e.printStackTrace();
	} 
	catch (ChangeVetoException e)
	{
	    e.printStackTrace();
	}
	assertEquals(sid,0.75,1e-8);		
    }
}
