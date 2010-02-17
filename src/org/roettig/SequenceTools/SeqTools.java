package org.roettig.SequenceTools;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.biojava.bio.BioException;
import org.biojava.bio.SimpleAnnotation;
import org.biojava.bio.seq.Sequence;
import org.biojava.bio.seq.db.SequenceDB;
import org.biojava.bio.seq.impl.SimpleSequenceFactory;
import org.biojava.bio.seq.io.SeqIOTools;
import org.biojava.bio.seq.io.SymbolTokenization;
import org.biojava.bio.symbol.Alphabet;
import org.biojava.bio.symbol.AlphabetManager;
import org.biojava.bio.symbol.FiniteAlphabet;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.SimpleSymbolList;
import org.biojava.bio.symbol.SymbolList;

public class SeqTools
{
    static int[][] BLOSUM62 =
        //  A  R  N  D  C  Q  E  G  H  I  L  K  M  F  P  S  T  W  Y  V
       { {  4,-1,-2,-2, 0,-1,-1, 0,-2,-1,-1,-1,-1,-2,-1, 1, 0,-3,-2, 0},  // A
         { -1, 5, 0,-2,-3, 1, 0,-2, 0,-3,-2, 2,-1,-3,-2,-1,-1,-3,-2,-3},  // R
         { -2, 0, 6, 1,-3, 0, 0, 0, 1,-3,-3, 0,-2,-3,-2, 1, 0,-4,-2,-3},  // N
         { -2,-2, 1, 6,-3, 0, 2,-1,-1,-3,-4,-1,-3,-3,-1, 0,-1,-4,-3,-3},  // D
         {  0,-3,-3,-3, 9,-3,-4,-3,-3,-1,-1,-3,-1,-2,-3,-1,-1,-2,-2,-1},  // C
         { -1, 1, 0, 0,-3, 5, 2,-2, 0,-3,-2, 1, 0,-3,-1, 0,-1,-2,-1,-2},  // Q
         { -1, 0, 0, 2,-4, 2, 5,-2, 0,-3,-3, 1,-2,-3,-1, 0,-1,-3,-2,-2},  // E
         {  0,-2, 0,-1,-3,-2,-2, 6,-2,-4,-4,-2,-3,-3,-2, 0,-2,-2,-3,-3},  // G
         { -2, 0, 1,-1,-3, 0, 0,-2, 8,-3,-3,-1,-2,-1,-2,-1,-2,-2, 2,-3},  // H
         { -1,-3,-3,-3,-1,-3,-3,-4,-3, 4, 2,-3, 1, 0,-3,-2,-1,-3,-1, 3},  // I
         { -1,-2,-3,-4,-1,-2,-3,-4,-3, 2, 4,-2, 2, 0,-3,-2,-1,-2,-1, 1},  // L
         { -1, 2, 0,-1,-3, 1, 1,-2,-1,-3,-2, 5,-1,-3,-1, 0,-1,-3,-2,-2},  // K
         { -1,-1,-2,-3,-1, 0,-2,-3,-2, 1, 2,-1, 5, 0,-2,-1,-1,-1,-1, 1},  // M
         { -2,-3,-3,-3,-2,-3,-3,-3,-1, 0, 0,-3, 0, 6,-4,-2,-2, 1, 3,-1},  // F
         { -1,-2,-2,-1,-3,-1,-1,-2,-2,-3,-3,-1,-2,-4, 7,-1,-1,-4,-3,-2},  // P
         {  1,-1, 1, 0,-1, 0, 0, 0,-1,-2,-2, 0,-1,-2,-1, 4, 1,-3,-2,-2},  // S
         {  0,-1, 0,-1,-1,-1,-1,-2,-2,-1,-1,-1,-1,-2,-1, 1, 5,-2,-2, 0},  // T
         { -3,-3,-4,-4,-2,-2,-3,-2,-2,-3,-2,-3,-1, 1,-4,-3,-2,11, 2,-3},  // W
         { -2,-2,-2,-3,-2,-1,-2,-3, 2,-1,-1,-2,-1, 3,-3,-2,-2, 2, 7,-1},  // Y
         {  0,-3,-3,-3,-1,-2,-2,-3,-3, 3, 1,-2, 1,-1,-2,-2, 0,-3,-1, 4}   // V
        //  A  R  N  D  C  Q  E  G  H  I  L  K  M  F  P  S  T  W  Y  V
         };
    
