package com.gmail.volodymyrdotsenko.jee.batch.job1;

import java.util.List;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.inject.Named;

@Named
public class Job1ItemWriter extends AbstractItemWriter {

    @Override
    public void writeItems(List list) {
        System.out.println("writeItems: " + list);
    }

}