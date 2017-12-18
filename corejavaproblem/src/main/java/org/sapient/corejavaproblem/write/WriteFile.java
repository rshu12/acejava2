package org.sapient.corejavaproblem.write;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Properties;

import org.sapient.corejavaproblem.utility.Utility;

public class WriteFile {

	public WriteFile(Path path, Long wordCount, Long letterCount, Long vowelCount, Long spCharacterCount) {
		writeFiles(path, wordCount, letterCount, vowelCount, spCharacterCount);
	}

	public WriteFile() {
	}

	private void writeFiles(Path path, Long wordCount, Long letterCount, Long vowelCount, Long spCharacterCount) {
		Path folderFilePath = Utility.checkOrCreateAggreagteFile(path, ".dmtd");
		Properties props = Utility.getProperties();
		// Path smtdFilePath = Utility.checkOrCreateAggreagteFile(path,
		// ".smtd");
		String originalFilename = path.getFileName().toString();
		StringBuilder counts = new StringBuilder();
		String writeContent = counts.append(props.getProperty("word.count")).append(" = ").append(wordCount)
				.append(" | ").append(props.getProperty("letter.count")).append(" = ").append(letterCount).append(" | ")
				.append(props.getProperty("vowel.count")).append(" = ").append(vowelCount).append(" | ")
				.append(props.getProperty("special.character.count")).append(" = ").append(spCharacterCount)
				.append("\n").toString();
		String fileNameNew = originalFilename.substring(0, originalFilename.length() - ".txt".length()) + ".mtd";
		Path newFile = path.resolveSibling(fileNameNew);

		try {
			Path writePath = Files.write(newFile, writeContent.getBytes());
			Files.write(folderFilePath, new StringBuilder(newFile.getFileName().toString())
					.append(" :: ").append(writeContent).toString().getBytes(), StandardOpenOption.APPEND);

			System.out.println("******************File Written******************" + writePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Iterable<? extends CharSequence> getMap(Map<String, Long> map) {
		return () -> map.entrySet().stream().<CharSequence>map(e -> e.getKey() + "|" + e.getValue()).iterator();
	}

	public static void main(String[] args) {
		// new
		// WriteFile().checkOrCreateAggreagteFile(Paths.get("D:\\notes\\root\\sub1\\subsub2\\subsubsub3\\fa.txt"));
	}

}
