package ca.mikegabelmann.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author mgabelmann
 */
public final class Javap {
	/** Logger. */
	private static final Logger LOG = LogManager.getLogger(Javap.class);
	
	/** Collection of java versions keyed by majorminor version. */
	public static final TreeMap<String, JavaVersion> versions = new TreeMap<>();
	
	/** Collection of versions found. */
	private static final TreeSet<Double> records = new TreeSet<>();
	
	/** Minimum JDK. */
	public static final double MIN_VERSION = JavaVersion.JAVA_1_4.getVersion();
	
	/** Maximum JDK. */
	public static final double MAX_VERSION = JavaVersion.JAVA_11.getVersion();
	
	static {
		for (JavaVersion v : JavaVersion.values()) {
			versions.put(JavaVersion.getKey(v), v);
		}
	}
	
	/**
	 * Add record, synchronized because we have threads accessing this.
	 * @param version version
	 */
	public static synchronized void addRecord(final double version) {
		if (! records.contains(version)) {
			records.add(version);
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("added version: " + version);
			}
		}
	}
	
	/**
	 * Process files by calling this method with the path to file/directory.
	 * @param args command line arguments
	 */
	public static void main(final String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println("Javap <path to classes>");
			return;
		}

		if (LOG.isInfoEnabled()) {
			LOG.info("start");
		}

		//create fixed threadpool
		ExecutorService service = Executors.newFixedThreadPool(8);
		
		//do work
		ClassVisitor cv = new ClassVisitor(service);
		Files.walkFileTree(Paths.get(args[0]), cv);
		
		//stop accepting more work and wait till finished processing
		service.shutdown();
		
		try {
			//block and wait for work to complete or timeout occurs
			boolean terminated = service.awaitTermination(15, TimeUnit.MINUTES);

			if (!terminated) {
				LOG.warn("timeout reached");
			}

		} catch(InterruptedException ie) {
			LOG.warn("timed out - " + ie);
		}
		
		//LOG results
		if (LOG.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder("versions: ");

			for (Double d : Javap.records) {
				sb.append(d).append(" ");
			}
			
			LOG.info(sb.toString());
			LOG.info("finished");
		}
	}

}
