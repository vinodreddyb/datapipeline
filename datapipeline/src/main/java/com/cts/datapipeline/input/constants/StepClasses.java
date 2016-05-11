package com.cts.datapipeline.input.constants;

import com.cts.datapipeline.input.StepItems;

public enum StepClasses {

	replacer(new StepItems("replacerItemReader","replacerItemWriter",
			"replacerPartitionReader","outputFileListener")),
	merger(new StepItems("mergeItemReader","mergerWriter",
			null,null));
	
	private StepItems items;
	private StepClasses(StepItems items) {
		this.items = items;
		String[] s = {};
	}
	public StepItems getItems() {
		return items;
	}
	
	
	
}
