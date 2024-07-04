package com.starcases.prime.sql.csvoutput.impl;

import lombok.Getter;

public class CSVData
{
	@Getter
	public long index;

	@Getter
	public long prime;

	@Getter
	public long[] base;
	
	public boolean keep;

	public CSVData(final long index, final long prime, final long[] bases, final boolean keep)
	{
		this.index = index;
		this.prime = prime;
		this.base = bases;
		this.keep = keep;
	}
}
