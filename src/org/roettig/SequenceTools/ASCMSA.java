/**
 * 
 */
package org.roettig.SequenceTools;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.roettig.SequenceTools.base.Sequence;
import org.roettig.SequenceTools.base.impl.DefaultSequence;
import org.roettig.SequenceTools.base.impl.DefaultSequenceContainer;
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
		Sequence s = seqs.getByID("pdb");

		String idx = "";
		for(Integer i: ascidx)
		{
			idx+=String.format("_%d",i);
		}
		idx = idx.substring(0, idx.length());

		Sequence prot = DefaultSequence.create("pdb"+idx,s.getSequenceString());
		
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
	public static ASCMSA loadFromFile(String filename)
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
	public void load(String filename)
	{
		super.load(filename);

		for(Sequence s: seqs)
		{
			if(s.getID().startsWith("pdb"))
			{
				String sid = s.getID();

				String toks[] = sid.split("_");
				for(int i=1;i<toks.length;i++)
				{
					ascidx.add( Integer.parseInt(toks[i]) );
				}

				Sequence prot = DefaultSequence.create("pdb", s.getSequenceString());
				
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
	public DefaultSequenceContainer getSignatures(String sid)
	{
		return this.getSubSequences(ascidx,sid); 
	}

	/**
	 * Get the signature sequences from the MSA. 
	 * 
	 * @return SequenceSet
	 */
	public DefaultSequenceContainer getSignatures() throws Exception
	{
		return this.getSubSequences(ascidx,"pdb"); 
	}
}
