package ca.mikegabelmann.util;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Searches a path for Java classes and jar files.
 * @author mgabelmann
 */
public final class ClassVisitor extends SimpleFileVisitor<Path> {
	/** Logger. */
	private static final Logger LOG = LogManager.getLogger(ClassVisitor.class);
	
	/** Java class extension. */
	public static final String EXTENSION_CLASS	= ".class";
	
	/** Java archive extension. */
	public static final String EXTENSION_JAR	= ".jar";
	
	/** Service that manages threads. */
	private ExecutorService service;
	
	
	/**
	 * Constructor.
	 * @param service service
	 */
	public ClassVisitor(final ExecutorService service) {
		this.service = service;
	}

	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
			//print directory
			if (LOG.isDebugEnabled()) {
				LOG.debug(dir.toString());
			}
			
			return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
		//only process class files
		String f = file.toString();
		
		if (f.endsWith(EXTENSION_JAR)) {
			//must be a jar
			service.execute(new JarProcessor(file));
			
		} else if (f.endsWith(EXTENSION_CLASS)) {
			//must be a java class file
			service.execute(new ClassProcessor(file));
			
			//process all classes
			return FileVisitResult.CONTINUE;
			
		} else {
			//must be some other type of file
			if (LOG.isDebugEnabled()) {
				LOG.debug("skipping " + f);
			}
		}
		
		return FileVisitResult.CONTINUE;
	}
	
	/**
	 * Get class name.
	 * @param p path to file
	 * @return class name
	 */
	private String getClassName(final Path p) {
		return p.toString().replaceAll(".*?/?([\\w\\$_]+)\\" + EXTENSION_CLASS, "$1");
	}

}
