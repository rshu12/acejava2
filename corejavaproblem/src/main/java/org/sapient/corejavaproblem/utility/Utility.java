package org.sapient.corejavaproblem.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.Properties;

public class Utility {

	public final static FileSystem fileSystem = FileSystems.getDefault();
	// public final static PathMatcher matcher =
	// fileSystem.getPathMatcher("glob:*.{txt,csv}");

	public static Path checkOrCreateAggreagteFile(Path path, String extn) {
		String directoryName = path.getParent().getFileName().toString();
		StringBuilder sb = new StringBuilder();
		String newDirecty = sb.append(path.getParent()).append(File.separator).append(directoryName)
				.append(/* ".smtd" */extn).toString();
		Path newDir = Paths.get(newDirecty);
		if (!Files.exists(newDir)) {
			synchronized (Utility.class) {
				try {
					return Files.createFile(newDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return newDir;
	}

	public static Properties getProperties() {
		Properties prop = new Properties();
		InputStream in = new Utility().getClass().getClassLoader().getResourceAsStream("config.properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}

}
