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
	private static final Logger log = LogManager.getLogger(Javap.class);
	
	/** Collection of java versions keyed by majorminor version. */
	public static final TreeMap<String, JavaVersion> versions = new TreeMap<String, JavaVersion>();
	
	/** Collection of versions found. */
	private static final TreeSet<Double> records = new TreeSet<Double>();
	
	/** Minimum JDK. */
	public static final double MIN_VERSION = 1.2;
	
	/** Maximum JDK. */
	public static final double MAX_VERSION = 1.8;
	
	static {
		for (JavaVersion v : JavaVersion.values()) {
			versions.put("" + v.getMajor() + v.getMinor(), v);
		}
	}
	
	/**
	 * Add record, synchronized because we have threads accessing this.
	 * @param version
	 */
	public static synchronized void addRecord(double version) {
		if (! records.contains(version)) {
			records.add(version);
			
			if (log.isDebugEnabled()) {
				log.debug("added version: " + version);
			}
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
		
		//create fixed threadpool
		ExecutorService service = Executors.newFixedThreadPool(8);
		
		//do work
		ClassVisitor cv = new ClassVisitor(service);
		
		log.info("start");
		
		Files.walkFileTree(Paths.get(args[0]), cv);
		
		//stop accepting more work and wait till finished processing
		service.shutdown();
		
		try {
			//block and wait for work to complete or timeout occurs
			service.awaitTermination(15, TimeUnit.MINUTES);
		
		} catch(InterruptedException ie) {
			log.warn("timed out - " + ie);
		}
		
		//log results
		if (log.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder("versions: ");

			for (Double d : Javap.records) {
				sb.append(d + " ");
			}
			
			log.info(sb.toString());
		}
		
		log.info("finished");
	}
}
