package org.roettig.SequenceTools.base;

import java.util.List;

public interface SequenceContainer extends Iterable<Sequence>
{
	void add(Sequence seq);
	Sequence getByID(String id);
	Sequence getByIndex(int idx);
	void remove(Sequence seq);
	List<String> getIDs();
	int size();
	void clear();
}
