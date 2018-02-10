package uk.spindler.enterprisevault;

import static org.junit.Assert.*;


import org.junit.Test;


public class VersionInfoTest extends VersionInfo{

	@Test
	public void GivenUninitialisedObjectWhentestedForInitialisationThenFail() {
		VersionInfo info= new VersionInfo();
	
		assertTrue(info.isInitialised() == false);
	}
	
	@Test(expected = InvalidVersionException.class)
	public void GivenUninitialisedObjectWhengetSpeechTextThenThrow() throws InvalidVersionException {
		VersionInfo info= new VersionInfo();
	
		info.getVersionSpeechText();
	}
	
	@Test
	public void GivenNewObjectWhenDefaultConstructorThenCheckDefaults() {
		VersionInfo info= new VersionInfo();
		assertTrue(info != null);	
		assertTrue(info.getMajor() == null);
		assertTrue(info.getMinor() == null);	
		assertTrue(info.getBuild() == null);	
		assertTrue(info.getServicePack() == null);
	}

	@Test
	public void GivenNewObjectWhenNonDefaultConstructorThenCheckValues() {
		VersionInfo info= new VersionInfo("11","7","3", "2");
		assertTrue(info != null);	
		assertTrue(info.getMajor().equals("11"));
		assertTrue(info.getMinor().equals("7"));	
		assertTrue(info.getBuild().equals("3"));	
		assertTrue(info.getServicePack().equals("2"));	
	}
	
	@Test
	public void GivenStringWithOnlyOneDigitVersionWhenParsedThenCheckResult() {
	    String test = "7";
	    VersionInfo vi = new VersionInfo();
		VersionInfo.ParseVersionNumber(test, vi);
	

		assertTrue(vi.getMajor().equals("7"));
		assertTrue(vi.getMinor() == null);	
		assertTrue(vi.getBuild() == null);	
		assertTrue(vi.getServicePack() == null);
	}
	
	@Test
	public void GivenStringWithtTwoDigitVersionWhenParsedThenCheckResult() {
	    String test = "7.5";
	    VersionInfo vi = new VersionInfo();
		VersionInfo.ParseVersionNumber(test, vi);
	

		assertTrue(vi.getMajor().equals("7"));
		assertTrue(vi.getMinor().equals("5"));	
		assertTrue(vi.getBuild() == null);	
		assertTrue(vi.getServicePack() == null);
	}
	
	
	@Test
	public void GivenStringWithtThreeDigitVersionWhenParsedThenCheckResult() {
	    String test = "7.5.7";
	    VersionInfo vi = new VersionInfo();
		VersionInfo.ParseVersionNumber(test, vi);
	

		assertTrue(vi.getMajor().equals("7"));
		assertTrue(vi.getMinor().equals("5"));	
		assertTrue(vi.getBuild().equals("7"));	
		assertTrue(vi.getServicePack() == null);
	}
	
	@Test
	public void GivenStringWithTwoDigitVersionAndServicePackWhenParsedThenCheckResult() {
	    String test = "7.5 SP4";
	    VersionInfo vi = new VersionInfo();
		VersionInfo.ParseVersionNumber(test, vi);
	

		assertTrue(vi.getMajor().equals("7"));
		assertTrue(vi.getMinor().equals("5"));	
		assertTrue(vi.getBuild()==null);	
		assertTrue(vi.getServicePack().equals("4"));	
	}
	
	@Test
	public void GivenStringWithTwoDigitVersionAndLowerCaseServicePackWhenParsedThenCheckResult() {
	    String test = "7.5 Sp4";
	    VersionInfo vi = new VersionInfo();
		VersionInfo.ParseVersionNumber(test, vi);
	

		assertTrue(vi.getMajor().equals("7"));
		assertTrue(vi.getMinor().equals("5"));	
		assertTrue(vi.getBuild()==null);	
		assertTrue(vi.getServicePack().equals("4"));	
	}
	
	@Test
	public void GivenStringWithTwoDigitVersionAndLowerCaseServicePackWhenConstructedAndParsedThenCheckResult() {
	    String test = "7.5 Sp4";
	    VersionInfo vi = new VersionInfo(test);
		
	
		assertTrue(vi.getMajor().equals("7"));
		assertTrue(vi.getMinor().equals("5"));	
		assertTrue(vi.getBuild()==null);	
		assertTrue(vi.getServicePack().equals("4"));	
	}
	
	@Test
	public void GivenStringWithTwoDigitVersionAndLowerCaseServicePackWhengetMapKeyThenCheckResult() {
	    String test = "7.5 Sp4";
	    VersionInfo vi = new VersionInfo(test);
	
		assertTrue(vi.getMapKey().equals("7504"));
	}
	
	@Test
	public void GivenStringWithOneDigitVersionWhengetMapKeyThenCheckResult() {
	    String test = "4";
	    VersionInfo vi = new VersionInfo(test);
	
		assertTrue(vi.getMapKey().equals("4000"));
	}
	
	@Test
	public void GivenStringWiththreeDigitVersionWhengetMapKeyThenCheckResult() {
	    String test = "4.3.2";
	    VersionInfo vi = new VersionInfo(test);
	
		assertTrue(vi.getMapKey().equals("4320"));
	}
}