    //
    static double[][] BLOSUM62kn =
           //  A  R  N  D  C  Q  E  G  H  I  L  K  M  F  P  S  T  W  Y  V
        { { 1.0000,0.1487,0.0884,0.0884,0.1051,0.1487,0.1487,0.1768,0.0625,0.1768,0.1768,0.1487,0.1487,0.0884,0.1051,0.3536,0.2102,0.0263,0.0743,0.2500 },
    	  { 0.1487,1.0000,0.1487,0.0743,0.0312,0.2500,0.1768,0.0743,0.1051,0.0743,0.1051,0.3536,0.1250,0.0526,0.0625,0.1487,0.1250,0.0221,0.0625,0.0743 },
    	  { 0.0884,0.1487,1.0000,0.1768,0.0263,0.1487,0.1487,0.1250,0.1250,0.0625,0.0625,0.1487,0.0743,0.0442,0.0526,0.2500,0.1487,0.0131,0.0526,0.0625 },
    	  { 0.0884,0.0743,0.1768,1.0000,0.0263,0.1487,0.2973,0.0884,0.0625,0.0625,0.0442,0.1051,0.0526,0.0442,0.0743,0.1768,0.1051,0.0131,0.0372,0.0625 },
    	  { 0.1051,0.0312,0.0263,0.0263,1.0000,0.0312,0.0221,0.0263,0.0186,0.0743,0.0743,0.0312,0.0625,0.0372,0.0221,0.0743,0.0625,0.0156,0.0312,0.0743 },
    	  { 0.1487,0.2500,0.1487,0.1487,0.0312,1.0000,0.3536,0.0743,0.1051,0.0743,0.1051,0.2500,0.1768,0.0526,0.0884,0.2102,0.1250,0.0312,0.0884,0.1051 },
    	  { 0.1487,0.1768,0.1487,0.2973,0.0221,0.3536,1.0000,0.0743,0.1051,0.0743,0.0743,0.2500,0.0884,0.0526,0.0884,0.2102,0.1250,0.0221,0.0625,0.1051 },
    	  { 0.1768,0.0743,0.1250,0.0884,0.0263,0.0743,0.0743,1.0000,0.0442,0.0442,0.0442,0.0743,0.0526,0.0442,0.0526,0.1768,0.0743,0.0263,0.0372,0.0625 },
    	  { 0.0625,0.1051,0.1250,0.0625,0.0186,0.1051,0.1051,0.0442,1.0000,0.0442,0.0442,0.0743,0.0526,0.0625,0.0372,0.0884,0.0526,0.0186,0.1487,0.0442 },
    	  { 0.1768,0.0743,0.0625,0.0625,0.0743,0.0743,0.0743,0.0442,0.0442,1.0000,0.5000,0.0743,0.2973,0.1768,0.0526,0.1250,0.1487,0.0263,0.1051,0.7071 },
    	  { 0.1768,0.1051,0.0625,0.0442,0.0743,0.1051,0.0743,0.0442,0.0442,0.5000,1.0000,0.1051,0.4204,0.1768,0.0526,0.1250,0.1487,0.0372,0.1051,0.3536 },
    	  { 0.1487,0.3536,0.1487,0.1051,0.0312,0.2500,0.2500,0.0743,0.0743,0.0743,0.1051,1.0000,0.1250,0.0526,0.0884,0.2102,0.1250,0.0221,0.0625,0.1051 },
    	  { 0.1487,0.1250,0.0743,0.0526,0.0625,0.1768,0.0884,0.0526,0.0526,0.2973,0.4204,0.1250,1.0000,0.1487,0.0625,0.1487,0.1250,0.0442,0.0884,0.2973 },
    	  { 0.0884,0.0526,0.0442,0.0442,0.0372,0.0526,0.0526,0.0442,0.0625,0.1768,0.1768,0.0526,0.1487,1.0000,0.0263,0.0884,0.0743,0.0743,0.2973,0.1250 },
    	  { 0.1051,0.0625,0.0526,0.0743,0.0221,0.0884,0.0884,0.0526,0.0372,0.0526,0.0526,0.0884,0.0625,0.0263,1.0000,0.1051,0.0884,0.0110,0.0312,0.0743 },
    	  { 0.3536,0.1487,0.2500,0.1768,0.0743,0.2102,0.2102,0.1768,0.0884,0.1250,0.1250,0.2102,0.1487,0.0884,0.1051,1.0000,0.2973,0.0263,0.0743,0.1250 },
    	  { 0.2102,0.1250,0.1487,0.1051,0.0625,0.1250,0.1250,0.0743,0.0526,0.1487,0.1487,0.1250,0.1250,0.0743,0.0884,0.2973,1.0000,0.0312,0.0625,0.2102 },
    	  { 0.0263,0.0221,0.0131,0.0131,0.0156,0.0312,0.0221,0.0263,0.0186,0.0263,0.0372,0.0221,0.0442,0.0743,0.0110,0.0263,0.0312,1.0000,0.0884,0.0263 },
    	  { 0.0743,0.0625,0.0526,0.0372,0.0312,0.0884,0.0625,0.0372,0.1487,0.1051,0.1051,0.0625,0.0884,0.2973,0.0312,0.0743,0.0625,0.0884,1.0000,0.1051 },
    	  { 0.2500,0.0743,0.0625,0.0625,0.0743,0.1051,0.1051,0.0625,0.0442,0.7071,0.3536,0.1051,0.2973,0.1250,0.0743,0.1250,0.2102,0.0263,0.1051,1.0000 }
    	 };
    	
