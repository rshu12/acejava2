package org.sapient.corejavaproblem.tasks;

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.sapient.corejavaproblem.read.WatchDirectoryRecursively;

public class ReaderTask implements Callable<Integer> {

	private final BlockingQueue<Path> blockingQueue;
	private Path dir;

	public ReaderTask(BlockingQueue<Path> blockingQueue, Path path) {
		this.blockingQueue = blockingQueue;
		this.dir = path;
	}

	@Override
	public Integer call() throws Exception {
		new WatchDirectoryRecursively(dir, blockingQueue);
		return null;
	}

}
