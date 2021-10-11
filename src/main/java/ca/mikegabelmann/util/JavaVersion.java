package ca.mikegabelmann.util;

/**
 * List of Java version.
 * @author mgabelmann
 */
public enum JavaVersion {
	JAVA_1_0(45, 0, 1.0),
	JAVA_1_1(45, 3, 1.1),
	JAVA_1_2(46, 0, 1.2),
	JAVA_1_3(47, 0, 1.3),
	JAVA_1_4(48, 0, 1.4),
	JAVA_5_0(49, 0, 1.5),
	JAVA_6_0(50, 0, 1.6),
	JAVA_7_0(51, 0, 1.7),
	JAVA_8_0(52, 0, 1.8),
	JAVA_9(53, 0, 9),
	JAVA_10(54, 0, 10),
	JAVA_11(55, 0, 11),
	JAVA_12(56, 0, 12),
	JAVA_13(57, 0, 13),
	JAVA_14(58, 0, 14),
	JAVA_15(59, 0, 15),
	JAVA_16(60, 0, 16),
	JAVA_17(61, 0, 17),
	JAVA_18(62, 0, 18),
	;

	/** Java major version. */
	private int major;
	
	/** Java minor version. */
	private int minor;
	
	/** JDK version. */
	private double version;
	
	
	/**
	 * Constructor.
	 * @param major
	 * @param minor
	 * @param version
	 */
	private JavaVersion(final int major, final int minor, final double version) {
		this.major = major;
		this.minor = minor;
		this.version = version;
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
	
	@Override
	public String toString() {
		return "" + major + "." + minor + "\t" + version;
	}
}
