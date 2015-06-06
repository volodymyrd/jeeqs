package com.gmail.volodymyrdotsenko.phonebilling.batch;

import java.util.Properties;
import javax.batch.api.partition.PartitionMapper;
import javax.batch.api.partition.PartitionPlan;
import javax.batch.api.partition.PartitionPlanImpl;
import javax.enterprise.context.Dependent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/* Partition mapper artifact.
 * Determines the number of partitions (2) for the bill processing step
 * and the range of bills each partition should work on.
 */
//@Dependent
@Named
public class BillPartitionMapper implements PartitionMapper {

	@PersistenceContext
	EntityManager em;

	@Override
	public PartitionPlan mapPartitions() throws Exception {
		/* Create a new partition plan */
		return new PartitionPlanImpl() {

			/* Auxiliary method - get the number of bills */
			public long getBillCount() {
				String query = "SELECT COUNT(b) FROM PhoneBill b";
				Query q = em.createQuery(query);
				long n = ((Long) q.getSingleResult()).longValue();
				return n;
			}

			/*
			 * The number of partitions could be dynamically calculated based on
			 * many parameters. In this particular example, we are setting it to
			 * a fixed value for simplicity.
			 */
			@Override
			public int getPartitions() {
				return 2;
			}

			/*
			 * Obtaint the parameters for each partition. In this case, the
			 * parameters represent the range of items each partition of the
			 * step should work on.
			 */
			@Override
			public Properties[] getPartitionProperties() {

				/*
				 * Assign an (approximately) equal number of elements to each
				 * partition.
				 */
				long totalItems = getBillCount();
				long partItems = (long) totalItems / getPartitions();
				long remItems = totalItems % getPartitions();

				/*
				 * Populate a Properties array. Each Properties element in the
				 * array corresponds to a partition.
				 */
				Properties[] props = new Properties[getPartitions()];

//				for (int i = 0; i < getPartitions(); i++) {
//					props[i] = new Properties();
//					props[i].put("firstItem", i * partItems);
//					/* Last partition gets the remainder elements */
//					if (i == getPartitions() - 1) {
//						props[i].put("numItems", partItems + remItems);
//					} else {
//						props[i].put("numItems", partItems);
//					}
//				}
				props[0] = new Properties();
				props[0].put("firstItem", 1);
				props[0].put("numItems", 30);
				props[1] = new Properties();
				props[1].put("firstItem", 31);
				props[1].put("numItems", 50);
				return props;
			}
		};
	}
}