/**
 * 
 */
package org.roettig.SequenceTools.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.biojava.bio.seq.Sequence;
import org.junit.Before;
import org.junit.Test;
import org.roettig.SequenceTools.PairwiseAlignment;
import org.roettig.SequenceTools.SeqTools;
import org.roettig.SequenceTools.SequenceSet;
import org.roettig.SequenceTools.exception.FileParseErrorException;

/**
 * @author roettig
 *
 */
public class SequenceSetTest
{

    private SequenceSet seqs;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
	seqs = SequenceSet.readFromFile(PairwiseAlignment.class.getResource("/resources/test.fa").getFile());
    }

    /**
     * Test method for {@link org.roettig.SequenceTools.SequenceSet#SequenceSet(java.util.List)}.
     */
    @Test
    public void testSequenceSetListOfSequence()
    {
	List<Sequence> seqlist = new Vector<Sequence>();
	Sequence seq1 = SeqTools.makeProteinSequence("1","LKWPETER");
	Sequence seq2 = SeqTools.makeProteinSequence("2","PETERSLKW");
	seqlist.add(seq1);
	seqlist.add(seq2);
	SequenceSet seqs1 = new SequenceSet(seqlist);
	assertEquals(seqs1.size(),2);
    }

    /**
     * Test method for {@link org.roettig.SequenceTools.SequenceSet#clear()}.
     */
    @Test
    public void testClear()
    {
	SequenceSet seqs1 = new SequenceSet();
	assertEquals("",seqs1.size(),0);
	Sequence seq = SeqTools.makeProteinSequence("1","LKWPETER"); 
	seqs1.add( seq );
	assertEquals("",seqs1.size(),1);
	seqs1.clear();
	assertEquals("",seqs1.size(),0);
    }

    /**
     * Test method for {@link org.roettig.SequenceTools.SequenceSet#size()}.
     */
    @Test
    public void testSize()
    {
	assertEquals("",seqs.size(),2);
    }

    /**
     * Test method for {@link org.roettig.SequenceTools.SequenceSet#readFromFile(java.lang.String)}.
     */
    @Test
    public void testReadFromFile()
    {
	SequenceSet seqs1 = null;
	try
	{
	    seqs1 = SequenceSet.readFromFile(PairwiseAlignment.class.getResource("/resources/test.afa").getFile());
	} 
	catch (FileNotFoundException e)
	{
	    e.printStackTrace();
	    fail("FileNotFoundException during readFromFileTest");
	} 
	catch (FileParseErrorException e)
	{
	    e.printStackTrace();
	    fail("parseError during readFromFileTest");
	}
	assertEquals(seqs1.size(),1);
	// we can also read Fasta file with gap symbols
	assertEquals("LKWPETER-",seqs1.getById("1").seqString());
    }

    /**
     * Test method for {@link org.roettig.SequenceTools.SequenceSet#getByIndex(int)}.
     */
    @Test
    public void testGetByIndex()
    {
	Sequence s1 = seqs.getByIndex(0);
	Sequence s2 = seqs.getByIndex(1);
	assertTrue("",s1.getName().equals("1")||s2.getName().equals("1"));
    }

    /**
     * Test method for {@link org.roettig.SequenceTools.SequenceSet#getById(java.lang.String)}.
     */
    @Test
    public void testGetById()
    {
	Sequence s1 = seqs.getById("1");
	Sequence s2 = seqs.getById("2");
	assertEquals("",s1.getName(),"1");
	assertEquals("",s1.seqString(),"KGVAVEHRQAVSFLTGMQHQFPLSEDDIVMVKTSFSFDASVWQLFWWSLSGASAYLLPPGWEKDSALIVQAIHQENVTTAHFIPAMLNSFLDQAEIERLSDRTSLKRVFAGGEPLAPRTAARFASVLPQVSLIHGYGPTEATVDAAF");
	assertEquals("",s2.getName(),"2");
	assertEquals("",s2.seqString(),"KGVAIEHQGLTNYIWWARRVYVKGEKTNFPLYSSIAFDLTITSVFTPLITGNAIIVYGGENSTALLDSIIQDSRADIIKLTPAHLQLLKEINIPAECTIRKFIVGGDNLSTRLARSISGKFGGKIEIFNEYGPTETVVGCMI");
    }

    /**
     * Test method for {@link org.roettig.SequenceTools.SequenceSet#getIDs()}.
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
     * Test method for {@link org.roettig.SequenceTools.SequenceSet#add(org.biojava.bio.seq.Sequence)}.
     */
    @Test
    public void testAdd()
    {
	SequenceSet seqs1 = new SequenceSet();
	assertEquals("",seqs1.size(),0);
	Sequence seq = SeqTools.makeProteinSequence("1","LKWPETER"); 
	seqs1.add( seq );
	assertEquals("",seqs1.size(),1);
    }

    /**
     * Test method for {@link org.roettig.SequenceTools.SequenceSet#remove(org.biojava.bio.seq.Sequence)}.
     */
    @Test
    public void testRemove()
    {
	SequenceSet seqs1 = new SequenceSet();
	assertEquals("",seqs1.size(),0);
	Sequence seq = SeqTools.makeProteinSequence("1","LKWPETER"); 
	seqs1.add( seq );
	assertEquals("",seqs1.size(),1);
	seqs1.remove(seq);
	assertEquals("",seqs1.size(),0);
	Sequence seq1 = SeqTools.makeProteinSequence("1","LKWPETER");
	Sequence seq2 = SeqTools.makeProteinSequence("2","LKWPETER");
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
     * Test method for {@link org.roettig.SequenceTools.SequenceSet#store(java.lang.String)}.
     * @throws Exception 
     */
    @Test
    public void testStore() throws Exception
    {
	SequenceSet seqs1 = new SequenceSet();
	// we can also read Fasta file with gap symbols
	Sequence seq1 = SeqTools.makeProteinSequence("1","LKWPETER-");
	Sequence seq2 = SeqTools.makeProteinSequence("2","PETERSLKW");
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
     * Test method for {@link org.roettig.SequenceTools.SequenceSet#iterator()}.
     */
    @Test
    public void testIterator()
    {
	SequenceSet seqs1 = new SequenceSet();
	Sequence seq1 = SeqTools.makeProteinSequence("1","LKWPETER");
	Sequence seq2 = SeqTools.makeProteinSequence("2","PETERSLKW");
	seqs1.add( seq1 );
	seqs1.add( seq2 );
	int i=0;
	for(Sequence s: seqs1)
	{
	    if(i==0)
	    {
		assertEquals("",s.seqString(),"LKWPETER");
		assertEquals("",s.getName(),"1");
	    }
	    if(i==1)
	    {
		assertEquals("",s.seqString(),"PETERSLKW");
		assertEquals("",s.getName(),"2");
	    }
	    i++;
	}
    }

}