    // chem sim
    static double[][] CHEMSIMkn =
        { { 5.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,3.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,1.0000,1.0000,1.0000,0.0000,0.0000,0.0000 },
    	  { 0.0000,6.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,3.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000 },
    	  { 0.0000,0.0000,5.0000,3.0000,0.0000,2.0000,1.0000,0.0000,1.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,1.0000,1.0000,0.0000,0.0000,0.0000 },
    	  { 0.0000,0.0000,3.0000,5.0000,0.0000,1.0000,2.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000 },
    	  { 0.0000,0.0000,0.0000,0.0000,6.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,2.0000,0.0000,0.0000,0.0000,0.0000 },
    	  { 0.0000,0.0000,2.0000,1.0000,0.0000,5.0000,3.0000,0.0000,2.0000,0.0000,0.0000,1.0000,0.0000,0.0000,0.0000,1.0000,0.0000,0.0000,0.0000,0.0000 },
    	  { 0.0000,0.0000,1.0000,2.0000,0.0000,3.0000,5.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000 },
    	  { 3.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,6.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000 },
    	  { 0.0000,0.0000,1.0000,0.0000,0.0000,2.0000,0.0000,0.0000,6.0000,0.0000,0.0000,0.0000,0.0000,2.0000,0.0000,0.0000,0.0000,1.0000,2.0000,0.0000 },
    	  { 0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,5.0000,3.0000,0.0000,3.0000,1.0000,0.0000,0.0000,1.0000,1.0000,1.0000,2.0000 },
    	  { 0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,3.0000,5.0000,0.0000,3.0000,2.0000,1.0000,0.0000,0.0000,1.0000,1.0000,1.0000 },
    	  { 0.0000,3.0000,0.0000,0.0000,0.0000,1.0000,0.0000,0.0000,0.0000,0.0000,0.0000,5.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000 },
    	  { 0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,3.0000,3.0000,0.0000,6.0000,2.0000,0.0000,0.0000,0.0000,1.0000,1.0000,1.0000 },
    	  { 0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,2.0000,1.0000,2.0000,0.0000,2.0000,6.0000,0.0000,0.0000,0.0000,3.0000,3.0000,1.0000 },
    	  { 1.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,1.0000,0.0000,0.0000,0.0000,5.0000,0.0000,1.0000,0.0000,0.0000,1.0000 },
    	  { 1.0000,0.0000,1.0000,0.0000,2.0000,1.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,5.0000,3.0000,0.0000,0.0000,0.0000 },
    	  { 1.0000,0.0000,1.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,1.0000,0.0000,0.0000,0.0000,0.0000,1.0000,3.0000,5.0000,0.0000,0.0000,0.0000 },
    	  { 0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,1.0000,1.0000,1.0000,0.0000,1.0000,3.0000,0.0000,0.0000,0.0000,6.0000,2.0000,0.0000 },
    	  { 0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,2.0000,1.0000,1.0000,0.0000,1.0000,3.0000,0.0000,0.0000,0.0000,2.0000,6.0000,0.0000 },
    	  { 0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,0.0000,2.0000,1.0000,0.0000,1.0000,1.0000,1.0000,0.0000,0.0000,0.0000,0.0000,5.0000 },
        };

