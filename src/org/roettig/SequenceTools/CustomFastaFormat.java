/**
 * 
 */
package org.roettig.SequenceTools;

import java.io.IOException;

import org.biojava.bio.seq.io.ParseException;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.io.FastaFormat;
import org.biojavax.bio.seq.io.RichSeqIOListener;

/**
 * @author roettig
 *
 */
public class CustomFastaFormat extends FastaFormat
{

	@Override
	public void processHeader(String line, RichSeqIOListener rsiol, Namespace ns) throws IOException, ParseException
	{
		line = line.trim();
		rsiol.setAccession("");
		rsiol.setNamespace((ns==null?RichObjectFactory.getDefaultNamespace():ns));
		rsiol.setName(line.substring(1));
	}

}
