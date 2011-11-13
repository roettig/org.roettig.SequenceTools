package org.roettig.SequenceTools.format;

import java.io.FileWriter;
import java.io.IOException;

import org.roettig.SequenceTools.base.Sequence;
import org.roettig.SequenceTools.base.SequenceContainer;

public class FastaWriter implements SequenceWriter
{
	public static void write(SequenceContainer seqs, String filename)
	{
		new FastaWriter(filename).write(seqs);
	}
	
	protected String filename;
	
	public FastaWriter(String filename)
	{
		this.filename = filename;
	}
	
	private static final String NEWLINE = System.getProperty("line.separator");
	
	@Override
	public void write(SequenceContainer seqs)
	{
		FileWriter writer;
		try
		{
			writer = new FileWriter(filename);
			for(Sequence seq: seqs)
			{
				writer.write(">"+seq.getID()+NEWLINE);
				writer.write(seq.getSequenceString()+NEWLINE);
			}
			writer.close();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}

}
