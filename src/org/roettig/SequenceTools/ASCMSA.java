/**
 * 
 */
package org.roettig.SequenceTools;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.roettig.SequenceTools.exception.FileParseErrorException;

/**
 * ASCMSA is a subclass of MSA and additionally handles positions of determined
 * active site residues.
 * 
 * @author roettig
 *
 */
public class ASCMSA extends MSA
{

	protected Set<Integer> ascidx = new TreeSet<Integer>();
	protected String template_id = "pdb";

	/**
	 * Default constructor.
	 * 
	 */
	public ASCMSA()
	{
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param msa
	 */
	public ASCMSA(MSA msa)
	{
		super();
		for(Sequence s: msa)
		{
			this.add(s);
		}
	}

	public void setTemplateId(String tmplid)
	{
		template_id = tmplid;
	}


	/**
	 * Set the indices of active site residues. 
	 * 
	 * @param idx indices of active site residues
	 */
	public void setASCIdx(Set<Integer> idx)
	{
		ascidx.clear();
		for(Integer i: idx)
		{
			ascidx.add(i);
		}
	}

	/**
	 * Set the indices of active site residues. 
	 * 
	 * @param idx indices of active site residues
	 */
	public void setASCIdx(List<Integer> idx)
	{
		ascidx.clear();
		for(Integer i: idx)
		{
			ascidx.add(i);
		}
	}

	/**
	 * Load an ASCMSA from file. 
	 * 
	 * @param filename 
	 */
	@Override
	public void store(String filename)
	{
		Sequence s = seqs.getById("pdb");

		String idx = "";
		for(Integer i: ascidx)
		{
			idx+=String.format("_%d",i);
		}
		idx = idx.substring(0, idx.length());

		Sequence prot = null;
		try
		{
			prot  = ProteinTools.createProteinSequence( s.seqString(),"pdb"+idx);
		} 
		catch (IllegalSymbolException e)
		{
			e.printStackTrace();
		}
		seqs.remove(s);
		seqs.add(prot);
		super.store(filename);
	}

	/**
	 * Creation method that loads an ASCMSA from file.
	 * 
	 * @param filename
	 * @return ASCMSA
	 * @throws FileNotFoundException
	 * @throws FileParseErrorException
	 */
	public static ASCMSA loadFromFile(String filename) throws FileNotFoundException, FileParseErrorException
	{
		ASCMSA ret = new ASCMSA();
		ret.load(filename);
		return ret;
	}

	/**
	 * Load an ASCMSA from file. 
	 * 
	 * @param filename 
	 */
	@Override
	public void load(String filename) throws FileNotFoundException, FileParseErrorException
	{
		super.load(filename);

		for(Sequence s: seqs)
		{
			if(s.getName().startsWith("pdb"))
			{
				String sid = s.getName();

				String toks[] = sid.split("_");
				for(int i=1;i<toks.length;i++)
				{
					//System.out.println(Integer.parseInt(toks[i]));
					ascidx.add( Integer.parseInt(toks[i]) );
				}

				Sequence prot = null;
				try
				{
					prot  = ProteinTools.createProteinSequence( s.seqString(),"pdb");
				} 
				catch (IllegalSymbolException e)
				{
					e.printStackTrace();
				}
				seqs.remove(s);
				seqs.add(prot);
				break;
			}
		}

	}

	/**
	 * Get the signature sequences from the MSA. 
	 * 
	 * @param sid sequence ID of reference sequence
	 * @return SequenceSet
	 */
	public SequenceSet getSignatures(String sid)
	{
		return this.getSubSequences(ascidx,sid); 
	}

	/**
	 * Get the signature sequences from the MSA. 
	 * 
	 * @return SequenceSet
	 */
	public SequenceSet getSignatures() throws Exception
	{
		return this.getSubSequences(ascidx,"pdb"); 
	}
}
