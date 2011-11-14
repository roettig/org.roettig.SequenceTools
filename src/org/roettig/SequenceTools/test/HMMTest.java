/**
 * 
 */
package org.roettig.SequenceTools.test;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.roettig.SequenceTools.HMM;
import org.roettig.SequenceTools.MSA;
import org.roettig.SequenceTools.PairwiseAlignment;
import org.roettig.SequenceTools.base.SequenceContainer;
import org.roettig.SequenceTools.format.FastaReader;

/**
 * @author roettig
 *
 */
public class HMMTest extends TestCase
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
			SequenceContainer seqs = new FastaReader().read(PairwiseAlignment.class.getResourceAsStream("/resources/test.fa"));
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
	 * Test method for {@link org.roettig.SequenceTools.HMM#align(org.roettig.SequenceTools.base.impl.DefaultSequenceContainer)}.
	 */
	@Test
	public void testAlign()
	{
		MSA ali = null;
		try
		{	
			SequenceContainer seqs = new FastaReader().read(PairwiseAlignment.class.getResourceAsStream("/resources/test2.fa"));
			ali = hmm.align(seqs);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			fail("align failed");
		}
		assertEquals("KGIAIEHQGLTNYIWWARRVYVKGEKTNFPLYSSIAFDLTITSVFTPLITGNAIIVY--GGENSTALLDSIIQDSRADIIKLTPAHLQ-LLKEINI---PAECTIRKFIVGGDNLSTRLARSISGKFGGKIEIFNEYGPTETVVGCMI",ali.getById("3").getSequenceString());
	}

}
