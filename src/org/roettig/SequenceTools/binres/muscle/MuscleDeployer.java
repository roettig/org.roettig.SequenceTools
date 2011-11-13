package org.roettig.SequenceTools.binres.muscle;

import org.roettig.SequenceTools.binres.Deployer;

public class MuscleDeployer
{
	private static String LNX = "muscle_lnx";
	private static String WIN = "muscle_win";
	private static String MAC = "muscle_mac";
	
	public static String deployMUSCLE()
	{
		String OS = LNX;
		String path = Deployer.deploy(MuscleDeployer.class.getResourceAsStream(OS), MuscleDeployer.class.getCanonicalName());
		return path;
	}
}
