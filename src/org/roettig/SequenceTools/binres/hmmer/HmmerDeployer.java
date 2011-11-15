package org.roettig.SequenceTools.binres.hmmer;

import org.roettig.SequenceTools.binres.Deployer;

public class HmmerDeployer
{
	private static String[] ALIGNNAMES = {"hmmalign_lnx","hmmalign_mac","hmmalign_win"};
	private static String[] BUILDNAMES = {"hmmbuild_lnx","hmmbuild_mac","hmmbuild_win"};
	
	public static String deployHMMALIGN()
	{
		String OS = ALIGNNAMES[Deployer.getOS()];
		String path = Deployer.deploy(HmmerDeployer.class.getResourceAsStream(OS), HmmerDeployer.class.getCanonicalName()+"_"+OS);
		return path;
	}
	
	public static String deployHMMBUILD()
	{
		String OS = BUILDNAMES[Deployer.getOS()];
		String path = Deployer.deploy(HmmerDeployer.class.getResourceAsStream(OS), HmmerDeployer.class.getCanonicalName()+"_"+OS);
		return path;
	}
}