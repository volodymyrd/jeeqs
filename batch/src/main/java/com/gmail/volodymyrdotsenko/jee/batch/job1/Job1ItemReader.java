package com.gmail.volodymyrdotsenko.jee.batch.job1;

import java.io.Serializable;
import java.util.StringTokenizer;
import javax.batch.api.chunk.AbstractItemReader;
import javax.inject.Named;

@Named
public class Job1ItemReader extends AbstractItemReader {

	private StringTokenizer tokens;

    @Override
    public void open(Serializable checkpoint) throws Exception {
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