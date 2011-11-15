package org.roettig.SequenceTools.base.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.dom4j.Node;
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
	
	public static Map<String,Object> readFromDOM(Node root)
	{
		Map<String,Object> map = new HashMap<String,Object>();
		List<Node> props = root.selectNodes("properties/property");
        for(Node prop: props)
        {
        	String name = prop.valueOf("name/text()");
        	String data = prop.valueOf("value/text()");
        	String type = prop.valueOf("value/@type");
        	Object val  = null;
        	if(type.equals("double"))
        		val = Double.parseDouble(data);
        	else if(type.equals("int"))
        		val = Integer.parseInt(data);
        	else if(type.equals("boolean"))
        		val = Boolean.parseBoolean(data);
        	else val = data;
        	
        	map.put(name, val);
        }
		return map;
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
		else if(value instanceof Boolean)
		{
			type = "bool";
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