    //
    public static int getBLOSUM62Score(char x, char y)
    {
        int idx1 = OneLetterToInt(x);
        int idx2 = OneLetterToInt(y);
        if(x=='X' || y=='X')
            return 0;
        if(idx1==-1||idx2==-1)
            return 0;
        return BLOSUM62[idx1][idx2];
    }
    
    public static double getNormalizedBLOSUM62ScoreSeq(String x, String y)
    {
    	double sum = 0.0;
    	for(int i=0;i<x.length();i++)
    		sum += getNormalizedBLOSUM62Score(x.charAt(i), y.charAt(i));
    	return sum;
    }

    public static double getNormalizedChemSimSeq(String x, String y)
    {
    	double sum = 0.0;
    	for(int i=0;i<x.length();i++)
    		sum += getNormalizedChemicalSimilarity(x.charAt(i), y.charAt(i));
    	return sum;
    }
    
    public static double getNormalizedBLOSUM62Score(char x, char y)
    {
        int idx1 = OneLetterToInt(x);
        int idx2 = OneLetterToInt(y);
        if(x=='X' || y=='X')
            return 0.0;
        if(idx1==-1||idx2==-1)
            return 0.0;
        return BLOSUM62kn[idx1][idx2];
    }
    
    public static double getNormalizedChemicalSimilarity(char x, char y)
    {
        int idx1 = OneLetterToInt(x);
        int idx2 = OneLetterToInt(y);
        if(x=='X' || y=='X')
            return 0.0;
        if(idx1==-1||idx2==-1)
            return 0.0;
        return CHEMSIMkn[idx1][idx2];
    }
    public static String ThreeLetterToShort(String code)
    {
        code = code.toUpperCase();
        String ret = "X";
        if(code.equals("ALA"))
            ret = "A";
        if(code.equals("CYS"))
            ret = "C";
        if(code.equals("ASP"))
            ret = "D";
        if(code.equals("GLU"))
            ret = "E";
        if(code.equals("PHE"))
            ret = "F";
        if(code.equals("GLY"))
            ret = "G";
        if(code.equals("HIS"))
            ret = "H";
        if(code.equals("ILE"))
            ret = "I";
        if(code.equals("LYS"))
            ret = "K";
        if(code.equals("LEU"))
            ret = "L";
        if(code.equals("MET"))
            ret = "M";
        if(code.equals("ASN"))
            ret = "N";
        if(code.equals("PRO"))
            ret = "P";
        if(code.equals("GLN"))
            ret = "Q";
        if(code.equals("ARG"))
            ret = "R";
        if(code.equals("SER"))
            ret = "S";
        if(code.equals("THR"))
            ret = "T";
        if(code.equals("VAL"))
            ret = "V";
        if(code.equals("TRP"))
            ret = "W";
        if(code.equals("TYR"))
            ret = "Y";        
        //
        if(code.equals("HID"))
            ret = "H";
        if(code.equals("HIP"))
            ret = "H";
        if(code.equals("CYX"))
            ret = "C";        
        return ret;
    }
    
