package com.starcases.prime.sql.csvoutput;

import com.opencsv.bean.CsvBindByName;

import lombok.Getter;
import lombok.Setter;

public class CSVData
{
	@Setter
	@Getter
	@CsvBindByName
	public long index;

	@Setter
	@Getter
	@CsvBindByName
	public long prime;

	@Setter
	@Getter
	@CsvBindByName
	public Object[] base;

	public CSVData(final long index, final long prime, final Object[] bases)
	{
		this.index = index;
		this.prime = prime;
		this.base = bases;
	}
}
