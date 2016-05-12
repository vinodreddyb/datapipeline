package com.cts.datapipeline.merge;

import java.io.IOException;
import java.io.Writer;

import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.util.Assert;

public class HeaderWriter implements LineCallbackHandler, FlatFileHeaderCallback {
	private String header = "";
	
	@Override
	public void handleLine(String line) {
		Assert.notNull(line);
		this.header = line;
	}

	@Override
	public void writeHeader(Writer writer) throws IOException {
		writer.write("header - here ");
	}
}