package org.sapient.corejavaproblem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;

import org.sapient.corejavaproblem.tasks.ReaderTask;
import org.sapient.corejavaproblem.tasks.SmtdTask;
import org.sapient.corejavaproblem.tasks.WriteTask;
import org.sapient.corejavaproblem.utility.Utility;

public class TestApp {
	public static void main(String[] args) throws IOException, InterruptedException {
		Properties prop = Utility.getProperties();
		System.out.println(prop.getProperty("sort.parameter"));
		final BlockingQueue<Path> blockingQueue = new LinkedBlockingQueue<Path>();
		Path dir = Paths.get(prop.getProperty("root.dir"));
		System.out.println(dir);
		Integer threadCount = Runtime.getRuntime().availableProcessors();
		if (Files.exists(dir)) {
			ReaderTask readerTask = new ReaderTask(blockingQueue, dir);
			FutureTask<Integer> futureTask1 = new FutureTask<>(readerTask);
			ExecutorService readService = Executors.newSingleThreadExecutor();
			readService.submit(futureTask1);

			WriteTask writeTask = new WriteTask(blockingQueue);
			FutureTask<Integer> futureTask2 = new FutureTask<>(writeTask);
			ExecutorService writeService = Executors.newFixedThreadPool(threadCount);
			while(true) {
				Thread.sleep(1000);
				writeService.submit(futureTask2);
				if(futureTask1.isDone())
					break;
			}

			if (futureTask1.isDone() && futureTask2.isDone()) {
				SmtdTask smtdTask = new SmtdTask();
				FutureTask<Integer> smtdFuture = new FutureTask<>(smtdTask);
				writeService.submit(smtdFuture);
				// shut down executor service
				if(smtdFuture.isDone()) {
					readService.shutdown();
					writeService.shutdown();
				}
				System.out.println("Done");
			}
		} else {
			System.err.println("Path Not Correct");
		}

	}

}
