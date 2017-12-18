package org.sapient.corejavaproblem.process;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.sapient.corejavaproblem.write.WriteFile;

public class ProcessFileForOperationImpl {

	public ProcessFileForOperationImpl(BlockingQueue<Path> blockingQueue) {
		System.out.println("File Writing Started...");
		getFileRelatedDetail(blockingQueue);
	}

	public void getFileRelatedDetail(BlockingQueue<Path> blockingQueue) {
		while (!blockingQueue.isEmpty()) {
			Path path = null;
			try {
				path = blockingQueue.take();
				if (Files.exists(path)) {
					Long wordCount = findWords(path);
					Long letterCount = findLetters(path);
					Long vowelCount = findVowels(path);
					Long spCharacterCount = findSpecialCharacters(path);
					new WriteFile(path, wordCount, letterCount, vowelCount, spCharacterCount);
				} else {
					System.err.println("Wrong Path Name ::" + path);
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Long findWords(Path path) throws IOException {
		return getWords(path).count();
	}

	private Stream<String> getWords(Path path) throws IOException {
		return Files.lines(path, Charset.defaultCharset()).flatMap(line -> Arrays.stream(line.split("\\s+")));
	}

	public Long findLetters(Path path) throws IOException {
		return getWords(path).flatMap(word -> word.chars().mapToObj(i -> (char) i)).count();
	}

	public Long findVowels(Path path) throws IOException {
		return getWords(path).flatMap(word -> word.chars().mapToObj(i -> (char) i))
				.filter(x -> x.equals('a') || x.equals("e") || x.equals("i") || x.equals("o") || x.equals("u")
						|| x.equals('A') || x.equals("E") || x.equals("I") || x.equals("O") || x.equals("U"))
				.count();
	}

	public Long findSpecialCharacters(Path path) throws IOException {
		Pattern pattern = Pattern.compile("[^A-Za-z0-9]", Pattern.CASE_INSENSITIVE);
		Matcher m = pattern.matcher(getWords(path).flatMap(word -> word.chars().mapToObj(i -> (char) i)).toString());
		
		return (long) m.groupCount();
	}

}
