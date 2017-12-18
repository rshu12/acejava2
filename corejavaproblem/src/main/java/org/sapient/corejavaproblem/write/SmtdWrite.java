package org.sapient.corejavaproblem.write;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.sapient.corejavaproblem.utility.Utility;

public class SmtdWrite {
	public SmtdWrite() {
		writeSmtdFile();
	}

	private void writeSmtdFile() {
		Properties props = Utility.getProperties();
		PathMatcher matcher = Utility.fileSystem.getPathMatcher("glob:*.{dmtd}");
		Path dir = Paths.get(props.getProperty("root.dir"));
		try {
			Files.walk(dir).filter(Files::isRegularFile).forEach(pathName -> {
				if (matcher.matches(pathName.getFileName())) {
					// System.out.println("Smtd file read path::"+pathName);

					Map<String, Long> unordered = new HashMap<>();
					try (Stream<String> lines = Files.lines(pathName)) {
						System.out.println("DMTD FILE PRINT");
						lines.forEach(line -> {
							Pattern p = Pattern.compile(props.get("sort.parameter").toString() + " = \\d+");
							Matcher m = p.matcher(line);
							Pattern pattern = Pattern.compile(" :: ");
							Matcher matcherPre = pattern.matcher(line);
							String preFileName = null;
							while (matcherPre.find()) {
								preFileName = line.substring(matcherPre.regionStart(), matcherPre.end(0));
							}
							if (m.find()) {
								String matchS = new StringBuilder(preFileName).append(m.group()).toString();
								System.out.println(matchS);
								createSmtdFileMapping(matchS, unordered);
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
					createSortedSequence(unordered, pathName);
				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Map<String, Long> createSmtdFileMapping(String sortParam, Map<String, Long> map) {
		String arr[] = sortParam.split("=");
		map.put(arr[0].trim(), Long.parseLong(arr[1].trim()));
		// createSortedSequence(unordered);
		return map;
	}

	private void createSortedSequence(Map<String, Long> unordered, Path path) {

		Map<Object, Object> mapLinked = unordered.entrySet().stream()
				.sorted((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
		System.out.println("Sorted Map" + mapLinked);
		Path smtdPath = Utility.checkOrCreateAggreagteFile(path, ".smtd");
		try {
			Files.write(smtdPath, mapLinked.toString().getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Properties props = Utility.getProperties();
		Pattern p = Pattern.compile(/* "3.mtd :: "+ */props.get("sort.parameter").toString() + " = \\d+");
		String str = "3.mtd :: WordCount = 0 | LetterCount = 0 | VowelCount = 0 | SpecialCharacterCount = 0";
		Matcher m = p.matcher(str);
		System.out.println(m.find());
		System.out.println(m.group());
		System.out.println(m.pattern());
	}

}
