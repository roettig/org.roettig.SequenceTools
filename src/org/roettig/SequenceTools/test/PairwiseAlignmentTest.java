/**
 * 
 */
package org.roettig.SequenceTools.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import junit.framework.TestCase;

import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.utils.ChangeVetoException;
import org.junit.Before;
import org.junit.Test;
import org.roettig.SequenceTools.PairwiseAlignment;
import org.roettig.SequenceTools.base.SequenceContainer;
import org.roettig.SequenceTools.base.impl.AlignedSequenceIdentity;
import org.roettig.SequenceTools.base.impl.DefaultSequenceContainer;
import org.roettig.SequenceTools.exception.FileParseErrorException;
import org.roettig.SequenceTools.format.FastaReader;

/**
 * @author roettig
 *
 */
public class PairwiseAlignmentTest extends TestCase
{

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.PairwiseAlignment#align(org.biojava.bio.seq.Sequence, org.biojava.bio.seq.Sequence, org.roettig.SequenceTools.SequenceIdentity)}.
	 * @throws ChangeVetoException 
	 * @throws IllegalSymbolException 
	 * @throws FileParseErrorException 
	 * @throws FileNotFoundException 
	 */
	@Test
	public void testAlign() throws IllegalSymbolException, ChangeVetoException, FileNotFoundException, FileParseErrorException
	{
		SequenceContainer seqs = DefaultSequenceContainer.readFromFile(new FastaReader(PairwiseAlignment.class.getResource("/resources/test.fa").getFile().toString()));
		PairwiseAlignment pwa = new PairwiseAlignment();
		double pid = pwa.align( seqs.getByIndex(0).getSequenceString(), seqs.getByIndex(1).getSequenceString(), AlignedSequenceIdentity.getInstance() );
		assertEquals("pid of alignment",0.23404255319148937,pid,1e-8);
		pid = pwa.align( seqs.getByIndex(0).getSequenceString(), seqs.getByIndex(0).getSequenceString(), AlignedSequenceIdentity.getInstance() );
		assertEquals("pid of self-alignment",1.0,pid,1e-5);
	}

}
