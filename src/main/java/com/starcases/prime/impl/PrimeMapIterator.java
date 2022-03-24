package com.starcases.prime.impl;

import java.util.Iterator;

import com.starcases.prime.intfc.PrimeRefIntfc;

public class PrimeMapIterator implements Iterator<PrimeRefIntfc>
{
	private Iterator<PrimeMapEntry> entry;

	public PrimeMapIterator(Iterator<PrimeMapEntry> entry)
	{
		this.entry = entry;
	}

	@Override
	public boolean hasNext()
	{
		return entry.hasNext();
	}

	@Override
	public PrimeRefIntfc next()
	{
		return entry.next().getPrimeRef();
	}
}
