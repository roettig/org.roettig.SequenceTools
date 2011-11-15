/**
 * 
 */
package org.roettig.SequenceTools.test;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;
import org.junit.Test;
import org.roettig.SequenceTools.ASCMSA;
import org.roettig.SequenceTools.MSA;
import org.roettig.SequenceTools.PairwiseAlignment;
import org.roettig.SequenceTools.base.SequenceContainer;
import org.roettig.SequenceTools.base.impl.DefaultSequenceContainer;
import org.roettig.SequenceTools.exception.FileParseErrorException;

/**
 * @author roettig
 *
 */
public class ASCMSATest extends TestCase
{
	/**
	 * Test method for {@link org.roettig.SequenceTools.ASCMSA#load(java.lang.String)}.
	 */
	@Test
	public void testStore()
	{
		MSA msa = null;
		
		msa = MSA.loadFromFile(PairwiseAlignment.class.getResource("/resources/test3.afa").getFile());
		
		ASCMSA asc1 = new ASCMSA(msa);
		List<Integer> idx = new Vector<Integer>();
		idx.add(1);
		idx.add(5);
		idx.add(9);
		asc1.setASCIdx(idx);

		SequenceContainer sigs = null;
		try
		{
			sigs  = asc1.getSignatures("pdb");
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			fail("could not extract signatures");
		}
		assertEquals("LWE",sigs.getByID("1").getSequenceString());
		assertEquals("LWE",sigs.getByID("2").getSequenceString());
		File tmp = null;
		try
		{
			tmp = File.createTempFile("tmp","asc");
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			fail("could not create tempfile");
		}
		asc1.store(tmp.getAbsoluteFile().toString());

		ASCMSA asc2 = null;
		
		asc2 = ASCMSA.loadFromFile(tmp.getAbsoluteFile().toString());
		

		DefaultSequenceContainer sigs2 = asc2.getSignatures("pdb");
		assertEquals(sigs2.getByID("1").getSequenceString(),"LWE");
		assertEquals(sigs2.getByID("2").getSequenceString(),"LWE");

		tmp.delete();
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.ASCMSA#store(java.lang.String)}.
	 */
	@Test
	public void testLoad()
	{
		ASCMSA asc1 = null;
		
		asc1 = ASCMSA.loadFromFile(PairwiseAlignment.class.getResource("/resources/test.asca").getFile());
		
		DefaultSequenceContainer sigs2 = asc1.getSignatures("pdb");
		assertEquals(sigs2.getByID("1").getSequenceString(),"LWE");
		assertEquals(sigs2.getByID("2").getSequenceString(),"LWE");

	}    
}
