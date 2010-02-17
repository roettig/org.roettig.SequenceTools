package org.roettig.SequenceTools;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.*;

import org.biojava.bio.alignment.*;

import org.biojava.utils.ChangeVetoException;


import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;


public class PairwiseAlignment
{
    private SubstitutionMatrix matrix = null;
    private Alphabet            alpha = null;
    private FiniteAlphabet   alphabet = null;
    
    public String s1;
    public String s2;
    
    public PairwiseAlignment()
    {
	alpha    = AlphabetManager.alphabetForName("PROTEIN");
        alphabet = (FiniteAlphabet) AlphabetManager.alphabetForName("PROTEIN");
        try
        {
            InputStream ins = getClass().getResource("/resources/BLOSUM62").openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(ins));
            //matrix = SubstitutionMatrix.getSubstitutionMatrix(br);
            StringBuffer stringMatrix = new StringBuffer("");
            String	   newLine	= System.getProperty("line.separator");
	    while (br.ready()) 
	    {
		String line = br.readLine();
		stringMatrix.append(line);
		//System.out.println(line);
		stringMatrix.append(newLine);
	    }
	    br.close();
	    String mat = stringMatrix.toString();
	    //FiniteAlphabet alpha = (FiniteAlphabet) AlphabetManager.alphabetForName("PROTEIN-TERM");//guessAlphabet(new BufferedReader(new StringReader(mat)));
	    matrix = new SubstitutionMatrix(alphabet, mat, "BLOSUM62");
            //matrix = new SubstitutionMatrix(alphabet, blosum );
        } 
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        } 
        catch (BioException e)
        {
            e.printStackTrace();
        } 
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    public double align(Sequence x, Sequence y) throws IllegalSymbolException, ChangeVetoException
    {
        SequenceAlignment aligner = new NeedlemanWunsch( 
                (short) 0,   // match
                (short) 3,   // replace
                (short) 10,  // insert
                (short) 10,  // delete
                (short) 0.5, // gapExtend
                matrix       // SubstitutionMatrix
              );
       
        /*
        System.out.println(x.seqString());
        System.out.println(y.seqString());
        System.out.println(x.getAlphabet().equals(y.getAlphabet()));
        System.out.println(x.getAlphabet().equals(matrix.getAlphabet()));
        System.out.println(matrix.getAlphabet().getName());
        System.out.println(x.getAlphabet().getName());
        Iterator iter = matrix.getAlphabet().iterator();
        
        while(iter.hasNext())
        {
            Symbol s = (Symbol) iter.next();
            System.out.println(s.getName()+" "+s.getMatches());
        }
        System.out.println("####");
        */
        
        Alignment ali = null;
        try
        {
            ali = aligner.getAlignment(x, y);
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }

        s1 = "";
        s2 = "";
        
        int len = ali.length();

        for(int i=1;i<=len;i++)
        {
            String symb1 = ali.symbolAt(x.getName(),i).getName();
            String symb2 = ali.symbolAt(y.getName(),i).getName();
            //String symb1 = ali.symbolAt(1).getName();
            //String symb2 = ali.symbolAt(2).getName();

            if(symb1.equals("[]")||symb1.equals("gap"))
                symb1 = "-";
            else
                symb1 = SeqTools.ThreeLetterToShort(symb1);
            
            if(symb2.equals("[]")||symb2.equals("gap"))
                symb2 = "-";
            else
                symb2 = SeqTools.ThreeLetterToShort(symb2);
            
            s1 += symb1;
            s2 += symb2;
        }    
        //a = s1;
        //b = s2;
        //System.out.println(s1);
        //System.out.println(s2);
        return SeqTools.getAlignedSequenceIdentity(s1, s2);        
        //return SeqTools.getGlobalSequenceIdentity(s1, s2);
    }

    public static void main(String[] args) throws IllegalSymbolException, ChangeVetoException
    {
	SequenceSet seqs = SequenceSet.readFromFile(PairwiseAlignment.class.getResource("/resources/test.fa").getFile());
	PairwiseAlignment pwa = new PairwiseAlignment();
	double pid = pwa.align( seqs.getByIndex(0), seqs.getByIndex(1) );
	System.out.println("pid="+pid);
	pid = pwa.align( seqs.getByIndex(0), seqs.getByIndex(0) );
	System.out.println("pid="+pid);
	
	String s="(A,1,Te1)";
	System.out.println(s.matches("\\([A-Z|_],\\d+,[A-z|0-9][A-z|0-9][A-z|0-9]\\)"));
    }


}
