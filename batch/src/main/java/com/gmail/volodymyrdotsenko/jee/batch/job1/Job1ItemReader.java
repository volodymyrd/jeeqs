package com.gmail.volodymyrdotsenko.jee.batch.job1;

import java.io.Serializable;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class Job1ItemReader extends AbstractItemReader {

	@Inject
	JobContext jobCtx;

	private StringTokenizer tokens;

	@Override
	public void open(Serializable checkpoint) throws Exception {
		JobOperator jobOperator = BatchRuntime.getJobOperator();
		JobExecution je = jobOperator.getJobExecution(jobCtx.getExecutionId());
		System.out.println(je.getJobParameters());
		
		Properties prop = jobCtx.getProperties();
		System.out.println(prop);
		String fileName = prop.getProperty("log_file_name");
		System.out.println(fileName);

		tokens = new StringTokenizer("1,2,3,4,5,6,7,8,9,10", ",");
	}

	@Override
	public InputRecord readItem() {
		if (tokens.hasMoreTokens()) {
			return new InputRecord(Integer.valueOf(tokens.nextToken()));
		}
		return null;
	}
}