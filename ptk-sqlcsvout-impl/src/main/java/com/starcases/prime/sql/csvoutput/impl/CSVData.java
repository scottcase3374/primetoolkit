package com.starcases.prime.sql.csvoutput.impl;

import lombok.Getter;
import lombok.Setter;

public class CSVData
{
	@Setter
	@Getter
	public long index;

	@Setter
	@Getter
	public long prime;

	@Setter
	@Getter
	public Object[] base;

	public CSVData(final long index, final long prime, final Object[] bases)
	{
		this.index = index;
		this.prime = prime;
		this.base = bases;
	}
}
