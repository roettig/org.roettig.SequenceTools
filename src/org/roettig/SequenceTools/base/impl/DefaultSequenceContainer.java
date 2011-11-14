package org.roettig.SequenceTools.base.impl;

import org.roettig.SequenceTools.base.Annotated;
import org.roettig.SequenceTools.base.Sequence;
import org.roettig.SequenceTools.base.SequenceContainer;
import org.roettig.SequenceTools.exception.FileParseErrorException;
import org.roettig.SequenceTools.format.FastaReader;
import org.roettig.SequenceTools.format.FastaWriter;
import org.roettig.SequenceTools.format.SeqXMLReader;
import org.roettig.SequenceTools.format.SeqXMLWriter;
import org.roettig.SequenceTools.format.SequenceReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

/**
 * SequenceSet holds an ordered set of sequences.
 *  
 * @author roettig
 *
 */

public class DefaultSequenceContainer implements SequenceContainer, Serializable, Annotated
{	
	protected List<Sequence> seqs;
	protected Annotated props = new DefaultAnnotated();
	
	public DefaultSequenceContainer()
	{
		seqs = new ArrayList<Sequence>();
	}

	/**
	 * Copy ctor.
	 * @param seqs
	 */
	public DefaultSequenceContainer(SequenceContainer _seqs)
	{
		this();
		for(Sequence s: _seqs)
		{
			Sequence sclone = s.copy();
			seqs.add(sclone);
		}
	}

	public DefaultSequenceContainer(List<Sequence> seqs)
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
	@Override
	public void clear()
	{
		seqs.clear();
	}

	/**
	 * Get number of sequences in set.
	 * 
	 * @return int
	 */
	@Override
	public int size()
	{
		return seqs.size();
	}

	/**
	 * Creation method that reads sequences from a sequence file.
	 * 
	 * @param sequence filename
	 * @return SequenceContainer
	 *  
	 */
	public static SequenceContainer readFromFile(String filename)
	{
		if(filename.toLowerCase().endsWith(".seqxml"))
			return readFromSeqXMLFile(filename);
		else if(filename.toLowerCase().endsWith(".fasta"))
				return readFromFastaFile(filename);
			else // best guess
				return readFromFastaFile(filename);
	}
	
	public static void writeToFile(String filename, SequenceContainer seqs)
	{
		if(filename.toLowerCase().endsWith(".seqxml"))
			new SeqXMLWriter().write(seqs,filename);
		else if(filename.toLowerCase().endsWith(".fasta"))
				new FastaWriter().write(seqs,filename);
			else // best guess
				new FastaWriter().write(seqs,filename);
	}
	
	/**
	 * Creation method that reads sequences from Fasta file.
	 * 
	 * @param fasta filename
	 * @return SequenceContainer
	 *  
	 */
	public static SequenceContainer readFromFastaFile(String filename)
	{
		return new FastaReader().read(filename);
	}
	
	/**
	 * Creation method that reads sequences from Fasta file.
	 * 
	 * @param fasta filename
	 * @return SequenceContainer
	 *  
	 */
	public static SequenceContainer readFromFastaStream(InputStream in)
	{
		return new FastaReader().read(in);
	}
	
	/**
	 * Creation method that reads sequences from SeqXML file.
	 * 
	 * @param seqxml filename
	 * @return SequenceContainer
	 *  
	 */
	public static SequenceContainer readFromSeqXMLFile(String filename)
	{
		return new SeqXMLReader().read(filename);
	}

	/**
	 * Get sequence by index <i>idx</i>.
	 * @param idx
	 * @return Sequence
	 */
	@Override
	public final Sequence getByIndex(int idx)
	{
		return seqs.get(idx);
	}

	/**
	 * Get sequence by ID <i>id</i>.
	 * @param id
	 * @return
	 */
	@Override
	public Sequence getByID(String id)
	{
		for(Sequence s: seqs)
		{
			if(s.getID().equals(id))
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
		List<String> ids = new ArrayList<String>();
		for(Sequence s: seqs)
		{
			ids.add( s.getID() );
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
	}

	public Iterator<Sequence> iterator()
	{
		return seqs.iterator();
	}

	@Override
	public void addProperty(String name, Object obj)
	{
		props.addProperty(name, obj);
	}

	@Override
	public Object getProperty(String name)
	{
		return props.getProperty(name);
	}

	@Override
	public void removeProperty(String name)
	{
		props.removeProperty(name);
	}

	@Override
	public boolean hasProperty(String name)
	{
		return props.hasProperty(name);
	}

	@Override
	public List<String> getPropertyNames()
	{
		return props.getPropertyNames();
	}

	@Override
	public Map<String, Object> getMap()
	{
		return props.getMap();
	}
}
