/**
 * 
 */
package org.roettig.SequenceTools;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.IllegalSymbolException;

/**
 * @author roettig
 *
 */
public class ASCMSA extends MSA
{

    protected Set<Integer> ascidx = new TreeSet<Integer>();
    
    public ASCMSA()
    {
	super();
    }
    
    public ASCMSA(MSA msa)
    {
	super();
	for(Sequence s: msa)
	{
	    this.add(s);
	}
    }
    
    @Override
    public void store(String filename)
    {
	Sequence s = seqs.getById("pdb");
	
	String idx = "";
	for(Integer i: ascidx)
	{
	    idx+=String.format("_%d",i);
	}
	System.out.println("idx="+idx);
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
    
    public void setASCIdx(Set<Integer> idx)
    {
	ascidx.clear();
	for(Integer i: idx)
	{
	    ascidx.add(i);
	}
    }
    
    public void setASCIdx(List<Integer> idx)
    {
	ascidx.clear();
	for(Integer i: idx)
	{
	    ascidx.add(i);
	}
    }
    
    @Override
    public void load(String filename)
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
    
    public SequenceSet getSignatures() throws Exception
    {
	return this.getSubSequences(ascidx,"pdb"); 
    }
    
    public static void main(String[] args) throws Exception
    {	
	ASCMSA ascmsa = new ASCMSA();
	ascmsa.load("/tmp/all.asc");
	
	SequenceSet ss = ascmsa.getSignatures();
	ss.store("/tmp/raus");
	
	/*
	List<Integer> idx = new Vector<Integer>();
	idx.add(4);
	ascmsa.setASCIdx(idx);
	ascmsa.store("/tmp/raus2");
	
	ascmsa.load("/tmp/raus2");

	System.out.println(ascmsa.getById("pdb").seqString());
	
	SequenceSet sss = ascmsa.getSignatures();
	sss.store("/tmp/raus3");
	*/
    }

}
