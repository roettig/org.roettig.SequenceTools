package org.roettig.SequenceTools;
import org.biojava.bio.seq.*;
import org.biojava.bio.symbol.*;
import org.biojava.bio.*;
import org.biojava.bio.seq.db.*;
import org.biojava.bio.seq.io.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * SequenceSet holds an ordered set of sequences.
 *  
 * @author roettig
 *
 */

public class SequenceSet implements Iterable<Sequence>
{
    private Vector<Sequence> seqs = null;
    
    public SequenceSet()
    {
        seqs = new Vector<Sequence>();
    }
    
    public SequenceSet(Vector<Sequence> seqs)
    {
        for(Sequence s:seqs)
        {
            add( s );
        }
    }
    
    /**
     * Delete all sequences.
     */
    public void clear()
    {
	seqs.clear();
    }
    
    /**
     * Get number of sequences in set.
     * 
     * @return int
     */
    public int size()
    {
        return seqs.size();
    }
   
    /**
     * Creation method that reads sequences from Fasta file.
     * 
     * @param filename
     * @return SequenceSet
     */
    public static SequenceSet readFromFile(String filename)
    {
        SequenceSet ret        = new SequenceSet();
        BufferedInputStream is = null;
        try
        {
            is = new BufferedInputStream(new FileInputStream(filename));
        } 
        catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
        }
        Alphabet alpha = AlphabetManager.alphabetForName("PROTEIN");
    

        
        SequenceDB db = null;
        try
        {
            db = SeqIOTools.readFasta(is, alpha);
        } 
        catch (BioException e)
        {
            e.printStackTrace();
        }
        
        SequenceIterator iter =  db.sequenceIterator();
        
        while(iter.hasNext())
        {
            Sequence seq = null;
            try
            {
                seq = iter.nextSequence();
            } 
            catch (NoSuchElementException e)
            {
                e.printStackTrace();
            } 
            catch (BioException e)
            {
                e.printStackTrace();
            }
            
            ret.add( seq );
        }
        try
        {
            is.close();
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return ret;
    }
       
    /**
     * Get sequence by index <i>idx</i>.
     * @param idx
     * @return Sequence
     */
    public final Sequence getByIndex(int idx)
    {
        return seqs.get(idx);
    }
    
    /**
     * Get sequence by ID <i>id</i>.
     * @param id
     * @return
     */
    public Sequence getById(String id)
    {
        for(Sequence s: seqs)
        {
           if(s.getName().equals(id))
               return s;
        }
        throw new IndexOutOfBoundsException();
    }
    
    /**
     * Get list of all ids in the set.
     * @return id list
     */
    public List<String> getIDs()
    {
        Vector<String> ids = new Vector<String>();
        for(Sequence s: seqs)
        {
            ids.addElement( s.getName() );
            System.out.println( s.getName() );
        }
        return ids;
    }
    
    /**
     * Add sequence <i>s</i> to the set.
     * 
     * @param s
     */
    public void add(Sequence s)
    {
        seqs.addElement( s );
    }
    
    /**
     * Remove sequence <i>s</i> from the set.
     * @param s
     */
    public void remove(Sequence s)
    {
	seqs.remove(s);
    }
    
    /**
     * Store sequence set to Fasta file.
     * 
     * @param filename
     */
    public void store(String filename) 
    {
        try
        {
          BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(filename));
          for(Sequence s: seqs)
          {
            SeqIOTools.writeFasta(os, s);
          }
          os.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public Iterator<Sequence> iterator()
    {
        return seqs.iterator();
    }

    public static void main(String[] args) throws BioException, IOException
    {
        /*
        Sequence prot1 = ProteinTools.createProteinSequence("AFHS", "prot_1");
        Sequence prot2 = ProteinTools.createProteinSequence("LKWPETER", "prot_2");
        
        SequenceSet seqs = new SequenceSet();
        seqs.add( prot1 );
        seqs.add( prot2 );
        
        for(Sequence s: seqs)
        {
            System.out.println( s.getName() + " " + s.seqString() );
        }
        */
        
        SequenceSet seqs = SequenceSet.readFromFile("/tmp/all.afa");
        for(Sequence s: seqs)
        {
            System.out.println( s.getName() );
        }        
        seqs.store("/tmp/raus.afa");
    }
}