package org.roettig.SequenceTools;

public class HSSPScore implements SequenceIdentity
{
	private static SequenceIdentity instance = null;
	
	public static SequenceIdentity getInstance() 
	{
		if (instance == null) 
		{
			instance = new HSSPScore();
		}
		return instance;
	}
	
	
	@Override
	public double calculate(String s1, String s2)
	{
		// L is the number of aligned residues ...
		int L = 0;
		for(int i=0;i<s1.length();i++)
		{
			// ... excluding gaps
			if(s1.charAt(i)=='-'||s2.charAt(i)=='-')
				continue;
			
			if(s1.charAt(i)==s2.charAt(i))
				L++;
		}
		
		double pid = AlignedSequenceIdentity.getInstance().calculate(s1, s2);
		
		
		
		double f1 = 0.0;
		double f2 = 0.0;
		if(L<11)
		{
			f2 = 100.0;
		}
		else
		{
			if(L>=11 && L<=450)
			{
				f2 = 480.0 * Math.pow(L, -0.32*(1+Math.exp(-L/1000.0)));
			}
			else
				f2 = 19.5;
		}
		
		
		// distance from HSSP_curve(0)
		return 100*pid - (f1+f2);
	}

}
