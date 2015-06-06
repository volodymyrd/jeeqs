package com.gmail.volodymyrdotsenko.phonebilling.batch;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Properties;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemReader;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/* Reader batch artifact.
 * Reads bills from the entity manager.
 * This artifact is in a partitioned step.
 */
//@Dependent
@Named
public class BillReader implements ItemReader {

	private ItemNumberCheckpoint checkpoint;
	@Inject
	JobContext jobCtx;
	private Properties partParams;
	@PersistenceContext
	private EntityManager em;
	private Iterator iterator;

	public BillReader() {
	}
	
	@Inject
    @BatchProperty(name = "firstItem")
    private String startProp;

	@Inject
    @BatchProperty(name = "numItems")
    private String endProp;
	
	@Override
	public void open(Serializable ckpt) throws Exception {

		/* Get the parameters for this partition */
		JobOperator jobOperator = BatchRuntime.getJobOperator();
		long execID = jobCtx.getExecutionId();
		partParams = jobOperator.getParameters(execID);
		
		System.out.println(jobCtx.getProperties());

		/* Get the range of items to work on in this partition */
		long firstItem0 = ((Long) partParams.get("firstItem")).longValue();
		long numItems0 = ((Long) partParams.get("numItems")).longValue();

		if (ckpt == null) {
			/* Create a checkpoint object for this partition */
			checkpoint = new ItemNumberCheckpoint();
			checkpoint.setItemNumber(firstItem0);
			checkpoint.setNumItems(numItems0);
		} else {
			checkpoint = (ItemNumberCheckpoint) ckpt;
		}

		/* Adjust range for this partition from the checkpoint */
		long firstItem = checkpoint.getItemNumber();
		long numItems = numItems0 - (firstItem - firstItem0);

		/* Obtain an iterator for the bills in this partition */
		String query = "SELECT b FROM PhoneBill b ORDER BY b.phoneNumber";
		Query q = em.createQuery(query).setFirstResult((int) firstItem)
				.setMaxResults((int) numItems);
		iterator = q.getResultList().iterator();
	}

	@Override
	public void close() throws Exception {

	}

	@Override
	public Object readItem() throws Exception {
		if (iterator.hasNext()) {
			checkpoint.nextItem();
			checkpoint.setNumItems(checkpoint.getNumItems() - 1);
			return iterator.next();
		} else {
			return null;
		}
	}

	@Override
	public Serializable checkpointInfo() throws Exception {
		return checkpoint;
	}

}
