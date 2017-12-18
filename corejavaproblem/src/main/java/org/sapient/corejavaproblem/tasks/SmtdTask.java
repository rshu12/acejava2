package org.sapient.corejavaproblem.tasks;

import java.util.concurrent.Callable;

import org.sapient.corejavaproblem.write.SmtdWrite;

public class SmtdTask implements Callable<Integer> {

	@Override
	public Integer call() throws Exception {
		new SmtdWrite();
		return null;
	}

}
