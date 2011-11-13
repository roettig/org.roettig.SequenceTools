package org.roettig.SequenceTools.base.impl;

import org.dom4j.Element;
import org.roettig.SequenceTools.base.Annotated;

public class AnnotatedXMLConverter
{
	public static void insertIntoDOM(Element root, Annotated anno)
	{
		Element   props = root.addElement("properties");
		
		for(String key: anno.getPropertyNames())
		{
			Element prop = props.addElement("property");
			prop.addElement("name").addText(key);
			addObjectValue(prop,anno.getProperty(key));
		}
	}
	
	public static void addObjectValue(Element prop, Object value)
	{
		String type = "";
		String val  = "";
		if(value instanceof Integer)
		{
			type = "int";
			val  = value.toString();
		}
		else if(value instanceof Double)
		{
			type = "double";
			val  = value.toString();
		}
		else if(value instanceof String)
		{
			type = "string";
			val  = value.toString();
		}
		else if(value instanceof Object)
		{
			type = "object";
			val  = value.toString();
		}
		prop.addElement("value").addAttribute("type", type).addText(val);
	}
}