    public static int OneLetterToInt(char code)
    {
        //code = code.toUpperCase();
        //  A  R  N  D  C  Q  E  G  H  I  L  K  M  F  P  S  T  W  Y  V
        int ret = -1;
        if(code=='A')
            ret = 0;
        if(code=='R')
            ret = 1;
        if(code=='N')
            ret = 2;
        if(code=='D')
            ret = 3;
        if(code=='C')
            ret = 4;
        if(code=='Q')
            ret = 5;
        if(code=='E')
            ret = 6;
        if(code=='G')
            ret = 7;
        if(code=='H')
            ret = 8;
        if(code=='I')
            ret = 9;
        if(code=='L')
            ret = 10;
        if(code=='K')
            ret = 11;
        if(code=='M')
            ret = 12;
        if(code=='F')
            ret = 13;
        if(code=='P')
            ret = 14;
        if(code=='S')
            ret = 15;
        if(code=='T')
            ret = 16;
        if(code=='W')
            ret = 17;
        if(code=='Y')
            ret = 18;
        if(code=='V')
            ret = 19;        
        return ret;
    }
    
    public static double getAlignedSequenceIdentity(Sequence x, Sequence y)
    {
        String s1 = x.seqString();
        String s2 = y.seqString();
        return getAlignedSequenceIdentity(s1,s2);    
    }
    
    public static double getAlignedSequenceIdentity(String s1, String s2)
    {
        double matches    = 0.0;
        double mismatches = 0.0;
        

        for(int i=0;i<s1.length();i++)
        {
            String symb1 = s1.substring(i,i+1);
            String symb2 = s2.substring(i,i+1);
            
            if(symb1.equals("-") || symb2.equals("-"))
                continue;
            if(symb1.equals(symb2))
                matches++;
            else
                mismatches++;
        }
        return matches/(matches+mismatches);
    }

    public static double getGlobalSequenceIdentity(Sequence x, Sequence y)
    {
        String s1 = x.seqString();
        String s2 = y.seqString();
        return getAlignedSequenceIdentity(s1,s2);    
    }
    
    public static double getGlobalSequenceIdentity(String s1, String s2)
    {
        double matches    = 0.0;
        double mismatches = 0.0;
        double gaps       = 0;

        for(int i=0;i<s1.length();i++)
        {
            String symb1 = s1.substring(i,i+1);
            String symb2 = s2.substring(i,i+1);
            
            if(symb1.equals("-") || symb2.equals("-"))
            {
                gaps++;
                continue;
            }
            if(symb1.equals(symb2))
                matches++;
            else
                mismatches++;
        }
        return matches/(matches+mismatches+gaps);
    }
    
    public static boolean checkFastaFile(String filename)
    {
	 BufferedInputStream is = null;
	 try
	 {
	     is = new BufferedInputStream(new FileInputStream(filename));
	 } 
	 catch (FileNotFoundException e1)
	 {
	     e1.printStackTrace();
	 }
	 Alphabet alpha = AlphabetManager.alphabetForName("PROTEIN");



	 SequenceDB db = null;
	 try
	 {
	     db = SeqIOTools.readFasta(is, alpha);
	 } 
	 catch (BioException e)
	 {
	     //e.printStackTrace();
	     return false;
	 }
	 return true;
    }
    
    public static boolean checkFastaString(String s)
    {
	BufferedInputStream is = null;
        is = new BufferedInputStream(IOUtils.toInputStream(s));
        Alphabet alpha = AlphabetManager.alphabetForName("PROTEIN");
	SequenceDB db = null;
        try
        {
            db = SeqIOTools.readFasta(is, alpha);
        } 
        catch (BioException e)
        {
            return false;
        }
        return true;
    }
    
    public static Sequence makeProteinSequence(String id, String seq)
    {
	FiniteAlphabet fa = (FiniteAlphabet) AlphabetManager.alphabetForName("PROTEIN");
        SymbolTokenization p = null;
	try
	{
		p = fa.getTokenization("token");
	} 
	catch (BioException e)
	{
	    e.printStackTrace();
	}
        SymbolList sl=null;
	try
	{
	    sl = new SimpleSymbolList(p, seq);
	} 
	catch (IllegalSymbolException e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
        // make Sequence from SymbolList
        return new SimpleSequenceFactory().createSequence(sl,"", id, new SimpleAnnotation());
    }
    
    public static void main(String args[])
    {
	System.out.println(checkFastaString(">1\nLKWPETER\n>2\n#PETERLE"));
    }
}
