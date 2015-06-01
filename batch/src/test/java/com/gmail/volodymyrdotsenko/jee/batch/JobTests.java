package com.gmail.volodymyrdotsenko.jee.batch;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gmail.volodymyrdotsenko.jee.batch.util.BatchTestHelper;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.*;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class JobTests {
	@Deployment
	public static WebArchive createDeployment() {
		WebArchive war = ShrinkWrap
				.create(WebArchive.class)
				.addClass(BatchTestHelper.class)
				.addPackage("com.gmail.volodymyrdotsenko.jee.batch.job1")
				.addAsWebInfResource(EmptyAsset.INSTANCE,
						ArchivePaths.create("beans.xml"))
				.addAsResource("META-INF/batch-jobs/job1.xml");
		System.out.println(war.toString(true));
		return war;
	}

	/**
	 * In the test, we're just going to invoke the batch execution and wait for
	 * completion. To validate the test expected behaviour we need to query the
	 * +javax.batch.runtime.Metric+ object available in the step execution.
	 *
	 * The batch process itself will read and process 10 elements from numbers 1
	 * to 10, but only write the odd elements. Commits are executed after 3
	 * elements are read.
	 *
	 * @throws Exception
	 *             an exception if the batch could not complete successfully.
	 */
	@Test
	public void testChunkSimple() throws Exception {
		JobOperator jobOperator = BatchRuntime.getJobOperator();
		Properties prop = new Properties();
		prop.put("log_file_name", "mylog.log");
		Long executionId = jobOperator.start("job1", prop);
		JobExecution jobExecution = jobOperator.getJobExecution(executionId);

		jobExecution = BatchTestHelper.keepTestAlive(jobExecution);

		List<StepExecution> stepExecutions = jobOperator
				.getStepExecutions(executionId);
		for (StepExecution stepExecution : stepExecutions) {
			if (stepExecution.getStepName().equals("step1")) {
				Map<Metric.MetricType, Long> metricsMap = BatchTestHelper
						.getMetricsMap(stepExecution.getMetrics());

				// <1> The read count should be 10 elements. Check
				// +MyItemReader+.
				assertEquals(10L, metricsMap.get(Metric.MetricType.READ_COUNT)
						.longValue());
				// <2> The write count should be 5. Only half of the elements
				// read are processed to be written.
				assertEquals(10L / 2L,
						metricsMap.get(Metric.MetricType.WRITE_COUNT)
								.longValue());
				// <3> The commit count should be 4. Checkpoint is on every 3rd
				// read, 4 commits for read elements.
				assertEquals(10L / 3 + (10L % 3 > 0 ? 1 : 0),
						metricsMap.get(Metric.MetricType.COMMIT_COUNT)
								.longValue());
			}
		}

		// <4> Job should be completed.
		assertEquals(jobExecution.getBatchStatus(), BatchStatus.COMPLETED);
	}
}