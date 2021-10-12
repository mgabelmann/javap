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
	private static final Logger LOG = LogManager.getLogger(ClassProcessor.class);
	
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
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("processing " + className);
		}
		
		try {
			DataInputStream is = new DataInputStream(new FileInputStream(classFile.toFile()));
			int magic = is.readInt();
			
			if (magic != 0xCAFEBABE) {
				//NOTE: jasper files don't appear to have this set, are they really java files?
				//all java classes have this set
				LOG.warn("invalid class file - " + className);

				is.close();
				return;
			}

			int minor = is.readUnsignedShort();
			int major = is.readUnsignedShort();

			is.close();

			JavaVersion version = Javap.versions.get(JavaVersion.getKey(major, minor));
			
			//output version info
			if (version != null) {
				if (Double.compare(version.getVersion(), Javap.MAX_VERSION) > 0 || Double.compare(version.getVersion(), Javap.MIN_VERSION) < 0) {
					LOG.warn(className + "\t" + version);
					
				} else {
					LOG.info(className + "\t" + version.getVersion());
				}
				
				//add to list
				Javap.addRecord(version.getVersion());
			
			} else {
				//unknown version
				LOG.warn(className + "\tunknown version(" + major + "." + minor + ")");
			}
			
		} catch (IOException ie) {
			LOG.error(className + "\t" + ie.getMessage());
		}
	}

}
