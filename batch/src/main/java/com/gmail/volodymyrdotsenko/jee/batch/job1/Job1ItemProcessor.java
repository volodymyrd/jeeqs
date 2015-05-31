package com.gmail.volodymyrdotsenko.jee.batch.job1;

import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Named;

@Named
public class Job1ItemProcessor implements ItemProcessor {

    @Override
    public OutputRecord processItem(Object t) {
        System.out.println("processItem: " + t);

        return (((InputRecord) t).getId() % 2 == 0) ? null : new OutputRecord(((InputRecord) t).getId() * 2);
    }

}
