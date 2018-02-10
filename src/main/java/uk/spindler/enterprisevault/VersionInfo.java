package uk.spindler.enterprisevault;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;



public  class VersionInfo {
	
	public class InvalidVersionException extends Exception {
		  /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public InvalidVersionException() { super(); }
		  public InvalidVersionException(String message) { super(message); }
		  public InvalidVersionException(String message, Throwable cause) { super(message, cause); }
		  public InvalidVersionException(Throwable cause) { super(cause); }
		}
	
	
	private String major;
	private String minor;
	private String build;
	private String servicePack;
    
	public VersionInfo(String major, String minor, String build, String servicePack)
	{
		this.setMajor(major);
		this.setMinor(minor);
		this.setBuild(build);
		this.setServicePack(servicePack);
	}
	
	public boolean isInitialised()
	{
		if(getMajor() == null && getMinor() == null && getBuild() == null && getServicePack()==null)
			return false;
		else
			return true;
	}
	
	public VersionInfo(String versionStringToParse)
	{
		ParseVersionNumber(versionStringToParse, this);
	}

	public VersionInfo() {
	}

	public String getMajor() {
		return major;
	}

	public void setMajor(String major) {
		this.major = major;
	}

	public String getMinor() {
		return minor;
	}

	public void setMinor(String minor) {
		this.minor = minor;
	}

	public String getBuild() {
		return build;
	}

	public void setBuild(String build) {
		this.build = build;
	}
	
	public void setServicePack(String servicePack) {
		this.servicePack = servicePack;
	}
	
	public String getServicePack() {
		return servicePack;
	}
	
	public static void ParseVersionNumber(String versionNumberStr, VersionInfo vi)
	{
		final Pattern pattern = Pattern.compile("^(\\d+)(\\.(\\d+))?(\\.(\\d+))?(\\s+SP(\\d))?$");
		Matcher matcher = pattern.matcher(versionNumberStr.trim().toUpperCase());
		String major  = "";
		String minor = "";
		String build = "";
		String servicePack = "";
		
		if(matcher.find())
		{
			if(matcher.groupCount() > 1)
			{
				major = matcher.group(1);
			}
			if(matcher.groupCount() > 2)
			{
				minor =matcher.group(3);
			}
			if(matcher.groupCount() > 4)
			{
				build = matcher.group(5);
			}
			if(matcher.groupCount() > 6)
			{
				servicePack = matcher.group(7);
			}
			
			System.out.printf("major: %s, minor:%s, build: %s, SP: %s\n", major, minor, build, servicePack);
		}
		
		// Set the return values of the Versioninfo object
		vi.setMajor(major);
		vi.setMinor(minor);
		vi.setBuild(build);
		vi.setServicePack(servicePack);
	}
	
	public  String getMapKey()
	{
		String mapKey = getMajor();
		if(StringUtils.isNotEmpty(getMinor()))
			mapKey+=getMinor();
		else
			mapKey+="0";
		if(StringUtils.isNotEmpty(getBuild()))
			mapKey+=getBuild();
		else
			mapKey+="0";
		if(StringUtils.isNotEmpty(getServicePack()))
			mapKey+=getServicePack();
		else
			mapKey+="0";	
		
		return mapKey;
	}
	
	public String getVersionSpeechText() throws InvalidVersionException
	{
		String text = "Enterprise Vault Version " + getMajor();
		
		if(!isInitialised())
			throw new InvalidVersionException();
		
		if(StringUtils.isNotEmpty(getMinor()))
			text+= " point " + getMinor();
		
		if(StringUtils.isNotEmpty(getBuild()))
			text+= " point " + getBuild();
		
		if(StringUtils.isNotEmpty(getServicePack()))
			text+=" service Pack " + getServicePack();
		
		text += " was released on %s";
		
		return text;
	}
}