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
	private InputStream in;
	
	public FastaReader(String filename)
	{
		try
		{
			in = new FileInputStream(filename);
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public FastaReader(InputStream in)
	{
		this.in = in;
	}
	
	private static final int ID        = 0;
	private static final int SEQ       = 1;
	private static final int WHITELINE = 2;
	
	@Override
	public SequenceContainer read()
	{
		SequenceContainer seqs = new DefaultSequenceContainer();
		BufferedReader br   = new BufferedReader(new InputStreamReader(in));
		try
		{
			int state = 0;
			String line = "";
			String IDS="";
			StringBuffer SEQS = null;
			while( (line=br.readLine())!=null)
			{
				if(line.startsWith(">"))
					state = ID;
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
							seqs.add(DefaultSequence.create(IDS.trim(),SEQS.toString()));
						}
						IDS  = line.substring(1);
						SEQS = new StringBuffer();
						break;
					case(SEQ):
						SEQS.append(line.trim());
						break;
				}
			}
			seqs.add( DefaultSequence.create(IDS.trim(),SEQS.toString()));
		} 
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		return seqs;
	}

	public static void main(String[] args) throws FileNotFoundException
	{
		SequenceReader    r    = new FastaReader("/tmp/in.fa");
		SequenceContainer seqs = r.read();
		for(Sequence seq: seqs)
		{
			System.out.println(seq.toString());
		}

	}

}
