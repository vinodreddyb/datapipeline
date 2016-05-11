package com.cts.datapipeline.input.constants;

import com.cts.datapipeline.input.StepItems;

public enum StepClasses {

	replacer(new StepItems("replacerItemReader","replacerItemProcessor","replacerItemWriter",
			"replacerPartitionReader","outputFileListener")),
	merger(new StepItems("mergeItemReader",null,"mergerWriter",
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
