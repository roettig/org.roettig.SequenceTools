package org.roettig.SequenceTools.binres.muscle;

import org.roettig.SequenceTools.binres.Deployer;

public class MuscleDeployer
{
	private static String[] NAMES = {"muscle_lnx","muscle_mac","muscle_win"};
	
	public static String deployMUSCLE()
	{
		String OS = NAMES[Deployer.getOS()];
		String path = Deployer.deploy(MuscleDeployer.class.getResourceAsStream(OS), MuscleDeployer.class.getCanonicalName());
		return path;
	}
}
