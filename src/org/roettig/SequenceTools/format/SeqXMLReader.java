package org.roettig.SequenceTools.format;

import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.roettig.SequenceTools.base.SequenceContainer;

public class SeqXMLReader implements SequenceReader
{
	private Document document;
	
	public SeqXMLReader(String filename)
	{
		SAXReader reader = new SAXReader();
        try
		{
			document = reader.read(filename);
		} 
        catch (DocumentException e)
		{
        	throw new RuntimeException(e);
		}
	}

	@Override
	public SequenceContainer read()
	{
		Element root = document.getRootElement();
		Element seqs = (Element) document.selectSingleNode("//sequences");
		
        for ( Iterator i = seqs.elementIterator("sequence"); i.hasNext(); ) 
        {
            Element seq = (Element) i.next();
            String id  = seq.valueOf("id/text()");
            String sseq = seq.valueOf("seq/text()");
            System.out.println(id+" "+sseq);
        }
        return null;
	}

	public static void main(String[] args)
	{
		SeqXMLReader r = new SeqXMLReader("/tmp/seq.xml");
		r.read();

	}

}
