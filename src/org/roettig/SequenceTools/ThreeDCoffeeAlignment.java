/**
 * 
 */
package org.roettig.SequenceTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.biojava.bio.seq.Sequence;
import org.roettig.SequenceTools.exception.FileParseErrorException;
import org.roettig.SequenceTools.helper.ClientHttpRequest;

/**
 * @author roettig
 *
 */
public class ThreeDCoffeeAlignment
{
    private String PID;
    private SequenceSet seqs = new SequenceSet();
    private Map<String,String> masked_name_2_orig_name = new HashMap<String,String>();
    
    private static String RESULTURL = "http://www.igs.cnrs-mrs.fr/Tcoffee/Tmp/EXPA/";
    
    public static boolean isFinished(String job)
    {
	URL url = null;
	BufferedReader in = null;
	try
	{
	    url = new URL(RESULTURL+job);
	    in = new BufferedReader(new InputStreamReader(url.openStream()));
	    String line;
	    while ((line = in.readLine()) != null)
	    {
		if(line.indexOf("Error 404")!=-1)
		{
		    return false;
		}
	    }
	    in.close();
	} 
	catch (Exception e)
	{
	    //e.printStackTrace();
	    return false;
	}
	return true;
    }
    
    ThreeDCoffeeAlignment()
    {
	
    }
    
    ThreeDCoffeeAlignment(SequenceSet _seqs)
    {
	int idx = 1;
	for(Sequence s: _seqs)
	{
	    String sid = String.format("%d",idx);
	    Sequence sclone = SeqTools.makeProteinSequence(sid,s.seqString());
	    seqs.add(sclone);
	    masked_name_2_orig_name.put(sid,s.getName());
	    idx++;
	}
    }
    
    private static String fetchPID() throws IOException
    {
	URL url = new URL("http://www.igs.cnrs-mrs.fr/Tcoffee/tcoffee_cgi/index.cgi?stage1=1&daction=EXPRESSO(3DCoffee)::Advanced");
	BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	
	String ret = null;
	
	String line;

	while ((line = in.readLine()) != null)
	{
	    
	    if(line.indexOf("<input type=\"hidden\" name=\"pid\" value=\"")!=-1)
	    {
	        Pattern p = Pattern.compile("pid\" value=\"(\\d+)\"");
	        Matcher m = p.matcher(line);
	        boolean result = m.find();
	        if(result)
	            ret = m.group(1);
	    }
	}
	in.close();
	return ret;
    }
    
    public void align() throws Exception
    {
	PID = fetchPID();
	wrapSequences();
	sendData();
	fetchData();
    }

    private void sendData() throws IOException
    {
	 ClientHttpRequest req = new ClientHttpRequest("http://www.igs.cnrs-mrs.fr/Tcoffee/tcoffee_cgi/index.cgi");
	 req.setParameter("stage2","2"); 
	 req.setParameter("pid",PID);
	 req.setParameter("daction","EXPRESSO(3DCoffee)::Advanced");
	 req.setParameter("executable","t_coffee");
	 req.setParameter("-evaluate_mode","t_coffee_slow");
	 req.setParameter("-template_file","SCRIPT_webblast.pl@method#pdbid@database#expressopdb@blast_dir#blastexpresso");
	 req.setParameter("-output","fasta_aln,score_ascii,score_html");
	 req.setParameter("-case","upper");
	 req.setParameter("-seqnos","on");
	 req.setParameter("-in","Mslow_pair,Mfugue_pair,Msap_pair");
	 req.setParameter("-outorder","input");
	 req.setParameter("Submit","Submit");
	 req.setParameter("list_visible_outfile","fasta_aln,score_ascii,score_html");
	 req.setParameter("-uploaded_file-in",tmpIn); 
	 InputStream is = req.post();

	 BufferedReader bis = new BufferedReader(new InputStreamReader(is) );
	 String line; 
	 while ((line = bis.readLine()) != null) 
	 { 
	     //System.out.println(line);
	 }  
	 Map<String,List<String> > header = req.getRequestHeaders();
	 for(String s: header.keySet())
	 {
	     //System.out.println(s);
	     for(String vs: header.get(s) )
	     {
		 //System.out.println("\t"+vs);

		 if(s!=null && s.equals("Refresh"))
		 {
		     //String s = "feedback.cgi?level=Advanced&child=8132&result_file=tcfEXPA99715_8130.file_result.html&pg_source=/home/igs/public_html/Tcoffee/tcoffee_cgi/index.cgi&time_init=1269625526&email=&mode=EXPRESSO(3DCoffee)";
		     String toks1[] = vs.split("&");
		     String toks2[] = toks1[2].split("=");
		     System.out.println(toks2[1]);
		     joburl = toks2[1];
		     String toks3[] = joburl.split("\\.");
		     jobid  = toks3[0]; 
		     System.out.println("jobid="+jobid);
		 }

	     }
	 }
	
    }

    private String jobid;
    private File tmpIn   = null;
   
    
    private void wrapSequences()
    {
	try
	{
	    tmpIn  = File.createTempFile("3DCoffee", ".IN");
	}
	catch(IOException e)
	{
	    e.printStackTrace();
	}
	seqs.store(tmpIn.getAbsoluteFile().toString());
	
    }
    
    private void fetchData() throws Exception
    {
	int     nSteps = 10;
	int     nSecs  = 30;
	boolean ready  = false;
	for(int i=0;i<nSteps;i++)
	{
	    System.out.println("Waiting");
	    if(ready)
		break;
	    try
	    {
		Thread.sleep(nSecs*1000);
	    } 
	    catch (InterruptedException e)
	    {
		e.printStackTrace();
	    }
	    if(isFinished(joburl))
	    {
		ready = true;
		System.out.println("Finished");
	    }
	}
	if(ready)
	{
	    URL url = new URL(RESULTURL+jobid+".fasta_aln");
	    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	    SequenceSet rseqs = SequenceSet.readFromReader(in);
	    rseqs.store("/tmp/result.afa");
	}

    }
    
    private String joburl = "";

    public static void main(String[] args) throws Exception
    {
	System.setProperty("http.proxyHost", "134.2.12.41");
	System.setProperty("http.proxyPort", "3128");

	//ThreeDCoffeeAlignment.fetchPID();
	ThreeDCoffeeAlignment ali = new ThreeDCoffeeAlignment( SequenceSet.readFromFile("/tmp/seqs.fa") );
	ali.align();
	
	
	
    }

}
