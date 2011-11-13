package org.roettig.SequenceTools.binres.hmmer;

import org.roettig.SequenceTools.binres.Deployer;

public class HmmerDeployer
{
	private static String LNX_HMMALIGN = "hmmalign_lnx";
	private static String WIN_HMMALIGN = "hmmalign_win";
	private static String MAC_HMMALIGN = "hmmalign_mac";
	
	private static String LNX_HMMBUILD = "hmmbuild_lnx";
	private static String WIN_HMMBUILD = "hmmbuild_win";
	private static String MAC_HMMBUILD = "hmmbuild_mac";
	
	public static String deployHMMALIGN()
	{
		String OS = LNX_HMMALIGN;
		String path = Deployer.deploy(HmmerDeployer.class.getResourceAsStream(OS), HmmerDeployer.class.getCanonicalName()+"_"+OS);
		return path;
	}
	
	public static String deployHMMBUILD()
	{
		String OS = LNX_HMMBUILD;
		String path = Deployer.deploy(HmmerDeployer.class.getResourceAsStream(OS), HmmerDeployer.class.getCanonicalName()+"_"+OS);
		return path;
	}
}