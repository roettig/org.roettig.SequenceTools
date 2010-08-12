package org.roettig.SequenceTools;

import org.biojava.bio.BioException;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.symbol.*;
import org.biojava.bio.alignment.*;
import org.biojava.utils.ChangeVetoException;
import java.io.*;


/**
 * The PairwiseAlignment class is used for calculation of pairwise alignments
 * between sequence pairs.
 * 
 * @author roettig
 *
 */
public class PairwiseAlignment
{
    private SubstitutionMatrix  matrix   = null;
    private FiniteAlphabet      alphabet = null;

    private short gap_open = 10;
    private short gap_ext  = 1;

    public PairwiseAlignment()
    {
	alphabet = (FiniteAlphabet) AlphabetManager.alphabetForName("PROTEIN");
	try
	{
	    InputStream ins = getClass().getResource("/resources/BLOSUM62").openStream();
	    BufferedReader br = new BufferedReader(new InputStreamReader(ins));
	    StringBuffer stringMatrix = new StringBuffer("");
	    String	   newLine    = System.getProperty("line.separator");
	    while (br.ready()) 
	    {
		String line = br.readLine();
		stringMatrix.append(line);
		stringMatrix.append(newLine);
	    }
	    br.close();
	    String mat = stringMatrix.toString();
	    matrix = new SubstitutionMatrix(alphabet, mat, "BLOSUM62");
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
    
    public void setGapExtensionCost(short _e)
    {
	gap_ext  = _e;
    }
    
    public void setGapOpenCost(short _e)
    {
	gap_open = _e;
    }

    
    /**
     * Align the two sequences.
     * 
     * @param x
     * @param y
     * @return
     * @throws IllegalSymbolException
     * @throws ChangeVetoException
     */
    public double align(Sequence x, Sequence y) throws IllegalSymbolException, ChangeVetoException
    {
	return align(x,y,AlignedSequenceIdentity.getInstance());
    }
    
    /**
     * Align the two sequences.
     * 
     * @param x
     * @param y
     * @param id_calc SequenceIdentity calculation method
     * @return
     * @throws IllegalSymbolException
     * @throws ChangeVetoException
     */
    public double align(Sequence x, Sequence y, SequenceIdentity id_calc) throws IllegalSymbolException, ChangeVetoException
    {
	SequenceAlignment aligner = new NeedlemanWunsch( 
		(short) 0,   // match
		(short) 3,   // replace
		(short) gap_open,  // insert
		(short) gap_open,  // delete
		(short) gap_ext, // gapExtend
		matrix       // SubstitutionMatrix
	);
	Alignment ali = null;
	try
	{
	    ali = aligner.getAlignment(x, y);
	} 
	catch (Exception e)
	{
	    e.printStackTrace();
	}

	int len = ali.length();

	StringBuffer sbuf1 = new StringBuffer("");
	StringBuffer sbuf2 = new StringBuffer("");
	
	for(int i=1;i<=len;i++)
	{
	    String symb1 = ali.symbolAt(x.getName(),i).getName();
	    String symb2 = ali.symbolAt(y.getName(),i).getName();

	    if(symb1.equals("[]")||symb1.equals("gap"))
		symb1 = "-";
	    else
		symb1 = SeqTools.ThreeLetterToShort(symb1);

	    if(symb2.equals("[]")||symb2.equals("gap"))
		symb2 = "-";
	    else
		symb2 = SeqTools.ThreeLetterToShort(symb2);

	    sbuf1.append(symb1);
	    sbuf2.append(symb2);
	} 
	return id_calc.calculate(sbuf1.toString(), sbuf2.toString());
    }
}
