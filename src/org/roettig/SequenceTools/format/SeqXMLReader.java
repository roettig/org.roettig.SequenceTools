package org.roettig.SequenceTools.format;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.roettig.SequenceTools.base.SequenceContainer;
import org.roettig.SequenceTools.base.impl.DefaultSequence;
import org.roettig.SequenceTools.base.impl.DefaultSequenceContainer;

public class SeqXMLReader implements SequenceReader
{
	private Document document;
	
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
	
	@Override
	public SequenceContainer read(InputStream in)
	{
		SequenceContainer ret = new DefaultSequenceContainer();
		SAXReader reader = new SAXReader();
        try
		{
			document = reader.read(in);
		} 
        catch (DocumentException e)
		{
        	throw new RuntimeException(e);
		}
        
		Element root = document.getRootElement();
		Element seqs = (Element) document.selectSingleNode("//sequences");
		
        for ( Iterator i = seqs.elementIterator("sequence"); i.hasNext(); ) 
        {
            Element seq = (Element) i.next();
            String id  = seq.valueOf("id/text()");
            String sseq = seq.valueOf("seq/text()");
            ret.add( DefaultSequence.create( id, sseq) );
        }
        
        return ret;
	}

	public static void main(String[] args)
	{
		SeqXMLReader r = new SeqXMLReader();
		r.read("/tmp/seq.xml");

	}

}
