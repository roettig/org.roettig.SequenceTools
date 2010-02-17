package org.roettig.SequenceTools;

import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.io.*;
import java.net.URL;
import java.util.*;

import org.biojava.bio.Annotation;
import org.biojava.bio.BioException;
import org.biojava.bio.seq.ProteinTools;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.Location;
import org.biojava.utils.ChangeVetoException;





public class MSA implements Iterable<Sequence>
{
    protected SequenceSet seqs = null;
    protected HashMap<Integer,Integer> quality = null;
    private static String MUSCLEPATH = null;
    
    
    private static void checkMusclePath() throws Exception
    {
	    // msa.props must be in classpath
            URL url =  ClassLoader.getSystemResource("msa.props");
            
            File f = null;
            try
            {
        	f = new File(url.getFile());
            }
            catch(Exception e)
            {
        	MUSCLEPATH = "/usr/bin";
        	return ;
            }
            if(f.exists())
            {
                Properties prop = new Properties();
                try
                {
                  prop = new Properties();
                  prop.load(new FileInputStream(f));
                }
                catch(IOException e)
                {
                    System.out.println("could not find file msa.props");
                    e.printStackTrace();
                    // resort to default location
                    MUSCLEPATH = "/usr/bin";
                } 
                if(prop.containsKey("path"))
                {
                    MUSCLEPATH = prop.getProperty("path");
                }
                else
                {
                    throw new Exception("MUSCLEPATH not set");
                }
            }
            System.out.println("MUSCLEPATH="+MUSCLEPATH);

    }
    
    public static MSA createMuscleMSA(SequenceSet seqs) throws Exception
    {
        MSA ret     = new MSA();
        
        if(MUSCLEPATH==null)
        {
          checkMusclePath();            
        }
        
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
            return ret;
        }
        
        seqs.store(tmpIn.getAbsoluteFile().toString());
        
            
        try
        {
          ProcessBuilder builder = new ProcessBuilder( "/bin/bash", "-c", MUSCLEPATH+"/muscle -in "+tmpIn.getAbsoluteFile().toString()+" -out "+tmpOut.getAbsoluteFile().toString()); 
          Process p = builder.start();   
          p.waitFor();
          
          /*
          BufferedReader reader = new BufferedReader( new InputStreamReader(p.getErrorStream()) );
          String s;
          while( null != (s = reader.readLine()) ) 
          {
              System.out.println(s);
          }
          reader.close();
          */
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
    
        SequenceSet msaseqs = null;
        msaseqs = SequenceSet.readFromFile(tmpOut.toString());
        
        for(Sequence s: msaseqs)
        {
            ret.add( s );
        }
        
        tmpIn.delete();
        tmpOut.delete();
        return ret;
    }
    
    
    public void store(String filename)
    {
        seqs.store(filename);
    }
    
    public void load(String filename)
    {
        SequenceSet seqsIn = SequenceSet.readFromFile(filename);
        seqs.clear();
        for(Sequence s: seqsIn)
        {
           add( s );
        }
    }
    
    public Sequence getById(String id)
    {
    	return seqs.getById(id);
    }
    
    public Sequence getByIndex(int idx)
    {
    	return seqs.getByIndex(idx);
    }
    
    public MSA()
    {
        seqs    = new SequenceSet();
        quality = new HashMap<Integer,Integer>();
    }
    
    public int depth()
    {
        return seqs.size();
    }
    
    public int width()
    {
        if(depth()>0)
           return seqs.getByIndex(0).length();
        else
           return 0;
    }
    
    public SequenceSet getAlignedSubSequences(double frac)
    {
    	SequenceSet ret   = new SequenceSet();
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
            Sequence subseq = getSubSequence(s.getName(), keep);
            Annotation seqAn = subseq.getAnnotation();
            seqAn.setProperty("parent", s);
            ret.add( subseq );
        }
    	
    	return ret;
    }
    
    public SequenceSet getSubSequences( Set<Integer> cols, String refsid) throws ChangeVetoException, BioException
    {
        TreeMap<Integer,Integer> nidxs = new TreeMap<Integer,Integer>();
        for(Integer c: cols)
        {
            int idxN = mapSequenceIndexToColumnIndex(refsid,c);
            nidxs.put(idxN, 1);
        }
        SequenceSet ret = new SequenceSet();
        for(Sequence s: this)
        {
            Sequence subseq = getSubSequence(s.getName(), nidxs.keySet());
            Annotation seqAn = subseq.getAnnotation();
            seqAn.setProperty("parent", s);

            ret.add( subseq );
        }
        return ret;
    }
    
    
    public Sequence getSubSequence(String sid, Set<Integer> cols)
    {
        String subseq = "";
        for(Integer c: cols)
        {
            subseq += getSymbol(sid,  c-1 );
        }
        Sequence retseq = null;
        try
        {
            retseq = ProteinTools.createProteinSequence(subseq, sid);
        } 
        catch (IllegalSymbolException e)
        {
            e.printStackTrace();
        } 
        return retseq;
    }
    
    public final String getSymbol(int i, int j)
    {
        Sequence s = seqs.getByIndex(i);
        return s.seqString().substring(j, j+1);
    }
    
    public String getSymbol(String sid, int j)
    {
        Sequence s = seqs.getById(sid);
        return s.seqString().substring(j, j+1);
    }
    
    public int mapColumnIndexToSequenceIndex(String sid, int idx)
    {
        Sequence s = seqs.getById(sid);
        int sM=0;
        for(int i=0;i<idx;i++)
        {
         String symb = s.subStr(i+1,i+1);   
         if(!symb.equals("-"))             
           sM++;
        }
        return sM;
    }
    
    public int mapSequenceIndexToColumnIndex(String sid, int idx)
    {
        Sequence s = seqs.getById(sid);
        int sM=0;
        for(int i=0;i<s.length();i++)
        {
         if(idx==sM)
           return i;
         String symb = s.subStr(i+1,i+1);
         //System.out.println(symb);
         if(!symb.equals("-"))
           sM++;
        }
        return -1;
    }
    
    protected void add(Sequence seq)
    {
        seqs.add( seq );    
    }
    
    @Override
    public Iterator<Sequence> iterator()
    {
        return seqs.iterator();
    }
    
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
        
    public void setColumnQuality(int c, int q)
    {
        quality.put(c,q);
    }
    
    public int getColumnQuality(int c)
    {
        return quality.get(c);
    }
   

    public static void main(String[] args) throws Exception
    {
        /*
        SequenceSet coreseqs = SequenceSet.readFromFile("/tmp/core.fa");
        SequenceSet allseqs  = SequenceSet.readFromFile("/tmp/all.fa");
        
        allseqs.getIDs();
        
        Sequence s1 = allseqs.getByIndex(0);
        Sequence s2 = allseqs.getByIndex(1);
        
        PairwiseAlignment.align(s1, s2);
        */
        /*
        MSA msa = MSA.createMuscleMSA(coreseqs);
        
        for(Sequence s: msa)
        {
            System.out.println(s.seqString() );
        }
        
        HMM hmm = new HMM(msa);
        
        
        MSA allmsa = hmm.align(allseqs);
        allmsa.store("/tmp/all.afas");
        */
        /*
        MSA allmsa = new MSA();
        allmsa.load("/tmp/all.afas");
        System.out.println( allmsa.mapSequenceIndexToColumnIndex("pdb", 2) );
        System.out.println( allmsa.mapColumnIndexToSequenceIndex("pdb",821) );
        */
    }



}
