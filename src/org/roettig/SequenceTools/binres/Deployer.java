package org.roettig.SequenceTools.binres;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class Deployer
{
	public static int LNX = 0;
	public static int MAC = 1;
	public static int WIN = 2;
	
	public static int getOS()
	{
		String os   = System.getProperty("os.name");
		
		// default platform
		int OS = WIN;

		if(os.toLowerCase().contains("nux")||os.toLowerCase().contains("nix"))
		{
			OS = LNX;
		}
		if(os.toLowerCase().contains("mac"))
		{
			OS = MAC;
		}
		
		return OS;
	}
	
	public static String deploy(InputStream in, String ID)
	{
		File bindir = new File(System.getProperty("java.io.tmpdir")+File.separator+ID);
		bindir.mkdirs();
		String path = bindir.getAbsolutePath()+File.separator+"cmd";
		File   pathfile = new File(path);
		try
		{
			copyStream(in, pathfile);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		pathfile.setExecutable(true);
		return path;
	}

	public static void copyStream(InputStream in, File dest) throws IOException
	{
		FileOutputStream    out = new FileOutputStream(dest);
		BufferedInputStream bin = new BufferedInputStream(in);
		byte[] buffer = new byte[2048];
		int len;
 		while((len=bin.read(buffer, 0, 2048))!=-1)
 		{
 			out.write(buffer,0,len);
 		}
 		out.close();
 		bin.close();
	}
}