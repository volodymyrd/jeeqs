package com.gmail.volodymyrdotsenko.phonebilling.batch;

import java.math.BigDecimal;

import javax.batch.api.chunk.ItemProcessor;
import javax.batch.runtime.context.JobContext;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;

import com.gmail.volodymyrdotsenko.phonebilling.domain.CallRecord;

/* Processor batch artifact.
 * Calculate the price of every call.
 */
@Dependent
@Named("CallRecordProcessor")
public class CallRecordProcessor implements ItemProcessor {
    
    @Inject
    JobContext jobCtx;
    double airPrice;
    
    public CallRecordProcessor() { }

    @Override
    public Object processItem(Object obj) throws Exception {
        CallRecord call;
        double callPrice;
        
        /* Calculate the price of this call */
        String s_airPrice = jobCtx.getProperties().getProperty("airtime_price");
        airPrice = Double.parseDouble(s_airPrice);
        call = (CallRecord) obj;
        callPrice = airPrice*(1.0*call.getMinutes() + call.getSeconds()/60.0);
        call.setPrice(new BigDecimal(callPrice));
        return call;
    }
    
}
