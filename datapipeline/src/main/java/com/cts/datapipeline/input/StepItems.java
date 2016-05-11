package com.cts.datapipeline.input;

public class StepItems {

	private String reader;
	private String writer;
	private String partitioner;
	private String listener;
	
	public StepItems(String reader, String writer, String partitioner,String listener) {
		
		this.reader = reader;
		this.writer = writer;
		this.partitioner = partitioner;
		this.listener = listener;
	}
	
	public String getReader() {
		return reader;
	}
	public void setReader(String reader) {
		this.reader = reader;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public String getPartitioner() {
		return partitioner;
	}
	public void setPartitioner(String partitioner) {
		this.partitioner = partitioner;
	}

	public String getListener() {
		return listener;
	}

	public void setListener(String listener) {
		this.listener = listener;
	}
	
	
}
