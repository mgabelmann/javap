package ca.mikegabelmann.util;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Process a single Java class file and determine which JDK it was compiled with.
 * @author mgabelmann
 */
public final class ClassProcessor implements Runnable {
	/** Logger. */
	private static final Logger log = LogManager.getLogger(ClassProcessor.class);
	
	/** Class file to process. */
	private Path classFile;
	
	/**
	 * Constructor.
	 * @param file class file
	 */
	public ClassProcessor(final Path file) {
		this.classFile = file;
	}
	
	/** Process. */
	public void run() {
		String className = classFile.toString();
		
		if (log.isTraceEnabled()) {
			log.trace("processing " + className);
		}
		
		try {
			DataInputStream is = new DataInputStream(new FileInputStream(classFile.toFile()));
			int magic = is.readInt();
			
			if (magic != 0xcafebabe) {
				//NOTE: jasper files don't appear to have this set, are they really java files?
				//all java classes have this set
				log.warn("invalid class file - " + className);
				is.close();
				return;
			}
			
			String minor = "" + is.readUnsignedShort();
			String major = "" + is.readUnsignedShort();
			
			is.close();
			
			JavaVersion version = Javap.versions.get(major + minor);
			
			//output version info
			if (version != null) {
				if (Double.compare(version.getVersion(), Javap.MAX_VERSION) > 0 || Double.compare(version.getVersion(), Javap.MIN_VERSION) < 0) {
					log.warn(className + "\t" + version.toString());
					
				} else {
					log.info(className + "\t" + version.getVersion());
				}
				
				//add to list
				Javap.addRecord(version.getVersion());
			
			} else {
				//unknown version
				log.warn(className + "\tunknown version(" + major + "." + minor + ")");
			}
			
		} catch (IOException ie) {
			log.error(className + "\t" + ie.getMessage());	
		}
	}
}
