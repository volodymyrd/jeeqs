package com.gmail.volodymyrdotsenko.phonebilling.beans;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.gmail.volodymyrdotsenko.phonebilling.domain.PhoneBill;
import com.gmail.volodymyrdotsenko.phonebilling.tools.BatchTestHelper;
import com.gmail.volodymyrdotsenko.phonebilling.tools.CallRecordLogCreator;

/* Managed bean for the JSF front end pages.
 * - Shows the log file to the user.
 * - Enables the user to submit the job.
 * - Checks on the status of the job.
 * - Shows the results on a JSF page.
 */
@Named
@SessionScoped
public class JsfBean implements Serializable {

	CallRecordLogCreator logtool;
	private long execID;
	private JobOperator jobOperator;
	@PersistenceContext
	EntityManager em;
	private static final Logger logger = Logger.getLogger("JsfBean");
	private static final long serialVersionUID = 6775054787257816151L;

	/* Create a long log file of calls */
	public String createAndShowLog() throws FileNotFoundException, IOException {

		String log = "";
		BufferedReader breader;

		logtool = new CallRecordLogCreator();
		logtool.writeToFile("/home/logs/log1.txt");
		breader = new BufferedReader(new FileReader("/home/logs/log1.txt"));
		String line = breader.readLine();
		while (line != null) {
			log += line + '\n';
			line = breader.readLine();
		}
		return log;
	}

	/*
	 * Submit the batch job to the batch runtime. JSF Navigation method (return
	 * the name of the next page)
	 */
	public String startBatchJob() {
		jobOperator = BatchRuntime.getJobOperator();
		execID = jobOperator.start("phonebilling", null);
		return "jobstarted";
	}

	/* Get the status of the job from the batch runtime */
	public String getJobStatus() {
		JobExecution jobExecution = jobOperator.getJobExecution(execID);

		List<StepExecution> stepExecutions = jobOperator
				.getStepExecutions(execID);

		for (StepExecution stepExecution : stepExecutions) {
			System.out.println("Current step name:"
					+ stepExecution.getStepName());
			Map<Metric.MetricType, Long> metricsMap = BatchTestHelper
					.getMetricsMap(stepExecution.getMetrics());

			System.out.println("metric:" + metricsMap);

		}
		return jobExecution.getBatchStatus().toString();
	}

	public boolean isCompleted() {
		return (getJobStatus().compareTo("COMPLETED") == 0);
	}

	/*
	 * Because we can't output HTML code to a JSF page safely, we provide a list
	 * of bills. Each bill itself is a list of lines of text. This is for easy
	 * representation in JSF tables.
	 */
	public List<List<String>> getRowList() throws FileNotFoundException,
			IOException {
		List<List<String>> rowList = new ArrayList<>();

		if (isCompleted()) {
			String query = "SELECT b FROM PhoneBill b ORDER BY b.phoneNumber";
			Query q = em.createQuery(query);

			for (Object billObject : q.getResultList()) {
				/* Each bill */
				PhoneBill bill = (PhoneBill) billObject;
				List<String> lines = new ArrayList<>();

				try {
					FileReader reader = new FileReader("/home/logs/"
							+ bill.getPhoneNumber() + ".txt");
					try (BufferedReader breader = new BufferedReader(reader)) {
						String line = breader.readLine();
						while (line != null) {
							/* Each call in a bill */
							lines.add(line);
							line = breader.readLine();
						}
					}
				} catch (FileNotFoundException ex) {
					System.out.println(ex.getMessage());
				}
				System.out.println(lines);
				rowList.add(lines);
			}
		}
		return rowList;
	}
}
