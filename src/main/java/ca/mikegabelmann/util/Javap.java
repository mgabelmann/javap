package ca.mikegabelmann.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 
 * @author mgabelmann
 */
public final class Javap {
	/** Collection of java versions keyed by majorminor version. */
	public static final TreeMap<String, JavaVersion> versions = new TreeMap<String, JavaVersion>();
	
	/** Collection of versions found. */
	public static final TreeSet<Double> records = new TreeSet<Double>();
	
	static {
		for (JavaVersion v : JavaVersion.values()) {
			versions.put("" + v.getMajor() + v.getMinor(), v);
		}
	}
	
	/**
	 * Process files by calling this method with the path to file/directory.
	 * @param args
	 */
	public static void main(final String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Javap <path to classes>");
			return;
		}
		
		System.out.println("start");
		
		//do work
		Files.walkFileTree(Paths.get(args[0]), new ClassVisitor());
		
		System.out.print("versions: ");
		
		for (Double d : Javap.records) {
			System.out.print(d + " ");
		}
		
		System.out.print("\n");
		
		System.out.println("finished");
	}
}
