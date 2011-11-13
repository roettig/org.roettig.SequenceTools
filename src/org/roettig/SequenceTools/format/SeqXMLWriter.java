package org.roettig.SequenceTools.format;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.roettig.SequenceTools.base.Annotated;
import org.roettig.SequenceTools.base.Sequence;
import org.roettig.SequenceTools.base.SequenceContainer;
import org.roettig.SequenceTools.base.impl.AnnotatedXMLConverter;
import org.roettig.SequenceTools.base.impl.DefaultSequence;

public class SeqXMLWriter implements SequenceWriter
{
	protected Document document;
	protected Element  root;
	protected File     outputfile;
	
	public SeqXMLWriter(String filename)
	{
		outputfile = new File(filename);
	}
	
	@Override
	public void write(SequenceContainer seqs)
	{
		document = DocumentHelper.createDocument();
		root     = document.addElement( "seqxml" );
		Element seqsnode  = root.addElement("sequences");
		
		if(seqs instanceof Annotated)
		{
			AnnotatedXMLConverter.insertIntoDOM(root, (Annotated) seqs);
		}
		
		for(Sequence seq: seqs)
		{
			Element seqnode = seqsnode.addElement("sequence");
			seqnode.addElement("id").addText(seq.getID());
			seqnode.addElement("seq").addText(seq.getSequenceString());
			if(seq instanceof Annotated)
			{
				AnnotatedXMLConverter.insertIntoDOM(seqnode, (Annotated) seq);
			}
		}
		
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter    writer = null;
		
		try
		{
			writer = new XMLWriter( new FileWriter( outputfile ), format );
			writer.write( document );
			writer.close();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) throws FileNotFoundException
	{
		
		SequenceContainer seqs   = new FastaReader("/tmp/in.fa").read();
		((Annotated) seqs).addProperty("owner", "roettig");
		SeqXMLWriter    writer = new SeqXMLWriter("/tmp/seq.xml");
		int idx = 0;
		
		for(Sequence seq: seqs)
		{
			((DefaultSequence) seq).addProperty("test", String.format("%d",idx++));
		}
		
		writer.write(seqs);
	}

}
