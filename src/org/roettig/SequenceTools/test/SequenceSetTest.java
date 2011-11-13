/**
 * 
 */
package org.roettig.SequenceTools.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Vector;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.roettig.SequenceTools.PairwiseAlignment;

import org.roettig.SequenceTools.base.Sequence;
import org.roettig.SequenceTools.base.SequenceContainer;
import org.roettig.SequenceTools.base.impl.DefaultSequence;
import org.roettig.SequenceTools.base.impl.DefaultSequenceContainer;
import org.roettig.SequenceTools.format.FastaReader;

/**
 * @author roettig
 *
 */
public class SequenceSetTest extends TestCase
{

	private SequenceContainer seqs;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		seqs = new FastaReader().read(PairwiseAlignment.class.getResourceAsStream("/resources/test.fa"));
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.base.impl.DefaultSequenceContainer#SequenceSet(java.util.List)}.
	 */
	@Test
	public void testSequenceSetListOfSequence()
	{
		List<Sequence> seqlist = new Vector<Sequence>();
		Sequence seq1 = DefaultSequence.create("1","LKWPETER");
		Sequence seq2 = DefaultSequence.create("2","PETERSLKW");
		seqlist.add(seq1);
		seqlist.add(seq2);
		DefaultSequenceContainer seqs1 = new DefaultSequenceContainer(seqlist);
		assertEquals(seqs1.size(),2);
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.base.impl.DefaultSequenceContainer#clear()}.
	 */
	@Test
	public void testClear()
	{
		DefaultSequenceContainer seqs1 = new DefaultSequenceContainer();
		assertEquals("",seqs1.size(),0);
		Sequence seq = DefaultSequence.create("1","LKWPETER"); 
		seqs1.add( seq );
		assertEquals("",seqs1.size(),1);
		seqs1.clear();
		assertEquals("",seqs1.size(),0);
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.base.impl.DefaultSequenceContainer#size()}.
	 */
	@Test
	public void testSize()
	{
		assertEquals("",seqs.size(),2);
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.base.impl.DefaultSequenceContainer#readFromFile(java.lang.String)}.
	 */
	@Test
	public void testReadFromFile()
	{
		SequenceContainer seqs1 = null;
		
		seqs1 = new FastaReader().read(PairwiseAlignment.class.getResourceAsStream("/resources/test.afa"));
		
		assertEquals(seqs1.size(),1);
		// we can also read Fasta file with gap symbols
		assertEquals("LKWPETER-",seqs1.getByID("1").getSequenceString());
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.base.impl.DefaultSequenceContainer#getByIndex(int)}.
	 */
	@Test
	public void testGetByIndex()
	{
		Sequence s1 = seqs.getByIndex(0);
		Sequence s2 = seqs.getByIndex(1);
		assertTrue("",s1.getID().equals("1")||s2.getID().equals("1"));
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.base.impl.DefaultSequenceContainer#getById(java.lang.String)}.
	 */
	@Test
	public void testGetById()
	{
		Sequence s1 = seqs.getByID("1");
		Sequence s2 = seqs.getByID("2");
		assertEquals("",s1.getID(),"1");
		assertEquals("",s1.getSequenceString(),"KGVAVEHRQAVSFLTGMQHQFPLSEDDIVMVKTSFSFDASVWQLFWWSLSGASAYLLPPGWEKDSALIVQAIHQENVTTAHFIPAMLNSFLDQAEIERLSDRTSLKRVFAGGEPLAPRTAARFASVLPQVSLIHGYGPTEATVDAAF");
		assertEquals("",s2.getID(),"2");
		assertEquals("",s2.getSequenceString(),"KGVAIEHQGLTNYIWWARRVYVKGEKTNFPLYSSIAFDLTITSVFTPLITGNAIIVYGGENSTALLDSIIQDSRADIIKLTPAHLQLLKEINIPAECTIRKFIVGGDNLSTRLARSISGKFGGKIEIFNEYGPTETVVGCMI");
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.base.impl.DefaultSequenceContainer#getIDs()}.
	 */
	@Test
	public void testGetIDs()
	{
		List<String> ids = seqs.getIDs();
		boolean ok1 = false;
		boolean ok2 = false;
		for(String s: ids)
		{
			if(s.equals("1"))
				ok1 = true;
			if(s.equals("2"))
				ok2 = true;	    
		}
		assertTrue("",ok1);
		assertTrue("",ok2);
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.base.impl.DefaultSequenceContainer#add(org.biojava.bio.seq.Sequence)}.
	 */
	@Test
	public void testAdd()
	{
		DefaultSequenceContainer seqs1 = new DefaultSequenceContainer();
		assertEquals("",seqs1.size(),0);
		Sequence seq = DefaultSequence.create("1","LKWPETER"); 
		seqs1.add( seq );
		assertEquals("",seqs1.size(),1);
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.base.impl.DefaultSequenceContainer#remove(org.biojava.bio.seq.Sequence)}.
	 */
	@Test
	public void testRemove()
	{
		DefaultSequenceContainer seqs1 = new DefaultSequenceContainer();
		assertEquals("",seqs1.size(),0);
		Sequence seq = DefaultSequence.create("1","LKWPETER"); 
		seqs1.add( seq );
		assertEquals("",seqs1.size(),1);
		seqs1.remove(seq);
		assertEquals("",seqs1.size(),0);
		Sequence seq1 = DefaultSequence.create("1","LKWPETER");
		Sequence seq2 = DefaultSequence.create("2","LKWPETER");
		seqs1.add( seq1 );
		assertEquals("",seqs1.size(),1);
		seqs1.add( seq2 );
		assertEquals("",seqs1.size(),2);
		seqs1.remove(seq1);
		assertEquals("",seqs1.size(),1);
		seqs1.remove(seq2);
		assertEquals("",seqs1.size(),0);
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.base.impl.DefaultSequenceContainer#store(java.lang.String)}.
	 * @throws Exception 
	 */
	@Test
	public void testStore() throws Exception
	{
		DefaultSequenceContainer seqs1 = new DefaultSequenceContainer();
		// we can also read Fasta file with gap symbols
		Sequence seq1 = DefaultSequence.create("1","LKWPETER-");
		Sequence seq2 = DefaultSequence.create("2","PETERSLKW");
		seqs1.add(seq1);
		seqs1.add(seq2);
		File tmp = File.createTempFile("tmp","asc");

		String filename = tmp.getAbsolutePath().toString(); 
		seqs1.store(filename);

		BufferedReader reader = new BufferedReader(new FileReader(filename));

		String line = null;
		int i=0;
		while ((line=reader.readLine()) != null) 
		{
			if(i==0)
				assertEquals(">1 ",line);
			if(i==1)
				assertEquals("LKWPETER-",line);
			if(i==2)
				assertEquals(">2 ",line);            
			if(i==3)
				assertEquals("PETERSLKW",line);
			i++;
		}
		reader.close();
		tmp.delete();
	}

	/**
	 * Test method for {@link org.roettig.SequenceTools.base.impl.DefaultSequenceContainer#iterator()}.
	 */
	@Test
	public void testIterator()
	{
		DefaultSequenceContainer seqs1 = new DefaultSequenceContainer();
		Sequence seq1 = DefaultSequence.create("1","LKWPETER");
		Sequence seq2 = DefaultSequence.create("2","PETERSLKW");
		seqs1.add( seq1 );
		seqs1.add( seq2 );
		int i=0;
		for(Sequence s: seqs1)
		{
			if(i==0)
			{
				assertEquals("",s.getSequenceString(),"LKWPETER");
				assertEquals("",s.getID(),"1");
			}
			if(i==1)
			{
				assertEquals("",s.getSequenceString(),"PETERSLKW");
				assertEquals("",s.getID(),"2");
			}
			i++;
		}
	}

}
