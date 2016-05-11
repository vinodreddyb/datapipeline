package com.cts.datapipeline.input;

public class StepItems {

	private String reader;
	private String writer;
	private String partitioner;
	private String listener;
	
	private String processor;
	
	public StepItems(String reader,String processor, String writer, String partitioner,String listener) {
		
		this.reader = reader;
		this.writer = writer;
		this.partitioner = partitioner;
		this.listener = listener;
		this.setProcessor(processor);
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

	public String getProcessor() {
		return processor;
	}

	public void setProcessor(String processor) {
		this.processor = processor;
	}
	
	
}
