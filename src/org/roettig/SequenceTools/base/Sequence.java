package org.roettig.SequenceTools.base;

public interface Sequence
{
	public String getSequenceString();
	public String getID();
	public Sequence copy();
	public int length();
}
