package com.starcases.prime.sql.jsonoutput.impl;

import lombok.Getter;
import lombok.Setter;

public class JsonData
{
	@Setter
	@Getter
	private long index;

	@Setter
	@Getter
	private long prime;

	@Setter
	@Getter
	private long[] base;

	public JsonData(final long index, final long prime, final long[] bases)
	{
		this.index = index;
		this.prime = prime;
		this.base = bases;
	}
}
