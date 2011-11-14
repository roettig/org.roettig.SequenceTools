package org.roettig.SequenceTools;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.io.*;
import java.util.*;

import org.roettig.SequenceTools.base.Annotated;
import org.roettig.SequenceTools.base.Sequence;
import org.roettig.SequenceTools.base.SequenceContainer;
import org.roettig.SequenceTools.base.impl.DefaultSequence;
import org.roettig.SequenceTools.base.impl.DefaultSequenceContainer;
import org.roettig.SequenceTools.binres.muscle.MuscleDeployer;
import org.roettig.SequenceTools.exception.FileParseErrorException;
import org.roettig.SequenceTools.format.FastaReader;
import org.roettig.SequenceTools.format.FastaWriter;
import org.roettig.SequenceTools.format.SeqXMLReader;
import org.roettig.SequenceTools.format.SeqXMLWriter;
import org.roettig.SequenceTools.format.SequenceReader;
import org.roettig.SequenceTools.format.SequenceWriter;


/**
 * The MSA class is used to build and operate on multiple sequence alignments. 
 * 
 * @author roettig
 */
public class MSA implements Iterable<Sequence>, Serializable
{
	protected SequenceContainer seqs = null;
	protected HashMap<Integer,Integer> quality = null;
	
	private static String MUSCLEPATH;
	static
	{
		MUSCLEPATH = MuscleDeployer.deployMUSCLE();
	}
	
	public MSA()
	{
		seqs    = new DefaultSequenceContainer();
		quality = new HashMap<Integer,Integer>();
	}

	public MSA(SequenceContainer _seqs)
	{
		seqs    = new DefaultSequenceContainer(_seqs);
		quality = new HashMap<Integer,Integer>();
	}

