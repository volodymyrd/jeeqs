package org.javaee7.batch.sample.chunk.mapper;

import java.io.Serializable;
import java.util.StringTokenizer;
import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.AbstractItemReader;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Arun Gupta
 */
@Named
public class MyItemReader extends AbstractItemReader {
	public static int totalReaders = 0;
	private int readerId;

	private StringTokenizer tokens;

	@Inject
	@BatchProperty(name = "start")
	private String startProp;

	@Inject
	@BatchProperty(name = "end")
	private String endProp;

	@Inject
	private JobContext context;

	@Override
	public void open(Serializable e) {

		System.out.println("param:" + startProp + "-" + endProp);
		int start = new Integer(startProp);
		int end = new Integer(endProp);
		StringBuilder builder = new StringBuilder();
		for (int i = start; i <= end; i++) {
			builder.append(i);
			if (i < end)
				builder.append(",");
		}

		readerId = ++totalReaders;
		tokens = new StringTokenizer(builder.toString(), ",");
	}

	@Override
	public MyInputRecord readItem() {
		if (tokens.hasMoreTokens()) {
			int token = Integer.valueOf(tokens.nextToken());
			System.out.format("readItem (%d): %d\n", readerId, token);
			return new MyInputRecord(token);
		}
		return null;
	}
}
