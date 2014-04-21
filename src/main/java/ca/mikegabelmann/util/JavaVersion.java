package ca.mikegabelmann.util;

/**
 * 
 * @author mgabelmann
 */
public enum JavaVersion {
	JAVA_10(45, 3, 1.0),
	JAVA_11(45, 3, 1.1),
	JAVA_12(46, 0, 1.2),
	JAVA_13(47, 0, 1.3),
	JAVA_14(48, 0, 1.4),
	JAVA_15(49, 0, 1.5),
	JAVA_16(50, 0, 1.6),
	JAVA_17(51, 0, 1.7),
	//JAVA_18(52, 0, 1.8),
	;

	/**
	 * 
	 * @param major
	 * @param minor
	 * @param version
	 */
	private JavaVersion(final int major, final int minor, final double version) {
		this.major = major;
		this.minor = minor;
		this.version = version;
	}
	
	@Override
	public String toString() {
		return "" + major + "." + minor + "\t" + version;
	}
	
	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public double getVersion() {
		return version;
	}

	private int major;
	private int minor;
	private double version;
}
