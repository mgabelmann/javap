package ca.mikegabelmann.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Runs javap on every class file it finds and reports on the version.
 * @author mgabelmann
 */
public final class ClassVisitor extends SimpleFileVisitor<Path> {
	private static final String EXTENSION = ".class";
	
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		//print directory
		System.out.println(dir.toString());
		
		return super.preVisitDirectory(dir, attrs);
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		//only process class files
		if (! file.toString().endsWith(EXTENSION)) {
			return FileVisitResult.CONTINUE;
		}
		
		String className = file.toString();
		
		//call JavaP to process class file
		Process p = Runtime.getRuntime().exec("javap -v -c " + className);
		
		BufferedReader in = null;
		
		String minor = null;
		String major = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			String line = null;
			while ((line = in.readLine()) != null) {
				if (line.contains("minor")) {
					Matcher m1 = Pattern.compile(".*?minor\\sversion:\\s(\\d+).*").matcher(line);
					if (m1.find()) {
						minor = m1.group(1);
					}
					
				} else if (line.contains("major")) {
					Matcher m2 = Pattern.compile(".*?major\\sversion:\\s(\\d+).*").matcher(line);
					if (m2.find()) {
						major = m2.group(1);
					}
				}
				
				//exit once we find both, saves processing time
				if (minor != null && major != null) break;
			}
				
			if (minor == null) minor = "0";
			JavaVersion version = Javap.versions.get(major + minor);
			
			//output version info
			if (version != null) {
				//if (Double.compare(version.getVersion(), 1.4) > 0) {
					System.out.println(className + "\t" + version.toString());  
				//}
					
				if (! Javap.records.contains(version.getVersion())) {
					Javap.records.add(version.getVersion());
				}
				
			} else {
				System.out.println(className + "\tERROR - no version information");
			}
			
		} catch (IOException ie) {
			System.err.println(className + "\tERROR - " + ie.getMessage());
			
		} finally {
			//ensure stream is closed
			if (in != null) {
				in.close();
			}
		}
		
		return FileVisitResult.CONTINUE;
	}
	
	/**
	 * 
	 * @param p
	 * @return
	 */
	private String getClassName(final Path p) {
		return p.toString().replaceAll(".*?/?([\\w\\$\\_]+)\\.class", "$1");
	}
}
