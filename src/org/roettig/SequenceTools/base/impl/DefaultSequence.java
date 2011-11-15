package org.roettig.SequenceTools.base.impl;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.roettig.SequenceTools.base.Annotated;
import org.roettig.SequenceTools.base.Sequence;

public class DefaultSequence implements Sequence, Annotated
{
	protected String id;
	protected String seq;
	protected Annotated props = new DefaultAnnotated();
	
	public DefaultSequence(String id, String seq)
	{
		this.id  = id;
		this.seq = seq;
	}
	
	@Override
	public String getSequenceString()
	{
		return seq;
	}

	@Override
	public String getID()
	{
		return id;
	}

	@Override
	public int length()
	{
		return seq.length();
	}
	
	@Override
	public Sequence copy()
	{
		DefaultSequence ret = new DefaultSequence(id, seq);
		for(String key: props.getPropertyNames())
		{
			ret.addProperty(key, props.getProperty(key));
		}
		return ret;
	}

	public String toString()
	{
		return String.format("[id: %s seq:%s]",id,seq);
	}
	
	@Override
	public void addProperty(String name, Object obj)
	{
		props.addProperty(name, obj);
	}

	@Override
	public void removeProperty(String name)
	{
		props.removeProperty(name);
	}

	@Override
	public boolean hasProperty(String name)
	{
		return props.hasProperty(name);
	}

	@Override
	public Object getProperty(String name)
	{
		return props.getProperty(name);
	}

	@Override
	public List<String> getPropertyNames()
	{
		return props.getPropertyNames();
	}

	public static Sequence create(String sid, String seq)
	{
		return new DefaultSequence(sid, seq);
	}

	@Override
	public Map<String, Object> getMap()
	{
		return props.getMap();
	}
}
