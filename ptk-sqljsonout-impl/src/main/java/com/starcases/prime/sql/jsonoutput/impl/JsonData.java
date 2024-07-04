package com.starcases.prime.sql.jsonoutput.impl;

import lombok.Getter;

public class JsonData
{
	@Getter
	private final long index;

	@Getter
	private final long prime;

	@Getter
	private final long[] base;
	
	/**
	 * Whether to keep based on base predicate results 
	 */
	@Getter
	private final boolean keep;

	public JsonData(final long index, final long prime, final long[] bases, final boolean keep)
	{
		this.index = index;
		this.prime = prime;
		this.base = bases;
		this.keep = keep;
	}
}
