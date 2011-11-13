package org.roettig.SequenceTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;

import org.roettig.SequenceTools.base.SequenceContainer;
import org.roettig.SequenceTools.base.impl.DefaultSequenceContainer;
import org.roettig.SequenceTools.binres.hmmer.HmmerDeployer;
import org.roettig.SequenceTools.format.FastaReader;
import org.roettig.SequenceTools.format.FastaWriter;

/**
 * The HMM class is used to build and operate on Hidden Markov Models.
 * 
 * @author roettig
 *
 */

public class HMM implements Serializable
{
	private String hmmstring = null;
	
	private static String HMMALIGNPATH;
	private static String HMMBUILDPATH;
	
	static
	{
		HMMBUILDPATH = HmmerDeployer.deployHMMBUILD();
		HMMALIGNPATH = HmmerDeployer.deployHMMALIGN();
	}

	public HMM(MSA msa) throws Exception
	{
		createHMM(msa);
	}

	public HMM(MSA msa, String _hmmerpath) throws Exception
	{
		createHMM(msa);
	}


	public HMM(DefaultSequenceContainer seqs) throws Exception
	{
		MSA msa = MSA.createMuscleMSA(seqs);
		createHMM(msa);           
	}

	public HMM(DefaultSequenceContainer seqs, String _hmmerpath) throws Exception
	{
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
			throw new RuntimeException(e);
		}

		// store MSA in filesystem 
		msa.store(tmpIn.getAbsoluteFile().toString(), new FastaWriter());

		try
		{
			ProcessBuilder builder = new ProcessBuilder(HMMBUILDPATH,"-g","-F", tmpOut.toString(),tmpIn.toString()); 
			Process p = builder.start();   
			p.waitFor();
			loadFromFile(tmpOut.toString());
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
		
		
		tmpOut.delete();
		tmpIn.delete();

	}

	private static String NEWLINE = System.getProperty("line.separator");
	
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
			StringBuffer sb = new StringBuffer();
			
			String str = null;
			while ((str = in.readLine()) != null) 
			{
				sb.append(str+NEWLINE);
			}
			in.close();
			hmmstring = sb.toString();
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
		BufferedWriter os = new BufferedWriter(new FileWriter(filename));
		os.write(hmmstring);
		os.close();
	}

	public MSA align(SequenceContainer seqs) throws Exception
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
			throw new RuntimeException(e);
		}        

		new FastaWriter().write(seqs, tmpIn.getAbsolutePath());
		saveToFile(hmmFile.toString());

		try
		{
			ProcessBuilder builder = new ProcessBuilder( HMMALIGNPATH,"--outformat", "A2M", "-o",tmpOut.toString(),hmmFile.toString(),tmpIn.toString()); 
			Process p = builder.start();   
			p.waitFor();			
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		} 
		catch (IOException e1)
		{
			e1.printStackTrace();
			throw new RuntimeException(e1);
		} 


		ret.load(tmpOut.toString(), new FastaReader());
 

		// delete temporary files
		hmmFile.delete();
		tmpIn.delete();
		tmpOut.delete();
		
		return ret;
	}
}
