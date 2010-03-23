/**
 * 
 */
package org.roettig.SequenceTools.test;

import static org.junit.Assert.*;

import org.biojava.bio.seq.Sequence;
import org.junit.Before;
import org.junit.Test;
import org.roettig.SequenceTools.HMM;
import org.roettig.SequenceTools.MSA;
import org.roettig.SequenceTools.PairwiseAlignment;
import org.roettig.SequenceTools.SequenceSet;

/**
 * @author roettig
 *
 */
public class HMMTest
{

    private HMM hmm = null;
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
	MSA msa = null;
	try
	{
	    SequenceSet seqs = SequenceSet.readFromFile(PairwiseAlignment.class.getResource("/resources/test.fa").getFile());
	    msa = MSA.createMuscleMSA(seqs);
	} 
	catch (Exception e)
	{
	    e.printStackTrace();
	    fail("createMuscleMSA failed");
	}
	hmm = new HMM(msa);
    }

    /**
     * Test method for {@link org.roettig.SequenceTools.HMM#align(org.roettig.SequenceTools.SequenceSet)}.
     */
    @Test
    public void testAlign()
    {
	MSA ali = null;
	try
	{	
	    SequenceSet seqs = SequenceSet.readFromFile(PairwiseAlignment.class.getResource("/resources/test2.fa").getFile());
	    ali = hmm.align(seqs);
	} 
	catch (Exception e)
	{
	    e.printStackTrace();
	    fail("align failed");
	}
	assertEquals("",ali.getById("3").seqString(),"KGIAIEHQGLTNYIWWARRVYVKGEKTNFPLYSSIAFDLTITSVFTPLITGNAIIVY--GGENSTALLDSIIQDSRADIIKLTPAHLQ-LLKEINI---PAECTIRKFIVGGDNLSTRLARSISGKFGGKIEIFNEYGPTETVVGCMI");
    }

}
