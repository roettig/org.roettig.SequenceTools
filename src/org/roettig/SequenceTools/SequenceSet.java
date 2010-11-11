package org.roettig.SequenceTools;
import org.biojava.bio.seq.*;
import org.biojava.bio.*;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;
import org.biojavax.bio.seq.io.FastaFormat;
import org.biojavax.bio.seq.io.FastaHeader;
import org.biojavax.bio.seq.io.RichSequenceBuilderFactory;
import org.biojavax.bio.seq.io.RichStreamReader;
import org.roettig.SequenceTools.exception.FileParseErrorException;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * SequenceSet holds an ordered set of sequences.
 *  
 * @author roettig
 *
 */

public class SequenceSet implements Iterable<Sequence>, Serializable
{

	private static final long	serialVersionUID	= 4142771855458239633L;
	
	private List<Sequence> seqs = null;

	public SequenceSet()
	{
		seqs = new Vector<Sequence>();
	}

	/**
	 * Copy ctor.
	 * @param seqs
	 */
	public SequenceSet(SequenceSet _seqs)
	{
		this();
		for(Sequence s: _seqs)
		{
			Sequence sclone = SeqTools.makeProteinSequence(s.getName(),s.seqString());
			seqs.add(sclone);
		}
	}

	public SequenceSet(List<Sequence> seqs)
	{
		this();
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


	public static void main(String args[]) throws NoSuchElementException, BioException, IOException
	{

		BufferedReader br = new BufferedReader(new FileReader("/tmp/1.fa") );
		RichSequenceIterator iter = new RichStreamReader(br, new CustomFastaFormat(), ProteinTools.getTAlphabet().getTokenization("token"), RichSequenceBuilderFactory.FACTORY, null); //RichSequence.IOTools.readFastaProtein(br,null);
		while(iter.hasNext())
		{
			Sequence seq = iter.nextSequence();
			System.out.println(seq.getName()+" "+seq.seqString());
		}

		/*
	RichSequenceIterator iter = RichSequence.IOTools.readFile(new File("/tmp/test.fa"),null);
	while(iter.hasNext())
	{
	    Sequence seq = iter.nextSequence();
	    System.out.println(seq.getName()+" "+seq.seqString());
	}
		 */
	}

	/**
	 * Creation method that reads sequences from Fasta file.
	 * 
	 * @param filename
	 * @return SequenceSet
	 * @throws FileNotFoundException 
	 */
	public static SequenceSet readFromFile(String filename) throws FileNotFoundException, FileParseErrorException
	{
		BufferedReader  br = new BufferedReader(new FileReader(filename) );
		return readFromReader(br);
	}

	/**
	 * Creation method that reads sequences from Fasta file.
	 * 
	 * @param br
	 * @return SequenceSet
	 * @throws FileNotFoundException 
	 */
	public static SequenceSet readFromReader(BufferedReader br) throws FileParseErrorException
	{
		SequenceSet          ret  = new SequenceSet();
		//RichSequenceIterator iter = RichSequence.IOTools.readFastaProtein(br,null);
		//RichSequence.IOTools.readFastaProtein(br,null);
		RichSequenceIterator iter = null;
		try
		{
			iter = new RichStreamReader(br, new CustomFastaFormat(), ProteinTools.getTAlphabet().getTokenization("token"), RichSequenceBuilderFactory.FACTORY, null);
		} 
		catch (BioException e1)
		{
			e1.printStackTrace();
			throw new FileParseErrorException();
		}

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
				throw new FileParseErrorException();
			} 
			catch (BioException e)
			{
				e.printStackTrace();
				throw new FileParseErrorException();
			}
			// we make use of SeqTools.makeProteinSequence to regenerate
			// the protein sequences to prevent subsequent alphabet mismatch errors
			Sequence nseq = SeqTools.makeProteinSequence(seq.getName(), seq.seqString());
			ret.add( nseq );
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
		seqs.add( s );
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
		FastaHeader fh = new FastaHeader();
		// configure fasta header to allow for
		// verbatim copy of sequence name to file
		fh.setShowNamespace(false);
		fh.setShowIdentifier(false);
		fh.setShowAccession(true);
		fh.setShowDescription(false);
		fh.setShowVersion(false);
		fh.setShowName(false);

		try
		{
			BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(filename));
			for(Sequence s: seqs)
			{
				RichSequence.IOTools.writeFasta(os,s,null,fh);
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

}
