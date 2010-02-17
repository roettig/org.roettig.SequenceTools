package org.roettig.SequenceTools;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.io.SeqIOTools;



public class HMM
{
    private String hmmstring = null;
    private String HMMERPATH = null;
    
    
    public HMM(MSA msa) throws Exception
    {
           if(HMMERPATH==null)
           {
               URL url =  ClassLoader.getSystemResource("hmmer.props");
               File f = null;
               try
               {
        	   f = new File(url.getFile());
               }
               catch(Exception e)
               {
        	 HMMERPATH = "/usr/bin";
               }
               if(f!=null && f.exists())
               {
                   Properties prop = new Properties();
                   try
                   {
                     prop = new Properties();
                     prop.load(new FileInputStream(f));
                   }
                   catch(IOException e)
                   {
                       e.printStackTrace();
                   } 
                   if(prop.containsKey("path"))
                   {
                       HMMERPATH = prop.getProperty("path");
                   }
                   else
                   {
                       throw new Exception("HMMERPATH not set");
                   }
               }
           }
           
           createHMM(msa);
    }
    
    public HMM(MSA msa, String _hmmerpath)
    {
           HMMERPATH = _hmmerpath;
           createHMM(msa);
    }
    
    
    public HMM(SequenceSet seqs) throws Exception
    {
           MSA msa = MSA.createMuscleMSA(seqs);
           createHMM(msa);           
    }
    
    public HMM(SequenceSet seqs, String _hmmerpath) throws Exception
    {
           HMMERPATH = _hmmerpath;
           MSA msa = MSA.createMuscleMSA(seqs);
           createHMM(msa);           
    }
    
    private void createHMM(MSA msa)
    {
        File tmpIn  = null;
        File tmpOut = null;
        try
        {
           tmpIn  = File.createTempFile("createhmm", ".IN");
           tmpOut = File.createTempFile("createhmm", ".OUT");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        // store MSA in filesystem 
        msa.store(tmpIn.getAbsoluteFile().toString());
        
        try
        {
          ProcessBuilder builder = new ProcessBuilder( "/bin/bash", "-c", HMMERPATH+"/hmmbuild -g -F "+tmpOut.toString()+" "+tmpIn.toString()); 
          Process p = builder.start();   
          p.waitFor();
          loadFromFile(tmpOut.toString());
          //saveToFile("/tmp/raus.hmm");
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        } 
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        tmpOut.delete();
        tmpIn.delete();
        
    }
    
    private void loadFromFile(String filename) throws IOException
    {
        try 
        {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            hmmstring = "";
            String str = null;
            while ((str = in.readLine()) != null) 
            {
               hmmstring += str+"\n";
            }
            in.close();
            System.out.println("hmmstring="+hmmstring.substring(0,30));
        } 
        catch (IOException e) 
        {
          e.printStackTrace();    
        }
    }  
    
    private void saveToFile(String filename)
    {
        try
        {
          BufferedWriter os = new BufferedWriter(new FileWriter(filename));
          os.write(hmmstring);
          os.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public MSA align(SequenceSet seqs)
    {
        MSA ret = new MSA();
        
        File tmpIn   = null;
        File tmpOut  = null;
        File hmmFile = null;
        try
        {
           tmpIn   = File.createTempFile("createmsa", ".IN");
           tmpOut  = File.createTempFile("createmsa", ".OUT");
           hmmFile = File.createTempFile("createmsa", ".IN");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }        
        
        seqs.store(tmpIn.toString());
        saveToFile(hmmFile.toString());
        try
	{
	    Thread.sleep(30);
	} catch (InterruptedException e2)
	{
	    // TODO Auto-generated catch block
	    e2.printStackTrace();
	}
        System.out.println(HMMERPATH+"/hmmalign --outformat A2M -o "+tmpOut.toString()+" "+hmmFile.toString()+" "+tmpIn.toString());
        try
        {
          ProcessBuilder builder = new ProcessBuilder( "/bin/bash", "-c", HMMERPATH+"/hmmalign --outformat A2M -o "+tmpOut.toString()+" "+hmmFile.toString()+" "+tmpIn.toString()); 
          Process p = builder.start();   
          p.waitFor();
          BufferedReader reader = new BufferedReader( new InputStreamReader(p.getErrorStream()) );
          String s;
          while( null != (s = reader.readLine()) ) 
          {
              System.out.println(s);
          }
          reader.close();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        } 
        catch (IOException e1)
        {
            e1.printStackTrace();
        } 
        
        ret.load(tmpOut.toString());
        
        hmmFile.delete();
        tmpIn.delete();
        tmpOut.delete();
        return ret;
    }
    
    
    
    
    public static void main(String[] args) throws Exception
    {
	MSA coremsa = new MSA();
	coremsa.load("/tmp/ASCserver/jobs/48d23da92c0aab19f4edc34b908e89e3/core.afa");
	HMM corehmm = new HMM(coremsa);
	SequenceSet allseqs = SequenceSet.readFromFile("/tmp/ASCserver/jobs/48d23da92c0aab19f4edc34b908e89e3/s3.fa");
        // generate complete MSA using hmmalign
        MSA allmsa = corehmm.align(allseqs);
    }

}
