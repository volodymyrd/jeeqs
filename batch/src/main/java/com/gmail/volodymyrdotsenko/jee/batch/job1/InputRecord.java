package com.gmail.volodymyrdotsenko.jee.batch.job1;

public class InputRecord {
	private int id;

	public InputRecord() {
	}

	public InputRecord(int id) {
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
		return "InputRecord: " + id;
	}
}