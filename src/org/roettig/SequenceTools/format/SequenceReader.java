package org.roettig.SequenceTools.format;

import java.io.InputStream;

import org.roettig.SequenceTools.base.SequenceContainer;

public interface SequenceReader
{
	SequenceContainer read(String filename);
	SequenceContainer read(InputStream in);
}
