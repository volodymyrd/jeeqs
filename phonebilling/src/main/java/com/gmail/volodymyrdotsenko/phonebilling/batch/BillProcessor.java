package com.gmail.volodymyrdotsenko.phonebilling.batch;

import java.math.BigDecimal;

import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.gmail.volodymyrdotsenko.phonebilling.domain.PhoneBill;

/* Processor artifact for bills.
 * Compute amount and total for each bill
 */
//@Dependent
@Named
public class BillProcessor implements ItemProcessor {

	@Inject
	JobContext jobCtx;

	@PersistenceContext
	EntityManager em;

	@Override
	public Object processItem(Object billObject) throws Exception {

		String s_taxRate = jobCtx.getProperties().get("tax_rate").toString();
		double taxRate = Double.parseDouble(s_taxRate);
		PhoneBill bill = em.merge((PhoneBill) billObject);
		bill.calculate(new BigDecimal(taxRate));
		return bill;
	}
}