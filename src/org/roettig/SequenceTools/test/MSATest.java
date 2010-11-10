/**
 * 
 */
package org.roettig.SequenceTools.test;

import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.biojava.bio.seq.Sequence;
import org.junit.Before;
import org.junit.Test;
import org.roettig.SequenceTools.MSA;
import org.roettig.SequenceTools.PairwiseAlignment;
import org.roettig.SequenceTools.SequenceSet;

/**
 * @author roettig
 *
 */
public class MSATest extends TestCase
{

	MSA msa = null;

	@Before
	public void setUp() 
	{
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
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.MSA#createMuscleMSA(org.roettig.SequenceTools.SequenceSet)}.
	 */
	@Test
	public void testCreateMuscleMSA()
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
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.MSA#getById(java.lang.String)}.
	 */
	@Test
	public void testGetById()
	{
		Sequence s1 = msa.getById("1");
		assertEquals("checking sequence id",s1.getName(),"1");
		assertEquals("checking 1st sequence",s1.seqString(),"KGVAVEHRQAVSFLTGMQHQFPLSEDDIVMVKTSFSFDASVWQLFWWSLSGASAYLLPPGWEKDSALIVQAIHQENVTTAHFIPAMLNSFLDQAEIERLSDRTSLKRVFAGGEPLAPRTAARFASVL-PQVSLIHGYGPTEATVDAAF");
		Sequence s2 = msa.getById("2");
		assertEquals("checking sequence id",s2.getName(),"2");
		assertEquals("checking 2nd sequence",s2.seqString(),"KGVAIEHQGLTNYIWWARRVYVKGEKTNFPLYSSIAFDLTITSVFTPLITGNAIIVY--GGENSTALLDSIIQDSRADIIKLTPAHLQ-LLKEINI---PAECTIRKFIVGGDNLSTRLARSISGKFGGKIEIFNEYGPTETVVGCMI");
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.MSA#getByIndex(int)}.
	 */
	@Test
	public void testGetByIndex()
	{
		Sequence s1 = msa.getByIndex(0);
		Sequence s2 = msa.getByIndex(1);	
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.MSA#depth()}.
	 */
	@Test
	public void testDepth()
	{
		assertEquals("testing width",msa.depth(),2);
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.MSA#width()}.
	 */
	@Test
	public void testWidth()
	{
		assertEquals("testing width",msa.width(),148);
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.MSA#getSymbol(int, int)}.
	 */
	@Test
	public void testGetSymbolIntInt()
	{
		assertEquals("",msa.getSymbol(0, 0),"K");
		assertEquals("",msa.getSymbol(1, 0),"K");
		assertEquals("",msa.getSymbol(0, 1),"G");
		assertEquals("",msa.getSymbol(1, 1),"G");
		assertEquals("",msa.getSymbol(0, 2),"V");
		assertEquals("",msa.getSymbol(1, 2),"V");
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.MSA#getSymbol(java.lang.String, int)}.
	 */
	@Test
	public void testGetSymbolStringInt()
	{
		assertEquals("",msa.getSymbol("1", 0),"K");
		assertEquals("",msa.getSymbol("2", 0),"K");
		assertEquals("",msa.getSymbol("1", 1),"G");
		assertEquals("",msa.getSymbol("2", 1),"G");
		assertEquals("",msa.getSymbol("1", 2),"V");
		assertEquals("",msa.getSymbol("2", 2),"V");
		assertEquals("",msa.getSymbol("1", 4),"V");
		assertEquals("",msa.getSymbol("2", 4),"I");	
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.MSA#mapColumnIndexToSequenceIndex(java.lang.String, int)}.
	 */
	@Test
	public void testMapColumnIndexToSequenceIndex()
	{
		assertEquals(msa.mapColumnIndexToSequenceIndex("2", 95),92);
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.MSA#mapSequenceIndexToColumnIndex(java.lang.String, int)}.
	 */
	@Test
	public void testMapSequenceIndexToColumnIndex()
	{
		assertEquals(msa.mapSequenceIndexToColumnIndex("2", 95),101);
	}

}
