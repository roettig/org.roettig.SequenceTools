package org.roettig.SequenceTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import org.roettig.SequenceTools.exception.FileParseErrorException;

/**
 * The HMM class is used to build and operate on Hidden Markov Models.
 * 
 * @author roettig
 *
 */

public class HMM
{
    private String hmmstring = null;
    private String HMMERPATH = null;


    public HMM(MSA msa) throws Exception
    {
	/*
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
	*/
	
	String path = System.getProperty("hmmerpath");
	if(path!=null)
	    HMMERPATH = path;
	else
	    HMMERPATH = "/usr/bin";
	
	createHMM(msa);
    }

    public HMM(MSA msa, String _hmmerpath) throws Exception
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

    private void createHMM(MSA msa) throws Exception
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
	    throw(e);
	} 
	catch (IOException e)
	{
	    e.printStackTrace();
	    throw(e);
	}
	tmpOut.delete();
	tmpIn.delete();

    }

    /**
     * Load HMM from file.
     * 
     * @param filename
     * @throws IOException
     */
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
	} 
	catch (IOException e) 
	{
	    e.printStackTrace();    
	}
    }  
    
    public void save(String filename) throws IOException
    {
	saveToFile(filename);
    }

    /**
     * Save HMM to file.
     * 
     * @param filename
     */
    private void saveToFile(String filename)  throws IOException
    {
//	try
//	{
	    BufferedWriter os = new BufferedWriter(new FileWriter(filename));
	    os.write(hmmstring);
	    os.close();
//	}
//	catch(IOException e)
//	{
//	    e.printStackTrace();
//	}
    }

    public MSA align(SequenceSet seqs) throws Exception
    {
	MSA ret = new MSA();

	File tmpIn   = null;
	File tmpOut  = null;
	File hmmFile = null;
	try
	{
	    tmpIn   = File.createTempFile("createhmmali", ".IN");
	    tmpOut  = File.createTempFile("createhmmali", ".OUT");
	    hmmFile = File.createTempFile("createhmmali", ".IN");
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
	} 
	catch (InterruptedException e)
	{
	    e.printStackTrace();
	}

	//System.out.println(HMMERPATH+"/hmmalign --outformat A2M -o "+tmpOut.toString()+" "+hmmFile.toString()+" "+tmpIn.toString());

	try
	{
	    ProcessBuilder builder = new ProcessBuilder( "/bin/bash", "-c", HMMERPATH+"/hmmalign --outformat A2M -o "+tmpOut.toString()+" "+hmmFile.toString()+" "+tmpIn.toString()); 
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
	catch (IOException e1)
	{
	    e1.printStackTrace();
	} 


	try
	{
	    ret.load(tmpOut.toString());
	} 
	catch (FileNotFoundException e)
	{	
	    e.printStackTrace();
	    throw(e);
	} 
	catch (FileParseErrorException e)
	{
	    e.printStackTrace();
	    throw(e);
	}

	// delete temporary files
	hmmFile.delete();
	tmpIn.delete();
	tmpOut.delete();
	return ret;
    }




    public static void main(String[] args) throws Exception
    {
	/*
	MSA coremsa = new MSA();
	coremsa.load("/tmp/ASCserver/jobs/48d23da92c0aab19f4edc34b908e89e3/core.afa");
	HMM corehmm = new HMM(coremsa);
	SequenceSet allseqs = SequenceSet.readFromFile("/tmp/ASCserver/jobs/48d23da92c0aab19f4edc34b908e89e3/s3.fa");
        // generate complete MSA using hmmalign
        MSA allmsa = corehmm.align(allseqs);
	 */
    }

}
