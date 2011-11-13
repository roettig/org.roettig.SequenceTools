package org.roettig.SequenceTools.format;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.roettig.SequenceTools.base.Sequence;
import org.roettig.SequenceTools.base.SequenceContainer;
import org.roettig.SequenceTools.base.impl.DefaultSequence;
import org.roettig.SequenceTools.base.impl.DefaultSequenceContainer;

public class FastaReader implements SequenceReader
{
	private static final int ID        = 0;
	private static final int SEQ       = 1;
	private static final int WHITELINE = 2;
	
	@Override
	public SequenceContainer read(String filename)
	{
		SequenceContainer ret = null;
		try
		{
			ret = read(new FileInputStream(filename));
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return ret;
	}
	
	public SequenceContainer read(InputStream in)
	{
		SequenceContainer seqs = new DefaultSequenceContainer();
		BufferedReader    br   = new BufferedReader(new InputStreamReader(in));
		
		try
		{
			int state = -1;
			String line = "";
			String IDS="";
			StringBuffer SEQS = null;
			while( (line=br.readLine())!=null)
			{
				if(line.startsWith(">"))
				{
					if(state==ID)
						throw new RuntimeException("corrupt FASTA file supplied");
					state = ID;
				}
				else
					if(!line.startsWith(" "))
					{
						state = SEQ;
					}
					else
					{
						state = WHITELINE;
					}
				
				switch(state)
				{
					case(ID):
						if(SEQS!=null)
						{
							IDS = IDS.trim();
							String seq = SEQS.toString();
							if(!checkSeq(seq))
								throw new RuntimeException("invalid sequence found with id "+IDS);
							seqs.add(DefaultSequence.create(IDS,seq));
						}
						IDS  = line.substring(1);
						SEQS = new StringBuffer();
						break;
					case(SEQ):
						SEQS.append(line.trim());
						break;
				}
			}
			String seq = SEQS.toString();
			if(!checkSeq(seq))
				throw new RuntimeException("invalid sequence found with id "+IDS);
			seqs.add(DefaultSequence.create(IDS,seq));
		} 
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		return seqs;
	}

	private boolean checkSeq(String seq)
	{
		return seq.matches("^[ACDEFGHIKLMNPQRSTVWXYacdefghiklmnpqrstvwxy-]+$");
	}

	public static void main(String[] args) throws FileNotFoundException
	{
		SequenceReader    r    = new FastaReader();
		SequenceContainer seqs = null;
		try
		{
			 seqs = r.read("/tmp/in.fa");	
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		for(Sequence seq: seqs)
		{
			System.out.println(seq.toString());
		}
		System.out.println("L KWPE-tER".matches("^[ACDEFGHIKLMNPQRSTVWXYacdefghiklmnpqrstvwxy-]+$"));
	}

}
