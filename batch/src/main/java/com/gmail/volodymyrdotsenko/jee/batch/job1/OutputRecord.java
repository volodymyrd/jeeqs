package com.gmail.volodymyrdotsenko.jee.batch.job1;

public class OutputRecord {
	private int id;

	public OutputRecord() {
	}

	public OutputRecord(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "OutputRecord: " + id;
	}
}