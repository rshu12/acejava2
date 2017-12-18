package org.sapient.corejavaproblem.read;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.sapient.corejavaproblem.utility.Utility;

public class WatchDirectoryRecursively {
	private final static FileSystem fileSystem = Utility.fileSystem;
	private final WatchService watcher;
	private final Map<WatchKey, Path> keys;
	private final BlockingQueue<Path> blockingQueue;
	private final static PathMatcher matcher = fileSystem.getPathMatcher("glob:*.{txt,csv}");

	/**
	 * Creates a WatchService and registers the given directory
	 */
	public WatchDirectoryRecursively(Path dir, BlockingQueue<Path> blockingQueue) throws IOException {
		this.watcher = fileSystem.newWatchService();
		this.keys = new HashMap<WatchKey, Path>();
		this.blockingQueue = blockingQueue;
		readAndLoadFile(dir);
		walkAndRegisterDirectories(dir);
		processEvents();
	}

	private void readAndLoadFile(Path dir) throws IOException {
		Files.walk(dir).filter(Files::isRegularFile).forEach(pathName -> {
			if (matcher.matches(pathName.getFileName())) {
				System.out.println(pathName);
				try {
					blockingQueue.put(pathName);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Register the given directory with the WatchService; This function will be
	 * called by FileVisitor
	 */
	private void registerDirectory(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void walkAndRegisterDirectories(final Path start) throws IOException {
		// register directory and sub-directories
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				registerDirectory(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	public void processEvents() {
		for (;;) {

			/*
			 * try { Thread.sleep(5000); } catch (InterruptedException e) {
			 * e.printStackTrace(); }
			 */
			// wait for key to be signalled
			WatchKey key;
			try {
//				 key = watcher.take();
				key = watcher.poll(10, TimeUnit.SECONDS);
			} catch (InterruptedException x) {
				return;
			}

			Path dir = keys.get(key);
			if (dir == null) {
				System.err.println("WatchKey not recognized!!");
//				blockingQueue.notifyAll();
//				Thread.currentThread().interrupt();
//				continue;
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}

			for (WatchEvent<?> event : key.pollEvents()) {
				@SuppressWarnings("rawtypes")
				WatchEvent.Kind kind = event.kind();

				// Context for directory entry event is the file name of entry
				@SuppressWarnings("unchecked")
				Path name = ((WatchEvent<Path>) event).context();
				Path child = dir.resolve(name);
				File filename = child.toFile();
				// print out event
				if (matcher.matches(name.getFileName())) {
					System.out.println("Previous BLocking Queue"+blockingQueue);

					System.out.println(filename.lastModified());
//					System.out.println("Child::" + name);
					try {
						blockingQueue.put(child);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
//					blockingQueue.notifyAll();
					System.out.println("Later BLocking Queue"+blockingQueue);
				}
				// if directory is created, and watching recursively, then
				// register it and its sub-directories
				if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
					try {
						if (Files.isDirectory(child)) {
							walkAndRegisterDirectories(child);
						}
					} catch (IOException x) {
						// do something useful
					}
				}
//				blockingQueue.notifyAll();
			}

			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}

}