	/**
	 * Static creation method to create new MSA using Muscle.
	 * 
	 * @param seqs
	 * @return MSA
	 * @throws Exception
	 */
	public static MSA createMuscleMSA(SequenceContainer seqs) throws Exception
	{
		MSA ret     = new MSA();
		
		File tmpIn  = null;
		File tmpOut = null;
		try
		{
			tmpIn  = File.createTempFile("createmsa", ".IN");
			tmpOut = File.createTempFile("createmsa", ".OUT");
		}
		catch(IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		Map<String,Map<String,Object>> annos = new HashMap<String, Map<String,Object>>();
		for(Sequence seq: seqs)
		{
			annos.put(seq.getID(),((Annotated) seq).getMap());
		}
		
		new FastaWriter().write(seqs,tmpIn.getAbsolutePath());

		try
		{
			ProcessBuilder builder = new ProcessBuilder( MUSCLEPATH, "-in",tmpIn.getAbsoluteFile().toString(),"-out",tmpOut.getAbsoluteFile().toString());
			Process p = builder.start();   
			p.waitFor();

			
			BufferedReader reader = new BufferedReader( new InputStreamReader(p.getErrorStream()) );
			StringBuffer log = new StringBuffer();
			String s;
			while( null != (s = reader.readLine()) ) 
			{
				log.append(s);
			}
			reader.close();
			
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		
		ret.seqs = new FastaReader().read(tmpOut.toString());

		((Annotated) ret.seqs).addProperty("aligned", true);
		
		for(Sequence seq: ret.seqs)
		{
			Map<String,Object> map = annos.get(seq.getID());
			for(String key: map.keySet())
			{
				((Annotated) seq).addProperty(key, map.get(key));
			}
		}
		

		tmpIn.delete();
		tmpOut.delete();
		
		return ret;
	}

	/**
	 * Store MSA to file.
	 * 
	 * @param filename
	 */
	public void store(String filename)
	{
		new SeqXMLWriter().write(seqs,filename);
	}
	
	/**
	 * Store MSA to file.
	 * 
	 * @param filename
	 */
	public void store(String filename, SequenceWriter writer)
	{
		writer.write(seqs, filename);
	}

	public static MSA loadFromFile(String filename)
	{
		MSA ret = new MSA();
		ret.load(filename, new SeqXMLReader());
		return ret;
	}

	/**
	 * Load MSA from file.
	 * 
	 * @param filename
	 */
	public void load(String filename, SequenceReader reader)
	{
		SequenceContainer seqsIn = null;	
		seqsIn = reader.read(filename);
		seqs.clear();
		for(Sequence s: seqsIn)
		{
			add( s );
		}
	}
	
	/**
	 * Load MSA from file.
	 * 
	 * @param filename
	 */
	public void load(String filename)
	{
		SequenceContainer seqsIn = null;	
		seqsIn = new SeqXMLReader().read(filename);
		seqs.clear();
		for(Sequence s: seqsIn)
		{
			add( s );
		}
	}

	public static MSA align(MSA msa, DefaultSequenceContainer seqs)
	{
		HMM hmm = null;
		try
		{
			hmm = new HMM(msa);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}

		MSA ret = null;

		try
		{
			ret = hmm.align(seqs);
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Get sequence in MSA by ID.
	 * @param id
	 * @return Sequence
	 */
	public Sequence getById(String id)
	{
		return seqs.getByID(id);
	}

	/**
	 * Get i-th sequence in MSA.
	 * @param idx
	 * @return Sequence
	 */
	public Sequence getByIndex(int idx)
	{
		return seqs.getByIndex(idx);
	}

	/**
	 * Get number of sequences in the MSA (its depth).
	 * @return depth
	 */
	public int depth()
	{
		return seqs.size();
	}

	/**
	 * Get number of columns in the MSA (its width).
	 * @return width
	 */
	public int width()
	{
		if(depth()>0)
			return seqs.getByIndex(0).length();
		else
			return 0;
	}

	/**
	 * Get all columns from the MSA that have at most <i>fraction</i>*depth gaps. 
	 * 
	 * @param frac  fraction of gaps allowed in a column to be kept 
	 * @return SequenceSet
	 */
	public DefaultSequenceContainer getAlignedSubSequences(double frac)
	{
		DefaultSequenceContainer ret   = new DefaultSequenceContainer();
		Set<Integer> keep = new HashSet<Integer>();
		for(int c=0;c<width();c++)
		{
			int nGaps        = 0;
			int nAllowedGaps = (int) (depth()*frac);
			boolean keepCol = true;
			for(int r=0;r<depth();r++)
			{
				if(getSymbol(r,c).equals("-"))
					nGaps++;
				if(nGaps>nAllowedGaps)
				{
					keepCol = false;
					break;
				}
			}

			if(keepCol)
				keep.add(c+1);
		}

		for(Sequence s: this)
		{
			Sequence subseq = getSubSequence(s.getID(), keep);
			((Annotated) subseq).addProperty("parent",s.getID());
			ret.add( subseq );
		}

		return ret;
	}

	/**
	 * Get all columns with indices given by <i>cols</i>. Indices are interpreted
	 * relative to the sequence with ID given by <i>refsid</i>.
	 * @param cols
	 * @param refsid
	 * @return SequenceSet
	 * 
	 */
	public DefaultSequenceContainer getSubSequences( Collection<Integer> cols, String refsid)
	{
		TreeMap<Integer,Integer> nidxs = new TreeMap<Integer,Integer>();
		for(Integer c: cols)
		{
			int idxN = mapSequenceIndexToColumnIndex(refsid,c);
			nidxs.put(idxN, 1);
		}
		DefaultSequenceContainer ret = new DefaultSequenceContainer();
		for(Sequence s: this)
		{
			Sequence subseq = getSubSequence(s.getID(), nidxs.keySet());
			((Annotated) subseq).addProperty("parent",s.getID());
			ret.add( subseq );
		}
		return ret;
	}


	/**
	 * Get all columns with indices given by <i>cols</i>. Indices are interpreted
	 * relative to the sequence with ID given by <i>refsid</i>. 
	 * @param sid
	 * @param cols
	 * @return Sequence
	 */
	public Sequence getSubSequence(String sid, Set<Integer> cols)
	{
		StringBuffer sb = new StringBuffer();
		for(Integer c: cols)
		{
			sb.append(getSymbol(sid,  c-1 ));
			//subseq += getSymbol(sid,  c-1 );
		}
		Sequence retseq = DefaultSequence.create(sid, sb.toString());
		return retseq;
	}

	/**
	 * Get symbol in row <i>i</i> and column <i>j</i> in MSA.
	 * 
	 * @param i
	 * @param j
	 * @return String
	 */
	public final String getSymbol(int i, int j)
	{
		Sequence s = seqs.getByIndex(i);
		return s.getSequenceString().substring(j, j+1);
	}

	/**
	 * Get symbol in sequence with ID <i>sid</i> and column <i>j</i> in MSA.
	 * 
	 * @param sid
	 * @param j
	 * @return String
	 */
	public String getSymbol(String sid, int j)
	{
		Sequence s = seqs.getByID(sid);
		return s.getSequenceString().substring(j, j+1);
	}

	/**
	 * Map column index <i>idx</i> of MSA to sequence-internal index
	 * of sequence given by ID <i>sid</i>. 
	 * 
	 * @param sid
	 * @param idx
	 * @return int
	 */
	public int mapColumnIndexToSequenceIndex(String sid, int idx)
	{
		Sequence s = seqs.getByID(sid);
		int sM=0;
		for(int i=0;i<idx;i++)
		{
			char symb = s.getSequenceString().charAt(i);   
			if(symb!='-')             
				sM++;
		}
		return sM;
	}

	/**
	 * Map sequence-internal index <i>idx</i> of sequence given by ID <i>sid</i> to
	 * column index of MSA.  
	 * @param sid
	 * @param idx
	 * @return int
	 */
	public int mapSequenceIndexToColumnIndex(String sid, int idx)
	{
		Sequence s = seqs.getByID(sid);
		int sM=0;
		for(int i=0;i<s.length();i++)
		{
			if(idx==sM)
				return i;
			char symb = s.getSequenceString().charAt(i);
			if(symb!='-')
				sM++;
		}
		return -1;
	}

	/**
	 * Add sequence to MSA.
	 * 
	 * @param seq
	 */
	protected void add(Sequence seq)
	{
		seqs.add( seq );    
	}

	@Override
	public Iterator<Sequence> iterator()
	{
		return seqs.iterator();
	}

	/**
	 * Calculate the overall quality of the MSA.
	 * 
	 */
	public void calculateQuality()
	{

		for(int c=0;c<width();c++)
		{
			double s1 = 0.0;
			double s2 = 0.0;
			double s  = 0.0;

			boolean gappy = false;
			for(int r1=0;r1<depth();r1++)
			{
				int nGaps = 0;
				for(int r2=0;r2<depth();r2++)
				{
					if(getSymbol(r2,c).equals("-"))
						nGaps++;
					if(getSymbol(r1,c).equals("-") || getSymbol(r2,c).equals("-"))
					{
						s2+=12;
						continue;
					}

					double z1 = 10+SeqTools.getBLOSUM62Score(getSymbol(r1,c).charAt(0),getSymbol(r2,c).charAt(0));
					double z2 = 10+SeqTools.getBLOSUM62Score(getSymbol(r2,c).charAt(0),getSymbol(r1,c).charAt(0));
					double n1 = 10+SeqTools.getBLOSUM62Score(getSymbol(r1,c).charAt(0),getSymbol(r1,c).charAt(0));
					double n2 = 10+SeqTools.getBLOSUM62Score(getSymbol(r2,c).charAt(0),getSymbol(r2,c).charAt(0));
					/*
                if(c==21||c==20||c==22)
                {
                    System.out.format("z1:%.3f z2:%.3f  n1:%.3f n2:%.3f %s %s\n",z1,z2,n1,n2,getSymbol(r1,c),getSymbol(r2,c));

                }
					 */
					s+=(z1/(1.0*n1))+(z2/(1.0*n2));
					s1+=(z1+z2);
					s2+=(n1+n2);
				}
				if( nGaps > (0.6*depth()) )
				{
					//gappy = true;
					//break;
				}
			}
			if(gappy)
			{
				int qual = 0;
				setColumnQuality(c, qual);
			}
			else
			{
				int qual = (int) ((s1/s2)*10);
				//int qual = (int) s;
				setColumnQuality(c, qual);
			}
		}

	}

	/**
	 * Set the overall quality of column <i>c</i> within the MSA.
	 * 
	 * @param c
	 * @param q
	 */
	public void setColumnQuality(int c, int q)
	{
		quality.put(c,q);
	}

	/**
	 * Get the overall quality of column <i>c</i> within the MSA.
	 * 
	 * @param c
	 * @return int
	 */
	public int getColumnQuality(int c)
	{
		return quality.get(c);
	}
}
