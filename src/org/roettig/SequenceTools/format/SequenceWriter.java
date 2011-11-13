package org.roettig.SequenceTools.format;

import org.roettig.SequenceTools.base.SequenceContainer;

public interface SequenceWriter
{
	void write(SequenceContainer seqs, String filename);
}
