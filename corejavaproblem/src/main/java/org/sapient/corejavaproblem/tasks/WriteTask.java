package org.sapient.corejavaproblem.tasks;

import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import org.sapient.corejavaproblem.process.ProcessFileForOperationImpl;

public class WriteTask implements Callable<Integer> {

	private final BlockingQueue<Path> blockingQueue;

	public WriteTask(BlockingQueue<Path> blockingQueue) {
		this.blockingQueue = blockingQueue;
	}

	@Override
	public Integer call() throws Exception {

		new ProcessFileForOperationImpl(blockingQueue);
		return null;
	}

}
