/**
 * 
 */
package org.roettig.SequenceTools.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author roettig
 *
 */
public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for org.roettig.SequenceTools.test");
		//$JUnit-BEGIN$
		suite.addTestSuite(HMMTest.class);
		suite.addTestSuite(MSATest.class);
		suite.addTestSuite(PairwiseAlignmentTest.class);
		suite.addTestSuite(ASCMSATest.class);
		//$JUnit-END$
		return suite;
	}

}
