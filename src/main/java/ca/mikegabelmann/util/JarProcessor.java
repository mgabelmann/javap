package ca.mikegabelmann.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Process a single Java archive file and determine which JDK it was compiled with.
 * @author mgabelmann
 */
public final class JarProcessor implements Runnable {
	/** Logger. */
	private static final Logger log = LogManager.getLogger(JarProcessor.class);
	
	/** Java archive file. */
	private Path jarFile;
	
	/**
	 * Constructor.
	 * @param file
	 */
	public JarProcessor(final Path file) {
		this.jarFile = file;
	}
	
	/** Process. */
	public void run() {		
		try {
			ZipFile zf = new ZipFile(jarFile.toFile());
			Enumeration<? extends ZipEntry> e = zf.entries();
			boolean processed = false;
			
			while (e.hasMoreElements() && ! processed) {
				ZipEntry ze = e.nextElement();
				
				if (ze.getName().endsWith(ClassVisitor.EXTENSION_CLASS)) {
					if (log.isTraceEnabled()) {
						log.trace("processing zip entry: " + ze.getName());
					}
					
					DataInputStream is = new DataInputStream(zf.getInputStream(ze));
					int magic = is.readInt();
					
					if (magic != 0xcafebabe) {
						log.warn("invalid class file - " + ze.getName());
						is.close();
						break;
					}
					
					String minor = "" + is.readUnsignedShort();
					String major = "" + is.readUnsignedShort();
					
					is.close();
					
					JavaVersion version = Javap.versions.get(major + minor);
					
					//output version info
					if (version != null) {
						if (Double.compare(version.getVersion(), Javap.MAX_VERSION) > 0 || Double.compare(version.getVersion(), Javap.MIN_VERSION) < 0) {
							log.warn(jarFile.toString() + "\t" + version.toString());
						
						} else {
							log.info(jarFile.toString() + "\t" + version.getVersion());
						}
						
						//add to list
						Javap.addRecord(version.getVersion());
						
						//set flag so we only process 1 class file / jar
						processed = true;
					}
				}
			}
			
			zf.close();
			
		} catch (IOException ie) {
			log.warn(ie);
		}
	}

}
