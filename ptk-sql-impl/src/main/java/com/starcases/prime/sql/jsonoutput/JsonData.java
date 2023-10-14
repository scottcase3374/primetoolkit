package com.starcases.prime.sql.jsonoutput;

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
	private Object[] base;

	public JsonData(final long index, final long prime, final Object[] bases)
	{
		this.index = index;
		this.prime = prime;
		this.base = bases;
	}
}
